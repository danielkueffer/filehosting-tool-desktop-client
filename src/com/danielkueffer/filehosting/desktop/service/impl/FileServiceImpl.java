package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.helper.NetworkHelper;
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

	private static final Logger _log = LogManager
			.getLogger(FileServiceImpl.class.getName());

	private static final String FILE_URL = "resource/file";
	private static final String DOWNLOAD_URL = "resource/file/download";
	private static final String ADD_FOLDER_URL = "resource/file/folder/add";
	private static final String FILE_UPLOAD_URL = "resource/file/upload";
	private static final String DELETED_FILES_URL = "resource/file/deleted";

	private FileClient fileClient;
	private PropertyService propertyService;
	private UserService userService;

	private List<Path> filePaths;
	private String homeFolder;
	private JsonArray jsonFileArray;

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
		this.filePaths = new ArrayList<Path>();

		// URL to the file resource
		String fileUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ FILE_URL;

		// The home folder
		this.homeFolder = this.propertyService
				.getProperty(PropertiesKeys.HOME_FOLDER.getValue());

		// Get a list with the files of the current user
		String userFiles = this.fileClient.getFilesByUser(fileUrl,
				this.userService.getAuthToken());

		JsonReader reader = Json.createReader(new StringReader(userFiles));
		this.jsonFileArray = reader.readObject().getJsonArray("files");

		_log.info("Starting synchronization");

		this.deleteFilesOnDisk();
		this.lookupFilesOnServer();
		this.lookupFilesInHomeFolder();

		_log.info("Synchronization complete");
	}

	/**
	 * Delete the files on disk which are deleted on the server
	 */
	private void deleteFilesOnDisk() {
		_log.info("Check for deleted files on server");

		String deletedFilesUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ DELETED_FILES_URL;

		String deletedFiles = this.fileClient.getDeletedFilesByUser(
				deletedFilesUrl, this.userService.getAuthToken());

		JsonReader reader = Json.createReader(new StringReader(deletedFiles));
		JsonArray deletedArray = reader.readArray();

		FileSystem fs = FileSystems.getDefault();

		// Create the JSON Array with the deleted files
		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		StringWriter writer = new StringWriter();
		JsonGenerator gen = factory.createGenerator(writer);

		gen.writeStartArray();
		
		for (int i = 0; i < deletedArray.size(); i++) {
			
			JsonObject jObj = deletedArray.getJsonObject(i);
			String filePath = jObj.getString("path");
			Timestamp lastModified = Timestamp.valueOf(jObj
					.getString("lastModified"));
			int clientDeleted = jObj.getInt("clientDeleted");

			// Check if it's not been deleted before
			if (clientDeleted == 0) {
				Path path = fs.getPath(this.homeFolder).resolve(filePath);

				File file = path.toFile();

				// Delete from disk
				if (file.exists()) {
					// Directory
					if (file.isDirectory()) {
						try {
							FileUtils.deleteDirectory(file);
							_log.info("Folder deleted: " + filePath);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						// File
						if (file.lastModified() == lastModified.getTime()) {
							file.delete();

							_log.info("File deleted: " + filePath);
						}
					}

					// Add the id to the JSON array
					gen.writeStartObject().write("id", jObj.getInt("id"))
							.writeEnd();
				}
			}
		}

		gen.writeEnd().flush();

		this.fileClient.updateDeletedFiles(deletedFilesUrl, writer.toString(),
				this.userService.getAuthToken());
	}

	/**
	 * Get the files form the server and save them in the download folder if not
	 * existing
	 */
	private void lookupFilesOnServer() {

		// URL to the download file resource
		String downloadUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ DOWNLOAD_URL;

		FileSystem fs = FileSystems.getDefault();

		// Loop over the files array
		for (int i = 0; i < this.jsonFileArray.size(); i++) {
			JsonObject jObj = this.jsonFileArray.getJsonObject(i);
			String filePath = jObj.getString("path");
			String type = jObj.getString("type");
			String lastModified = jObj.getString("lastModified");

			Timestamp lastModifiedStamp = Timestamp.valueOf(lastModified);

			Path path = fs.getPath(this.homeFolder).resolve(filePath);
			this.filePaths.add(path);

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

						// Set the file last modified
						file.setLastModified(lastModifiedStamp.getTime());

						is.close();

						_log.info("File added: " + filePath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	/**
	 * Get the files from the home folder and check if they are existing on the
	 * server. Upload a new file.
	 * 
	 * @param jArr
	 */
	private void lookupFilesInHomeFolder() {
		// URL to add folder resource
		String folderAddUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ ADD_FOLDER_URL;

		// URL to upload a file
		String fileUploadUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ FILE_UPLOAD_URL;

		this.walkDir(this.homeFolder, folderAddUrl, fileUploadUrl);
	}

	/**
	 * Walk trough the home folder recursively
	 * 
	 * @param path
	 */
	private void walkDir(String path, String folderAddUrl, String fileUploadUrl) {
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				// Directory, walk further
				walkDir(f.getAbsolutePath(), folderAddUrl, fileUploadUrl);

				// Check if the directory is existing on the server
				if (!this.filePaths.contains(f.getAbsoluteFile().toPath())) {
					String parentPath = NetworkHelper.getParentPath(
							f.getParent(), this.homeFolder);

					int parent = this.getParentIdFromPath(parentPath);

					// Create the directory
					this.fileClient.createFolder(folderAddUrl, f.getName(),
							parent, this.userService.getAuthToken());

					_log.info("Folder uploaded: " + f.getName());
				}
			} else {
				// Check if the file is existing on the server
				if (!this.filePaths.contains(f.getAbsoluteFile().toPath())) {
					String parentPath = NetworkHelper.getParentPath(
							f.getParent(), this.homeFolder);

					int parent = this.getParentIdFromPath(parentPath);

					// Upload the file
					this.fileClient.uploadFile(fileUploadUrl, f, f.getName(),
							parent, this.userService.getAuthToken());

					_log.info("File uploaded: " + f.getName());
				}
			}
		}
	}

	/**
	 * Get the parent id from path
	 * 
	 * @param path
	 * @return
	 */
	private int getParentIdFromPath(String path) {
		for (int i = 0; i < this.jsonFileArray.size(); i++) {
			JsonObject jObj = this.jsonFileArray.getJsonObject(i);
			String filePath = jObj.getString("path");

			if (filePath.equals(path)) {
				return jObj.getInt("id");
			}
		}

		return 0;
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
