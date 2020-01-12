package org.prathdev.accord;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

	private static final Logger log = LoggerFactory.getLogger(MainApp.class);
	
	public static Stage authenticationMenu;
	public static final Path iniPath = Paths.get(Paths.get(System.getProperty("user.home"), ".accord").toString(),"accord.ini");

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public void start(Stage stage) throws Exception {
		Path path = Paths.get(System.getProperty("user.home"), ".accord");
		if(!Files.exists(path)) {
			Files.createDirectory(path);
		}
		if(!Files.exists(iniPath)) {
			Files.createFile(iniPath);
		}

		log.info("Starting application...");

		String fxmlFile = "/fxml/authenticationMenu.fxml";
		log.debug("Loading FXML for main view from: {}", fxmlFile);
		FXMLLoader loader = new FXMLLoader();
		Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

		log.debug("Showing Authentication Menu");
		Scene scene = new Scene(rootNode);
		scene.getStylesheets().add("/styles/styles.css");

		stage.setTitle("accord - Authentication Menu");
		stage.setScene(scene);
		stage.setResizable(false);
		authenticationMenu = stage;
		stage.show();
	}
}
