package cc.prather.tr0wel.controller.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

@Component
public class LoadingBoxController {
	private static final Logger logger = LoggerFactory.getLogger(LoadingBoxController.class);

	@FXML
	private TextArea loadingTextArea;
	private static TextArea loadingTextAreaCopy;

	public void initialize() {
		loadingTextAreaCopy = loadingTextArea;
		loadingTextAreaCopy.setText("Loader initialized.");
	}

	public static void setLoadingText(String val) {
		loadingTextAreaCopy.appendText("\n" + val);
	}
}
