package dev.prath.accord.controller.conversation;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
@Component
public class EditTabController {
	
	@FXML
	private TextField newMessageTextField;
	@FXML
	private Button editSelectionsButton;
	@FXML
	private Text progressText;

}
