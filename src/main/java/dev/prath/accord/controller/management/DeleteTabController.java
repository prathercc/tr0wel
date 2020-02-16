package dev.prath.accord.controller.management;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.MessageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

@Component
public class DeleteTabController {
	@FXML
	private Button deleteSelectionsButton;
	@FXML
	private Text progressText;

	private static ListView<Message> listView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static CheckBox selectAllButton;
	private static Text numOfMsgText;
	private static ChoiceBox<User> userSelectionBox;

	@Autowired
	AccountService accountService;

	@Autowired
	MessageService messageService;

	private static final Logger logger = LoggerFactory.getLogger(DeleteTabController.class);

	public void deleteSelections() {
		Thread thread = new Thread(getNewDeletionTask());
		thread.setDaemon(true);
		thread.start();
	}

	public Task<Void> getNewDeletionTask() {
		Task<Void> deletionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				List<Message> msgsToDelete = new ArrayList<Message>();
				List<Message> selectedMessagesList = listView.getItems().stream().filter(message -> message.getIsSelected().get())
						.collect(Collectors.toList());
				Channel selectedChannel = accountService.getSelectedChannel();
				Conversation selectedConversation = accountService.getSelectedConversation();
				for (Message msg : selectedMessagesList) {
					String selectedId = selectedChannel != null ? selectedChannel.getId() : selectedConversation.getId();
					var response = messageService.deleteMessage(msg, selectedId);
					if (response) {
						updateText(progressText, "Deletion Success - " + msg.getId());
						msgsToDelete.add(msg);
					} else {
						updateText(progressText, "Deletion Failure - " + msg.getId());
					}
					Thread.sleep(250);
				}
				updateMessages(msgsToDelete,selectedChannel,selectedConversation);
				updateText(progressText, "");
				toggleControls(false);
				return null;
			}
		};
		return deletionTask;
	}
	
	private void updateMessages(List<Message> msgsToDelete, Channel selectedChannel, Conversation selectedConversation) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				msgsToDelete.stream().forEach(message -> {
					if (selectedChannel != null)
						selectedChannel.getMessages().remove(message);
					else
						selectedConversation.getMessages().remove(message);
					listView.getItems().remove(message);
				});
				updateText(numOfMsgText,
						listView.getItems().size() != 0
								? "Found " + listView.getItems().size() + " messages by user."
								: "");
			}
		});
	}

	private void toggleControls(boolean val) {
		selectAllButton.setDisable(val);
		deleteTab.setDisable(val);
		exportTab.setDisable(val);
		editTab.setDisable(val);
		listView.setDisable(val);
		userSelectionBox.setDisable(val);
	}

	private void updateText(Text text, String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(val);
			}
		});
	}

	protected static void setParentControls(ListView<Message> list, Tab[] tabList, CheckBox selectButton, Text numText,
			ChoiceBox<User> usersBox) {
		listView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
		userSelectionBox = usersBox;
	}
}
