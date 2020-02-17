package dev.prath.tr0wel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.prath.tr0wel.utility.Properties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxLauncher extends Application {
	private static final Logger logger = LoggerFactory.getLogger(FxLauncher.class);
	public static Parent rootNode;
	public static Stage authenticationMenu;
	public static FXMLLoader fxmlLoader;
	public static Stage currentStage;
	
	@Override
	public void start(Stage stage) throws Exception {
		fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Authentication/AuthenticationMenu.fxml"));
		fxmlLoader.setControllerFactory(Main.springContext::getBean);
		rootNode = fxmlLoader.load();
		stage.setTitle(Properties.applicationName);
		stage.setScene(new Scene(rootNode));
		stage.setResizable(false);
		authenticationMenu = stage;
		stage.show();
	}
	@Override
	public void stop() {
		logger.warn("All JavaFx windows are now closed! Stopping Spring Boot...");
		Main.springContext.close();
		logger.info("Spring Boot has been shut down.");
	}
	public static void startFxApplication(String[] args) {
		launch();
	}
}
