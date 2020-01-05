package org.prathdev.accord.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
	@JsonProperty("id")
	private String id = "";

	@JsonProperty("name")
	private String name = "";

	@JsonProperty("guild_id")
	private String guildId = "";

	@JsonProperty("nsfw")
	private boolean isNsfw = false;
	
	@JsonProperty("type")
	private int type = 0;
	
	private List<Message> messages = new ArrayList<Message>();
	
	public List<Message> getMessages(){
		return messages;
	}
	public void setMessages(Message[] val) {
		for(Message m: val) {
			messages.add(m);
		}
	}
	
	public int getType() {
		return type;
	}
	public void setType(int val) {
		type = val;
	}

	public String getId() {
		return id;
	}

	public void setId(String val) {
		id = val;
	}

	public String getName() {
		return name;
	}

	public void setName(String val) {
		name = val;
	}

	public String getGuildId() {
		return guildId;
	}

	public void setGuildId(String val) {
		guildId = val;
	}

	public boolean getIsNsfw() {
		return isNsfw;
	}

	public void setIsNsfw(boolean val) {
		isNsfw = val;
	}
	
	public String toString() {
		return getName();
	}
}
