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

	private UserClient userClient;
	private PropertyService propertyService;

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

		return this.userClient.login(loginUrl, username, password);
	}

	@Override
	public User getUser() {
		// TODO Auto-generated method stub
		return null;
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

}
