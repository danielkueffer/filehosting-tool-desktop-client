package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import com.danielkueffer.filehosting.desktop.Main;
import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;
import com.danielkueffer.filehosting.desktop.service.PropertyService;
import com.danielkueffer.filehosting.desktop.service.UserService;

/**
 * The user account controller
 * 
 * @author dkueffer
 * 
 */
public class UserAccountController extends Parent implements Initializable {

	@FXML
	private Label userAccountTitle;

	@FXML
	private TextArea connectionLabel;

	@FXML
	private TextArea localFolderLabel;

	@FXML
	private Label usedDiskSpaceTitle;

	@FXML
	private Label quotaLabel;

	@FXML
	private Label usedDiskSpaceLabel;

	@FXML
	private Button syncButton;

	@FXML
	private Button closeButton;

	@FXML
	private Button editAccountButton;

	@FXML
	private ProgressBar diskSpaceBar;

	private ResourceBundle bundle;
	private Main application;
	private UserService userService;
	private PropertyService propertyService;
	private String action = "";

	private boolean isSync = true;
	private boolean started;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.userAccountTitle.setText(this.bundle
				.getString("settingsUserAccount"));
		this.editAccountButton.setText(this.bundle
				.getString("settingsEditAccount"));

		this.usedDiskSpaceTitle.setText(this.bundle
				.getString("settingsUsedDiskSpace"));

		this.closeButton.setText(this.bundle.getString("settingsClose"));

		this.syncButton.setText(this.bundle.getString("settingsStartSync"));
	}

	/**
	 * Set the application
	 * 
	 * @param application
	 * @param settingsController
	 */
	public void setApp(Main application) {
		this.application = application;

		this.setProgressBarWidth();
	}

	/**
	 * Set the user service
	 * 
	 * @param userService
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Set the property service and check the connection to the server
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	/**
	 * Check the connection to the server
	 */
	public void checkConnection() {

		final String serverAddress = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue());

		this.setControlsDisabled(serverAddress);

		final String username = this.propertyService
				.getProperty(PropertiesKeys.USERNAME.getValue());

		final String password = this.propertyService
				.getProperty(PropertiesKeys.PASSWORD.getValue());

		String localFolder = this.propertyService
				.getProperty(PropertiesKeys.HOME_FOLDER.getValue());

		// Set the local folder
		if (localFolder != null) {
			this.localFolderLabel.setText(this.bundle
					.getString("setupLocalFolder") + ": " + localFolder);
		} else {
			this.localFolderLabel.setText(this.bundle
					.getString("settingsLocalFolderEmpty"));
		}

		// Create a task to run the connection check loop
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
						application.startSync();

						started = true;
					} else {
						started = false;
					}

					// Set the sync button label
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (started) {
								syncButton.setText(bundle
										.getString("settingsStopSync"));
							} else {
								syncButton.setText(bundle
										.getString("settingsStartSync"));
							}
						}
					});
				}
			}
		};

		// Create a thread with the task
		Thread connectionThread = new Thread(task);
		connectionThread.start();
	}

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
				if (this.application.getLoggedInUser() == null) {

					// Login
					boolean login = this.application.login(username, password);

					if (login) {
						// Login successful, set the current user
						User currentUser = this.userService.getUser();
						this.application.setLoggedInUser(currentUser);

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
				this.application.setLoggedInUser(null);
			}
		} else {
			// No server address, logout the user
			this.application.setLoggedInUser(null);
		}

		// Perform UI changes on the JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (action.equals("enableControls")) {
					setControlsEnabled(serverAddress, username);
					setDiskProgress();
				} else if (action.equals("disableControls")) {
					setControlsDisabled(serverAddress);
				} else {
					connectionLabel.setText(bundle
							.getString("settingsNoAddress"));

					syncButton.setDisable(true);
				}
			}
		});

		return loggedIn;
	}

	/**
	 * Set the progress bar value
	 */
	private void setDiskProgress() {
		User user = this.application.getLoggedInUser();

		// Disk quota in GB
		long diskQuota = user.getDiskQuota();
		double diskQuotaBytes = diskQuota * 1024 * 1024 * 1024;

		// Used disk space in bytes
		double usedDiskSpace = user.getUsedDiskSpace();

		double percent = (usedDiskSpace / diskQuotaBytes) * 100;

		// Set progress bar value
		this.diskSpaceBar.progressProperty().set(percent / 100);

		// Set the labels
		this.quotaLabel.setText(this.bundle.getString("settingsQuota") + " "
				+ user.getDiskQuota() + " GB");

		String usedStr = 0 + " KB";

		long usedSpace = user.getUsedDiskSpace() / 1024;

		if (usedSpace < 1024) {
			usedStr = usedSpace + " KB";
		} else {
			usedStr = (usedSpace / 1024) + " MB";
		}

		this.usedDiskSpaceLabel.setText(this.bundle
				.getString("settingsDiskSpaceUsed") + " " + usedStr);
	}

	/**
	 * Set the width of the progress bar
	 */
	private void setProgressBarWidth() {
		Scene scene = this.application.getPrimaryStage().getScene();
		double width = scene.getWidth();
		this.diskSpaceBar.setPrefWidth(width);

		// Set the progress bar with on window resize
		scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number oldVal, Number newVal) {
				diskSpaceBar.setPrefWidth(newVal.doubleValue());
			}

		});
	}

	/**
	 * Set the controls as disabled if not connected with the server
	 * 
	 * @param serverAddress
	 */
	private void setControlsDisabled(String serverAddress) {
		this.connectionLabel.setText(this.bundle
				.getString("settingsNotConnected") + " " + serverAddress);

		this.quotaLabel.setText(this.bundle.getString("settingsQuota"));
		this.usedDiskSpaceLabel.setText(this.bundle
				.getString("settingsDiskSpaceUsed"));
		this.diskSpaceBar.progressProperty().set(0);

		this.syncButton.setDisable(true);
	}

	/**
	 * Set the controls enabled and connected
	 * 
	 * @param serverAddress
	 * @param username
	 */
	private void setControlsEnabled(String serverAddress, String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.bundle.getString("settingsConnected"));
		sb.append(" ");
		sb.append(serverAddress);
		sb.append(" ");
		sb.append(this.bundle.getString("settingsAs"));
		sb.append(" ");
		sb.append(username);

		this.connectionLabel.setText(sb.toString());

		this.syncButton.setDisable(false);
	}

	/**
	 * Edit setup button
	 * 
	 * @param evt
	 */
	public void editSetup(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.goToSetupServer();
	}

	/**
	 * Close event
	 * 
	 * @param evt
	 */
	public void closeEvent(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.hide();
	}

	/**
	 * Synchronization start or stop event
	 * 
	 * @param evt
	 */
	public void syncEvent(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		// Set the synchronization flag
		if (this.isSync) {
			this.isSync = false;
		} else {
			this.isSync = true;
		}

	}
}
