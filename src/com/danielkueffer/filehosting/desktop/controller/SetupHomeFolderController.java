package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import com.danielkueffer.filehosting.desktop.Main;

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
	private Button connectButton;

	private ResourceBundle bundle;
	private Main application;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.homeFolderTitle.setText(this.bundle
				.getString("setupHomeFolderTitle"));
		this.connectButton.setText(this.bundle.getString("setupConnect"));
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
	 * Next button event
	 * 
	 * @param evt
	 */
	public void connect(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.goToSettings();
	}
}
