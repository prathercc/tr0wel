package dev.prath.accord.controller.conversation;

import org.springframework.stereotype.Component;

import dev.prath.accord.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
@Component
public class ExportTabController {

	@FXML
	private CheckBox exportedSelectedCheckbox;
	@FXML 
	private CheckBox exportFromUsersCheckbox;
	@FXML
	private Button exportButton;
	@FXML
	private Text progressText;
	@FXML
	private ListView<User> participatingUsersList;
	
}
