package dev.prath.accord.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dev.prath.accord.domain.Message;
import dev.prath.accord.utility.Properties;

@Service
public class DisposalService {

	private static final Logger logger = LoggerFactory.getLogger(DisposalService.class);

	@Autowired
	AccountService accountService;

	public DisposalService() {
		logger.info("DisposalService has been initialized.");
	}

	public boolean deleteChannelMessage(Message msg) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = Properties.discordChannelsUrl + "/" + accountService.getSelectedChannel().getId()
					+ "/messages/" + msg.getId();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", accountService.getDiscordAccount().getAuthorization());
			headers.set("user-agent", Properties.userAgent);
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			var response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, request, String.class);
			return response.getStatusCodeValue() == 204;
		}
		catch(Exception e) {
			logger.error("DisposalService could not delete Message: " + msg.getId());
			return false;
		}
	}

	public boolean deleteConversationMessage(Message msg) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String requestUrl = Properties.discordChannelsUrl + "/" + accountService.getSelectedConversation().getId()
					+ "/messages/" + msg.getId();
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", accountService.getDiscordAccount().getAuthorization());
			headers.set("user-agent", Properties.userAgent);
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(headers);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			var response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, request, String.class);
			return response.getStatusCodeValue() == 204;
		}
		catch(Exception e) {
			logger.error("DisposalService could not delete Message: " + msg.getId());
			return false;
		}
		
	}
}