package com.danielkueffer.filehosting.desktop.service;

/**
 * The file service
 * 
 * @author dkueffer
 * 
 */
public interface FileService {
	void startSynchronization();
	
	void stopSynchronization();
	
	String getActivities();
}
