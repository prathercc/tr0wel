package dev.prath.accord.controller.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.prath.accord.domain.Message;
import dev.prath.accord.domain.User;
import dev.prath.accord.service.AccountService;
import dev.prath.accord.service.DisposalService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;
@Component
public class ChannelManagerController {
	@FXML
	private ListView<Message> channelListView;
	@FXML
	private Button selectAllButton;
	@FXML
	private ChoiceBox<User> userSelectionBox;
	@FXML
	private Text numOfMsgText;
	@FXML
	private Tab exportTab;
	@FXML
	private Tab editTab;
	@FXML
	private Tab deleteTab;
	
	@Autowired
	AccountService accountService;

	@Autowired
	DisposalService disposalService;
	
	private boolean selectOrientation = false;

	private static final Logger logger = LoggerFactory.getLogger(ChannelManagerController.class);
	
	public void initialize() {
		channelListView.setCellFactory(CheckBoxListCell.forListView(Message::getIsSelected));
		accountService.getSelectedChannel().getParticipatingUsers().stream()
				.forEach(user -> userSelectionBox.getItems().add(user));
		ChannelDeleteTabController.setParentControls(channelListView, new Tab[] {exportTab, editTab, deleteTab}, selectAllButton, numOfMsgText, userSelectionBox);
		ChannelEditTabController.setParentControls(channelListView, new Tab[] {exportTab, editTab, deleteTab}, selectAllButton, numOfMsgText, userSelectionBox);
		ChannelExportTabController.setParentControls(channelListView, new Tab[] {exportTab, editTab, deleteTab}, selectAllButton, numOfMsgText, userSelectionBox);
	}
	
	public void selectAll() {
		selectOrientation = !selectOrientation ? true : false;
		channelListView.getItems().stream().forEach(message -> message.setIsSelected(selectOrientation));
	}
	
	public void selectUser() {
		Thread thread = new Thread(getNewSelectionTask());
		thread.setDaemon(true);
		thread.start();
	}
	
	public Task<Void> getNewSelectionTask() {
		Task<Void> selectionTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				toggleControls(true);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						channelListView.getItems().clear();
						if (userSelectionBox.getValue() != null) {
							var selectedChannelMessages = accountService.getSelectedChannel().getMessages();
							var selectedUserId = userSelectionBox.getValue().getId();
							selectedChannelMessages.stream()
									.filter(message -> message.getAuthor().getId().equalsIgnoreCase(selectedUserId))
									.forEach(message -> channelListView.getItems().add(message));
							updateText(numOfMsgText, "Found " + channelListView.getItems().size() + " messages by user.");
						}
					}
				});
				toggleControls(false);
				return null;
			}
		};
		return selectionTask;
	}
	
	private void toggleControls(boolean val) {
		channelListView.setDisable(val);
		selectAllButton.setDisable(val);
		userSelectionBox.setDisable(val);
		exportTab.setDisable(val);
		editTab.setDisable(val);
		deleteTab.setDisable(val);
	}
	
	private void updateText(Text text, String val) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(val);
			}
		});
	}
}
