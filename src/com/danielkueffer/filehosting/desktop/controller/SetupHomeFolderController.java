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
public class SetupHomeFolderController extends AnchorPane implements
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

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
