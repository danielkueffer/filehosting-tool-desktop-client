package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.danielkueffer.filehosting.desktop.Main;

import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 * The activity controller
 * 
 * @author dkueffer
 * 
 */
public class ActivityController extends Parent implements Initializable {

	private Main application;
	private SettingsController settingsController;

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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

}
