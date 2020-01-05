package org.prathdev.accord.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	@JsonProperty("id")
	private String id = "";

	@JsonProperty("username")
	private String username = "";
	
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
		return "Username: " + getUsername() + " | User Id: " + getId();
	}
}
