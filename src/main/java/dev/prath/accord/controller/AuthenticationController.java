package dev.prath.accord.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Authorization;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Credentials;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.AuthenticationService;
import dev.prath.accord.service.FileService;
import dev.prath.accord.service.StageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class AuthenticationController {
	@FXML
	private TextField emailTextField;
	@FXML
	private PasswordField passwordTextField;
	@FXML
	private Button authenticateButton;
	@FXML
	private Text progressText;
	@FXML
	private CheckBox rememberMeCheckBox;

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	AuthenticationService service;

	@Autowired
	AccountService accountService;

	@Autowired
	FileService ioService;

	@Autowired
	StageService stageService;

	public static Stage configurationStage;

	public void initialize() {
		ioService.checkIniFolderPath();
		String iniValue = ioService.getIniValue();
		if (iniValue.length() != 0) {
			rememberMeCheckBox.setSelected(true);
			emailTextField.setText(iniValue);
			emailTextField.setFocusTraversable(false);
		}
	}

	public void authenticate() throws InterruptedException {
		Thread thread = new Thread(getNewAuthTask());
		thread.setDaemon(true);
		thread.start();
	}

	private void launchConfiguration() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ioService.setIniValue(rememberMeCheckBox.isSelected() ? emailTextField.getText() : "");
				setProgressText("Launching configuration menu...");
				Stage stage = stageService.getNewStage("accord - Configuration Menu",
						"/fxml/ConfigurationMenu/ConfigurationMenu.fxml");
				if (stage != null) {
					configurationStage = stage;
					stage.show();
					dev.prath.accord.FxLauncher.authenticationMenu.hide();
				} else {
					setProgressText("Error launching configuration menu!");
				}
			}
		});
	}

	private Task<Void> getNewAuthTask() {
		Task<Void> authenticationTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				Credentials credentials = new Credentials(emailTextField.getText(), passwordTextField.getText());
				DiscordAccount discordAccount = createDiscordAccount(credentials);
				if (discordAccount != null) {
					accountService.updateDiscordAccount(discordAccount);
					launchConfiguration();
				} else {
					toggleControls(false);
					setProgressText("User authentication failed!");
				}
				return null;
			}
		};
		return authenticationTask;
	}

	private void toggleControls(boolean val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				authenticateButton.setDisable(val);
				emailTextField.setDisable(val);
				passwordTextField.setDisable(val);
				rememberMeCheckBox.setDisable(val);
			}
		});
	}

	private void setProgressText(String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				progressText.setText(val);
			}
		});
	}

	private DiscordAccount createDiscordAccount(Credentials credentials) {
		DiscordAccount discordAccount = new DiscordAccount(credentials);
		Authorization authorization = service.fetchAuthorization(credentials);
		if (authorization != null) {
			discordAccount.setAuthorization(authorization);
			setProgressText("Fetching user data...");
			User userdata = service.fetchUserData(discordAccount);
			discordAccount.setUser(userdata);
			setProgressText("Fetching conversations...");
			List<Conversation> conversations = service.fetchConversations(discordAccount);
			conversations.stream().forEach(c -> c.getRecipients().add(discordAccount.getUser()));
			discordAccount.setConversations(conversations);
			setProgressText("Fetching guilds...");
			List<Guild> guilds = service.fetchGuilds(discordAccount);
			guilds.stream().forEach(guild -> guild.setChannels(service.fetchChannels(guild, discordAccount)));
			discordAccount.setGuilds(guilds);
			return userdata != null ? discordAccount : null;
		}
		return null;
	}
}