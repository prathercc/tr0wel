package cc.prather.tr0wel.controller.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.domain.Message;
import cc.prather.tr0wel.domain.StatsRow;
import cc.prather.tr0wel.service.AccountService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class InformationTabController {
	@FXML
	private TableColumn nameColumn;
	@FXML
	private TableColumn valueColumn;
	@FXML
	private TableView informationTable;

	@Autowired
	AccountService accountService;

	private static final Logger logger = LoggerFactory.getLogger(InformationTabController.class);

	public void initialize() {
		var selectedChannel = accountService.getSelectedChannel();
		var selectedConversation = accountService.getSelectedConversation();
		var users = selectedChannel != null ? selectedChannel.getParticipatingUsers()
				: selectedConversation.getRecipients();

		Message lastMessage = null;
		try {
			lastMessage = selectedChannel != null ? selectedChannel.getMessages().get(0)
					: selectedConversation.getMessages().get(0);
		} catch (Exception e) {
			logger.warn("InformationTabController was unable to find a last message for "
					+ (selectedChannel != null ? "channel '" + selectedChannel.getName() + "'"
							: "conversation '" + selectedConversation.toString() + "'"));
		}
		final ObservableList<StatsRow> data = FXCollections.observableArrayList(
				new StatsRow("Name: ",selectedChannel != null ? selectedChannel.getName() : selectedConversation.toString()),
				new StatsRow("Id: ",selectedChannel != null ? selectedChannel.getId() : selectedConversation.getId()),
				new StatsRow("Type: ",selectedChannel != null ? "Channel" : "Conversation"),
				new StatsRow("Latest Message: ",lastMessage != null ? lastMessage.getDatePosted() : "N/A"),
				new StatsRow("Active Users: ",Integer.toString(users.size())),
				new StatsRow("NSFW: ",selectedConversation != null ? "N/A" : selectedChannel.getIsNsfw() ? "Yes" : " No"));
				
		nameColumn.setCellValueFactory(new PropertyValueFactory<StatsRow, String>("name"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<StatsRow, String>("value"));
		informationTable.setItems(data);
	}
}
