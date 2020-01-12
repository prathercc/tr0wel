package dev.prath.accord.domain;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
	
	private BooleanProperty isSelected = new SimpleBooleanProperty();
	
	public final BooleanProperty getIsSelected() {
		return isSelected;
	}
	
	public final void setIsSelected(final boolean val) {
		getIsSelected().set(val);
	}

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
		TimeZone tz = Calendar.getInstance().getTimeZone();
		String date = OffsetDateTime.parse(getDatePosted()).format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(tz.toZoneId()));
		return "[" + date + "] " + getAuthor().getUsername() + ": " + getMessage();
	}
}
