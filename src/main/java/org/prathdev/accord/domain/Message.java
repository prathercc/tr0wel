package org.prathdev.accord.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

	@JsonProperty("author")
	private Author author = new Author();

	@JsonProperty("id")
	private String id = "";

	@JsonProperty("content")
	private String message = "";

	@JsonProperty("channel_id")
	private String channelId = "";

	@JsonProperty("timestamp")
	private String datePosted = "";

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author val) {
		author = val;
	}

	public String getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(String val) {
		datePosted = val;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String val) {
		channelId = val;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String val) {
		message = val;
	}

	public String getId() {
		return id;
	}

	public void setId(String val) {
		id = val;
	}

	public String toString() {
		return "Id: " + getId() + " | Message: " + getMessage() + " | Date Posted: " + getDatePosted()
				+ " | Channel Id: " + getChannelId() + " | " + getAuthor().toString();
	}
}