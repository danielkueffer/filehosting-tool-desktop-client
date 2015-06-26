package com.danielkueffer.filehosting.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import com.danielkueffer.filehosting.desktop.Main;
import com.danielkueffer.filehosting.desktop.repository.pojos.Activity;

/**
 * The activity controller
 * 
 * @author dkueffer
 * 
 */
public class ActivityController extends Parent implements Initializable {

	@FXML
	private Label activityTitle;

	@FXML
	private Button closeButton;

	@FXML
	private TableView<Activity> activityTable;

	@FXML
	private TableColumn<Activity, Activity> dateCol;

	@FXML
	private TableColumn<Activity, String> fileCol;

	@FXML
	private TableColumn<Activity, String> folderCol;

	@FXML
	private TableColumn<Activity, String> actionCol;

	private ResourceBundle bundle;
	private Main application;

	/**
	 * Initialize the controller
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.activityTitle.setText(this.bundle.getString("settingsActivities"));
		this.closeButton.setText(this.bundle.getString("settingsClose"));

		this.dateCol.setText(this.bundle.getString("activityDate"));
		this.fileCol.setText(this.bundle.getString("activityFile"));
		this.folderCol.setText(this.bundle.getString("activityFolder"));
		this.actionCol.setText(this.bundle.getString("activityAction"));

		this.dateCol
				.setCellFactory(new Callback<TableColumn<Activity, Activity>, TableCell<Activity, Activity>>() {
					@Override
					public TableCell<Activity, Activity> call(
							TableColumn<Activity, Activity> param) {
						return new TableCell<Activity, Activity>() {
							@Override
							public void updateItem(Activity item, boolean empty) {
								super.updateItem(item, empty);
								if (!isEmpty()) {
									setText(item.getDate().toString());
								}
							}
						};
					}
				});

		this.fileCol
				.setCellValueFactory(new PropertyValueFactory<Activity, String>(
						"file"));
		this.folderCol
				.setCellValueFactory(new PropertyValueFactory<Activity, String>(
						"homeFolder"));
		this.actionCol
				.setCellValueFactory(new PropertyValueFactory<Activity, String>(
						"action"));
	}

	/**
	 * Set the application
	 * 
	 * @param application
	 * @param settingsController
	 */
	public void setApp(Main application) {
		this.application = application;
	}

	/**
	 * Update the activity table
	 * 
	 * @param activityList
	 */
	public void updateActivityTable(ObservableList<Activity> activityList) {
		this.activityTable.setItems(activityList);
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
}
