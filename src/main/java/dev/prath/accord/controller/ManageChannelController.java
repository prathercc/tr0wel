package dev.prath.accord.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.DisposalService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;

@Component
public class ManageChannelController {
	@FXML
	private ListView<Message> listView;
	@FXML
	private Button selectAllButton;
	@FXML
	private ChoiceBox<User> userSelectionBox;
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

	private boolean selectOrientation = false;

	private static final Logger logger = LoggerFactory.getLogger(ManageChannelController.class);

	public void initialize() {
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		accountService.getSelectedChannel().getParticipatingUsers().stream()
				.forEach(user -> userSelectionBox.getItems().add(user));
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
					var response = disposalService.deleteChannelMessage(msg);
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

						if (listView.getItems().size() != 0) {
							updateText(numOfMsgText, "Found " + listView.getItems().size() + " messages by user.");
						} else {
							// No more messages? Pull them off the user selection list
							User userToDelete = (User) userSelectionBox.getValue();
							accountService.getSelectedChannel().getParticipatingUsers().remove(userToDelete);
							userSelectionBox.getSelectionModel().clearSelection();
							userSelectionBox.getItems().clear();
							accountService.getSelectedChannel().getParticipatingUsers().stream()
									.forEach(user -> userSelectionBox.getItems().add(user));
							updateText(numOfMsgText, "");
						}
					}
				});
				updateText(progressText, "");
				toggleControls(false);
				return null;
			}
		};
		return deletionTask;
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
							var selectedChannelMessages = accountService.getSelectedChannel().getMessages();
							var selectedUserId = userSelectionBox.getValue().getId();
							selectedChannelMessages.stream()
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				listView.setDisable(val);
				selectAllButton.setDisable(val);
				userSelectionBox.setDisable(val);
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
