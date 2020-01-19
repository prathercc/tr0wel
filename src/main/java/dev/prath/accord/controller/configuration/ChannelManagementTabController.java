package dev.prath.accord.controller.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.controller.AuthenticationController;
import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.MessageService;
import dev.prath.accord.service.StageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ChannelManagementTabController {

	@FXML
	private ChoiceBox<Guild> guildSelectionBox;
	@FXML
	private ChoiceBox<Channel> channelSelectionBox;
	@FXML
	private Button manageChannelButton;

	@Autowired
	MessageService service;

	@Autowired
	AccountService accountService;

	@Autowired
	StageService stageService;

	@FXML
	private Text configProgressText;

	private static final Logger logger = LoggerFactory.getLogger(ChannelManagementTabController.class);

	public void initialize() {
		DiscordAccount discordAccount = accountService.getDiscordAccount();
		discordAccount.getGuilds().stream().forEach(guild -> guildSelectionBox.getItems().add(guild));
	}

	private void launchManageChannel(List<Message> channelMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Channel selectedChannel = (Channel) channelSelectionBox.getValue();
				selectedChannel.setMessages(channelMessages);
				accountService.setSelectedChannel(selectedChannel);
				Stage stage = stageService.getNewStageAsDialog("accord - Channel Manager Menu",
						"/fxml/manageChannelMenu.fxml", AuthenticationController.configurationStage);
				if(stage != null) {
					stage.show();
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
		selectedGuild.getChannels().stream().forEach(channel -> channelSelectionBox.getItems().add(channel));
		manageChannelButton.setDisable(true); // New guild was selected, so disable the 'manageChannelButton'
	}

	private Task<Void> getNewChannelTask() {
		Task<Void> messageTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				var selectedChannel = (Channel) channelSelectionBox.getValue();
				accountService.setSelectedChannel(selectedChannel);
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> messageList = new ArrayList<Message>();
				while (reachedEnd != true) {
					List<Message> newMessagesList = service.fetchChannelMessages(lastId);
					if (newMessagesList.size() < 100) {
						updateConfigProgress(
								"Completed loading for channel " + selectedChannel.getId());
						reachedEnd = true; // If the data length was less than 100, we know we have reached the end
					}
					messageList.addAll(newMessagesList); // Populate our messageList with the additional data
					Thread.sleep(250);
					lastId = newMessagesList.get(newMessagesList.size() - 1).getId(); // Save the last id we are on
					updateConfigProgress(newMessagesList.size() != 0 ? "Loading - [" + lastId + "]"
							: "[" + (lastId.length() > 0 ? lastId : "Last Id not found") + "] - Failure!");
				}
				updateConfigProgress("");
				toggleControls(false);
				launchManageChannel(messageList);
				return null;
			}
		};
		return messageTask;
	}

	private void toggleControls(boolean val) {
		if (!val && channelSelectionBox.getValue() != null) {
			manageChannelButton.setDisable(val); // Only re-enable if there is a channel selected
		}
		if (val) {
			manageChannelButton.setDisable(val);
			channelSelectionBox.setDisable(val);
		}
		if (!val && guildSelectionBox.getValue() != null) {
			channelSelectionBox.setDisable(val);
		}

		guildSelectionBox.setDisable(val);
	}

	private void updateConfigProgress(String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				configProgressText.setText(val);
			}
		});
	}
}
