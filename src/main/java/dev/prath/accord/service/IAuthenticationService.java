package dev.prath.accord.service;

import org.springframework.http.ResponseEntity;

import dev.prath.accord.domain.Authorization;
import dev.prath.accord.domain.Channel;
import dev.prath.accord.domain.Conversation;
import dev.prath.accord.domain.Credentials;
import dev.prath.accord.domain.DiscordAccount;
import dev.prath.accord.domain.Guild;
import dev.prath.accord.domain.User;
import javafx.scene.text.Text;

public interface IAuthenticationService {
	
	public ResponseEntity<Authorization> fetchAuthorization(Credentials credentials);
	
	public User fetchUserData(DiscordAccount discordAccount);
	
	public Conversation[] fetchConversations(DiscordAccount discordAccount);
	
	public Channel[] fetchChannels(Guild guild, DiscordAccount discordAccount);
	
	public Guild[] fetchGuilds(DiscordAccount discordAccount, Text progressText);
}
