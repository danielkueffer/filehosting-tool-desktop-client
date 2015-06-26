package com.danielkueffer.filehosting.desktop.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import com.danielkueffer.filehosting.desktop.Main;
import com.danielkueffer.filehosting.desktop.enums.TabKeys;
import com.danielkueffer.filehosting.desktop.repository.pojos.Activity;
import com.danielkueffer.filehosting.desktop.service.PropertyService;

/**
 * The settings controller
 * 
 * @author dkueffer
 * 
 */
public class SettingsController extends AnchorPane implements Initializable {

	@FXML
	private TabPane settingsTabPane;

	@FXML
	private Tab userAccountTab;

	@FXML
	private Tab activitiesTab;

	@FXML
	private Tab networkTab;

	private ResourceBundle bundle;
	private Main application;
	private PropertyService propertyService;
	private ActivityController activityController;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.userAccountTab.setText(this.bundle
				.getString("settingsUserAccount"));
		this.activitiesTab.setText(this.bundle.getString("settingsActivities"));
		this.networkTab.setText(this.bundle.getString("settingsNetwork"));

		// Add Tab ChangeListener
		this.settingsTabPane.getSelectionModel().selectedItemProperty()
				.addListener(this.tabListener);

		// Select first tab
		this.settingsTabPane.getSelectionModel().selectFirst();
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
	 * Set the property service
	 * 
	 * @param propertyService
	 */
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	/**
	 * Show user account view
	 */
	public UserAccountController goToUserAccount() {
		Tab tab = this.settingsTabPane.getSelectionModel().getSelectedItem();

		UserAccountController userAccountController = null;

		try {
			userAccountController = (UserAccountController) this
					.replaceTabContent("view/UserAccount.fxml", tab);
			userAccountController.setApp(this.application);
			userAccountController.setPropertyService(this.propertyService);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userAccountController;
	}

	/**
	 * Show activities view
	 */
	public ActivityController goToActivities() {
		Tab tab = this.settingsTabPane.getSelectionModel().getSelectedItem();

		ActivityController activityController = null;

		try {
			activityController = (ActivityController) this.replaceTabContent(
					"view/Activity.fxml", tab);
			activityController.setApp(this.application);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return activityController;
	}

	/**
	 * Show network view
	 */
	public void goToNetwork() {
		Tab tab = this.settingsTabPane.getSelectionModel().getSelectedItem();

		try {
			NetworkController networkController = (NetworkController) this
					.replaceTabContent("view/Network.fxml", tab);
			networkController.setApp(this.application);
			networkController.setPropertyService(this.propertyService);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Select a tab
	 * 
	 * @param idx
	 */
	public void selectIndexTab(int idx) {
		this.settingsTabPane.getSelectionModel().select(idx);
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
		loader.setResources(ResourceBundle.getBundle(Main.PATH
				+ "resources/i18n/messages",
				this.application.getCurrentLocale()));

		Parent root;

		try {
			root = (Parent) loader.load(in);
		} finally {
			in.close();
		}

		tab.setContent(root);
		return (Initializable) loader.getController();
	}

	/**
	 * ChangeListener to change the view of the tab
	 */
	private ChangeListener<Tab> tabListener = new ChangeListener<Tab>() {
		@Override
		public void changed(ObservableValue<? extends Tab> observable,
				Tab oldValue, Tab newValue) {
			if (newValue.getContent() == null) {
				if (newValue.getText().equals(
						bundle.getString("settingsUserAccount"))) {

					application.goToSettings(TabKeys.USER);
				} else if (newValue.getText().equals(
						bundle.getString("settingsActivities"))) {

					activityController = goToActivities();
				} else {
					goToNetwork();
				}
			} else {
				newValue.getContent();
			}
		}
	};

	/**
	 * Update the activity table
	 * 
	 * @param activityList
	 */
	public void updateActivityTable(ObservableList<Activity> activityList) {
		if (activityController != null) {
			this.activityController.updateActivityTable(activityList);
		}
	}
}
