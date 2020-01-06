package org.prathdev.accord.controller;

import java.util.ArrayList;
import java.util.List;

import org.prathdev.accord.domain.Author;
import org.prathdev.accord.domain.Channel;
import org.prathdev.accord.domain.DiscordAccount;
import org.prathdev.accord.domain.Message;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

public class ManageChannelController {
	@FXML
	private ListView<Message> listView;
	@FXML
	private Button selectAllButton;
	
	Channel selectedChannel = null;
	DiscordAccount discordAccount = null;

	private boolean selectOrientation = false;

	
	public void setUpMessageData(DiscordAccount acc, Channel channel) {
		selectedChannel = channel;
		discordAccount = acc;
		listView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		
		System.out.println("Loaded ManageChannelController for channel '" + selectedChannel.getName() + "', containing " + selectedChannel.getMessages().size() + " messages!");
	}

	public void selectAll() {
		if (!selectOrientation)
			selectOrientation = true;
		else
			selectOrientation = false;
		for (Message m : listView.getItems()) {
			m.setIsSelected(selectOrientation);
		}
	}


}
