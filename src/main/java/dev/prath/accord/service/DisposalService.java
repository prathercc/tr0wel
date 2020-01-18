package dev.prath.accord.service;

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
public class DisposalService {

	Properties properties = new Properties();

	@Autowired
	AccountService accountService;

	public ResponseEntity<String> deleteChannelMessage(Message msg) {
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = properties.getDiscordChannelsUrl() + "/" + accountService.getSelectedChannel().getId()
				+ "/messages/" + msg.getId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", accountService.getDiscordAccount().getAuthorization());
		headers.set("user-agent", properties.getUserAgent());
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate.exchange(requestUrl, HttpMethod.DELETE, request, String.class);
	}

	public ResponseEntity<String> deleteConversationMessage(Message msg) {
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = properties.getDiscordChannelsUrl() + "/" + accountService.getSelectedConversation().getId()
				+ "/messages/" + msg.getId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", accountService.getDiscordAccount().getAuthorization());
		headers.set("user-agent", properties.getUserAgent());
		HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate.exchange(requestUrl, HttpMethod.DELETE, request, String.class);
	}
}
