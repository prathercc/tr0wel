package dev.prath.accord.controller.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.service.AccountService;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

@Component
public class InformationTabController {
	@FXML
	private Text nameText;
	@FXML
	private Text idText;
	@FXML
	private Text activeUsersText;
	@FXML
	private Text typeText;
	@FXML
	private Text nsfwText;
	@FXML
	private Text lastMessageDateText;

	@Autowired
	AccountService accountService;

	public void initialize() {
		var selectedChannel = accountService.getSelectedChannel();
		var selectedConversation = accountService.getSelectedConversation();
		var users = selectedChannel != null ? selectedChannel.getParticipatingUsers()
				: selectedConversation.getRecipients();
		Message lastMessage = selectedChannel != null ? selectedChannel.getMessages().get(0)
				: selectedConversation.getMessages().get(0);

		nameText.setText(selectedChannel != null ? selectedChannel.getName() : selectedConversation.toString());
		idText.setText(selectedChannel != null ? selectedChannel.getId() : selectedConversation.getId());
		activeUsersText.setText(Integer.toString(users.size()));
		typeText.setText(selectedChannel != null ? "Channel" : "Conversation");
		nsfwText.setText(selectedConversation != null ? "N/A" : selectedChannel.getIsNsfw() ? "Yes" : " No");
		lastMessageDateText.setText(lastMessage.getDatePosted());
	}
}
