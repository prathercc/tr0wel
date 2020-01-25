package dev.prath.accord.controller.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.controller.AuthenticationController;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
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
public class ConversationManagementTabController {
	
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

	@FXML
	private Text configProgressText;

	public void initialize() {
		DiscordAccount discordAccount = accountService.getDiscordAccount();
		discordAccount.getConversations().stream().forEach(conversation -> dmUserDropDown.getItems().add(conversation));
	}

	private void launchManageConversation(List<Message> conversationMessages) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Conversation selectedConversation = (Conversation) dmUserDropDown.getValue();
				selectedConversation.setMessages(conversationMessages);
				accountService.setSelectedConversation(selectedConversation);
				Stage stage = stageService.getNewStageAsDialog("accord - Conversation Manager",
						"/fxml/ConversationManager/ConversationManager.fxml", AuthenticationController.configurationStage);
				if(stage != null) {
					stage.show();
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
				while (reachedEnd != true) {
					List<Message> newMessagesList = service.fetchConversationMessages(lastId);
					if (newMessagesList.size() < 100) {
						updateConfigProgress("Loaded conversation "
								+ accountService.getSelectedConversation().getId());
						reachedEnd = true; // If the data length was less than 100, we know we have reached the end
					}
					messageList.addAll(newMessagesList); // Populate our messageList with the additional data
					Thread.sleep(250);
					lastId = newMessagesList.get(newMessagesList.size() - 1).getId(); // Save the last id we are on
					updateConfigProgress(newMessagesList.size() != 0 ? "Loading - [" + lastId + "]" : "[" + (lastId.length() > 0 ? lastId : "Last Id not found") + "] - Failure!");
				}
				updateConfigProgress("");
				toggleControls(false);
				launchManageConversation(messageList);
				return null;
			}
		};
		return messageTask;
	}

	private void toggleControls(boolean val) {
		if (!val && dmUserDropDown.getValue() != null) {
			manageDmButton.setDisable(val); // Only re-enable if there is a conversation selected
		}
		if (val) {
			manageDmButton.setDisable(val);
		}
		dmUserDropDown.setDisable(val);
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
