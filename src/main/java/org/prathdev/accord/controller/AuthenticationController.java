package org.prathdev.accord.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.prathdev.accord.controller.ConfigurationController;
import org.prathdev.accord.domain.Authorization;
import org.prathdev.accord.domain.Channel;
import org.prathdev.accord.domain.Credentials;
import org.prathdev.accord.domain.DiscordAccount;
import org.prathdev.accord.domain.Guild;
import org.prathdev.accord.domain.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class AuthenticationController {
	@FXML
	private TextField emailTextField;
	@FXML
	private PasswordField passwordTextField;
	@FXML
	private Button authenticateButton;
	@FXML
	private Text progressText;

	public static Stage configurationStage;

	private void launchConfiguration(DiscordAccount discordAccount) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setProgressText("Launching configuration menu...");
				try {
					String fxml = "/fxml/configurationMenu.fxml";
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource(fxml));
					Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxml));
					Scene scene = new Scene(rootNode);
					Stage stage = new Stage();
					ConfigurationController controller = loader.getController();
					controller.setDiscordAccount(discordAccount);
					controller.fillGuildChoiceBox();
					stage.setTitle("accord - Configuration Menu");
					stage.setScene(scene);
					stage.setResizable(false);
					configurationStage = stage;
					stage.show();
					org.prathdev.accord.MainApp.authenticationMenu.hide();
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
			String requestUrl = "https://discordapp.com/api/users/@me";
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<User> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, User.class);
			return response.getBody();
		} catch (Exception e) {
			setProgressText("Error fetching user data for account - " + discordAccount.getCredentials().getEmail());
			return null;
		}
	}

	private Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount) {
		setProgressText("Fetching channels for guild: " + guild.getId() + "...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = "https://discordapp.com/api/guilds/" + guild.getId() + "/channels";
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Channel[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
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
			String requestUrl = "https://discordapp.com/api/users/@me/guilds";
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Guild[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
					Guild[].class);
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
				String requestUrl = "https://discordapp.com/api/auth/login";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("user-agent",
						"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
				
				HttpEntity<Credentials> request = new HttpEntity<Credentials>(credentials, headers);
				
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				try {
					ResponseEntity<Authorization> response = restTemplate.exchange(requestUrl, HttpMethod.POST, request,
							Authorization.class);
					if (response.getStatusCodeValue() == 200) {
						Authorization authorization = response.getBody();
						discordAccount.setAuthorization(authorization);
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
}