package cc.prather.tr0wel.controller.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.domain.Message;
import cc.prather.tr0wel.domain.User;
import cc.prather.tr0wel.service.AccountService;
import cc.prather.tr0wel.service.MessageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ManagerController {
	@FXML
	private ListView<Message> listView;
	@FXML
	private ComboBox<User> userSelectionBox;
	@FXML
	private Text numOfMsgText;
	@FXML
	private Tab exportTab;
	@FXML
	private Tab editTab;
	@FXML
	private Tab deleteTab;
	@FXML
	private Tab informationTab;
	@FXML
	private TabPane conversationTabPane;
	@FXML
	private CheckBox selectAllCheckBox;
	
	public static Stage stage;

	@Autowired
	AccountService accountService;
	@Autowired
	MessageService messageService;

	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

	public void initialize() {
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		var selectedChannel = accountService.getSelectedChannel();
		var selectedConversation = accountService.getSelectedConversation();
		if (selectedConversation != null) {
			selectedConversation.getRecipients().stream().forEach(user -> userSelectionBox.getItems().add(user));
		} else if (selectedChannel != null) {
			selectedChannel.getParticipatingUsers().stream().forEach(user -> userSelectionBox.getItems().add(user));
		}
		DeleteTabController.setParentControls(listView, new Tab[] { exportTab, editTab, deleteTab }, selectAllCheckBox,
				numOfMsgText, userSelectionBox);
		EditTabController.setParentControls(listView, new Tab[] { exportTab, editTab, deleteTab }, selectAllCheckBox,
				numOfMsgText, userSelectionBox);
		ExportTabController.setParentControls(listView, new Tab[] { exportTab, editTab, deleteTab }, selectAllCheckBox,
				numOfMsgText, userSelectionBox);
	}

	public void selectAll() {
		listView.getItems().stream().forEach(message -> message.setIsSelected(selectAllCheckBox.isSelected()));
	}

	public void selectUser() {
		Thread thread = new Thread(getNewSelectionTask());
		thread.setDaemon(true);
		thread.start();
	}

	public Task<Void> getNewSelectionTask() {
		Task<Void> selectionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						listView.getItems().clear();
						if (userSelectionBox.getValue() != null) {
							var sessionUID = accountService.getDiscordAccount().getUser().getId();
							var selectedChannel = accountService.getSelectedChannel();
							var selectedConversation = accountService.getSelectedConversation();
							var selectedMessages = selectedConversation != null ? selectedConversation.getMessages()
									: selectedChannel.getMessages();
							var selectedUserId = userSelectionBox.getValue().getId();
							selectAllCheckBox.setSelected(false);

							/*
							 * If the user selected another user in a conversation, disable the edit/delete
							 * tabs
							 */
							if (selectedConversation != null) {
								var selectedTab = conversationTabPane.getSelectionModel().getSelectedItem();
								conversationTabPane.getSelectionModel()
										.select(selectedTab == editTab || selectedTab == deleteTab ? informationTab
												: selectedTab);
								editTab.setDisable(!sessionUID.equalsIgnoreCase(selectedUserId));
								deleteTab.setDisable(!sessionUID.equalsIgnoreCase(selectedUserId));
							}
							/**************************************************************************************/

							selectedMessages.stream()
									.filter(message -> message.getAuthor().getId().equalsIgnoreCase(selectedUserId))
									.forEach(message -> listView.getItems().add(message));
							updateText(numOfMsgText, "Found " + listView.getItems().size() + " messages by user.");
						}
					}
				});
				toggleControls(false);
				return null;
			}
		};
		return selectionTask;
	}

	private void toggleControls(boolean val) {
		listView.setDisable(val);
		selectAllCheckBox.setDisable(val);
		userSelectionBox.setDisable(val);
		exportTab.setDisable(val);
		editTab.setDisable(val);
		deleteTab.setDisable(val);
	}

	private void updateText(Text text, String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(val);
			}
		});
	}
}
