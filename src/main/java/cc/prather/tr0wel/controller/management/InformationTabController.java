package cc.prather.tr0wel.controller.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.domain.Message;
import cc.prather.tr0wel.service.AccountService;
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

	private static final Logger logger = LoggerFactory.getLogger(InformationTabController.class);

	public void initialize() {
		var selectedChannel = accountService.getSelectedChannel();
		var selectedConversation = accountService.getSelectedConversation();
		var users = selectedChannel != null ? selectedChannel.getParticipatingUsers()
				: selectedConversation.getRecipients();

		Message lastMessage = null;
		try {
			lastMessage = selectedChannel != null ? selectedChannel.getMessages().get(0)
					: selectedConversation.getMessages().get(0);
		} catch (Exception e) {
			logger.warn("InformationTabController was unable to find a last message for "
					+ (selectedChannel != null ? "channel '" + selectedChannel.getName() + "'"
							: "conversation '" + selectedConversation.toString() + "'"));
		}

		nameText.setText(selectedChannel != null ? selectedChannel.getName() : selectedConversation.toString());
		idText.setText(selectedChannel != null ? selectedChannel.getId() : selectedConversation.getId());
		activeUsersText.setText(Integer.toString(users.size()));
		typeText.setText(selectedChannel != null ? "Channel" : "Conversation");
		nsfwText.setText(selectedConversation != null ? "N/A" : selectedChannel.getIsNsfw() ? "Yes" : " No");
		lastMessageDateText.setText(lastMessage != null ? lastMessage.getDatePosted() : "N/A");
	}
}
