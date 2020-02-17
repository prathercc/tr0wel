package dev.prath.tr0wel.controller.management;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.tr0wel.domain.Channel;
import dev.prath.tr0wel.domain.Conversation;
import dev.prath.tr0wel.domain.Message;
import dev.prath.tr0wel.domain.User;
import dev.prath.tr0wel.service.AccountService;
import dev.prath.tr0wel.service.MessageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

@Component
public class EditTabController {
	@FXML
	private TextField newMessageTextField;
	@FXML
	private Button editSelectionsButton;
	@FXML
	private Text progressText;

	private static ListView<Message> listView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static CheckBox selectAllButton;
	private static Text numOfMsgText;
	private static ChoiceBox<User> userSelectionBox;

	@Autowired
	MessageService messageService;

	@Autowired
	AccountService accountService;

	private static final Logger logger = LoggerFactory.getLogger(EditTabController.class);

	public void editSelections() {
		Thread thread = new Thread(getNewEditTask());
		thread.setDaemon(true);
		thread.start();
	}

	public Task<Void> getNewEditTask() {
		Task<Void> deletionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				List<Message> msgsToEdit = new ArrayList<Message>();
				List<Message> selectedMessagesList = listView.getItems().stream()
						.filter(message -> message.getIsSelected().get()).collect(Collectors.toList());
				for (Message msg : selectedMessagesList) {
					Channel selectedChannel = accountService.getSelectedChannel();
					Conversation selectedConversation = accountService.getSelectedConversation();
					String selectedId = selectedChannel != null ? selectedChannel.getId()
							: selectedConversation.getId();
					var response = messageService.editMessage(msg, newMessageTextField.getText(), selectedId);
					if (response) {
						updateText(progressText, "Edit Success - " + msg.getId());
						msgsToEdit.add(msg);
					} else {
						updateText(progressText, "Edit Failure - " + msg.getId());
					}
					Thread.sleep(550);
				}
				updateMessages(msgsToEdit);
				updateText(progressText, "");
				toggleControls(false);
				return null;
			}
		};
		return deletionTask;
	}

	private void updateMessages(List<Message> msgsToEdit) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				msgsToEdit.stream().forEach(newMessage -> {
					listView.getItems().stream().forEach(oldMessage -> {
						if (oldMessage.getId().equalsIgnoreCase(newMessage.getId())) {
							oldMessage.setMessage(newMessageTextField.getText()); // Update message
							oldMessage.setIsSelected(false); // De-select the edited messages.
						}
					});
				});
				listView.refresh();
				updateText(numOfMsgText, listView.getItems().size() != 0
						? "Found " + listView.getItems().size() + " messages by user."
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
		editSelectionsButton.setDisable(val);
		newMessageTextField.setDisable(val);
		userSelectionBox.setDisable(val);
	}

	private void updateText(Text text, String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(val);
			}
		});
	}

	protected static void setParentControls(ListView<Message> list, Tab[] tabList, CheckBox selectButton, Text numText,
			ChoiceBox<User> usersBox) {
		listView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
		userSelectionBox = usersBox;
	}
}
