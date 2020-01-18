package dev.prath.accord.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.prath.accord.FxLauncher;
import dev.prath.accord.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Service
public class StageService {
	
	private static final Logger logger = LoggerFactory.getLogger(StageService.class);
	private FXMLLoader fxmlLoader;
	private Parent rootNode;
	
	public StageService() {
		logger.info("StageService has been initialized.");
	}
	
	public Stage getNewStage(String title, String fxml) throws IOException {
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setControllerFactory(Main.springContext::getBean);
		fxmlLoader.setLocation(getClass().getResource(fxml));
		rootNode = (Parent) fxmlLoader.load(getClass().getResourceAsStream(fxml));
		Scene scene = new Scene(rootNode);
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(false);
		logger.info("StageService is returning a new Stage.");
		return stage;
	}
	
	public Stage getNewStageAsDialog(String title, String fxml, Stage parent) throws IOException {
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setControllerFactory(Main.springContext::getBean);
		fxmlLoader.setLocation(getClass().getResource(fxml));
		rootNode = (Parent) fxmlLoader.load(getClass().getResourceAsStream(fxml));
		Scene scene = new Scene(rootNode);
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(parent);
		logger.info("StageService is returning a new Stage dialog.");
		return stage;
	}
}