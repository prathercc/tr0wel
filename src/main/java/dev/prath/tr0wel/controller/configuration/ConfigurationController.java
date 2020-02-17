
package dev.prath.tr0wel.controller.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
@Component
public class ConfigurationController {
	
	@FXML
	private Tab channelManagementTab;
	@FXML
	private Tab conversationManagementTab;
	@FXML
	private Tab statsTab;
	@FXML
	private Accordion configurationAccordian;
	@FXML
	private TitledPane conversationTitlePane;
	@FXML
	private TitledPane channelTitlePane;
	@FXML
	private TitledPane statsTitlePane;
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
	
	public void initialize() {
		TitledPane[] titlePaneArr = {conversationTitlePane, channelTitlePane, statsTitlePane};
		ChannelManagementTabController.setParentControls(configurationAccordian, titlePaneArr);
		ConversationManagementTabController.setParentControls(configurationAccordian, titlePaneArr);
	}
}
