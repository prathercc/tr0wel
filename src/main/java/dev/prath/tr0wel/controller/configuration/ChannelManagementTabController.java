package dev.prath.tr0wel.controller.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.tr0wel.controller.authentication.AuthenticationController;
import dev.prath.tr0wel.domain.Channel;
import dev.prath.tr0wel.domain.DiscordAccount;
import dev.prath.tr0wel.domain.Guild;
import dev.prath.tr0wel.domain.Message;
import dev.prath.tr0wel.service.AccountService;
import dev.prath.tr0wel.service.MessageService;
import dev.prath.tr0wel.service.StageService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ChannelManagementTabController {

	@FXML
	private Button manageChannelButton;
	@FXML
	private ListView<Guild> guildListView;
	@FXML
	private ListView<Channel> channelListView;
	@FXML
	private Text progressText;

	private static Accordion configurationAccordian;
	private static TitledPane conversationTitlePane;
	private static TitledPane channelTitlePane;
	private static TitledPane statsTitlePane;

	@Autowired
	MessageService service;

	@Autowired
	AccountService accountService;

	@Autowired
	StageService stageService;

	private static final Logger logger = LoggerFactory.getLogger(ChannelManagementTabController.class);

	public void initialize() {
		initializeListViews();
		DiscordAccount discordAccount = accountService.getDiscordAccount();
		discordAccount.getGuilds().stream().forEach(guild -> guildListView.getItems().add(guild));
	}

	private void launchManageChannel(List<Message> channelMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Channel selectedChannel = channelListView.getSelectionModel().getSelectedItem();
				selectedChannel.setMessages(channelMessages);
				accountService.setSelectedChannel(selectedChannel);
				Stage stage = stageService.getNewStageAsDialog("", "/fxml/Management/Manager.fxml",
						AuthenticationController.configurationStage);
				if (stage != null)
					stage.show();
				else
					logger.error("ChannelManagementTabController received null value for stage.");
			}
		});
	}

	public void manageChannel() {
		Thread thread = new Thread(getNewChannelTask());
		thread.setDaemon(true);
		thread.start();
	}

	private Task<Void> getNewChannelTask() {
		Task<Void> messageTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				var selectedChannel = channelListView.getSelectionModel().getSelectedItem();
				accountService.setSelectedChannel(selectedChannel);
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> messageList = new ArrayList<Message>();
				while (reachedEnd != true) {
					List<Message> newMessagesList = service.fetchChannelMessages(lastId);
					if (newMessagesList.size() < 100) {
						updateConfigProgress("Loaded channel " + selectedChannel.getId());
						reachedEnd = true; // If the data length was less than 100, we know we have reached the end
					}
					messageList.addAll(newMessagesList); // Populate our messageList with the additional data
					Thread.sleep(250);
					lastId = newMessagesList.size() != 0 ? newMessagesList.get(newMessagesList.size() - 1).getId() : "";
					updateConfigProgress(newMessagesList.size() != 0 ? "Loading - " + lastId
							: (lastId.length() > 0 ? lastId : "Last Id not found") + " - Failure!");
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
		guildListView.setDisable(val);
		channelListView.setDisable(val);
		manageChannelButton.setDisable(val);
		configurationAccordian.setDisable(val);
	}

	private void updateConfigProgress(String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				progressText.setText(val);
			}
		});
	}

	private void initializeListViews() {
		guildListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Guild>() {
			@Override
			public void changed(ObservableValue<? extends Guild> observable, Guild oldValue, Guild newValue) {
				channelListView.getItems().clear(); // Reset channelListView items
				channelListView.scrollTo(Integer.MIN_VALUE); // Reset channelListView scroll position
				Guild selectedGuild = guildListView.getSelectionModel().getSelectedItem();
				selectedGuild.getChannels().stream().forEach(channel -> channelListView.getItems().add(channel));
				manageChannelButton.setDisable(true); // New guild was selected, so disable the 'manageChannelButton'
			}
		});
		channelListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Channel>() {
			@Override
			public void changed(ObservableValue<? extends Channel> observable, Channel oldValue, Channel newValue) {
				manageChannelButton.setDisable(false); // Allow manageChannelButton to be pressed
			}
		});
	}

	protected static void setParentControls(Accordion accordian, TitledPane[] titlePaneArr) {
		conversationTitlePane = titlePaneArr[0];
		channelTitlePane = titlePaneArr[1];
		statsTitlePane = titlePaneArr[2];
		configurationAccordian = accordian;
	}
}
