package cc.prather.tr0wel.controller.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class AuthenticationController {

	@FXML
	private VBox authorizationAuthVbox;
	@FXML
	private VBox credentialAuthVbox;

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	public static Stage configurationStage;

	public void initialize() {
		AuthorizationAuthController.setParentControls(authorizationAuthVbox, credentialAuthVbox);
		CredentialAuthController.setParentControls(authorizationAuthVbox, credentialAuthVbox);
	}

}