package dev.prath.accord.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import dev.prath.accord.utility.Properties;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;

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

	Conversation selectedConversation = null;
	DiscordAccount discordAccount = null;

	private boolean selectOrientation = false;

	public void setUpMessageData(DiscordAccount acc, Conversation conversation) {
		selectedConversation = conversation;
		discordAccount = acc;
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));

		for (Message m : selectedConversation.getMessages()) {
			if(m.getAuthor().getId().equalsIgnoreCase(acc.getUser().getId())) {
				listView.getItems().add(m); // Only display messages that belong to the logged in user
			}
			
		}
		updateNumOfMessagesText("Found " + listView.getItems().size() + " messages by you in this conversation");
	}

	public void selectAll() {
		if (!selectOrientation)
			selectOrientation = true;
		else
			selectOrientation = false;
		for (Message m : listView.getItems()) {
			m.setIsSelected(selectOrientation);
		}
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
							RestTemplate restTemplate = new RestTemplate();
							String requestUrl = properties.getDiscordChannelsUrl() + "/" + selectedConversation.getId()
									+ "/messages/" + msg.getId();
							HttpHeaders headers = new HttpHeaders();
							headers.set("authorization", discordAccount.getAuthorization());
							headers.set("user-agent", properties.getUserAgent());
							HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
							restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
							ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE,
									request, String.class);
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
						for (Message delMsg : msgToDelete) {
							listView.getItems().remove(delMsg);
						}
						updateNumOfMessagesText("Found " + listView.getItems().size() + " messages by you in this conversation");
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
