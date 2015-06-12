package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private SettingsController settingsController;
	private UserService userService;
	private PropertyService propertyService;

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
	}

	/**
	 * Set the application
	 * 
	 * @param application
	 * @param settingsController
	 */
	public void setApp(Main application, SettingsController settingsController) {
		this.application = application;
		this.settingsController = settingsController;
		this.setDiskProgress();
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
	 * Set the property service
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;

		String serverAddress = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue());

		User user = this.application.getLoggedInUser();

		// Set the connection label
		if (serverAddress != null) {
			// Connected
			if (this.userService.checkServerStatus(serverAddress)) {
				String username = "";

				if (user != null) {
					username = user.getUsername();
				}

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
			} else {
				// Not connected
				StringBuilder sb = new StringBuilder();
				sb.append(this.bundle.getString("settingsNotConnected") + " "
						+ serverAddress);

				this.connectionLabel.setText(sb.toString());
				this.syncButton.setDisable(true);
			}
		} else {
			this.connectionLabel.setText(this.bundle
					.getString("settingsNoAddress"));

			this.syncButton.setDisable(true);
		}

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

	}

	/**
	 * Set the progress bar
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

		// Set progress bar width
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
		
		System.out.println("quota: " + diskQuotaBytes + " used: "
				+ usedSpace + " percent: " + percent);
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

		System.out.println("close");
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

		System.out.println("sync");
	}
}
