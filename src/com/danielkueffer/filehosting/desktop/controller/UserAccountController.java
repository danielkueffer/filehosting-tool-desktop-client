package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.danielkueffer.filehosting.desktop.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
	private Button editAccountButton;

	private ResourceBundle bundle;
	private Main application;
	private SettingsController settingsController;

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
}
