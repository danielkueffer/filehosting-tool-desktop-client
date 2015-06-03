package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import com.danielkueffer.filehosting.desktop.Main;

/**
 * The controller to set the URL of the file hosting server
 * 
 * @author dkueffer
 * 
 */
public class SetupServerUrlController extends AnchorPane implements
		Initializable {

	private Main application;

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
	public void goToNext(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.goToSetupAccount();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}
