package org.prathdev.accord.Authentication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @FXML private TextField userNameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button authenticateButton;
    
    public void authenticate() {
    	String username = userNameTextField.getText();
    	String password = passwordTextField.getText();
   
    	String json = "        {\"id\": \"value\"}       ";
    	
    }
    
    private void post(String json) {
    	
    }
    
    
    
}