package org.prathdev.accord.domain;

import java.util.ArrayList;
import java.util.List;

public class DiscordAccount {
	private String authorization = "";
	private List<Guild> guilds = new ArrayList<Guild>();
	private User user = new User();
	private Credentials credentials = null;
	
	public DiscordAccount(Credentials creds) {
		credentials = creds;
	}
	
	public Credentials getCredentials() {
		return credentials;
	}
	public void setCredentials(Credentials creds) {
		credentials = creds;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User val) {
		user = val;
	}

	public List<Guild> getGuilds() {
		return guilds;
	}

	public void setGuilds(Guild[] val) {
		for (Guild g : val) {
			guilds.add(g);
		}
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization val) {
		authorization = val.getToken();
	}

	public String toString() {
		String guildString = "[";
		int counter = 0;
		for(Guild g: guilds) {
			guildString = counter == 0 ? guildString + g.toString() + "]" : guildString + " | [" + g.toString() + "]";
			counter++;
		}
		
		return user.toString() + " | Email: " + credentials.getEmail() + " | Password: " + credentials.getPassword() + " | Authorization: " + getAuthorization() + " | Guilds: " + guildString;
	}
}
