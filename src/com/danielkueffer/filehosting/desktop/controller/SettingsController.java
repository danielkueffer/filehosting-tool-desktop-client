package com.danielkueffer.filehosting.desktop.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import com.danielkueffer.filehosting.desktop.Main;

/**
 * The settings controller
 * 
 * @author dkueffer
 * 
 */
public class SettingsController extends AnchorPane implements Initializable {

	private static final String USER_ACCOUNT_TAB = "User Account";
	private static final String ACTIVITY_TAB = "Activities";

	@FXML
	private TabPane settingsTabPane;

	private Main application;

	/**
	 * Set the application
	 * 
	 * @param application
	 */
	public void setApp(Main application) {
		this.application = application;

		// Add Tab ChangeListener
		this.settingsTabPane.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Tab>() {
					public void changed(
							ObservableValue<? extends Tab> observable,
							Tab oldValue, Tab newValue) {
						if (newValue.getContent() == null) {
							if (newValue.getText().equals(USER_ACCOUNT_TAB)) {
								goToUserAccount();
							} else if (newValue.getText().equals(ACTIVITY_TAB)) {
								goToActivities();
							} else {
								goToNetwork();
							}
						} else {
							newValue.getContent();
						}
					}
				});

		this.settingsTabPane.getSelectionModel().selectFirst();
		this.goToUserAccount();
	}

	/**
	 * Show user account view
	 */
	public void goToUserAccount() {
		Tab tab = this.settingsTabPane.getSelectionModel().getSelectedItem();

		try {
			UserAccountController userAccountController = (UserAccountController) this
					.replaceTabContent("view/UserAccount.fxml", tab);
			userAccountController.setApp(this.application, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show activities view
	 */
	public void goToActivities() {
		Tab tab = this.settingsTabPane.getSelectionModel().getSelectedItem();

		try {
			ActivityController activityController = (ActivityController) this
					.replaceTabContent("view/Activity.fxml", tab);
			activityController.setApp(this.application, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show network view
	 */
	public void goToNetwork() {
		Tab tab = this.settingsTabPane.getSelectionModel().getSelectedItem();

		try {
			NetworkController networkController = (NetworkController) this
					.replaceTabContent("view/Network.fxml", tab);
			networkController.setApp(this.application, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Change tab template
	 * 
	 * @param fxml
	 * @return
	 * @throws Exception
	 */
	private Initializable replaceTabContent(String fxml, Tab tab)
			throws Exception {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = Main.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(Main.class.getResource(fxml));

		Parent root;

		try {
			root = (Parent) loader.load(in);
		} finally {
			in.close();
		}

		tab.setContent(root);
		return (Initializable) loader.getController();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
}
