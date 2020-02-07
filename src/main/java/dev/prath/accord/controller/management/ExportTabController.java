package dev.prath.accord.controller.management;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
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
	
	private static ListView<Message> listView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static CheckBox selectAllButton;
	private static Text numOfMsgText;
	private static ChoiceBox<User> userSelectionBox;
	
	public void exportUsersCheck(){
		exportAllCheckBox.setSelected(false);
		participatingUsersList.setDisable(!exportFromUsersCheckBox.isSelected());
	}
	
	public void exportAllCheck(){
		exportFromUsersCheckBox.setSelected(false);
		participatingUsersList.setDisable(true);
	}
	
	public void initialize() {
		participatingUsersList.setCellFactory(CheckBoxListCell.forListView(User::getIsSelected));
		var selectedChannel = accountService.getSelectedChannel();
		var selectedConversation = accountService.getSelectedConversation();
		var userList = selectedChannel != null ? selectedChannel.getParticipatingUsers() : selectedConversation.getRecipients();
		userList.stream().forEach(user -> participatingUsersList.getItems().add(user));
	}

	protected static void setParentControls(ListView<Message> list, Tab[] tabList, CheckBox selectButton, Text numText, ChoiceBox<User> usersBox) {
		listView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
		userSelectionBox = usersBox;
	}
}
