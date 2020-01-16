package dev.prath.accord.service;

import dev.prath.accord.domain.DiscordAccount;

public class AccountService implements IAccountService {
	
	private DiscordAccount discordAccount;
	
	public AccountService(DiscordAccount val) {
		discordAccount = val;
	}
	
	public DiscordAccount getDiscordAccount() {
		return discordAccount;
	}
	
	public void updateDiscordAccount(DiscordAccount val) {
		discordAccount = val;
	}
	
}