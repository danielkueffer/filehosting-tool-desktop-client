package com.danielkueffer.filehosting.desktop.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import com.danielkueffer.filehosting.desktop.Main;
import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.enums.TabKeys;
import com.danielkueffer.filehosting.desktop.service.PropertyService;

/**
 * The controller to set the URL of the file hosting server
 * 
 * @author dkueffer
 * 
 */
public class SetupHomeFolderController extends AnchorPane implements
		Initializable {

	@FXML
	private Label homeFolderTitle;

	@FXML
	private Label localFolderLabel;

	@FXML
	private Label homeFolderErrorLabel;

	@FXML
	private Button connectButton;

	@FXML
	private Button backButton;

	@FXML
	private Button chooseFolderButton;

	private ResourceBundle bundle;
	private Main application;
	private PropertyService propertyService;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.homeFolderTitle.setText(this.bundle
				.getString("setupHomeFolderTitle"));
		this.connectButton.setText(this.bundle.getString("setupConnect"));
		this.backButton.setText(this.bundle.getString("setupBack"));
		this.localFolderLabel
				.setText(this.bundle.getString("setupLocalFolder"));
		this.chooseFolderButton.setText(this.bundle
				.getString("setupChooseFolder"));
	}

	/**
	 * Set the application
	 * 
	 * @param application
	 */
	public void setApp(Main application) {
		this.application = application;
	}

	/**
	 * Set the property service
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;

		String homeFolder = this.propertyService
				.getProperty(PropertiesKeys.HOME_FOLDER.getValue());

		if (homeFolder != null) {
			this.chooseFolderButton.setText(homeFolder);
		}

		if (this.propertyService.getProperty(PropertiesKeys.HOME_FOLDER
				.getValue()) != null) {
			this.connectButton.setDisable(false);
		}
	}

	/**
	 * Next button event
	 * 
	 * @param evt
	 */
	public void connect(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		if (this.propertyService.getProperty(PropertiesKeys.HOME_FOLDER
				.getValue()) != null) {

			this.application.setSync(true);
			this.application.goToSettings(TabKeys.USER);
		} else {
			this.homeFolderErrorLabel.setText(this.bundle
					.getString("setupChooseFolderError"));
			this.homeFolderErrorLabel.setVisible(true);
		}
	}

	/**
	 * Back button event
	 * 
	 * @param evt
	 */
	public void goToBack(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.goToSetupAccount();
	}

	/**
	 * Choose a directory event
	 * 
	 * @param evt
	 */
	public void chooseDirectory(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle(this.bundle.getString("setupChooseFolder"));
		File dir = dc.showDialog(this.application.getPrimaryStage());

		if (dir != null) {
			String path = dir.getPath();
			this.chooseFolderButton.setText(path);
			this.propertyService.saveProperty(
					PropertiesKeys.HOME_FOLDER.getValue(), path);

			this.homeFolderErrorLabel.setVisible(false);
			this.connectButton.setDisable(false);
		}
	}
}
