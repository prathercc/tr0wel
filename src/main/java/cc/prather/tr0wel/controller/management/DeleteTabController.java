package cc.prather.tr0wel.controller.management;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.FxLauncher;
import cc.prather.tr0wel.controller.utility.LoadingBoxController;
import cc.prather.tr0wel.domain.Channel;
import cc.prather.tr0wel.domain.Conversation;
import cc.prather.tr0wel.domain.Message;
import cc.prather.tr0wel.domain.User;
import cc.prather.tr0wel.service.AccountService;
import cc.prather.tr0wel.service.MessageService;
import cc.prather.tr0wel.service.StageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

@Component
public class DeleteTabController {
	@FXML
	private Button deleteSelectionsButton;
	@FXML
	private Text progressText;

	private static ListView<Message> listView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static CheckBox selectAllButton;
	private static Text numOfMsgText;
	private static ComboBox<User> userSelectionBox;
	private static Button deleteSelectionsButtonCopy;

	@Autowired
	AccountService accountService;

	@Autowired
	MessageService messageService;

	@Autowired
	StageService stageService;

	private static final Logger logger = LoggerFactory.getLogger(DeleteTabController.class);

	public void initialize() {
		deleteSelectionsButtonCopy = deleteSelectionsButton;
	}

	public void deleteSelections() {
		List<Message> selectedMessagesList = listView.getItems().stream()
				.filter(message -> message.getIsSelected().get()).collect(Collectors.toList());
		if (selectedMessagesList.size() > 0) {
			Thread thread = new Thread(getNewDeletionTask());
			thread.setDaemon(true);
			thread.start();
		}
	}

	public Task<Void> getNewDeletionTask() {
		Task<Void> deletionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				List<Message> msgsToDelete = new ArrayList<Message>();
				List<Message> selectedMessagesList = listView.getItems().stream()
						.filter(message -> message.getIsSelected().get()).collect(Collectors.toList());
				Channel selectedChannel = accountService.getSelectedChannel();
				Conversation selectedConversation = accountService.getSelectedConversation();
				for (Message msg : selectedMessagesList) {
					String selectedId = selectedChannel != null ? selectedChannel.getId()
							: selectedConversation.getId();
					var response = messageService.deleteMessage(msg, selectedId);
					if (response) {
						updateText(progressText, "Deleted message " + msg.getId().substring(msg.getId().length() - 4));
						msgsToDelete.add(msg);
					} else {
						updateText(progressText,
								"Could not delete message " + msg.getId().substring(msg.getId().length() - 4));
					}
					Thread.sleep(250);
				}
				updateMessages(msgsToDelete, selectedChannel, selectedConversation);
				toggleControls(false);
				return null;
			}
		};
		deletionTask.setOnRunning(e -> {
			toggleControls(true);
			stageService.launchLoadingWindow(ManagerController.stage);
		});
		deletionTask.setOnSucceeded(e -> {
			stageService.getTempStage().hide();
		});
		return deletionTask;
	}

	private void updateMessages(List<Message> msgsToDelete, Channel selectedChannel,
			Conversation selectedConversation) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				msgsToDelete.stream().forEach(message -> {
					if (selectedChannel != null)
						selectedChannel.getMessages().remove(message);
					else
						selectedConversation.getMessages().remove(message);
					listView.getItems().remove(message);
				});
				numOfMsgText.setText(
						listView.getItems().size() != 0 ? "Found " + listView.getItems().size() + " messages by user."
								: "");
			}
		});
	}

	private void toggleControls(boolean val) {
		selectAllButton.setDisable(val);
		deleteTab.setDisable(val);
		exportTab.setDisable(val);
		editTab.setDisable(val);
		listView.setDisable(val);
		userSelectionBox.setDisable(val);
	}

	private void updateText(Text text, String val) {
		LoadingBoxController.setLoadingText(val);
	}

	protected static void setParentControls(ListView<Message> list, Tab[] tabList, CheckBox selectButton, Text numText,
			ComboBox<User> userSelectionBox2) {
		listView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
		userSelectionBox = userSelectionBox2;
		selectAllButton.setOnMouseClicked(e -> {
			Integer numOfSelected = listView.getItems().stream().filter(message -> message.getIsSelected().get())
					.collect(Collectors.toList()).size();
			if (numOfSelected != 0) {
				deleteSelectionsButtonCopy.setDisable(false);
			} else {
				deleteSelectionsButtonCopy.setDisable(true);
			}
		});
		listView.setOnMouseMoved(e -> {
			Integer numOfSelected = listView.getItems().stream().filter(message -> message.getIsSelected().get())
					.collect(Collectors.toList()).size();
			if (numOfSelected != 0) {
				deleteSelectionsButtonCopy.setDisable(false);
			} else {
				deleteSelectionsButtonCopy.setDisable(true);
			}
		});
	}
}
