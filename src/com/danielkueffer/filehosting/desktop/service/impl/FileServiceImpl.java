package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.helper.NetworkHelper;
import com.danielkueffer.filehosting.desktop.repository.client.FileClient;
import com.danielkueffer.filehosting.desktop.repository.pojos.Activity;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;
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
	private static final String FILE_DELETE_URL = "resource/file/client";

	private static final String CACHE_DIR = "cache";
	private static final String CACHE_FILE = "file-cache.txt";

	private FileClient fileClient;
	private PropertyService propertyService;
	private UserService userService;

	private String appConfigPath;

	private List<Path> filePaths;
	private List<String> deletedOnDiskPaths;
	private List<Activity> activityList;
	private String homeFolder;
	private String fileUrl;
	private String fileUploadUrl;
	private String cachePath;
	private JsonArray jsonFileArray;
	private User user;

	public FileServiceImpl(FileClient fileClient,
			PropertyService propertyService, UserService userService,
			String appConfigPath) {
		this.fileClient = fileClient;
		this.propertyService = propertyService;
		this.userService = userService;
		this.activityList = new ArrayList<Activity>();

		this.appConfigPath = appConfigPath;
	}

	/**
	 * Start the synchronization
	 * 
	 * Step 1. Check for deleted files on the server - check the files_deleted
	 * table on the server for deleted files (user, clientDeleted = 0) - only
	 * delete if the last modify date is same - set deleted files in the
	 * files_deleted table as clientDeleted = 1
	 * 
	 * Step 2. Check for deleted files on disk - check if the file-cache.txt
	 * file exits - check the file-cache.txt file in the desktop application for
	 * missing files on disk - delete missing files on the server
	 * 
	 * Step 3. Download files to the client - download new files from server -
	 * if a file is already existing -> check last modified date and download or
	 * upload the file - clear file-cache.txt file - write the files to the
	 * file-cache.txt file in desktop application
	 * 
	 * Step 4. Check for new files in the home folder - upload new files from
	 * disk - write the files to file-cache.txt file in desktop application
	 */
	@Override
	public void startSynchronization() {
		this.filePaths = new ArrayList<Path>();
		this.deletedOnDiskPaths = new ArrayList<String>();

		User user = this.userService.getUser();

		// Set the initial user
		if (this.user == null) {
			this.user = user;
		}

		// Check if the user has changed and flush the activity list
		if (this.user.getId() != user.getId()) {
			this.activityList = new ArrayList<Activity>();
		}

		this.user = user;

		this.cachePath = this.appConfigPath + "/" + CACHE_DIR + "/"
				+ user.getUsername() + "-" + CACHE_FILE;

		// URL to the file resource
		this.fileUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ FILE_URL;

		// The home folder
		this.homeFolder = this.propertyService
				.getProperty(PropertiesKeys.HOME_FOLDER.getValue());

		// URL to upload a file
		this.fileUploadUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ FILE_UPLOAD_URL;

		// Step 1 - check for deleted files on the server
		this.deleteFilesOnDisk();

		// Step 2 - check for deleted files on disk
		this.deleteFilesOnServer();

		// Get a list with the files of the current user
		String userFiles = this.fileClient.getFilesByUser(fileUrl,
				this.userService.getAuthToken());

		JsonReader reader = Json.createReader(new StringReader(userFiles));
		this.jsonFileArray = reader.readObject().getJsonArray("files");

		// Step 3 - download files to the client
		this.lookupFilesOnServer();

		// Step 4 - check for new files in the home folder
		this.lookupFilesInHomeFolder();
	}

	/**
	 * Check for deleted files on the server. Delete the files on the client
	 * which are deleted on the server
	 */
	private void deleteFilesOnDisk() {

		String deletedFilesUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ DELETED_FILES_URL;

		String deletedFiles = this.fileClient.getDeletedFilesByUser(
				deletedFilesUrl, this.userService.getAuthToken());

		JsonReader reader = Json.createReader(new StringReader(deletedFiles));
		JsonArray deletedArray = reader.readArray();

		FileSystem fs = FileSystems.getDefault();

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
				String fileName = file.getName();

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

					// Create activity
					Activity activity = new Activity();
					activity.setDate(new Date());
					activity.setAction("delete");
					activity.setFile(fileName);
					activity.setHomeFolder(this.homeFolder);

					this.activityList.add(activity);

					// Add the path to a list to prevent it from deletion twice
					// in the next step
					this.deletedOnDiskPaths.add(path.toString());
				}
			}
		}

		this.fileClient.updateDeletedFiles(deletedFilesUrl,
				this.userService.getAuthToken());
	}

	/**
	 * Check for deleted files on disk. Delete files on the server which are
	 * deleted on the client
	 */
	private void deleteFilesOnServer() {

		File cacheFile = new File(this.cachePath);

		String fileDeleteUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ FILE_DELETE_URL;

		// Check if the cache file exists
		if (cacheFile.exists()) {
			String line;

			try {
				InputStream is = new FileInputStream(this.cachePath);
				InputStreamReader isr = new InputStreamReader(is,
						Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);

				while ((line = br.readLine()) != null) {
					File file = new File(line);

					// Check if the file was not previously deleted from server
					if (!this.deletedOnDiskPaths.contains(line)) {

						// File is missing delete it on the server
						if (!file.exists()) {
							String filePath = NetworkHelper.getRelativePath(
									line, this.homeFolder);

							fileDeleteUrl = fileDeleteUrl + "/" + filePath;

							this.fileClient.deleteFile(fileDeleteUrl,
									this.userService.getAuthToken());

							// Create activity
							Activity activity = new Activity();
							activity.setDate(new Date());
							activity.setAction("delete");
							activity.setFile(filePath);
							activity.setHomeFolder(this.homeFolder);

							this.activityList.add(activity);

							_log.info("File deleted on server: " + filePath);
						}
					}
				}

				br.close();
				isr.close();
				is.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Download files which are not existing to the client. Upload or download
	 * modified files
	 */
	private void lookupFilesOnServer() {

		// Create the cache file for the files
		this.createCacheDir();

		try {
			OutputStream output = new FileOutputStream(this.cachePath);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output,
					"UTF-8"), true);

			FileSystem fs = FileSystems.getDefault();

			// Loop over the files array
			for (int i = 0; i < this.jsonFileArray.size(); i++) {
				JsonObject jObj = this.jsonFileArray.getJsonObject(i);
				String filePath = jObj.getString("path");
				String type = jObj.getString("type");
				String lastModified = jObj.getString("lastModified");
				int parent = jObj.getInt("parent");

				Timestamp lastModifiedStamp = Timestamp.valueOf(lastModified);

				Path path = fs.getPath(this.homeFolder).resolve(filePath);
				this.filePaths.add(path);

				File file = path.toFile();

				// If the file doesn't existing download the file and create it
				if (!file.exists()) {
					if (type.equals("folder")) {
						file.mkdirs();

						_log.info("Folder added: " + filePath);
					} else {

						// Download the file
						this.downloadFile(path, filePath);

						// Set the file last modified
						file.setLastModified(lastModifiedStamp.getTime());

						_log.info("File added: " + filePath);
					}

					// Create activity
					Activity activity = new Activity();
					activity.setDate(new Date());
					activity.setAction("download");
					activity.setFile(filePath);
					activity.setHomeFolder(this.homeFolder);

					this.activityList.add(activity);
				} else {

					// Update a file if the last modified date has changed
					if (!type.equals("folder")) {

						// File on disk is newer
						if (file.lastModified() > lastModifiedStamp.getTime()) {

							// Upload the file
							this.fileClient.uploadFile(fileUploadUrl, file,
									file.getName(), parent,
									this.userService.getAuthToken());

							_log.info("File updated on server:" + filePath);

							// Create activity
							Activity activity = new Activity();
							activity.setDate(new Date());
							activity.setAction("updated");
							activity.setFile(file.getName());
							activity.setHomeFolder(this.homeFolder);

							this.activityList.add(activity);

							// File on disk is older
						} else if (file.lastModified() < lastModifiedStamp
								.getTime()) {

							// Download the file
							this.downloadFile(path, filePath);

							// Set the file last modified
							file.setLastModified(lastModifiedStamp.getTime());

							// Create activity
							Activity activity = new Activity();
							activity.setDate(new Date());
							activity.setAction("updated");
							activity.setFile(file.getName());
							activity.setHomeFolder(this.homeFolder);

							this.activityList.add(activity);

							_log.info("File updated on disk: " + filePath);
						}
					}
				}

				// Print the path to the cache file
				writer.println(path.toString());
			}

			writer.close();
			output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Check for new files in the home folder. Upload new files to the server.
	 */
	private void lookupFilesInHomeFolder() {

		// URL to add folder resource
		String folderAddUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ ADD_FOLDER_URL;

		try {
			OutputStream output = new FileOutputStream(this.cachePath, true);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output,
					"UTF-8"), true);

			this.walkDir(this.homeFolder, folderAddUrl, writer);

			writer.close();
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Walk trough the home folder recursively
	 * 
	 * @param path
	 */
	private void walkDir(String path, String folderAddUrl, PrintWriter writer) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null) {
			return;
		}

		for (File f : list) {
			if (!f.getName().startsWith("~")) {
				if (!f.isDirectory()) {
					// Check if the file is existing on the server
					if (!this.filePaths.contains(f.getAbsoluteFile().toPath())) {
						String parentPath = NetworkHelper.getRelativePath(
								f.getParent(), this.homeFolder);

						// Get the parent id
						int parent = this.getParentIdFromPath(parentPath);

						// Upload the file
						this.fileClient.uploadFile(fileUploadUrl, f,
								f.getName(), parent,
								this.userService.getAuthToken());

						// Write the path to the cache file
						writer.println(f.getAbsolutePath());

						// Create activity
						Activity activity = new Activity();
						activity.setDate(new Date());
						activity.setAction("upload");
						activity.setFile(f.getName());
						activity.setHomeFolder(this.homeFolder);

						this.activityList.add(activity);

						_log.info("File uploaded: " + f.getAbsoluteFile());
					}
				} else {
					// Check if the directory is existing on the server
					if (!this.filePaths.contains(f.getAbsoluteFile().toPath())) {
						String parentPath = NetworkHelper.getRelativePath(
								f.getParent(), this.homeFolder);

						// Get the parent id
						int parent = this.getParentIdFromPath(parentPath);

						// Create the directory
						this.fileClient.createFolder(folderAddUrl, f.getName(),
								parent, this.userService.getAuthToken());

						// Write the path to the cache file
						writer.println(f.getAbsolutePath());

						// Create activity
						Activity activity = new Activity();
						activity.setDate(new Date());
						activity.setAction("upload");
						activity.setFile(f.getName());
						activity.setHomeFolder(this.homeFolder);

						this.activityList.add(activity);

						_log.info("Folder uploaded: " + f.getAbsolutePath());
					}

					// Directory, walk further
					walkDir(f.getAbsolutePath(), folderAddUrl, writer);
				}
			}
		}
	}

	/**
	 * Download a file
	 * 
	 * @param path
	 * @param filePath
	 */
	private void downloadFile(Path systemPath, String filePath) {

		// URL to the download file resource
		String downloadUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ DOWNLOAD_URL;

		try {
			// Get the file
			InputStream is = this.fileClient.getFileByPath(downloadUrl + "/"
					+ filePath, this.userService.getAuthToken());

			// Write the file
			Files.copy(is, systemPath);

			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the parent id from path
	 * 
	 * @param path
	 * @return
	 */
	private int getParentIdFromPath(String path) {

		// Get a list with the files of the current user
		String userFiles = this.fileClient.getFilesByUser(this.fileUrl,
				this.userService.getAuthToken());

		JsonReader reader = Json.createReader(new StringReader(userFiles));
		JsonArray jArray = reader.readObject().getJsonArray("files");

		for (int i = 0; i < jArray.size(); i++) {
			JsonObject jObj = jArray.getJsonObject(i);
			String filePath = jObj.getString("path");

			if (filePath.equals(path)) {
				return jObj.getInt("id");
			}
		}

		return 0;
	}

	/**
	 * Create the cache directory if it's not existing
	 */
	private void createCacheDir() {
		// Check for the cache directory
		File dir = new File(this.appConfigPath + "/" + CACHE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * Get the activity list
	 */
	@Override
	public ObservableList<Activity> getActivities() {
		return FXCollections.observableArrayList(this.activityList);
	}
}
