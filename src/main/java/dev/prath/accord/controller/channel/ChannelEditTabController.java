package dev.prath.accord.controller.channel;

import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
@Component
public class ChannelEditTabController {
	
	@FXML
	private TextField newMessageTextField;
	@FXML
	private Button editSelectionsButton;
	@FXML
	private Text progressText;
	
	private static ListView<Message> channelListView;
	private static Tab exportTab;
	private static Tab editTab;
	private static Tab deleteTab;
	private static Button selectAllButton;
	private static Text numOfMsgText;
	private static ChoiceBox<User> userSelectionBox;
	
	protected static void setParentControls(ListView<Message> list, Tab[] tabList, Button selectButton, Text numText, ChoiceBox<User> usersBox) {
		channelListView = list;
		exportTab = tabList[0];
		editTab = tabList[1];
		deleteTab = tabList[2];
		selectAllButton = selectButton;
		numOfMsgText = numText;
		userSelectionBox = usersBox;
	}
}
