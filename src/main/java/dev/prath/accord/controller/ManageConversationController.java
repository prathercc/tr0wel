package dev.prath.accord.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.DisposalService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;

@Component
public class ManageConversationController {
	@FXML
	private ListView<Message> listView;
	@FXML
	private Button selectAllButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Text numOfMsgText;
	@FXML
	private Text progressText;

	@Autowired
	AccountService accountService;

	@Autowired
	DisposalService disposalService;

	private static final Logger logger = LoggerFactory.getLogger(ManageConversationController.class);

	private boolean selectOrientation = false;

	public void initialize() {
		var sessionUID = accountService.getDiscordAccount().getUser().getId();
		var conversationMessages = accountService.getSelectedConversation().getMessages();
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		conversationMessages.stream().filter(message -> message.getAuthor().getId().equalsIgnoreCase(sessionUID))
				.forEach(message -> listView.getItems().add(message));
		updateText(numOfMsgText, "Found " + listView.getItems().size() + " messages by you in this conversation");
	}

	public void selectAll() {
		selectOrientation = !selectOrientation ? true : false;
		listView.getItems().stream().forEach(message -> message.setIsSelected(selectOrientation));
	}

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
				var selectedMessagesList = listView.getItems().stream().filter(message -> message.getIsSelected().get())
						.collect(Collectors.toList());
				for (Message msg : selectedMessagesList) {
					var response = disposalService.deleteConversationMessage(msg);
					if (response) {
						updateText(progressText, "Deletion Success - [" + msg.getId() + "]");
						msgsToDelete.add(msg);
					} else {
						updateText(progressText, "Deletion Failure - [" + msg.getId() + "]");
					}
					Thread.sleep(250);
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						msgsToDelete.stream().forEach(message -> listView.getItems().remove(message));
						updateText(numOfMsgText,
								"Found " + listView.getItems().size() + " messages by you in this conversation");
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				listView.setDisable(val);
				selectAllButton.setDisable(val);
				deleteButton.setDisable(val);
			}
		});
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
