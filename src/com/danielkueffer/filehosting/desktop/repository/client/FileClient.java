package com.danielkueffer.filehosting.desktop.repository.client;

import java.io.File;

import com.danielkueffer.filehosting.desktop.repository.pojos.User;

/**
 * The file client
 * 
 * @author dkueffer
 * 
 */
public interface FileClient {
	boolean uploadFile(File file, String authToken);
	
	String getFileByPath(String path, String authToken);
	
	String getFilesByUser(User currentUser, String authToken);
	
	boolean deleteFile(String path, String authToken);
}
