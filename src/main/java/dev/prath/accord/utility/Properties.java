package dev.prath.accord.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Properties {
	
	public static final String discordUsersUrl = "https://discordapp.com/api/users";
	public static final String discordGuildsUrl = "https://discordapp.com/api/guilds";
	public static final String discordAuthUrl = "https://discordapp.com/api/auth";
	public static final String discordChannelsUrl = "https://discordapp.com/api/channels";
	public static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:17.0) Gecko/20121202 Firefox/17.0 Iceweasel/17.0.1";
	public static final Path iniPath = Paths.get(Paths.get(System.getProperty("user.home"), ".accord").toString(),"accord.ini");
	public static final Path iniFolderPath = Paths.get(System.getProperty("user.home"), ".accord");
	public static final String sourceCodeLink = "https://github.com/aaprather/accord";
	public static final String prathDevLink = "https://prath.dev/software/accord";
	
}

