package dev.prath.tr0wel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.prath.tr0wel.Main;
import dev.prath.tr0wel.utility.Properties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Service
public class StageService {

	private static final Logger logger = LoggerFactory.getLogger(StageService.class);
	private FXMLLoader fxmlLoader;
	private Parent rootNode;

	public StageService() {
		logger.info("StageService has been initialized.");
	}

	public Stage getNewStage(String title, String fxml) {
		title = title.length() < 1 ? Properties.applicationName : title;
		try {
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
		} catch (Exception e) {
			logger.error("StageService could not return a new Stage!");
			return null;
		}
	}

	public Stage getNewStageAsDialog(String title, String fxml, Stage parent) {
		title = title.length() < 1 ? Properties.applicationName : title;
		try {
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
			stage.initStyle(StageStyle.UTILITY);
			logger.info("StageService is returning a new Stage dialog.");
			return stage;
		} catch (Exception e) {
			logger.error("StageService could not return a new Stage dialog!");
			return null;
		}
	}
}