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
import com.danielkueffer.filehosting.desktop.service.UserService;

/**
 * The controller to set the URL of the file hosting server
 * 
 * @author dkueffer
 * 
 */
public class SetupServerUrlController extends AnchorPane implements
		Initializable {

	@FXML
	private Label connectTitle;

	@FXML
	private Button nextButton;

	private ResourceBundle bundle;
	private Main application;
	private UserService userService;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.connectTitle.setText(this.bundle.getString("setupConnectTitle"));
		this.nextButton.setText(this.bundle.getString("setupNext"));
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
	 * Set the user service
	 * 
	 * @param userService
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Next button event
	 * 
	 * @param evt
	 */
	public void goToNext(ActionEvent evt) {
		if (this.application == null) {
			return;
		}
		
		this.userService.checkServerStatus("test");

		this.application.goToSetupAccount();
	}
}
