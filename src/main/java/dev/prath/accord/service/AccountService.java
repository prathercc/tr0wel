package dev.prath.accord.service;

import org.springframework.stereotype.Service;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
@Service
public class AccountService {
	
	private DiscordAccount discordAccount;
	
	private Conversation selectedConversation;
	
	private Channel selectedChannel;
	
	public void setSelectedConversation(Conversation val) {
		selectedConversation = val;
	}
	public Conversation getSelectedConversation() {
		return selectedConversation;
	}
	
	public void setSelectedChannel(Channel val) {
		selectedChannel = val;
	}
	
	public Channel getSelectedChannel() {
		return selectedChannel;
	}
	
	public DiscordAccount getDiscordAccount() {
		return discordAccount;
	}
	
	public void updateDiscordAccount(DiscordAccount val) {
		discordAccount = val;
	}
	
}