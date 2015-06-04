package com.danielkueffer.filehosting.desktop.repository.client.impl;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

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
		Client client = ClientBuilder.newClient();

		Response res = null;

		try {
			res = client.target(url).request().get();
			return res.readEntity(String.class);
		} catch (ProcessingException pe) {
			return null;
		}
	}
}
