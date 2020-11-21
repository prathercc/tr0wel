package cc.prather.tr0wel.controller.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.FxLauncher;
import cc.prather.tr0wel.controller.authentication.AuthenticationController;
import cc.prather.tr0wel.controller.utility.LoadingBoxController;
import cc.prather.tr0wel.domain.Channel;
import cc.prather.tr0wel.domain.Conversation;
import cc.prather.tr0wel.domain.DiscordAccount;
import cc.prather.tr0wel.domain.Guild;
import cc.prather.tr0wel.domain.Message;
import cc.prather.tr0wel.service.AccountService;
import cc.prather.tr0wel.service.MessageService;
import cc.prather.tr0wel.service.StageService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ConversationManagementTabController {

	@FXML
	private ListView<Conversation> conversationListView;
	@FXML
	private Button manageDmButton;

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

	private static final Logger logger = LoggerFactory.getLogger(ConversationManagementTabController.class);

	public void initialize() {
		initializeListViews();
		DiscordAccount discordAccount = accountService.getDiscordAccount();
		discordAccount.getConversations().stream()
				.forEach(conversation -> conversationListView.getItems().add(conversation));
	}

	private void launchManageConversation(List<Message> conversationMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Conversation selectedConversation = accountService.getSelectedConversation();
				selectedConversation.setMessages(conversationMessages);
				accountService.setSelectedConversation(selectedConversation);
				Stage stage = stageService.getNewStageAsDialog("", "/fxml/Management/Manager.fxml",
						AuthenticationController.configurationStage);
				if (stage != null)
					stage.show();
				else
					logger.error("ConversationManagementTabController received null value for stage.");
			}
		});
	}

	public void manageConversation() {
		Thread thread = new Thread(getNewConversationTask());
		thread.setDaemon(true);
		thread.start();
	}

	public void selectConversation() {
		manageDmButton.setDisable(false);
	}

	private Task<Void> getNewConversationTask() {
		Task<Void> messageTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				accountService.setSelectedConversation(conversationListView.getSelectionModel().getSelectedItem());
				boolean reachedEnd = false;
				String lastId = "";
				List<Message> messageList = new ArrayList<Message>();
				while (reachedEnd != true) {
					List<Message> newMessagesList = service.fetchConversationMessages(lastId);
					if (newMessagesList.size() < 100) {
						reachedEnd = true; // If the data length was less than 100, we know we have reached the end
					}
					messageList.addAll(newMessagesList); // Populate our messageList with the additional data
					updateConfigProgress("Loaded " + messageList.size() + " messages...");
					Thread.sleep(250);
					if(newMessagesList.size() > 0) {
						lastId = newMessagesList.get(newMessagesList.size() - 1).getId(); // Save the last id we are on
					}
				}
				toggleControls(false);
				launchManageConversation(messageList);
				return null;
			}
		};
		messageTask.setOnRunning(e -> {
			toggleControls(true);
			stageService.launchLoadingWindow(FxLauncher.authenticationMenu);
		});
		messageTask.setOnSucceeded(e -> {
			stageService.getTempStage().hide();
		});
		return messageTask;
	}

	private void toggleControls(boolean val) {
		conversationListView.setDisable(val);
		manageDmButton.setDisable(val);
		configurationAccordian.setDisable(val);
	}

	private void updateConfigProgress(String val) {
		LoadingBoxController.setLoadingText(val);
	}

	private void initializeListViews() {
		conversationListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Conversation>() {
			@Override
			public void changed(ObservableValue<? extends Conversation> observable, Conversation oldValue,
					Conversation newValue) {
				manageDmButton.setDisable(false);
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
