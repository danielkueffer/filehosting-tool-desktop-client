package com.danielkueffer.filehosting.desktop.service;

import com.danielkueffer.filehosting.desktop.repository.pojos.Activity;

import javafx.collections.ObservableList;

/**
 * The file service
 * 
 * @author dkueffer
 * 
 */
public interface FileService {
	void startSynchronization();

	ObservableList<Activity> getActivities();
}
