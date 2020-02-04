package dev.prath.accord.controller.management;

import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

@Component
public class ExportTabController {
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
	
	private static ListView<Message> listView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static Button selectAllButton;
	private static Text numOfMsgText;
	private static ChoiceBox<User> userSelectionBox;
	
	protected static void setParentControls(ListView<Message> list, Tab[] tabList, Button selectButton, Text numText, ChoiceBox<User> usersBox) {
		listView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
		userSelectionBox = usersBox;
	}
}
