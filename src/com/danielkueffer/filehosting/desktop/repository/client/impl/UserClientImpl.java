package com.danielkueffer.filehosting.desktop.repository.client.impl;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

	private String authToken;

	/**
	 * Login to the REST resource and get the authToken
	 */
	@Override
	public boolean login(String url, String username, String password) {
		Client client = ClientBuilder.newClient();

		Form form = new Form();
		form.param("username", username);
		form.param("password", password);

		Response res = client
				.target(url)
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(form,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		JsonReader reader = Json.createReader(new StringReader(res
				.readEntity(String.class)));

		JsonObject jObj = reader.readObject();

		if (jObj.containsKey("auth_token")) {
			this.authToken = jObj.getString("auth_token");
			return true;
		}

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
