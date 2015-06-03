package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.File;

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
@SuppressWarnings("unused")
public class UserServiceImpl implements UserService {

	private UserClient userClient;
	private PropertyService propertyService;

	public UserServiceImpl(UserClient userClient,
			PropertyService propertyService) {
		this.userClient = userClient;
		this.propertyService = propertyService;
	}

	@Override
	public boolean login(String username, String password) {
		// TODO Auto-generated method stub
		return false;
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

		if (!url.startsWith("http://") || !url.startsWith("https://")) {
			url = "http://" + url;
		}

		url = url + "/resource/status";

		String res = this.userClient.checkServerStatus(url);

		System.out.println(res);

		return false;
	}

}
