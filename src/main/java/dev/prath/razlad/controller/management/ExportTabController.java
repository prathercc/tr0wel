package dev.prath.razlad.controller.management;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.razlad.domain.Channel;
import dev.prath.razlad.domain.Conversation;
import dev.prath.razlad.domain.Message;
import dev.prath.razlad.domain.User;
import dev.prath.razlad.service.AccountService;
import dev.prath.razlad.service.FileService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;

@Component
public class ExportTabController {
	@FXML
	private CheckBox exportAllCheckBox;
	@FXML
	private CheckBox exportFromUsersCheckBox;
	@FXML
	private Button exportButton;
	@FXML
	private Text progressText;
	@FXML
	private ListView<User> participatingUsersList;

	@Autowired
	AccountService accountService;
	@Autowired
	FileService fileService;

	private static ListView<Message> listView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static CheckBox selectAllButton;
	private static Text numOfMsgText;
	private static ChoiceBox<User> userSelectionBox;

	public void exportMessages() {
		Channel selectedChannel = accountService.getSelectedChannel();
		Conversation selectedConversation = accountService.getSelectedConversation();

		List<User> userExportList = exportAllCheckBox
				.isSelected()
						? participatingUsersList.getItems()
						: exportFromUsersCheckBox.isSelected()
								? participatingUsersList.getItems().stream().filter(user -> user.getIsSelected().get())
										.collect(Collectors.toList())
								: null;

		if (userExportList != null) {
			List<Message> allMessages = selectedChannel != null ? selectedChannel.getMessages()
					: selectedConversation.getMessages();

			List<String> userIdList = new ArrayList<String>();
			userExportList.stream().forEach(user -> userIdList.add(user.getId()));
			userIdList.stream().distinct().collect(Collectors.toList());
			
			List<Message> messagesToExport = allMessages.stream()
					.filter(msg -> userIdList.contains(msg.getAuthor().getId())).collect(Collectors.toList());
			
			fileService.exportMessages(messagesToExport);
		}

	}

	public void exportUsersCheck() {
		exportAllCheckBox.setSelected(false);
		participatingUsersList.setDisable(!exportFromUsersCheckBox.isSelected());
	}

	public void exportAllCheck() {
		exportFromUsersCheckBox.setSelected(false);
		participatingUsersList.setDisable(true);
	}

	public void initialize() {
		participatingUsersList.setCellFactory(CheckBoxListCell.forListView(User::getIsSelected));
		var selectedChannel = accountService.getSelectedChannel();
		var selectedConversation = accountService.getSelectedConversation();
		var userList = selectedChannel != null ? selectedChannel.getParticipatingUsers()
				: selectedConversation.getRecipients();
		userList.stream().forEach(user -> participatingUsersList.getItems().add(user));
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
