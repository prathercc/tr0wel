
package dev.prath.accord.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.MessageService;
import dev.prath.accord.service.StageService;
import dev.prath.accord.utility.Properties;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
@Component
public class ConfigurationController {

	@FXML
	private ChoiceBox<Guild> guildSelectionBox;
	@FXML
	private ChoiceBox<Channel> channelSelectionBox;
	@FXML
	private Button manageChannelButton;
	@FXML
	private Text configProgressText;
	@FXML
	private ChoiceBox<Conversation> dmUserDropDown;
	@FXML
	private Button manageDmButton;

	@Autowired
	MessageService service;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	StageService stageService;
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	public void initialize() {
		DiscordAccount discordAccount = accountService.getDiscordAccount();
		discordAccount.getGuilds().stream().forEach(guild -> guildSelectionBox.getItems().add(guild));
		discordAccount.getConversations().stream().forEach(conversation -> dmUserDropDown.getItems().add(conversation));
	}

	/* Conversation */

	private void launchManageConversation(List<Message> conversationMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Conversation selectedConversation = (Conversation) dmUserDropDown.getValue();
				selectedConversation.setMessages(conversationMessages);
				try {
					accountService.setSelectedConversation(selectedConversation);
					Stage stage = stageService.getNewStageAsDialog("accord - Conversation Manager", "/fxml/manageConversationMenu.fxml", AuthenticationController.configurationStage);
					stage.show();
				} catch (Exception e) {
				}
			}
		});
	}

	public void manageConversation() {
		try {
			Thread thread = new Thread(getNewConversationTask());
			thread.setDaemon(true);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectConversation() {
		manageDmButton.setDisable(false);
	}

	private Task<Void> getNewConversationTask() {
		Task<Void> messageTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				accountService.setSelectedConversation((Conversation) dmUserDropDown.getValue());
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> messageList = new ArrayList<Message>();

				while (reachedEnd != true && accountService.getSelectedConversation() != null) {
					try {
						List<Message> newMessagesList = service.fetchConversationMessages(lastId);
						if (newMessagesList.size() < 100) {
							updateConfigProgress("Completed loading for conversation " + accountService.getSelectedConversation().getId());
							reachedEnd = true; // If the data length was less than 100, we know we have reached the end
						}
						messageList.addAll(newMessagesList); // Populate our messageList with the additional data
						Thread.sleep(250);
						lastId = newMessagesList.get(newMessagesList.size() - 1).getId(); // Save the last id we are on
						updateConfigProgress("Loading - [" + lastId + "]");
					} catch (Exception e) {
						updateConfigProgress("[" + (lastId.length() > 0 ? lastId : "Last Id not found") + "] - Failure!");
					}
				}
				updateConfigProgress("");
				toggleControls(false);
				launchManageConversation(messageList);
				return null;
			}
		};
		return messageTask;
	}

	/************/
	/************/
	/************/
	/************/

	/* Channel */

	private void launchManageChannel(List<Message> channelMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Channel selectedChannel = (Channel) channelSelectionBox.getValue();
				selectedChannel.setMessages(channelMessages);
				try {
					accountService.setSelectedChannel(selectedChannel);
					Stage stage = stageService.getNewStageAsDialog("accord - Channel Manager Menu", "/fxml/manageChannelMenu.fxml", AuthenticationController.configurationStage);
					stage.show();
				} catch (Exception e) {
				}
			}
		});
	}

	public void manageChannel() {
		try {
			Thread thread = new Thread(getNewChannelTask());
			thread.setDaemon(true);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectChannel() {
		manageChannelButton.setDisable(false);
	}

	@SuppressWarnings("unchecked")
	public void selectGuild() {
		channelSelectionBox.setDisable(false);
		channelSelectionBox.getItems().clear();
		channelSelectionBox.getSelectionModel().clearSelection();
		Guild selectedGuild = (Guild) guildSelectionBox.getValue();
		for (Channel channel : selectedGuild.getChannels()) {
			channelSelectionBox.getItems().add(channel);
		}
		manageChannelButton.setDisable(true); // New guild was selected, so disable the 'manageChannelButton'
	}

	private Task<Void> getNewChannelTask() {
		Task<Void> messageTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				accountService.setSelectedChannel((Channel) channelSelectionBox.getValue());
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> messageList = new ArrayList<Message>();

				while (reachedEnd != true) {
					try {
						List<Message> newMessagesList = service.fetchChannelMessages(lastId);
						if (newMessagesList.size() < 100) {
							updateConfigProgress("Completed loading for channel " + accountService.getSelectedChannel().getId());
							reachedEnd = true; // If the data length was less than 100, we know we have reached the end
						}
						messageList.addAll(newMessagesList); // Populate our messageList with the additional data
						Thread.sleep(250);
						lastId = newMessagesList.get(newMessagesList.size() - 1).getId(); // Save the last id we are on
						updateConfigProgress("Loading - [" + lastId + "]");
					} catch (Exception e) {
						updateConfigProgress("[" + (lastId.length() > 0 ? lastId : "Last Id not found") + "] - Failure!");
					}
				}
				updateConfigProgress("");
				toggleControls(false);
				launchManageChannel(messageList);
				return null;
			}
		};
		return messageTask;
	}

	/*******/
	/*******/
	/*******/
	/*******/
	/*******/
	

	/* Util */

	private void toggleControls(boolean val) {
		/* Channel Controls */
		if (!val && channelSelectionBox.getValue() != null) {
			manageChannelButton.setDisable(val); // Only re-enable if there is a channel selected
		}
		if (val) {
			manageChannelButton.setDisable(val);
			channelSelectionBox.setDisable(val);
		}
		if(!val && guildSelectionBox.getValue() != null) {
			channelSelectionBox.setDisable(val);
		}
		
		guildSelectionBox.setDisable(val);
		/*****************/

		/* Conversation Controls */
		if (!val && dmUserDropDown.getValue() != null) {
			manageDmButton.setDisable(val); // Only re-enable if there is a conversation selected
		}
		if (val) {
			manageDmButton.setDisable(val);
		}
		dmUserDropDown.setDisable(val);
		/**********************/
	}

	private void updateConfigProgress(String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				configProgressText.setText(val);
			}
		});
	}

	/**/

}
