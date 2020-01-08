package org.prathdev.accord.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.prathdev.accord.controller.ConfigurationController;
import org.prathdev.accord.domain.Channel;
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
	private TextField passwordTextField;
	@FXML
	private Button authenticateButton;
	@FXML
	private Text progressText;
	
	public static Stage configurationStage;

	private void launchConfiguration(DiscordAccount discordAccount) {
		progressText.setText("Launching configuration menu...");
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
			configurationStage = stage;
			stage.show();
			org.prathdev.accord.MainApp.authenticationMenu.hide();
		} catch (Exception e) {
			System.out.println(
					"Error executing launchConfiguration(DiscordAccount) : void, in AuthenticationController!)");
			progressText.setText("");
		}
	}

	public void authenticate() throws InterruptedException {
		Thread thread = new Thread(getNewAuthTask());
		thread.setDaemon(true);
		thread.start();
	}

	private User fetchUserData(DiscordAccount discordAccount) {
		progressText.setText("Fetching user data...");
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
			Thread.sleep(1000);
			return response.getBody();
		} catch (Exception e) {
			System.out.println("Unable to fetch User data for account '" + discordAccount.getEmail() + "'");
			progressText.setText("");
			return null;
		}
	}

	private Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount) {
		progressText.setText("Fetching channels for guild: " + guild.getId() + "...");
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
			Thread.sleep(1000);
			return response.getBody();
		} catch (Exception e) {
			System.out.println("Unable to fetch channels for guild '" + guild.getName() + "'");
			progressText.setText("");
			return null;
		}
	}

	private Guild[] fetchGuilds(DiscordAccount discordAccount) {
		progressText.setText("Fetching guilds...");
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
			Thread.sleep(1000);
			return response.getBody();
		} catch (Exception e) {
			System.out.println("Unable to fetch guilds for specified DiscordUser '" + discordAccount.getEmail() + "'");
			progressText.setText("");
			return null;
		}
	}

	private Task<Void> getNewAuthTask(){
		Task<Void> authenticationTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				authenticateButton.setDisable(true);
				emailTextField.setDisable(true);
				passwordTextField.setDisable(true);
				String email = emailTextField.getText();
				String password = passwordTextField.getText();
				DiscordAccount discordAccount = new DiscordAccount(email, password);

				RestTemplate restTemplate = new RestTemplate();
				String requestUrl = "https://discordapp.com/api/auth/login";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("user-agent",
						"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
				HttpEntity<String> request = new HttpEntity<String>(
						"{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}", headers);
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				try {
					ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, request,
							String.class);
					if (response.getStatusCodeValue() == 200) {
						String authorization = response.getBody();
						authorization = authorization.replace("{\"token\": \"", "");
						authorization = authorization.replace("\"}", "");
						discordAccount.setAuthorization(authorization);
						discordAccount.setGuilds(fetchGuilds(discordAccount));
						for (Guild guild : discordAccount.getGuilds()) {
							guild.setChannels(fetchChannels(guild, discordAccount));
						}
						discordAccount.setUser(fetchUserData(discordAccount));
						System.out.println(discordAccount.toString());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								launchConfiguration(discordAccount);
							}
						});
					}
				} catch (Exception e) {
					System.out.println("Unable to authenticate user!\n" + e.toString());
					authenticateButton.setDisable(false);
					emailTextField.setDisable(false);
					passwordTextField.setDisable(false);
					progressText.setText("");
				}
				return null;
			}
		};
		return authenticationTask;
	}
}