package dev.prath.accord.controller.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Conversation;
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
public class ConversationManagerController {

	@FXML
	private Tab exportTab;
	@FXML
	private Tab editTab;
	@FXML
	private Tab deleteTab;
	@FXML
	private ListView<Message> conversationListView;
	@FXML
	private Button selectAllButton;
	@FXML
	private Text numOfMsgText;
	@FXML
	private ChoiceBox<User> userSelectionBox;
	@FXML
	private TabPane conversationTabPane;

	@Autowired
	AccountService accountService;

	@Autowired
	MessageService messageService;

	private static final Logger logger = LoggerFactory.getLogger(ConversationManagerController.class);

	private boolean selectOrientation = false;

	public void initialize() {
		Conversation conversation = accountService.getSelectedConversation();
		conversationListView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		conversation.getRecipients().stream().forEach(user -> userSelectionBox.getItems().add(user));
		ConversationDeleteTabController.setParentControls(conversationListView,
				new Tab[] { exportTab, editTab, deleteTab }, selectAllButton, numOfMsgText);
		ConversationEditTabController.setParentControls(conversationListView,
				new Tab[] { exportTab, editTab, deleteTab }, selectAllButton, numOfMsgText);
		ConversationExportTabController.setParentControls(conversationListView,
				new Tab[] { exportTab, editTab, deleteTab }, selectAllButton, numOfMsgText);
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
						conversationListView.getItems().clear();
						if (userSelectionBox.getValue() != null) {
							var sessionUID = accountService.getDiscordAccount().getUser().getId();
							var selectedConversationMessages = accountService.getSelectedConversation().getMessages();
							var selectedUserId = userSelectionBox.getValue().getId();

							/* If the user selected another user, disable the edit/delete tabs */
							conversationTabPane.getSelectionModel()
									.select(!sessionUID.equalsIgnoreCase(selectedUserId) ? exportTab
											: conversationTabPane.getSelectionModel().getSelectedItem());
							editTab.setDisable(!sessionUID.equalsIgnoreCase(selectedUserId));
							deleteTab.setDisable(!sessionUID.equalsIgnoreCase(selectedUserId));
							/*******************************************************************/

							selectedConversationMessages.stream()
									.filter(message -> message.getAuthor().getId().equalsIgnoreCase(selectedUserId))
									.forEach(message -> conversationListView.getItems().add(message));
							updateText(numOfMsgText,
									"Found " + conversationListView.getItems().size() + " messages by user.");
						}
					}
				});
				toggleControls(false);
				return null;
			}
		};
		return selectionTask;
	}

	public void selectAll() {
		selectOrientation = !selectOrientation ? true : false;
		conversationListView.getItems().stream().forEach(message -> message.setIsSelected(selectOrientation));
	}

	private void toggleControls(boolean val) {
		conversationListView.setDisable(val);
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
