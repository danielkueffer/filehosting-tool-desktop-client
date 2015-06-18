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
	String uploadFile(String url, File file, String fileName, int parent,
			String authToken);

	String createFolder(String url, String folderName, int parent,
			String authToken);

	InputStream getFileByPath(String url, String authToken);

	String getFilesByUser(String url, String authToken);

	String deleteFile(String url, String authToken);
	
	String getDeletedFilesByUser(String url, String authToken);
	
	String updateDeletedFiles(String url, String authToken);
}
