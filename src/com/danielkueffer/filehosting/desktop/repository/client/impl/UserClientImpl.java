package com.danielkueffer.filehosting.desktop.repository.client.impl;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.danielkueffer.filehosting.desktop.repository.client.UserClient;

/**
 * The user client implementation
 * 
 * @author dkueffer
 * 
 */
public class UserClientImpl implements UserClient {

	/**
	 * Login to the REST resource and get the authToken
	 */
	@Override
	public String login(String url, String username, String password) {
		Client client = ClientBuilder.newClient();

		Form form = new Form();
		form.param("username", username);
		form.param("password", password);

		Response res = client
				.target(url)
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(form,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return res.readEntity(String.class);
	}

	@Override
	public boolean logout(String authToken) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserByUsername(String username, String authToken) {
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
