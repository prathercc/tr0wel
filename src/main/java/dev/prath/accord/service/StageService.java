package dev.prath.accord.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import dev.prath.accord.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Service
public class StageService {
	
	private FXMLLoader fxmlLoader;
	private Parent rootNode;
	
	
	public Stage getNewStage(String title, String fxml) throws IOException {
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setControllerFactory(Main.springContext::getBean); //SET ME!!!!
		fxmlLoader.setLocation(getClass().getResource(fxml));
		rootNode = (Parent) fxmlLoader.load(getClass().getResourceAsStream(fxml));

		Scene scene = new Scene(rootNode);
		Stage stage = new Stage();

		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(false);
		
		return stage;
	}
	public Stage getNewStageAsDialog(String title, String fxml, Stage parent) throws IOException {
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setControllerFactory(Main.springContext::getBean); //SET ME!!!!
		fxmlLoader.setLocation(getClass().getResource(fxml));
		rootNode = (Parent) fxmlLoader.load(getClass().getResourceAsStream(fxml));
		
		Scene scene = new Scene(rootNode);
		Stage stage = new Stage();

		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(parent);
		
		return stage;
	}
}
