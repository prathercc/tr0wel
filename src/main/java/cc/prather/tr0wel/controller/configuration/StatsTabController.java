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
import javafx.scene.text.Text;

@Component
public class StatsTabController {
	
	@FXML
	private Text authorizationText;
	@FXML
	private Text usernameText;
	@FXML
	private Text idText;
	@FXML
	private Text discriminatorText;
	@FXML
	private Text activeGuildsText;
	@FXML
	private Text emailText;
	@FXML
	private Text activeConversationsText;
	
	@FXML
	private TableColumn nameColumn;
	@FXML
	private TableColumn valueColumn;
	
	@FXML
	private TableView statsTable;
	
	@Autowired
	AccountService accountService;
	
	public void initialize() {
		String censor = "*********";
		var account = accountService.getDiscordAccount();
//		authorizationText.setText(account.getAuthorization().substring(0, 30) + censor);
//		usernameText.setText(account.getUser().getUsername() != null ? account.getUser().getUsername() : "N/A");
//		idText.setText(account.getUser().getId() != null ? account.getUser().getId() : "N/A");
//		discriminatorText.setText(account.getUser().getDiscriminator() != null ? account.getUser().getDiscriminator() : "N/A");
//		activeGuildsText.setText(account.getGuilds().size() + "");
//		activeConversationsText.setText(account.getConversations().size() + "");
//		emailText.setText(account.getUser().getEmail() != null ? account.getUser().getEmail() : "N/A");
		
		
		final ObservableList<StatsRow> data = FXCollections.observableArrayList(
				new StatsRow("Authorization: ",account.getAuthorization().substring(0, 30) + censor),
				new StatsRow("Username: ",account.getUser().getUsername() != null ? account.getUser().getUsername() : "N/A"),
				new StatsRow("Discriminator: ",account.getUser().getDiscriminator() != null ? account.getUser().getDiscriminator() : "N/A"),
				new StatsRow("Email: ",account.getUser().getEmail() != null ? account.getUser().getEmail() : "N/A"),
				new StatsRow("Id: ",account.getUser().getId() != null ? account.getUser().getId() : "N/A"),
				new StatsRow("Active Guilds: ",account.getGuilds().size() + ""),
				new StatsRow("Active Conversations: ",account.getConversations().size() + ""));
				
		nameColumn.setCellValueFactory(new PropertyValueFactory<StatsRow, String>("name"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<StatsRow, String>("value"));
		statsTable.setItems(data);
	}
	
}
