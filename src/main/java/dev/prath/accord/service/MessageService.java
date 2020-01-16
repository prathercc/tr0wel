package dev.prath.accord.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Message;
import dev.prath.accord.utility.Properties;

public class MessageService implements IMessageService {
	
	
	Properties properties = new Properties();
	
	public Message[] fetchConversationMessages(Conversation selectedConversation, DiscordAccount discordAccount, String lastId) {
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = lastId.length() < 1
				? properties.getDiscordChannelsUrl() + "/" + selectedConversation.getId()
						+ "/messages?limit=100"
				: properties.getDiscordChannelsUrl() + "/" + selectedConversation.getId()
						+ "/messages?limit=100&before=" + lastId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", discordAccount.getAuthorization());
		headers.set("user-agent", properties.getUserAgent());
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
				Message[].class);
		return response.getBody();
	}
	
	public Message[] fetchChannelMessages(Channel selectedChannel, DiscordAccount discordAccount, String lastId) {
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = lastId.length() < 1
				? properties.getDiscordChannelsUrl() + "/" + selectedChannel.getId()
						+ "/messages?limit=100"
				: properties.getDiscordChannelsUrl() + "/" + selectedChannel.getId()
						+ "/messages?limit=100&before=" + lastId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", discordAccount.getAuthorization());
		headers.set("user-agent", properties.getUserAgent());
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
				Message[].class);
		return response.getBody();
	}

}