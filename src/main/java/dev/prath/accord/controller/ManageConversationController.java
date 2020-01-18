package dev.prath.accord.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.DisposalService;
import dev.prath.accord.utility.Properties;
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

	Properties properties = new Properties();

	@Autowired
	AccountService accountService;

	@Autowired
	DisposalService disposalService;

	private boolean selectOrientation = false;

	public void initialize() {
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		accountService.getSelectedConversation().getMessages().stream()
				.filter(message -> message.getAuthor().getId()
						.equalsIgnoreCase(accountService.getDiscordAccount().getUser().getId()))
				.forEach(message -> listView.getItems().add(message));
		updateNumOfMessagesText("Found " + listView.getItems().size() + " messages by you in this conversation");
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
				List<Message> msgToDelete = new ArrayList<Message>();
				for (Message msg : listView.getItems()) {
					if (msg.getIsSelected().get()) {
						try {
							ResponseEntity<String> response = disposalService.deleteConversationMessage(msg);
							if (response.getStatusCodeValue() == 204) {
								updateProgressText("Deletion Success - [" + msg.getId() + "]");
								msgToDelete.add(msg);
							}
							Thread.sleep(250);

						} catch (Exception e) {
							updateProgressText("Deletion Failure - [" + msg.getId() + "]");
						}
					}
				}

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						msgToDelete.stream().forEach(message -> listView.getItems().remove(message));
						updateNumOfMessagesText(
								"Found " + listView.getItems().size() + " messages by you in this conversation");
					}
				});
				updateProgressText("");
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

	private void updateProgressText(String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				progressText.setText(val);
			}
		});
	}

	private void updateNumOfMessagesText(String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				numOfMsgText.setText(val);
			}
		});
	}

}
