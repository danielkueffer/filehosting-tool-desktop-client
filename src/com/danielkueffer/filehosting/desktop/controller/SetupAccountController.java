package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import com.danielkueffer.filehosting.desktop.Main;
import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;
import com.danielkueffer.filehosting.desktop.service.PropertyService;
import com.danielkueffer.filehosting.desktop.service.UserService;

/**
 * The controller to set the URL of the file hosting server
 * 
 * @author dkueffer
 * 
 */
public class SetupAccountController extends AnchorPane implements Initializable {

	@FXML
	private Label accountTitle;

	@FXML
	private Label usernameLabel;

	@FXML
	private Label passwordLabel;

	@FXML
	private Label userAccountErrorLabel;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button nextButton;

	@FXML
	private Button backButton;

	private ResourceBundle bundle;
	private Main application;
	private UserService userService;
	private PropertyService propertyService;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.accountTitle.setText(this.bundle.getString("setupUserDataTitle"));
		this.nextButton.setText(this.bundle.getString("setupNext"));
		this.backButton.setText(this.bundle.getString("setupBack"));
		this.usernameLabel.setText(this.bundle.getString("setupUsername"));
		this.passwordLabel.setText(this.bundle.getString("setupPassword"));

		this.usernameField.textProperty().addListener(this.textChange);
		this.passwordField.textProperty().addListener(this.passwordChange);
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
	 * Set the property service
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;

		String username = this.propertyService
				.getProperty(PropertiesKeys.USERNAME.getValue());
		String password = this.propertyService
				.getProperty(PropertiesKeys.PASSWORD.getValue());

		if (username != null) {
			this.usernameField.setText(username);
		}

		if (password != null) {
			this.passwordField.setText(password);
			this.nextButton.requestFocus();
		}
	}

	/**
	 * Next button event, save user data
	 * 
	 * @param evt
	 */
	public void goToNext(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		String username = this.usernameField.getText();
		String password = this.passwordField.getText();

		if (!this.userService.login(username, password)) {
			this.userAccountErrorLabel.setText(this.bundle
					.getString("setupUserAccountError"));

			this.userAccountErrorLabel.setVisible(true);
			return;
		} else {
			this.userAccountErrorLabel.setVisible(false);

			// Save user name and password
			this.propertyService.saveProperty(
					PropertiesKeys.USERNAME.getValue(), username);
			this.propertyService.saveProperty(
					PropertiesKeys.PASSWORD.getValue(), password);
			
			// Set the current user
			User currentUser = this.userService.getUser();
			this.application.setLoggedInUser(currentUser);
			
			// Set the language of the user
			if (currentUser.getLanguage().equals("de")) {
				this.application.setCurrentLocale(new Locale("de", "DE"));
			} else {
				this.application.setCurrentLocale(new Locale("en", "EN"));
			}
		}

		this.application.goToSetupHomeFolder();
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

		this.application.goToSetupServer();
	}

	/**
	 * Text input change
	 */
	public ChangeListener<String> textChange = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldVal, String newVal) {

			if (!newVal.trim().equals("")
					&& !passwordField.getText().trim().equals("")) {
				nextButton.setDisable(false);
			} else {
				nextButton.setDisable(true);
				userAccountErrorLabel.setVisible(false);
			}
		}
	};

	/**
	 * Password input change
	 */
	public ChangeListener<String> passwordChange = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldVal, String newVal) {

			if (!newVal.trim().equals("")
					&& !usernameField.getText().trim().equals("")) {
				nextButton.setDisable(false);
			} else {
				nextButton.setDisable(true);
				userAccountErrorLabel.setVisible(false);
			}
		}
	};
}
