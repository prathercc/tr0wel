package dev.prath.accord.service;

import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Message;

public interface IMessageService {

	public Message[] fetchConversationMessages(Conversation selectedConversation, DiscordAccount discordAccount,
			String lastId);

	public Message[] fetchChannelMessages(Channel selectedChannel, DiscordAccount discordAccount, String lastId);

}
