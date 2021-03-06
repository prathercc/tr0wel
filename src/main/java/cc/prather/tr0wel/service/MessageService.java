package cc.prather.tr0wel.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
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

import cc.prather.tr0wel.domain.Message;
import cc.prather.tr0wel.utility.Properties;

@Service
public class MessageService {

	private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

	@Autowired
	AccountService accountService;

	public MessageService() {
		logger.info("MessageService has been initialized.");
	}
	
	public boolean editMessage(Message msg, String newMessageContent, String channelId) {
		//Unfortunately I cannot use PATCH with RestTemplate, so we have to use Apache's HttpClient to make this request.
		newMessageContent = newMessageContent.length() >= 2000 ? newMessageContent.substring(0, 1999) : newMessageContent;
		var sessionAuthorization = accountService.getDiscordAccount().getAuthorization();
		try {
			String requestUrl = Properties.discordChannelsUrl + "/" + channelId
					+ "/messages/" + msg.getId();
			HttpClient httpclient = HttpClients.createDefault();
		    HttpPatch httpPatch = new HttpPatch(requestUrl);
		    httpPatch.setHeader("authorization", sessionAuthorization);
		    httpPatch.setHeader("user-agent", Properties.userAgent);
		    StringEntity params =new StringEntity("{\"content\": \"" + newMessageContent + "\"}");
		    params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		    httpPatch.setEntity(params);
		    var response = httpclient.execute(httpPatch);
		    return response.getStatusLine().getStatusCode() == 200;
		}
		catch(Exception e) {
			logger.error("MessageService could not edit Message: " + msg.getId());
			return false;
		}
	}
	
	public boolean deleteMessage(Message msg, String channelId) {
		var sessionAuthorization = accountService.getDiscordAccount().getAuthorization();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = Properties.discordChannelsUrl + "/" + channelId
					+ "/messages/" + msg.getId();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", sessionAuthorization);
			headers.set("user-agent", Properties.userAgent);
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			var response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, request, String.class);
			return response.getStatusCodeValue() == 204;
		}
		catch(Exception e) {
			logger.error("MessageService could not delete Message: " + msg.getId());
			return false;
		}
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