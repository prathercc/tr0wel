package dev.prath.accord.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Properties {
	
	private String discordUsersUrl = "https://discordapp.com/api/users";
	private String discordGuildsUrl = "https://discordapp.com/api/guilds";
	private String discordAuthUrl = "https://discordapp.com/api/auth";
	private String discordChannelsUrl = "https://discordapp.com/api/channels";
	private String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:17.0) Gecko/20121202 Firefox/17.0 Iceweasel/17.0.1";
	
	private final Path iniPath = Paths.get(Paths.get(System.getProperty("user.home"), ".accord").toString(),"accord.ini");
	
	public Path getIniPath() {
		return iniPath;
	}
	
	public String getDiscordUsersUrl() {
		return discordUsersUrl;
	}

	public String getDiscordGuildsUrl() {
		return discordGuildsUrl;
	}
	public String getDiscordAuthUrl() {
		return discordAuthUrl;
	}
	
	public String getDiscordChannelsUrl() {
		return discordChannelsUrl;
	}
	public String getUserAgent() {
		return userAgent;
	}
	
}

