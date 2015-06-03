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
	boolean uploadFile(File file);
	
	String getFileByPath(String path);
	
	String getFilesByUser(User currentUser);
	
	boolean deleteFile(String path);
}
