package com.danielkueffer.filehosting.desktop;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.danielkueffer.filehosting.desktop.controller.SettingsController;
import com.danielkueffer.filehosting.desktop.controller.SetupAccountController;
import com.danielkueffer.filehosting.desktop.controller.SetupHomeFolderController;
import com.danielkueffer.filehosting.desktop.controller.SetupServerUrlController;
import com.danielkueffer.filehosting.desktop.controller.UserAccountController;
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

	private static final Logger _log = LogManager.getLogger(Main.class
			.getName());

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
	private boolean started;
	private boolean isSync = true;

	private String serverAddress;
	private String username;
	private String password;
	private String action = "";

	private Stage primaryStage;
	private User loggedInUser;
	private Locale currentLocale;

	private UserClient userClient;
	private FileClient fileClient;
	private PropertyService propertyService;
	private UserService userService;
	private FileService fileService;

	private ResourceBundle bundle;
	private Thread connectionThread;

	private UserAccountController userAccountController;

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

		String serverAddress = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue());

		String username = this.propertyService
				.getProperty(PropertiesKeys.USERNAME.getValue());
		String password = this.propertyService
				.getProperty(PropertiesKeys.PASSWORD.getValue());
		String language = this.propertyService
				.getProperty(PropertiesKeys.LANGUAGE.getValue());

		this.currentLocale = new Locale("de", "DE");

		// Set the language if present
		if (language != null) {
			if (language.equals("en")) {
				this.currentLocale = new Locale("en", "EN");
			}
		}

		boolean hasConfig = false;

		// Check for server connection first, then login
		if (serverAddress != null && username != null && password != null) {
			hasConfig = true;
		}

		// Create the connection thread
		this.connectionThread = new Thread(this.task);

		// Check if the user is logged in
		if (!hasConfig) {
			this.goToSetupServer();
		} else {
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

			// Check connection
			this.serverAddress = this.propertyService
					.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue());

			this.username = this.propertyService
					.getProperty(PropertiesKeys.USERNAME.getValue());

			this.password = this.propertyService
					.getProperty(PropertiesKeys.PASSWORD.getValue());

			SettingsController settingsController = (SettingsController) this
					.replaceSceneContent("view/Settings.fxml");
			settingsController.setApp(this);
			settingsController.setUserService(this.userService);
			settingsController.setPropertyService(this.propertyService);

			// Select a tab
			switch (view) {
			case USER:
				this.userAccountController = settingsController
						.goToUserAccount();
				break;
			case ACTIVITIES:
				settingsController.selectIndexTab(1);
				break;
			case NETWORK:
				settingsController.selectIndexTab(2);
				break;
			}

			// Start the connection and sync thread if not started
			if (!this.connectionThread.isAlive()) {
				_log.info("In connection thread start");

				this.connectionThread.start();
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

			// add a mouseListener to the trayIcon to show the application with
			// a single click
			trayIcon.addMouseListener(this.clickListener);

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
	 * Stage show mouseListener on single click
	 */
	private MouseListener clickListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.show();
						primaryStage.centerOnScreen();
					}
				});
			}
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
	 * Login to the rest service
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean login(String username, String password) {
		return this.userService.login(username, password);
	}

	/**
	 * Create a task to run the connection check loop
	 */
	final Task<Object> task = new Task<Object>() {
		@Override
		protected Object call() throws Exception {
			while (true) {

				// Check connection and login the user
				boolean loggedIn = connectionLoop(serverAddress, username,
						password);

				// User is logged in
				if (loggedIn && isSync) {

					// start the synchronization
					startSync();

					started = true;
				} else {
					started = false;
				}

				// Set the sync button label
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (started) {
							userAccountController.getSyncButton().setText(
									bundle.getString("settingsStopSync"));
						} else {
							userAccountController.getSyncButton().setText(
									bundle.getString("settingsStartSync"));
						}
					}
				});
				
				Thread.sleep(1000);
			}
		}
	};

	/**
	 * Loop to test the connection and to login the user
	 * 
	 * @param serverAddress
	 * @param username
	 * @param password
	 */
	private boolean connectionLoop(final String serverAddress,
			final String username, String password) {

		boolean loggedIn = false;

		// Check if the server address is set
		if (serverAddress != null) {

			// Check connection
			if (this.userService.checkServerStatus(serverAddress)) {

				// Check if not logged in
				if (this.getLoggedInUser() == null) {

					// Login
					boolean login = this.login(username, password);

					if (login) {
						// Login successful, set the current user
						User currentUser = this.userService.getUser();
						this.setLoggedInUser(currentUser);

						action = "enableControls";

						loggedIn = true;
					} else {
						// Login failed
						action = "disableControls";
					}
				} else {
					loggedIn = true;
				}
			} else {
				// No connection
				action = "disableControls";

				// Logout the user
				this.setLoggedInUser(null);
			}
		} else {
			// No server address, logout the user
			this.setLoggedInUser(null);
		}

		// Perform UI changes on the JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (action.equals("enableControls")) {
					userAccountController.setControlsEnabled(serverAddress,
							username);
					userAccountController.setDiskProgress();
				} else if (action.equals("disableControls")) {
					userAccountController.setControlsDisabled(serverAddress);
				} else {
					userAccountController.getConnectionLabel().setText(
							bundle.getString("settingsNoAddress"));

					userAccountController.getSyncButton().setDisable(true);
				}
			}
		});

		return loggedIn;
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

	/**
	 * @return the isSync
	 */
	public boolean isSync() {
		return isSync;
	}

	/**
	 * @param isSync
	 *            the isSync to set
	 */
	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}
}
