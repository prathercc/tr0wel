package dev.prath.razlad.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.prath.razlad.domain.Channel;
import dev.prath.razlad.domain.Conversation;
import dev.prath.razlad.domain.DiscordAccount;
import dev.prath.razlad.domain.Message;
import dev.prath.razlad.domain.User;

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
		selectedChannel = null;
		attachReferences(val.getMessages(), val.getRecipients());
		selectedConversation = val;
		logger.warn("AccountService has changed the selected conversation from "
				+ (selectedConversation != null ? selectedConversation.getId() : "[Empty]") + " to " + val.getId());
	}

	public Conversation getSelectedConversation() {
		return selectedConversation;
	}

	public void setSelectedChannel(Channel val) {
		selectedConversation = null;
		attachReferences(val.getMessages(), val.getParticipatingUsers());
		selectedChannel = val;
		logger.warn("AccountService has changed the selected channel from "
				+ (selectedChannel != null ? selectedChannel.getId() : "[Empty]") + " to " + val.getId());
	}

	public Channel getSelectedChannel() {
		return selectedChannel;
	}

	public DiscordAccount getDiscordAccount() {
		return discordAccount;
	}

	public void updateDiscordAccount(DiscordAccount val) {
		discordAccount = val;
		logger.warn("AccountService has updated the discord account");
	}

	private void attachReferences(List<Message> messages, List<User> users) {
		messages.stream().forEach(m -> m.setMessage(m.getMessage().replaceAll("<@!", "<@")));
		for (Message msg : messages) {
			String content = msg.getMessage();

			var rawUserIds = StringUtils.substringsBetween(content, "<@", ">");

			if (rawUserIds != null) {
				var distinctIds = Arrays.stream(rawUserIds).distinct().collect(Collectors.toList());

				for (String id : distinctIds) {
					User matchedUser = null;
					try {
						matchedUser = users.stream().filter(user -> user.getId().equalsIgnoreCase(id))
								.collect(Collectors.toList()).get(0);
					} catch (Exception e) {
						content = content.replaceAll("<@" + id + ">", "@[USER: " + id + "]");
					}
					if (matchedUser != null) {
						content = content.replaceAll("<@" + id + ">", "@" + matchedUser.getUsername());
					}
				}
				msg.setMessage(content);
			}
		}
	}
}