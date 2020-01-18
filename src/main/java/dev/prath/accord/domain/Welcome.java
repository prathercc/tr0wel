package dev.prath.accord.domain;

public class Welcome {

	private String message = "";
	
	public Welcome(String val) {
		message = val;
	}
	
	public void setMessage(String val) {
		message = val;
	}
	
	public String getMessage() {
		return message;
	}
	
}
