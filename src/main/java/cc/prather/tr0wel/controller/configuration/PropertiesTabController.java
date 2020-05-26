package cc.prather.tr0wel.controller.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.prather.tr0wel.domain.StatsRow;
import cc.prather.tr0wel.service.AccountService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class PropertiesTabController {
	@FXML
	private TableColumn nameColumn;
	@FXML
	private TableColumn valueColumn;
	
	@FXML
	private TableView propertiesTable;
	
	@Autowired
	AccountService accountService;
	
	public void initialize() {
		String censor = "*********";
		var account = accountService.getDiscordAccount();
		final ObservableList<StatsRow> data = FXCollections.observableArrayList(
				new StatsRow("Authorization: ",account.getAuthorization()),
				new StatsRow("Username: ",account.getUser().getUsername() != null ? account.getUser().getUsername() : "N/A"),
				new StatsRow("Discriminator: ",account.getUser().getDiscriminator() != null ? account.getUser().getDiscriminator() : "N/A"),
				new StatsRow("Email: ",account.getUser().getEmail() != null ? account.getUser().getEmail() : "N/A"),
				new StatsRow("Id: ",account.getUser().getId() != null ? account.getUser().getId() : "N/A"),
				new StatsRow("Active Guilds: ",account.getGuilds().size() + ""),
				new StatsRow("Active Conversations: ",account.getConversations().size() + ""));
		nameColumn.setCellValueFactory(new PropertyValueFactory<StatsRow, String>("name"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<StatsRow, String>("value"));
		propertiesTable.setItems(data);
	}
}
