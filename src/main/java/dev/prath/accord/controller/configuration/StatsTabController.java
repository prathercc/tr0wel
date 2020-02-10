package dev.prath.accord.controller.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.service.AccountService;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

@Component
public class StatsTabController {
	
	@FXML
	private Text authorizationText;
	@FXML
	private Text usernameText;
	@FXML
	private Text idText;
	@FXML
	private Text discriminatorText;
	@FXML
	private Text activeGuildsText;
	@FXML
	private Text emailText;
	@FXML
	private Text activeConversationsText;
	
	@Autowired
	AccountService accountService;
	
	public void initialize() {
		String censor = "*********";
		var account = accountService.getDiscordAccount();
		authorizationText.setText(account.getAuthorization().substring(0, 30) + censor);
		usernameText.setText(account.getUser().getUsername() != null ? account.getUser().getUsername() : "N/A");
		idText.setText(account.getUser().getId() != null ? account.getUser().getId() : "N/A");
		discriminatorText.setText(account.getUser().getDiscriminator() != null ? account.getUser().getDiscriminator() : "N/A");
		activeGuildsText.setText(account.getGuilds().size() + "");
		activeConversationsText.setText(account.getConversations().size() + "");
		emailText.setText(account.getUser().getEmail() != null ? account.getUser().getEmail() : "N/A");
	}
	
}
