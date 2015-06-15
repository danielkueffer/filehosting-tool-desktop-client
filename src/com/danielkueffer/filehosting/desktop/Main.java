package com.danielkueffer.filehosting.desktop;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;

import com.danielkueffer.filehosting.desktop.controller.SettingsController;
import com.danielkueffer.filehosting.desktop.controller.SetupAccountController;
import com.danielkueffer.filehosting.desktop.controller.SetupHomeFolderController;
import com.danielkueffer.filehosting.desktop.controller.SetupServerUrlController;
import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.enums.TabKeys;
import com.danielkueffer.filehosting.desktop.helper.NetworkHelper;
import com.danielkueffer.filehosting.desktop.repository.client.FileClient;
import com.danielkueffer.filehosting.desktop.repository.client.UserClient;
import com.danielkueffer.filehosting.desktop.repository.client.impl.FileClientImpl;
import com.danielkueffer.filehosting.desktop.repository.client.impl.UserClientImpl;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;
import com.danielkueffer.filehosting.desktop.service.FileService;
import com.danielkueffer.filehosting.desktop.service.PropertyService;
import com.danielkueffer.filehosting.desktop.service.UserService;
import com.danielkueffer.filehosting.desktop.service.impl.FileServiceImpl;
import com.danielkueffer.filehosting.desktop.service.impl.PropertyServiceImpl;
import com.danielkueffer.filehosting.desktop.service.impl.UserServiceImpl;

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
	private Locale currentLocale;

	private UserClient userClient;
	private FileClient fileClient;
	private PropertyService propertyService;
	private UserService userService;
	private FileService fileService;

	private ResourceBundle bundle;

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

		this.userClient = new UserClientImpl();
		this.fileClient = new FileClientImpl();

		this.propertyService = new PropertyServiceImpl();
		this.userService = new UserServiceImpl(userClient, propertyService);
		this.fileService = new FileServiceImpl(fileClient, propertyService,
				userService);

		// Set the propertyService in the network helper
		NetworkHelper.setPropertyService(this.propertyService);

		this.primaryStage.setTitle(APP_NAME);
		this.primaryStage.setMinWidth(MINIMUM_WINDOW_WIDTH);
		this.primaryStage.setMaxHeight(MINIMUM_WINDOW_HEIGHT);

		this.currentLocale = new Locale("de", "DE");

		String serverAddress = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue());

		String username = this.propertyService
				.getProperty(PropertiesKeys.USERNAME.getValue());
		String password = this.propertyService
				.getProperty(PropertiesKeys.PASSWORD.getValue());

		boolean loginSuccess = false;

		// Check for server connection first, then login
		if (serverAddress != null && username != null && password != null) {
			if (this.userService.checkServerStatus(serverAddress)) {
				loginSuccess = this.userService.login(username, password);
			}
		}

		// Check if the user is logged in
		if (!loginSuccess) {
			this.goToSetupServer();
		} else {
			this.loggedInUser = this.userService.getUser();

			// Set the language of the logged in user
			if (this.loggedInUser.getLanguage().equals("en")) {
				this.currentLocale = new Locale("en", "EN");
			}

			this.goToSettings(TabKeys.USER);
		}

		// Load the resource bundle
		this.bundle = ResourceBundle.getBundle(Main.PATH
				+ "resources/i18n/messages", this.currentLocale);

		// Create the tray icon
		this.createTrayIcon();
		Platform.setImplicitExit(false);

		this.primaryStage.show();
		this.primaryStage.centerOnScreen();
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
			setupServerUrlController.setUserService(this.userService);
			setupServerUrlController.setPropertyService(this.propertyService);
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
			setupAccountControler.setUserService(this.userService);
			setupAccountControler.setPropertyService(this.propertyService);
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
			setupHomeFolderController.setPropertyService(this.propertyService);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Go to the settings view
	 */
	public void goToSettings(TabKeys view) {
		try {
			this.setupScene = false;

			SettingsController settingsController = (SettingsController) this
					.replaceSceneContent("view/Settings.fxml");
			settingsController.setApp(this);
			settingsController.setUserService(this.userService);
			settingsController.setPropertyService(this.propertyService);

			// Select a tab
			switch (view) {
			case USER:
				settingsController.goToUserAccount();
				break;
			case ACTIVITIES:
				settingsController.selectIndexTab(1);
				break;
			case NETWORK:
				settingsController.selectIndexTab(2);
				break;
			}

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

		loader.setResources(ResourceBundle.getBundle(PATH
				+ "resources/i18n/messages", this.currentLocale));

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
	 * Create the trayIcon to minimize the application to the system tray
	 * 
	 * @param stage
	 */
	public void createTrayIcon() {
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();

			BufferedImage trayImage = null;

			try {
				trayImage = ImageIO.read(this
						.getClass()
						.getClassLoader()
						.getResourceAsStream(
								PATH + "resources/images/folder_sync.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			int trayIconWidth = new TrayIcon(trayImage).getSize().width;

			this.primaryStage
					.setOnCloseRequest(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent t) {
							primaryStage.hide();
						}
					});

			// construct a TrayIcon
			TrayIcon trayIcon = new TrayIcon(trayImage.getScaledInstance(
					trayIconWidth, -1, Image.SCALE_SMOOTH), "Title",
					this.getContextMenu());

			// set the TrayIcon properties
			trayIcon.addActionListener(this.showListener);

			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * Get the context menu
	 * 
	 * @return
	 */
	private PopupMenu getContextMenu() {
		PopupMenu popup = new PopupMenu();

		MenuItem showItem = new MenuItem(this.bundle.getString("trayShow"));
		showItem.addActionListener(this.showListener);
		popup.add(showItem);

		MenuItem closeItem = new MenuItem(this.bundle.getString("trayExit"));
		closeItem.addActionListener(this.closeListener);
		popup.add(closeItem);

		return popup;
	}

	/**
	 * The stage show actionListener
	 */
	private ActionListener showListener = new ActionListener() {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					primaryStage.show();
					primaryStage.centerOnScreen();
				}
			});
		}
	};

	/**
	 * The exit actionListener
	 */
	private ActionListener closeListener = new ActionListener() {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			System.exit(0);
		}
	};

	/**
	 * Hide the stage
	 * 
	 * @param stage
	 */
	public void hide() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (SystemTray.isSupported()) {
					primaryStage.hide();
				} else {
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Start synchronization
	 */
	public void startSync() {
		this.fileService.startSynchronization();
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

	/**
	 * The loggedInUser to set
	 * 
	 * @param user
	 */
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	/**
	 * @return the currentLocale
	 */
	public Locale getCurrentLocale() {
		return currentLocale;
	}

	/**
	 * @param currentLocale
	 *            the currentLocale to set
	 */
	public void setCurrentLocale(Locale currentLocale) {
		this.currentLocale = currentLocale;
	}
}
