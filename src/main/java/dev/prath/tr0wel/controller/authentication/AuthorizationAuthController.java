package dev.prath.tr0wel.controller.authentication;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.tr0wel.FxLauncher;
import dev.prath.tr0wel.domain.Authorization;
import dev.prath.tr0wel.domain.Conversation;
import dev.prath.tr0wel.domain.Credentials;
import dev.prath.tr0wel.domain.DiscordAccount;
import dev.prath.tr0wel.domain.Guild;
import dev.prath.tr0wel.domain.User;
import dev.prath.tr0wel.service.AccountService;
import dev.prath.tr0wel.service.AuthenticationService;
import dev.prath.tr0wel.service.FileService;
import dev.prath.tr0wel.service.StageService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class AuthorizationAuthController {
	@FXML
	private TextField authorizationCodeTextField;
	@FXML
	private Button authorizeButton;

	private static Text progressText;
	private static VBox authorizationAuthVbox;
	private static VBox credentialAuthVbox;

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationAuthController.class);

	@Autowired
	AuthenticationService service;

	@Autowired
	AccountService accountService;

	@Autowired
	FileService ioService;

	@Autowired
	StageService stageService;

	public void initialize() {

	}

	public void authorize() {
		Thread thread = new Thread(getNewAuthTask());
		thread.setDaemon(true);
		thread.start();
	}

	private void launchConfiguration() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setProgressText("Launching configuration menu...");
				Stage stage = stageService.getNewStage("", "/fxml/ConfigurationMenu/ConfigurationMenu.fxml");
				if (stage != null) {
					AuthenticationController.configurationStage = stage;
					stage.show();
					FxLauncher.authenticationMenu.hide();
				} else {
					setProgressText("Error launching configuration menu!");
					logger.error("AuthorizationAuthController received null value for stage.");
				}
			}
		});
	}

	private Task<Void> getNewAuthTask() {
		Task<Void> authenticationTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				Credentials credentials = new Credentials("", "");
				DiscordAccount discordAccount = createDiscordAccount(credentials);
				if (discordAccount != null) {
					accountService.updateDiscordAccount(discordAccount);
					launchConfiguration();
				} else {
					toggleControls(false);
					setProgressText("User authentication failed!");
					logger.error("AuthorizationAuthController was unable to authenticate user.");
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
				authorizationAuthVbox.setDisable(val);
				credentialAuthVbox.setDisable(val);
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
		Authorization authorization = new Authorization();
		authorization.setToken(authorizationCodeTextField.getText());
		if (authorization != null) {
			try {
				discordAccount.setAuthorization(authorization);
				setProgressText("Fetching user data...");
				User userdata = service.fetchUserData(discordAccount);
				discordAccount.setUser(userdata);
				setProgressText("Fetching conversations...");
				List<Conversation> conversations = service.fetchConversations(discordAccount);
				discordAccount.setConversations(conversations);
				setProgressText("Fetching guilds...");
				List<Guild> guilds = service.fetchGuilds(discordAccount);
				guilds.stream().forEach(guild -> guild.setChannels(service.fetchChannels(guild, discordAccount)));
				discordAccount.setGuilds(guilds);
				return userdata != null ? discordAccount : null;
			} catch (Exception e) {
				logger.error("AuthorizationAuthController ran into an error creating the DiscordAccount object.");
				return null;
			}
		}
		return null;
	}

	protected static void setParentControls(Text progress, VBox authorization, VBox credential) {
		progressText = progress;
		authorizationAuthVbox = authorization;
		credentialAuthVbox = credential;
	}
}
