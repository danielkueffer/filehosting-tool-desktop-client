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
	public boolean uploadFile(File file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFileByPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilesByUser(User currentUser) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFile(String path) {
		// TODO Auto-generated method stub
		return false;
	}
}
