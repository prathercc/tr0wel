package cc.prather.tr0wel.controller.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.utility.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class AuthenticationController {

	@FXML
	private Text progressText;
	@FXML
	private VBox authorizationAuthVbox;
	@FXML
	private VBox credentialAuthVbox;
	@FXML
	private ImageView githubLogo;
	@FXML
	private ImageView githubLogoText;
	@FXML
	private ImageView pratherccLogo;
	@FXML
	private Hyperlink whatIsThisLink;

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	public static Stage configurationStage;

	public void initialize() {
		AuthorizationAuthController.setParentControls(progressText, authorizationAuthVbox, credentialAuthVbox);
		CredentialAuthController.setParentControls(progressText, authorizationAuthVbox, credentialAuthVbox);
		
		githubLogo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			logger.info("GitHub image was clicked, opening - " + Properties.sourceCodeLink);
		});
		githubLogoText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			logger.info("GitHub image was clicked, opening - " + Properties.sourceCodeLink);
		});
		pratherccLogo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			logger.info("Prather.cc image was clicked, opening - " + Properties.pratherccLink);
		});
	}
	
	public void whatIsThisLink() {
		logger.info("'What is this?' hyperlink was clicked, opening - ");
	}
}