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
 * The activity controller
 * 
 * @author dkueffer
 * 
 */
public class ActivityController extends Parent implements Initializable {

	@FXML
	private Label activityTitle;

	@FXML
	private Button closeButton;

	private ResourceBundle bundle;
	private Main application;
	private SettingsController settingsController;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.activityTitle.setText(this.bundle.getString("settingsActivities"));
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
	}

	/**
	 * Close the window
	 * 
	 * @param evt
	 */
	public void closeAction(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.hide();
	}
}
