
package dev.prath.accord.controller.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
@Component
public class ConfigurationController {
	
	@FXML
	private Text configProgressText;
	@FXML
	private Tab channelManagementTab;
	@FXML
	private Tab conversationManagementTab;
	@FXML
	private Tab statsTab;
	@FXML
	private TabPane configurationTabPane;
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
	
	public void initialize() {
		ChannelManagementTabController.setParentControls(configProgressText, configurationTabPane);
		ConversationManagementTabController.setParentControls(configProgressText, configurationTabPane);
	}
}
