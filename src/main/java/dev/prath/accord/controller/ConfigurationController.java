
package dev.prath.accord.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.Message;
import dev.prath.accord.service.MessageService;
import dev.prath.accord.utility.Properties;

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

	Properties properties = new Properties();

	MessageService service = new MessageService();
	
	private DiscordAccount discordAccount = null;

	public void setUpConfigurationMenu(DiscordAccount val) {
		discordAccount = val;
		for (Guild guild : discordAccount.getGuilds()) {
			guildSelectionBox.getItems().add(guild);
		}
		for (Conversation convo : discordAccount.getConversations()) {
			dmUserDropDown.getItems().add(convo);
		}
	}

	/* Conversation */

	private void launchManageConversation(List<Message> conversationMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Conversation selectedConversation = (Conversation) dmUserDropDown.getValue();
				selectedConversation.setMessages(conversationMessages);
				try {
					String fxml = "/fxml/manageConversationMenu.fxml";
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource(fxml));
					Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxml));
					Scene scene = new Scene(rootNode);
					Stage stage = new Stage();
					ManageConversationController controller = loader.getController();
					controller.setUpMessageData(discordAccount, selectedConversation);
					stage.setTitle("accord - Conversation Manager");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.initModality(Modality.WINDOW_MODAL);
					stage.initOwner(AuthenticationController.configurationStage);
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
				Conversation selectedConversation = (Conversation) dmUserDropDown.getValue();
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> retList = new ArrayList<Message>();

				while (reachedEnd != true) {
					try {
						Message[] responseArr = service.fetchConversationMessages(selectedConversation, discordAccount, lastId);
						if (responseArr.length < 100) {
							updateConfigProgress("Completed loading for conversation " + selectedConversation.getId());
							reachedEnd = true; // If the data length was less than 100, we know we have reached the end
						}
						for (Message msg : responseArr) {
							retList.add(msg); // Populate our retList with the additional data
						}
						Thread.sleep(250);
						lastId = responseArr[responseArr.length - 1].getId(); // Save the last id we are on
						updateConfigProgress("Loading - [" + lastId + "]");
					} catch (Exception e) {
						updateConfigProgress("[" + lastId + "] - Failure!");
					}
				}
				updateConfigProgress("");
				toggleControls(false);
				launchManageConversation(retList);
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
					String fxml = "/fxml/manageChannelMenu.fxml";
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource(fxml));
					Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxml));
					Scene scene = new Scene(rootNode);
					Stage stage = new Stage();
					ManageChannelController controller = loader.getController();
					controller.setUpMessageData(discordAccount, selectedChannel);
					stage.setTitle("accord - Channel Manager Menu");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.initModality(Modality.WINDOW_MODAL);
					stage.initOwner(AuthenticationController.configurationStage);
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
				Channel selectedChannel = (Channel) channelSelectionBox.getValue();
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> retList = new ArrayList<Message>();

				while (reachedEnd != true) {
					try {
						Message[] responseArr = service.fetchChannelMessages(selectedChannel, discordAccount, lastId);
						if (responseArr.length < 100) {
							updateConfigProgress("Completed loading for channel " + selectedChannel.getId());
							reachedEnd = true; // If the data length was less than 100, we know we have reached the end
						}
						for (Message msg : responseArr) {
							retList.add(msg); // Populate our retList with the additional data
						}
						Thread.sleep(250);
						lastId = responseArr[responseArr.length - 1].getId(); // Save the last id we are on
						updateConfigProgress("Loading - [" + lastId + "]");
					} catch (Exception e) {
						updateConfigProgress("[" + lastId + "] - Failure!");
					}
				}
				updateConfigProgress("");
				toggleControls(false);
				launchManageChannel(retList);
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
