package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
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

	private static final String FILE_URL = "resource/file";
	private static final String DOWNLOAD_URL = "resource/file/download";

	private FileClient fileClient;
	private PropertyService propertyService;
	private UserService userService;

	public FileServiceImpl(FileClient fileClient,
			PropertyService propertyService, UserService userService) {
		this.fileClient = fileClient;
		this.propertyService = propertyService;
		this.userService = userService;
	}

	/**
	 * Start the synchronization
	 */
	@Override
	public void startSynchronization() {
		// URL to the file resource
		String fileUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ FILE_URL;

		// URL to the download file resource
		String downloadUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ DOWNLOAD_URL;

		// The home folder
		String homeFolder = this.propertyService
				.getProperty(PropertiesKeys.HOME_FOLDER.getValue());

		// Get a list with the files of the current user
		String userFiles = this.fileClient.getFilesByUser(fileUrl,
				this.userService.getAuthToken());

		JsonReader reader = Json.createReader(new StringReader(userFiles));
		JsonArray jArr = reader.readObject().getJsonArray("files");

		// Loop over the files array
		for (int i = 0; i < jArr.size(); i++) {
			JsonObject jObj = jArr.getJsonObject(i);
			String filePath = jObj.getString("path");
			String type = jObj.getString("type");

			FileSystem fs = FileSystems.getDefault();
			Path path = fs.getPath(homeFolder).resolve(filePath);

			File file = path.toFile();

			// If the file doesn't existing download the file and create it
			if (!file.exists()) {
				if (type.equals("folder")) {
					file.mkdirs();
				} else {
					try {
						// Get the file
						InputStream is = this.fileClient.getFileByPath(
								downloadUrl + "/" + filePath,
								this.userService.getAuthToken());

						// Write the file
						Files.copy(is, path);

						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
