package dev.prath.accord.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dev.prath.accord.FxLauncher;
import dev.prath.accord.domain.Authorization;
import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Credentials;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.User;
import dev.prath.accord.utility.Properties;
import javafx.scene.text.Text;
@Service
public class AuthenticationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	public AuthenticationService() {
		logger.info("AuthenticationService has been initialized.");
	}
	
	public ResponseEntity<Authorization> fetchAuthorization(Credentials credentials) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<Credentials> request = new HttpEntity<Credentials>(credentials, headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		ResponseEntity<Authorization> response = restTemplate.exchange(Properties.discordAuthUrl + "/login",
				HttpMethod.POST, request, Authorization.class);
		return response;
	}

	public User fetchUserData(DiscordAccount discordAccount) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", discordAccount.getAuthorization());
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<User> response = restTemplate.exchange(Properties.discordUsersUrl + "/@me", HttpMethod.GET,
				request, User.class);
		return response.getBody();
	}

	public Conversation[] fetchConversations(DiscordAccount discordAccount) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", discordAccount.getAuthorization());
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Conversation[]> response = restTemplate.exchange(
				Properties.discordUsersUrl + "/@me/channels", HttpMethod.GET, request, Conversation[].class);
		return response.getBody();
	}

	public Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", discordAccount.getAuthorization());
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Channel[]> response = restTemplate.exchange(
				Properties.discordGuildsUrl + "/" + guild.getId() + "/channels", HttpMethod.GET, request,
				Channel[].class);
		return response.getBody();
	}

	public Guild[] fetchGuilds(DiscordAccount discordAccount, Text progressText) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", discordAccount.getAuthorization());
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Guild[]> response = restTemplate.exchange(Properties.discordUsersUrl + "/@me/guilds",
				HttpMethod.GET, request, Guild[].class);
		return response.getBody();
	}
}