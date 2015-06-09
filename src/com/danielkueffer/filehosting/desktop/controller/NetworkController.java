package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import com.danielkueffer.filehosting.desktop.Main;

/**
 * The network controller
 * 
 * @author dkueffer
 * 
 */
public class NetworkController extends Parent implements Initializable {

	@FXML
	private Label networkTitle;

	@FXML
	private Label proxySettingsLabel;

	@FXML
	private Label proxyServerLabel;

	@FXML
	private Label proxyUsernameLabel;

	@FXML
	private Label proxyPasswordLabel;

	@FXML
	private Label separatorLabel;

	@FXML
	private RadioButton noProxyRadio;

	@FXML
	private RadioButton manualProxyRadio;

	@FXML
	private CheckBox requiresPasswordCheckBox;

	@FXML
	private TextField proxyServerField;

	@FXML
	private TextField proxyPortField;

	@FXML
	private TextField proxyUsernameField;

	@FXML
	private PasswordField proxyPasswordField;

	@FXML
	private Button saveButton;

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
		this.networkTitle.setText(this.bundle.getString("settingsNetwork"));
		this.proxySettingsLabel.setText(this.bundle.getString("proxySettings"));
		this.proxyServerLabel.setText(this.bundle.getString("proxyServer"));
		this.proxyUsernameLabel.setText(this.bundle.getString("proxyUsername"));
		this.proxyPasswordLabel.setText(this.bundle.getString("proxyPassword"));

		this.noProxyRadio.setText(this.bundle.getString("proxyNoProxy"));
		this.noProxyRadio.setUserData(false);

		this.manualProxyRadio.setText(this.bundle.getString("proxyManual"));
		this.manualProxyRadio.setUserData(true);

		this.saveButton.setText(this.bundle.getString("settingsSave"));
		this.closeButton.setText(this.bundle.getString("settingsClose"));

		this.requiresPasswordCheckBox.setText(this.bundle
				.getString("proxyPasswordRequired"));

		// Add a change listener to the radio buttons
		final ToggleGroup group = this.noProxyRadio.toggleGroupProperty().get();
		group.selectedToggleProperty().addListener(
				new ChangeListener<Toggle>() {

					@Override
					public void changed(ObservableValue<? extends Toggle> ob,
							Toggle oldToggle, Toggle newToggle) {

						boolean hasProxy = (boolean) group.getSelectedToggle()
								.getUserData();

						if (hasProxy) {
							// Show the PROXY server fields
							proxyServerLabel.getStyleClass()
									.remove("grey-text");
							separatorLabel.getStyleClass().remove("grey-text");
							proxyServerField.setDisable(false);
							proxyPortField.setDisable(false);
							requiresPasswordCheckBox.setDisable(false);

							if (requiresPasswordCheckBox.selectedProperty()
									.get()) {
								showProxyAuthentication();
							}
						} else {
							// Hide the PROXY server fields
							proxyServerLabel.getStyleClass().add("grey-text");
							separatorLabel.getStyleClass().add("grey-text");
							proxyServerField.setDisable(true);
							proxyPortField.setDisable(true);
							requiresPasswordCheckBox.setDisable(true);
							hideProxyAuthentication();
						}
					}

				});

		// Add a change listener to the checkBox
		this.requiresPasswordCheckBox.selectedProperty().addListener(
				this.checkBoxChange);
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
	 * Show the PROXY authentication fields
	 */
	private void showProxyAuthentication() {
		this.proxyUsernameLabel.getStyleClass().remove("grey-text");
		this.proxyPasswordLabel.getStyleClass().remove("grey-text");
		this.proxyUsernameField.setDisable(false);
		this.proxyPasswordField.setDisable(false);
	}

	/**
	 * Hide the PROXY authentication fields
	 */
	private void hideProxyAuthentication() {
		this.proxyUsernameLabel.getStyleClass().add("grey-text");
		this.proxyPasswordLabel.getStyleClass().add("grey-text");
		this.proxyUsernameField.setDisable(true);
		this.proxyPasswordField.setDisable(true);
	}

	/**
	 * Save the PROXY settings
	 * 
	 * @param evt
	 */
	public void saveAction(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		System.out.println("save");
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

		System.out.println("close");
	}

	/**
	 * CheckBox change
	 */
	public ChangeListener<Boolean> checkBoxChange = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> ov,
				Boolean oldVal, Boolean newVal) {

			if (newVal) {
				showProxyAuthentication();
			} else {
				hideProxyAuthentication();
			}
		}
	};
}
