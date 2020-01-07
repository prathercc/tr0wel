package org.prathdev.accord.controller;

import java.util.ArrayList;
import java.util.List;

import org.prathdev.accord.domain.Channel;
import org.prathdev.accord.domain.DiscordAccount;
import org.prathdev.accord.domain.Message;
import org.prathdev.accord.domain.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;

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

	Channel selectedChannel = null;
	DiscordAccount discordAccount = null;

	private boolean selectOrientation = false;

	public void setUpMessageData(DiscordAccount acc, Channel channel) {
		selectedChannel = channel;
		discordAccount = acc;
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));

		for (User user : channel.getParticipatingUsers()) {
			userSelectionBox.getItems().add(user);
		}

		System.out.println("Loaded ManageChannelController for channel '" + selectedChannel.getName() + "', containing "
				+ selectedChannel.getMessages().size() + " messages!");
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
				listView.setDisable(true);
				selectAllButton.setDisable(true);
				userSelectionBox.setDisable(true);
				deleteButton.setDisable(true);
				List<Message> msgToDelete = new ArrayList<Message>();

				for (Message msg : listView.getItems()) {

					if (msg.getIsSelected().get()) {
						try {
							RestTemplate restTemplate = new RestTemplate();
							String requestUrl = "https://discordapp.com/api/channels/" + selectedChannel.getId()
									+ "/messages/" + msg.getId();
							HttpHeaders headers = new HttpHeaders();
							headers.set("authorization", discordAccount.getAuthorization());
							headers.set("user-agent",
									"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
							HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
							restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
							ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE,
									request, String.class);
							if (response.getStatusCodeValue() == 204) {
								System.out.println("Successfully deleted: " + msg.toString());
								msgToDelete.add(msg);
							}
							Thread.sleep(1000);

						} catch (Exception e) {
							System.out.println("Was unable to delete: " + msg.toString());
						}
					}
				}

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						for (Message delMsg : msgToDelete) {
							listView.getItems().remove(delMsg);
						}
					}
				});

				listView.setDisable(false);
				selectAllButton.setDisable(false);
				userSelectionBox.setDisable(false);
				deleteButton.setDisable(false);
				return null;
			}
		};
		return deletionTask;
	}

	public Task<Void> getNewSelectionTask() {
		Task<Void> selectionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				listView.setDisable(true);
				selectAllButton.setDisable(true);
				userSelectionBox.setDisable(true);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						listView.getItems().clear();

						User selectedUser = (User) userSelectionBox.getValue();

						for (Message msg : selectedChannel.getMessages()) {
							if (msg.getAuthor().getId().equalsIgnoreCase(selectedUser.getId())) {
								listView.getItems().add(msg);
							}
						}
						numOfMsgText.setText("Found " + listView.getItems().size() + " messages by user.");

					}
				});

				listView.setDisable(false);
				selectAllButton.setDisable(false);
				userSelectionBox.setDisable(false);
				return null;
			}

		};
		return selectionTask;

	}

}
