package dev.prath.accord.controller.conversation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.controller.channel.ChannelDeleteTabController;
import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.MessageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
@Component
public class ConversationEditTabController {
	
	@FXML
	private TextField newMessageTextField;
	@FXML
	private Button editSelectionsButton;
	@FXML
	private Text progressText;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	AccountService accountService;
	
	private static ListView<Message> conversationListView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static Button selectAllButton;
	private static Text numOfMsgText;
	
	private static final Logger logger = LoggerFactory.getLogger(ConversationEditTabController.class);
	
	public void editSelections() {
		Thread thread = new Thread(getNewEditTask());
		thread.setDaemon(true);
		thread.start();
	}
	
	public Task<Void> getNewEditTask() {
		Task<Void> deletionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				List<Message> msgsToEdit = new ArrayList<Message>();
				var selectedMessagesList = conversationListView.getItems().stream().filter(message -> message.getIsSelected().get())
						.collect(Collectors.toList());
				for (Message msg : selectedMessagesList) {
					var response = messageService.editMessage(msg, newMessageTextField.getText(), accountService.getSelectedConversation().getId());
					if (response) {
						updateText(progressText, "Edit Success - [" + msg.getId() + "]");
						msgsToEdit.add(msg);
					} else {
						updateText(progressText, "Edit Failure - [" + msg.getId() + "]");
					}
					Thread.sleep(250);
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						msgsToEdit.stream().forEach(newMessage -> {
							conversationListView.getItems().stream().forEach(oldMessage -> {
								if(oldMessage.getId().equalsIgnoreCase(newMessage.getId())) {
									oldMessage.setMessage(newMessageTextField.getText()); // Update message
									oldMessage.setIsSelected(false); // De-select the edited messages.
								}
							});
						});
						conversationListView.refresh();
						updateText(numOfMsgText,
								"Found " + conversationListView.getItems().size() + " messages by you in this conversation");
					}
				});
				updateText(progressText, "");
				toggleControls(false);
				return null;
			}
		};
		return deletionTask;
	}
	
	private void toggleControls(boolean val) {
		selectAllButton.setDisable(val);
		deleteTab.setDisable(val);
		exportTab.setDisable(val);
		editTab.setDisable(val);
		conversationListView.setDisable(val);
		editSelectionsButton.setDisable(val);
		newMessageTextField.setDisable(val);
	}
	
	private void updateText(Text text, String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(val);
			}
		});
	}
	
	protected static void setParentControls(ListView<Message> list, Tab[] tabList, Button selectButton, Text numText) {
		conversationListView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
	}
}
