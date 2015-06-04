package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	private Label serverAddressLabel;

	@FXML
	private Label serverAddressErrorLabel;

	@FXML
	private Button nextButton;

	@FXML
	private TextField serverAddressField;

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
		this.serverAddressLabel.setText(this.bundle
				.getString("setupServerAddress"));

		this.serverAddressField.textProperty().addListener(textChange);
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

		String serverAddr = this.serverAddressField.getText();
		String url = serverAddr;

		if (!serverAddr.startsWith("http://")
				&& !serverAddr.startsWith("https://")) {
			url = "http://" + serverAddr;
		}

		// Check if the server URL is correct
		if (!this.userService.checkServerStatus(url)) {
			
			this.serverAddressErrorLabel.setText(this.bundle
					.getString("setupServerAddressError") + url);

			this.serverAddressErrorLabel.setVisible(true);

			return;
		} else {
			this.serverAddressErrorLabel.setVisible(false);
		}

		this.application.goToSetupAccount();
	}

	/**
	 * Text input change
	 */
	public ChangeListener<String> textChange = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldVal, String newVal) {

			if (!newVal.trim().equals("")) {
				nextButton.setDisable(false);
			} else {
				nextButton.setDisable(true);
			}
		}
	};
}
