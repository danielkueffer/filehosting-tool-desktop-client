package com.danielkueffer.filehosting.desktop.service;

import com.danielkueffer.filehosting.desktop.repository.pojos.Activity;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;

import javafx.collections.ObservableList;

/**
 * The file service
 * 
 * @author dkueffer
 * 
 */
public interface FileService {
	void startSynchronization(User loggedInUser);

	ObservableList<Activity> getActivities();

	boolean isSynchronizationComplete();
}
