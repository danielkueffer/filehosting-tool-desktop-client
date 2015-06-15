package com.danielkueffer.filehosting.desktop.repository.client;

import java.io.File;
import java.io.InputStream;

/**
 * The file client
 * 
 * @author dkueffer
 * 
 */
public interface FileClient {
	boolean uploadFile(File file, String authToken);
	
	InputStream getFileByPath(String url, String authToken);
	
	String getFilesByUser(String url, String authToken);
	
	boolean deleteFile(String path, String authToken);
}
