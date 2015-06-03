package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.danielkueffer.filehosting.desktop.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * The network controller
 * 
 * @author dkueffer
 * 
 */
public class NetworkController extends Parent implements Initializable {

	@FXML
	private Label networkTitle;

	private ResourceBundle bundle;
	private Main application;
	private SettingsController settingsController;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.networkTitle.setText(this.bundle.getString("settingsNetwork"));
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
}
