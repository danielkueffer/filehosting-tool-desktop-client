package com.danielkueffer.filehosting.desktop;

import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import com.danielkueffer.filehosting.desktop.controller.SettingsController;
import com.danielkueffer.filehosting.desktop.controller.SetupAccountController;
import com.danielkueffer.filehosting.desktop.controller.SetupHomeFolderController;
import com.danielkueffer.filehosting.desktop.controller.SetupServerUrlController;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;

/**
 * Main application
 * 
 * @author dkueffer
 * 
 */
public class Main extends Application {

	/* Name of the application */
	public static final String APP_NAME = "Filehosting-Tool";

	/* Path */
	public static final String PATH = "com/danielkueffer/filehosting/desktop/";

	/* Minimum window size */
	private static final double MINIMUM_WINDOW_WIDTH = 390.0;
	private static final double MINIMUM_WINDOW_HEIGHT = 500.0;

	/* Window size */
	private static final double WINDOW_WIDTH = 700.0;
	private static final double WINDOW_HEIGHT = 500.0;

	/* Setup Window size */
	private static final double SETUP_WINDOW_WIDTH = 700.0;
	private static final double SETUP_WINDOW_HEIGHT = 400.0;

	private boolean setupScene;

	private Stage primaryStage;
	private User loggedInUser;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(Main.class, (java.lang.String[]) null);
	}

	/**
	 * Start application
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(APP_NAME);
		this.primaryStage.setMinWidth(MINIMUM_WINDOW_WIDTH);
		this.primaryStage.setMaxHeight(MINIMUM_WINDOW_HEIGHT);

		// Check if the user is logged in
		if (this.loggedInUser == null) {
			this.goToSetupServer();
		} else {
			this.goToSettings();
		}

		this.primaryStage.show();
	}

	/**
	 * Go to setup server URL view
	 */
	public void goToSetupServer() {
		try {
			this.setupScene = true;

			SetupServerUrlController setupServerUrlController = (SetupServerUrlController) this
					.replaceSceneContent("view/SetupServerUrl.fxml");
			setupServerUrlController.setApp(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Go to setup account view
	 */
	public void goToSetupAccount() {
		try {
			this.setupScene = true;

			SetupAccountController setupAccountControler = (SetupAccountController) this
					.replaceSceneContent("view/SetupAccount.fxml");
			setupAccountControler.setApp(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Go to setup home folder view
	 */
	public void goToSetupHomeFolder() {
		try {
			this.setupScene = true;

			SetupHomeFolderController setupHomeFolderController = (SetupHomeFolderController) this
					.replaceSceneContent("view/SetupHomeFolder.fxml");
			setupHomeFolderController.setApp(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Go to the settings view
	 */
	public void goToSettings() {
		try {
			this.setupScene = false;

			SettingsController settingsController = (SettingsController) this
					.replaceSceneContent("view/Settings.fxml");
			settingsController.setApp(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Change FXML template
	 * 
	 * @param fxml
	 * @return
	 * @throws Exception
	 */
	private Initializable replaceSceneContent(String fxml) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(PATH + fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(this.getClass().getClassLoader()
				.getResource(PATH + fxml));

		AnchorPane pane;

		try {
			pane = (AnchorPane) loader.load(in);
		} finally {
			in.close();
		}

		double winWidth = WINDOW_WIDTH;
		double winHeight = WINDOW_HEIGHT;

		if (this.setupScene) {
			winWidth = SETUP_WINDOW_WIDTH;
			winHeight = SETUP_WINDOW_HEIGHT;
		}

		Scene scene = new Scene(pane, winWidth, winHeight);
		this.primaryStage.setScene(scene);
		this.primaryStage.sizeToScene();

		return (Initializable) loader.getController();
	}

	/**
	 * @return the primaryStage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * @return the loggedInUser
	 */
	public User getLoggedInUser() {
		return loggedInUser;
	}
}
