package dev.prath.accord.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Authorization;
import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Credentials;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.AuthenticationService;
import dev.prath.accord.service.FileService;
import dev.prath.accord.service.StageService;
import dev.prath.accord.utility.Properties;
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
	
	Properties properties = new Properties();
	
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
				finalizeIni();
				setProgressText("Launching configuration menu...");
				try {
					Stage stage = stageService.getNewStage("accord - Configuration Menu", "/fxml/configurationMenu.fxml");
					configurationStage = stage;
					stage.show();
					dev.prath.accord.FxLauncher.authenticationMenu.hide();
				} catch (Exception e) {
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
				DiscordAccount discordAccount = new DiscordAccount(credentials);

				try {
					ResponseEntity<Authorization> response = service.fetchAuthorization(credentials);
					if (response.getStatusCodeValue() == 200) {
						Authorization authorization = response.getBody();
						discordAccount.setAuthorization(authorization);
						discordAccount.setConversations(fetchConversations(discordAccount));
						discordAccount.setGuilds(fetchGuilds(discordAccount));
						for (Guild guild : discordAccount.getGuilds()) {
							guild.setChannels(fetchChannels(guild, discordAccount));
						}
						discordAccount.setUser(fetchUserData(discordAccount));
						accountService.updateDiscordAccount(discordAccount);
						launchConfiguration();
					}
				} catch (Exception e) {
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

	private void finalizeIni() {
		if (rememberMeCheckBox.isSelected()) {
			ioService.setIniValue(emailTextField.getText());
		} else {
			ioService.setIniValue("");
		}
	}
	
	private User fetchUserData(DiscordAccount discordAccount) {
		setProgressText("Fetching user data...");
		try {
			return service.fetchUserData(discordAccount);
		} catch (Exception e) {
			setProgressText("Error fetching user data for account - " + discordAccount.getCredentials().getEmail());
			return null;
		}
	}

	private Conversation[] fetchConversations(DiscordAccount discordAccount) {
		setProgressText("Fetching conversations...");
		try {
			return service.fetchConversations(discordAccount);
		} catch (Exception e) {
			setProgressText("Error fetching conversations for user - " + discordAccount.getUser().getId());
			return null;
		}
	}

	private Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount) {
		setProgressText("Fetching channels for guild: " + guild.getId() + "...");
		try {
			return service.fetchChannels(guild, discordAccount);
		} catch (Exception e) {
			setProgressText("Error fetching channels from guild id - " + guild.getId());
			return null;
		}
	}

	private Guild[] fetchGuilds(DiscordAccount discordAccount) {
		setProgressText("Fetching guilds...");
		try {
			return service.fetchGuilds(discordAccount, progressText);
		} catch (Exception e) {
			setProgressText("Error fetching guilds for user - " + discordAccount.getUser().getId());
			return null;
		}
	}
}