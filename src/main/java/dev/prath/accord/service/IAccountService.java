package dev.prath.accord.service;

import dev.prath.accord.domain.DiscordAccount;

public interface IAccountService {
	
	public DiscordAccount getDiscordAccount();
	
	public void updateDiscordAccount(DiscordAccount val);
	
}