package dev.prath.accord.controller.conversation;

import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
@Component
public class ConversationExportTabController {

	@FXML
	private CheckBox exportedSelectedCheckbox;
	@FXML 
	private CheckBox exportFromUsersCheckbox;
	@FXML
	private Button exportButton;
	@FXML
	private Text progressText;
	@FXML
	private ListView<User> participatingUsersList;
	
	private static ListView<Message> conversationListView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static Button selectAllButton;
	private static Text numOfMsgText;
	
	protected static void setParentControls(ListView<Message> list, Tab[] tabList, Button selectButton, Text numText) {
		conversationListView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
	}
}
