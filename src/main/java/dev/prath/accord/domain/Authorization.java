package dev.prath.accord.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorization {
	
	@JsonProperty("token")
	private String token = "";
	
	public String getToken() {
		return token;
	}
	
}