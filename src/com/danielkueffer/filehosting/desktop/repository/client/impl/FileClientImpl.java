package com.danielkueffer.filehosting.desktop.repository.client.impl;

import java.io.File;

import com.danielkueffer.filehosting.desktop.repository.client.FileClient;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;

/**
 * File client implementation
 * 
 * @author dkueffer
 * 
 */
public class FileClientImpl implements FileClient {

	@Override
	public boolean uploadFile(File file, String authToken) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFileByPath(String path, String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilesByUser(User currentUser, String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFile(String path, String authToken) {
		// TODO Auto-generated method stub
		return false;
	}
}
