package dev.prath.accord.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dev.prath.accord.MainApp;
import dev.prath.accord.controller.ConfigurationController;
import dev.prath.accord.domain.Authorization;
import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Credentials;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.User;
import dev.prath.accord.utility.Properties;

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

	Properties properties = new Properties();

	public static Stage configurationStage;

	public void initialize() {
		StringBuilder builder = new StringBuilder();
		try {
			Stream<String> fileStream = Files.lines(MainApp.iniPath);
			fileStream.forEach(s -> builder.append(s));
			fileStream.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		if (builder.toString().length() != 0) {
			rememberMeCheckBox.setSelected(true);
			emailTextField.setText(builder.toString());
			emailTextField.setFocusTraversable(false);
		}
	}

	private void launchConfiguration(DiscordAccount discordAccount) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				finalizeIni();
				setProgressText("Launching configuration menu...");
				try {
					String fxml = "/fxml/configurationMenu.fxml";
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource(fxml));
					Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxml));
					Scene scene = new Scene(rootNode);
					Stage stage = new Stage();
					ConfigurationController controller = loader.getController();
					controller.setUpConfigurationMenu(discordAccount);
					stage.setTitle("accord - Configuration Menu");
					stage.setScene(scene);
					stage.setResizable(false);
					configurationStage = stage;
					stage.show();
					dev.prath.accord.MainApp.authenticationMenu.hide();
				} catch (Exception e) {
					setProgressText("Error launching configuration menu!");
				}
			}
		});
	}

	public void authenticate() throws InterruptedException {
		Thread thread = new Thread(getNewAuthTask());
		thread.setDaemon(true);
		thread.start();
	}

	private User fetchUserData(DiscordAccount discordAccount) {
		setProgressText("Fetching user data...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent", properties.getUserAgent());
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<User> response = restTemplate.exchange(properties.getDiscordUsersUrl() + "/@me",
					HttpMethod.GET, request, User.class);
			return response.getBody();
		} catch (Exception e) {
			setProgressText("Error fetching user data for account - " + discordAccount.getCredentials().getEmail());
			return null;
		}
	}
	
	private Conversation[] fetchConversations(DiscordAccount discordAccount) {
		setProgressText("Fetching conversations...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent", properties.getUserAgent());
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Conversation[]> response = restTemplate.exchange(properties.getDiscordUsersUrl() + "/@me/channels",
					HttpMethod.GET, request, Conversation[].class);
			return response.getBody();
		} catch (Exception e) {
			setProgressText("Error fetching conversations for user - " + discordAccount.getUser().getId());
			return null;
		}
	}

	private Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount) {
		setProgressText("Fetching channels for guild: " + guild.getId() + "...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent", properties.getUserAgent());
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Channel[]> response = restTemplate.exchange(
					properties.getDiscordGuildsUrl() + "/" + guild.getId() + "/channels", HttpMethod.GET, request,
					Channel[].class);
			return response.getBody();
		} catch (Exception e) {
			setProgressText("Error fetching channels from guild id - " + guild.getId());
			return null;
		}
	}

	private Guild[] fetchGuilds(DiscordAccount discordAccount) {
		setProgressText("Fetching guilds...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent", properties.getUserAgent());
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Guild[]> response = restTemplate.exchange(properties.getDiscordUsersUrl() + "/@me/guilds",
					HttpMethod.GET, request, Guild[].class);
			return response.getBody();
		} catch (Exception e) {
			setProgressText("Error fetching guilds for user - " + discordAccount.getUser().getId());
			return null;
		}
	}

	private Task<Void> getNewAuthTask() {
		Task<Void> authenticationTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				Credentials credentials = new Credentials(emailTextField.getText(), passwordTextField.getText());
				DiscordAccount discordAccount = new DiscordAccount(credentials);
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("user-agent", properties.getUserAgent());
				HttpEntity<Credentials> request = new HttpEntity<Credentials>(credentials, headers);
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				try {
					ResponseEntity<Authorization> response = restTemplate.exchange(
							properties.getDiscordAuthUrl() + "/login", HttpMethod.POST, request, Authorization.class);
					if (response.getStatusCodeValue() == 200) {
						Authorization authorization = response.getBody();
						discordAccount.setAuthorization(authorization);
						discordAccount.setConversations(fetchConversations(discordAccount));
						discordAccount.setGuilds(fetchGuilds(discordAccount));
						for (Guild guild : discordAccount.getGuilds()) {
							guild.setChannels(fetchChannels(guild, discordAccount));
						}
						discordAccount.setUser(fetchUserData(discordAccount));
						launchConfiguration(discordAccount);
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
			try {
				Files.write(MainApp.iniPath, emailTextField.getText().getBytes());
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		} else {
			try {
				Files.write(MainApp.iniPath, "".getBytes());
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}
	}
}