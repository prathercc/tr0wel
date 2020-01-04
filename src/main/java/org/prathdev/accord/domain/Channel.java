package org.prathdev.accord.domain;

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
		return "Channel Name: " + getName();
	}
}
