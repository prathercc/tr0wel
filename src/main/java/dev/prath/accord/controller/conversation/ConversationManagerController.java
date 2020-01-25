package dev.prath.accord.controller.conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.DisposalService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;
@Component
public class ConversationManagerController {
	
	@FXML
	private Tab exportTab;
	@FXML
	private Tab editTab;
	@FXML
	private Tab deleteTab;
	@FXML
	private ListView<Message> conversationListView;
	@FXML
	private Button selectAllButton;
	@FXML
	private Text numOfMsgText;
	
	@Autowired
	AccountService accountService;

	@Autowired
	DisposalService disposalService;

	private static final Logger logger = LoggerFactory.getLogger(ConversationManagerController.class);

	private boolean selectOrientation = false;
	
	public void initialize() {
		var sessionUID = accountService.getDiscordAccount().getUser().getId();
		var conversationMessages = accountService.getSelectedConversation().getMessages();
		conversationListView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		conversationMessages.stream().filter(message -> message.getAuthor().getId().equalsIgnoreCase(sessionUID))
				.forEach(message -> conversationListView.getItems().add(message));
		updateText(numOfMsgText, "Found " + conversationListView.getItems().size() + " messages by you in this conversation");
		ConversationDeleteTabController.setParentControls(conversationListView, new Tab[] {exportTab, editTab,deleteTab}, selectAllButton, numOfMsgText);
		ConversationEditTabController.setParentControls(conversationListView, new Tab[] {exportTab, editTab,deleteTab}, selectAllButton, numOfMsgText);
		ConversationExportTabController.setParentControls(conversationListView, new Tab[] {exportTab, editTab,deleteTab}, selectAllButton, numOfMsgText);
	}
	
	public void selectAll() {
		selectOrientation = !selectOrientation ? true : false;
		conversationListView.getItems().stream().forEach(message -> message.setIsSelected(selectOrientation));
	}
	
	private void updateText(Text text, String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(val);
			}
		});
	}
	

}
