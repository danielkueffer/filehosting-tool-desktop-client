package com.danielkueffer.filehosting.desktop.repository.client.impl;

import com.danielkueffer.filehosting.desktop.repository.client.UserClient;

/**
 * The user client implementation
 * 
 * @author dkueffer
 * 
 */
public class UserClientImpl implements UserClient {

	@Override
	public boolean login(String url, String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get the server status
	 */
	@Override
	public String checkServerStatus(String url) {
		return null;
	}
}
