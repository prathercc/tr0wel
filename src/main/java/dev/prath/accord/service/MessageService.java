package dev.prath.accord.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dev.prath.accord.domain.Message;
import dev.prath.accord.utility.Properties;

@Service
public class MessageService {

	private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

	@Autowired
	AccountService accountService;

	public MessageService() {
		logger.info("MessageService has been initialized.");
	}

	public List<Message> fetchConversationMessages(String lastId) {
		var conversationId = accountService.getSelectedConversation().getId();
		var sessionAuthorization = accountService.getDiscordAccount().getAuthorization();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = lastId.length() < 1
					? Properties.discordChannelsUrl + "/" + conversationId + "/messages?limit=100"
					: Properties.discordChannelsUrl + "/" + conversationId + "/messages?limit=100&before=" + lastId;
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", sessionAuthorization);
			headers.set("user-agent", Properties.userAgent);
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
					Message[].class);
			return Arrays.stream(response.getBody()).collect(Collectors.toList());
		}
		catch(Exception e) {
			logger.error("MessageService could not fetch messages for conversation " + conversationId + ", with last message id: " + lastId);
			return new ArrayList<Message>();
		}
	}

	public List<Message> fetchChannelMessages(String lastId) {
		var channelId = accountService.getSelectedChannel().getId();
		var sessionAuthorization = accountService.getDiscordAccount().getAuthorization();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = lastId.length() < 1
					? Properties.discordChannelsUrl + "/" + channelId + "/messages?limit=100"
					: Properties.discordChannelsUrl + "/" + channelId + "/messages?limit=100&before=" + lastId;
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", sessionAuthorization);
			headers.set("user-agent", Properties.userAgent);
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
					Message[].class);
			return Arrays.stream(response.getBody()).collect(Collectors.toList());
		}
		catch(Exception e) {
			logger.error("MessageService could not fetch messages for channel " + channelId + ", with last message id: " + lastId);
			return new ArrayList<Message>();
		}
	}
}