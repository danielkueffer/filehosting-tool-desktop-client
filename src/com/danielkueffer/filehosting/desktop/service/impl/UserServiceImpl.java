package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.File;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.repository.client.UserClient;
import com.danielkueffer.filehosting.desktop.repository.pojos.User;
import com.danielkueffer.filehosting.desktop.service.PropertyService;
import com.danielkueffer.filehosting.desktop.service.UserService;

/**
 * The user service implementation
 * 
 * @author dkueffer
 * 
 */
public class UserServiceImpl implements UserService {

	private static final String STATUS_URL = "resource/status";
	private static final String LOGIN_URL = "resource/user/login";
	private static final String LOGOUT_URL = "resource/user/logout";
	private static final String USER_INFO_URL = "resource/user";

	private UserClient userClient;
	private PropertyService propertyService;

	private String authToken;

	public UserServiceImpl(UserClient userClient,
			PropertyService propertyService) {
		this.userClient = userClient;
		this.propertyService = propertyService;
	}

	/**
	 * Login
	 */
	@Override
	public boolean login(String username, String password) {
		String serverUrl = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue());

		String loginUrl = serverUrl + LOGIN_URL;

		String res = this.userClient.login(loginUrl, username, password);

		JsonReader reader = Json.createReader(new StringReader(res));

		JsonObject jObj = reader.readObject();

		if (jObj.containsKey("auth_token")) {
			this.authToken = jObj.getString("auth_token");
			return true;
		}

		return false;
	}

	/**
	 * Get the user info as User object
	 */
	@Override
	public User getUser() {
		String url = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ USER_INFO_URL;

		String userInfo = this.userClient.getUserInfo(url, this.authToken);

		User user = null;

		JsonReader reader = Json.createReader(new StringReader(userInfo));

		try {
			JsonObject jObj = reader.readArray().getJsonObject(0);

			// Parse the JSON object
			int id = jObj.getInt("id");
			String username = jObj.getString("username");
			String displayName = jObj.getString("displayName");
			String email = jObj.getString("email");
			String language = jObj.getString("language");
			long diskQuota = jObj.getInt("diskQuota");
			long usedDiskSpace = jObj.getInt("usedDiskSpace");

			// Create a new user
			user = new User();
			user.setId(id);
			user.setUsername(username);
			user.setDisplayName(displayName);
			user.setEmail(email);
			user.setLanguage(language);
			user.setDiskQuota(diskQuota);
			user.setUsedDiskSpace(usedDiskSpace);
		} catch (JsonParsingException jpe) {
			// User not logged in
		}

		return user;
	}

	@Override
	public File getProfileImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Check the server status
	 */
	@Override
	public boolean checkServerStatus(String url) {

		// Add the status path to the URL
		if (url.endsWith("/")) {
			url = url + STATUS_URL;
		} else {
			url = url + "/" + STATUS_URL;
		}

		// Get the JSON string from the server
		String res = this.userClient.checkServerStatus(url);

		if (res == null) {
			return false;
		}

		// Read the string as JSON
		JsonReader reader = Json.createReader(new StringReader(res));
		try {
			JsonObject jObj = reader.readObject();

			// Address is correct
			return jObj.getBoolean("installed");
		} catch (JsonParsingException jpe) {
			// Invalid JSON, Address incorrect
			return false;
		}
	}

	/**
	 * Logout
	 */
	@Override
	public boolean logout() {
		String url = this.propertyService
				.getProperty(PropertiesKeys.SERVER_ADDRESS.getValue())
				+ LOGOUT_URL;

		return this.userClient.logout(url, this.authToken);
	}

	/**
	 * @return the authToken
	 */
	@Override
	public String getAuthToken() {
		return authToken;
	}
}
