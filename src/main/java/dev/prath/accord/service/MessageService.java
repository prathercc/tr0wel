package dev.prath.accord.service;

import java.util.ArrayList;
import java.util.List;

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

import dev.prath.accord.FxLauncher;
import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
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
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = lastId.length() < 1
				? Properties.discordChannelsUrl + "/" + accountService.getSelectedConversation().getId()
						+ "/messages?limit=100"
				: Properties.discordChannelsUrl + "/" + accountService.getSelectedConversation().getId()
						+ "/messages?limit=100&before=" + lastId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", accountService.getDiscordAccount().getAuthorization());
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
				Message[].class);
		List<Message> retList = new ArrayList<Message>();
		for (Message msg : response.getBody()) {
			retList.add(msg);
		}
		logger.info("MessageService is returning a list of messages for conversation:" + accountService.getSelectedConversation().getId() + ", starting at message id: " + lastId);
		return retList;
	}

	public List<Message> fetchChannelMessages(String lastId) {
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = lastId.length() < 1
				? Properties.discordChannelsUrl + "/" + accountService.getSelectedChannel().getId() + "/messages?limit=100"
				: Properties.discordChannelsUrl + "/" + accountService.getSelectedChannel().getId() + "/messages?limit=100&before="
						+ lastId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", accountService.getDiscordAccount().getAuthorization());
		headers.set("user-agent", Properties.userAgent);
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<Message[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request,
				Message[].class);
		List<Message> retList = new ArrayList<Message>();
		for (Message msg : response.getBody()) {
			retList.add(msg);
		}
		logger.info("MessageService is returning a list of messages for channel:" + accountService.getSelectedChannel().getId() + ", starting at message id: " + lastId);
		return retList;
	}
}