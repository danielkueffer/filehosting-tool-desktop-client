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
import com.danielkueffer.filehosting.desktop.repository.pojos.User;
import com.danielkueffer.filehosting.desktop.service.PropertyService;

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
	 * Set the property service and check the connection to the server
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	/**
	 * Set the progress bar value
	 */
	public void setDiskProgress() {
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
	public void setControlsDisabled(String serverAddress) {
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
	public void setControlsEnabled(String serverAddress, String username) {
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
		if (this.application.isSync()) {
			this.application.setSync(false);
		} else {
			this.application.setSync(true);
		}

	}

	/**
	 * @return the syncButton
	 */
	public Button getSyncButton() {
		return syncButton;
	}

	/**
	 * @return the connectionLabel
	 */
	public TextArea getConnectionLabel() {
		return connectionLabel;
	}
}
