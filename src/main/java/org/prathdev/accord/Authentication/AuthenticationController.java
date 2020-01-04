package org.prathdev.accord.Authentication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.prathdev.accord.domain.Channel;
import org.prathdev.accord.domain.DiscordAccount;
import org.prathdev.accord.domain.Guild;
import org.prathdev.accord.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class AuthenticationController {
	@FXML
	private TextField userNameTextField;
	@FXML
	private TextField passwordTextField;
	@FXML
	private Button authenticateButton;

	public void authenticate() {
    	String email = userNameTextField.getText();
    	String password = passwordTextField.getText();
    	DiscordAccount discordAccount = new DiscordAccount(email,password);

    	RestTemplate restTemplate = new RestTemplate();
    	String requestUrl = "https://discordapp.com/api/auth/login"; 
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.set("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
    	HttpEntity<String> request = new HttpEntity<String>("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}",headers);
    	restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    	
    	try {
    		ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, request, String.class);
        	if(response.getStatusCodeValue() == 200) {
        		String authorization = response.getBody();
        		authorization = authorization.replace("{\"token\": \"", "");
        		authorization = authorization.replace("\"}", "");
        		
        		discordAccount.setAuthorization(authorization);
        		discordAccount.setGuilds(fetchGuilds(discordAccount));
        		for(Guild guild: discordAccount.getGuilds()) {
        			Thread.sleep(1000);
        			guild.setChannels(fetchChannels(guild,discordAccount));
        		}
        		System.out.println(discordAccount.toString());
        		
        	}
    	}
    	catch(Exception e) {
    		System.out.println("Unable to authenticate user!");
    	}
    	
	}
	
	private Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = "https://discordapp.com/api/guilds/" + guild.getId() + "/channels"; 
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Channel[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, Channel[].class);
			return response.getBody();
		}
		catch(Exception e) {
			System.out.println("Unable to fetch channels for guild '" + guild.getName() + "'");
			return null;
		}
	}
	
	
	private Guild[] fetchGuilds(DiscordAccount discordAccount) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = "https://discordapp.com/api/users/@me/guilds"; 
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", discordAccount.getAuthorization());
			headers.set("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Guild[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, Guild[].class);
			return response.getBody();
		}
		catch(Exception e) {
			System.out.println("Unable to fetch guilds for specified DiscordUser '" + discordAccount.getEmail()+ "'");
			return null;
		}
	}

}






//RestTemplate restTemplate = new RestTemplate();
//String requestUrl = "https://discordapp.com/api/channels/211181834967580673/messages?limit=100"; 
//HttpHeaders headers = new HttpHeaders();
//headers.set("authorization", "");
//headers.set("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4000.3 Mobile Safari/537.36");
//HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
//restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, Message[].class);
//
//Message[] messages = response.getBody();
//for(Message message: messages) {
//	System.out.println(message.toString());
//}