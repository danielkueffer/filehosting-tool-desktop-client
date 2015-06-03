package com.danielkueffer.filehosting.desktop.service.impl;

import com.danielkueffer.filehosting.desktop.repository.client.FileClient;
import com.danielkueffer.filehosting.desktop.service.FileService;
import com.danielkueffer.filehosting.desktop.service.PropertyService;
import com.danielkueffer.filehosting.desktop.service.UserService;

/**
 * The file service implementation
 * 
 * @author dkueffer
 * 
 */
public class FileServiceImpl implements FileService {

	private FileClient fileClient;
	private PropertyService propertyService;
	private UserService userService;

	public FileServiceImpl(FileClient fileClient,
			PropertyService propertyService, UserService userService) {
		this.fileClient = fileClient;
		this.propertyService = propertyService;
		this.userService = userService;
	}

	@Override
	public void startSynchronization() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopSynchronization() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getActivities() {
		// TODO Auto-generated method stub
		return null;
	}

}
