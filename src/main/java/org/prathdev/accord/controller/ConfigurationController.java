
package org.prathdev.accord.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import org.prathdev.accord.domain.Channel;
import org.prathdev.accord.domain.DiscordAccount;
import org.prathdev.accord.domain.Guild;

public class ConfigurationController {

	@FXML
	private ChoiceBox guildSelectionBox;
	@FXML
	private ChoiceBox channelSelectionBox;
	@FXML
	private Button manageChannelButton;

	private String test = "default";

	public void setTest(String val) {
		test = val;
	}

	public String getTest() {
		return test;
	}

	private DiscordAccount discordAccount = null;

	public DiscordAccount getDiscordAccount() {
		return discordAccount;
	}

	public void setDiscordAccount(DiscordAccount val) {
		discordAccount = val;
	}

	@SuppressWarnings("unchecked")
	public void fillGuildChoiceBox() {
		for (Guild guild : discordAccount.getGuilds()) {
			guildSelectionBox.getItems().add(guild);
		}
	}

	public void manageChannel() {
		System.out.println("Manage channel button was pressed");
	}

	@SuppressWarnings("unchecked")
	public void selectGuild() {
		channelSelectionBox.setDisable(false);
		channelSelectionBox.getItems().clear();
		channelSelectionBox.getSelectionModel().clearSelection();
		Guild selectedGuild = (Guild) guildSelectionBox.getValue();
		for (Channel channel : selectedGuild.getChannels()) {
			channelSelectionBox.getItems().add(channel);
		}
		manageChannelButton.setDisable(true); // New guild was selected, so disable the 'manageChannelButton'
	}

	public void selectChannel() {
		manageChannelButton.setDisable(false);
	}

}
