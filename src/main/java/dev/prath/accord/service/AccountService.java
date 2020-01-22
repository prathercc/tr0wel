package dev.prath.accord.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;

@Service
public class AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	private DiscordAccount discordAccount;
	private Conversation selectedConversation;
	private Channel selectedChannel;

	public AccountService() {
		logger.info("AccountService has been initialized.");
	}

	public void setSelectedConversation(Conversation val) {
		selectedConversation = val;
		logger.info("AccountService has changed the selected conversation from "
				+ (selectedConversation != null ? selectedConversation.getId() : "[Empty]") + " to " + val.getId());
	}

	public Conversation getSelectedConversation() {
		logger.info("AccountService is returning the selected conversation");
		return selectedConversation;
	}

	public void setSelectedChannel(Channel val) {
		selectedChannel = val;
		logger.info("AccountService has changed the selected channel from "
				+ (selectedChannel != null ? selectedChannel.getId() : "[Empty]") + " to " + val.getId());
	}

	public Channel getSelectedChannel() {
		logger.info("AccountService is returning the selected channel");
		return selectedChannel;
	}

	public DiscordAccount getDiscordAccount() {
		logger.info("AccountService is returning the discord account");
		return discordAccount;
	}

	public void updateDiscordAccount(DiscordAccount val) {
		discordAccount = val;
		logger.info("AccountService has updated the discord account");
	}
}