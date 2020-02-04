package dev.prath.accord.controller.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.MessageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;

@Component
public class ManagerController {
	@FXML
	private ListView<Message> listView;
	@FXML
	private Button selectAllButton;
	@FXML
	private ChoiceBox<User> userSelectionBox;
	@FXML
	private Text numOfMsgText;
	@FXML
	private Tab exportTab;
	@FXML
	private Tab editTab;
	@FXML
	private Tab deleteTab;
	@FXML
	private TabPane conversationTabPane;

	@Autowired
	AccountService accountService;

	@Autowired
	MessageService messageService;

	private boolean selectOrientation = false;

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
		DeleteTabController.setParentControls(listView, new Tab[] { exportTab, editTab, deleteTab }, selectAllButton,
				numOfMsgText, userSelectionBox);
		EditTabController.setParentControls(listView, new Tab[] { exportTab, editTab, deleteTab }, selectAllButton,
				numOfMsgText, userSelectionBox);
		ExportTabController.setParentControls(listView, new Tab[] { exportTab, editTab, deleteTab }, selectAllButton,
				numOfMsgText, userSelectionBox);
	}

	public void selectAll() {
		selectOrientation = !selectOrientation ? true : false;
		listView.getItems().stream().forEach(message -> message.setIsSelected(selectOrientation));
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

							/* If the user selected another user in a conversation, disable the edit/delete tabs */
							if (selectedConversation != null) {
								conversationTabPane.getSelectionModel()
										.select(!sessionUID.equalsIgnoreCase(selectedUserId) ? exportTab
												: conversationTabPane.getSelectionModel().getSelectedItem());
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
		selectAllButton.setDisable(val);
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
