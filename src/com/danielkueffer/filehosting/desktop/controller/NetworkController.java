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
import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.service.PropertyService;

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

	@FXML
	private Button editAccountButton;

	private ResourceBundle bundle;
	private Main application;
	private SettingsController settingsController;
	private PropertyService propertyService;

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

		this.editAccountButton.setText(this.bundle
				.getString("settingsEditAccount"));

		this.requiresPasswordCheckBox.setText(this.bundle
				.getString("proxyPasswordRequired"));

		// Add a change listener to the radio buttons
		final ToggleGroup group = new ToggleGroup();
		this.noProxyRadio.setToggleGroup(group);
		this.manualProxyRadio.setToggleGroup(group);

		group.selectedToggleProperty().addListener(
				new ChangeListener<Toggle>() {

					@Override
					public void changed(ObservableValue<? extends Toggle> ob,
							Toggle oldToggle, Toggle newToggle) {

						if (group.getSelectedToggle() != null) {
							boolean hasProxy = (boolean) group
									.getSelectedToggle().getUserData();

							if (hasProxy) {
								// Show the PROXY server fields
								proxyServerLabel.getStyleClass().remove(
										"grey-text");
								separatorLabel.getStyleClass().remove(
										"grey-text");
								proxyServerField.setDisable(false);
								proxyPortField.setDisable(false);
								requiresPasswordCheckBox.setDisable(false);

								if (requiresPasswordCheckBox.selectedProperty()
										.get()) {
									showProxyAuthentication();
								}
							} else {
								// Hide the PROXY server fields
								proxyServerLabel.getStyleClass().add(
										"grey-text");
								separatorLabel.getStyleClass().add("grey-text");
								proxyServerField.setDisable(true);
								proxyPortField.setDisable(true);
								requiresPasswordCheckBox.setDisable(true);
								hideProxyAuthentication();
							}
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
		
		// Show the edit account button only if no user is logged in
		if (this.application.getLoggedInUser() == null) {
			this.editAccountButton.setVisible(true);
		}
	}

	/**
	 * Set the property service
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;

		boolean proxyEnabled = new Boolean(
				this.propertyService.getProperty(PropertiesKeys.PROXY_ENABLED
						.getValue()));

		// Enable or disable radio buttons
		if (proxyEnabled) {
			this.noProxyRadio.setSelected(false);
			this.manualProxyRadio.setSelected(true);
		} else {
			this.noProxyRadio.setSelected(true);
			this.manualProxyRadio.setSelected(false);
		}

		boolean proxyAuthentication = new Boolean(
				this.propertyService
						.getProperty(PropertiesKeys.PROXY_AUTHENTICATION
								.getValue()));

		// Enable PROXY authentication checkBox
		if (proxyAuthentication) {
			this.requiresPasswordCheckBox.setSelected(true);
		}

		// Get and set the field values
		String proxyServer = this.propertyService
				.getProperty(PropertiesKeys.PROXY_SERVER.getValue());
		String proxyPort = this.propertyService
				.getProperty(PropertiesKeys.PROXY_PORT.getValue());
		String proxyUsername = this.propertyService
				.getProperty(PropertiesKeys.PROXY_USERNAME.getValue());
		String proxyPassword = this.propertyService
				.getProperty(PropertiesKeys.PROXY_PASSWORD.getValue());

		if (proxyServer != null) {
			this.proxyServerField.setText(proxyServer);
		}

		if (proxyPort != null) {
			this.proxyPortField.setText(proxyPort);
		}

		if (proxyUsername != null) {
			this.proxyUsernameField.setText(proxyUsername);
		}

		if (proxyPassword != null) {
			this.proxyPasswordField.setText(proxyPassword);
		}
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

		boolean isManual = (boolean) this.noProxyRadio.toggleGroupProperty()
				.get().getSelectedToggle().getUserData();

		boolean proxyAuth = this.requiresPasswordCheckBox.selectedProperty()
				.get();

		// Save the PROXY settings
		if (isManual) {
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_ENABLED.getValue(), "true");

			String proxyServer = this.proxyServerField.getText();
			String proxyPort = this.proxyPortField.getText();
			String proxyUsername = this.proxyUsernameField.getText();
			String proxyPassword = this.proxyPasswordField.getText();

			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_SERVER.getValue(), proxyServer);
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_PORT.getValue(), proxyPort);
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_AUTHENTICATION.getValue(), proxyAuth
							+ "");
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_USERNAME.getValue(), proxyUsername);
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_PASSWORD.getValue(), proxyPassword);
		} else {
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_ENABLED.getValue(), "false");
			this.propertyService.saveProperty(
					PropertiesKeys.PROXY_AUTHENTICATION.getValue(), proxyAuth
							+ "");
		}
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

	/**
	 * Edit setup action
	 * 
	 * @param evt
	 */
	public void editSetup(ActionEvent evt) {
		if (this.application == null) {
			return;
		}

		this.application.goToSetupServer();
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
