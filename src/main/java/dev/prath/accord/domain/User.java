package dev.prath.accord.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	@JsonProperty("id")
	private String id = "";

	@JsonProperty("username")
	private String username = "";
	
	private BooleanProperty isSelected = new SimpleBooleanProperty();
	
	public final BooleanProperty getIsSelected() {
		return isSelected;
	}

	public final void setIsSelected(final boolean val) {
		getIsSelected().set(val);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String val) {
		id = val;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String val) {
		username = val;
	}
	
	public String toString() {
		return getUsername();
	}
}
