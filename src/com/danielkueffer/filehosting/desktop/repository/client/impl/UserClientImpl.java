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

	/**
	 * Logout of the REST resource
	 */
	@Override
	public boolean logout(String url, String authToken) {
		Client client = ClientBuilder.newClient();

		Form form = new Form();
		client.target(url)
				.request()
				.header("auth_token", authToken)
				.post(Entity.entity(form,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return true;
	}

	/**
	 * Get the user data of the logged in user
	 */
	@Override
	public String getUserInfo(String url, String authToken) {
		Client client = ClientBuilder.newClient();

		Response res = client.target(url).request()
				.header("auth_token", authToken).get();

		return res.readEntity(String.class);
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
