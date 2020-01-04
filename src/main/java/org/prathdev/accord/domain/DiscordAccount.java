package org.prathdev.accord.domain;

import java.util.ArrayList;
import java.util.List;

public class DiscordAccount {
	private String email = "";
	private String password = "";
	private String authorization = "";
	private List<Guild> guilds = new ArrayList<Guild>();

	public DiscordAccount(String mail, String pass) {
		email = mail;
		password = pass;
	}

	public List<Guild> getGuilds() {
		return guilds;
	}

	public void setGuilds(Guild[] val) {
		for (Guild g : val) {
			guilds.add(g);
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String val) {
		email = val;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String val) {
		password = val;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String val) {
		authorization = val;
	}

	public String toString() {
		String guildString = "[";
		int counter = 0;
		for(Guild g: guilds) {
			guildString = counter == 0 ? guildString + g.toString() + "]" : guildString + " | [" + g.toString() + "]";
			counter++;
		}
		
		return "Email: " + getEmail() + " | Password: " + getPassword() + " | Authorization: " + getAuthorization() + " | Guilds: " + guildString;
	}
}
