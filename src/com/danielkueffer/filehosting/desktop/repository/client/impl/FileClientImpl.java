package com.danielkueffer.filehosting.desktop.repository.client.impl;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.danielkueffer.filehosting.desktop.helper.NetworkHelper;
import com.danielkueffer.filehosting.desktop.repository.client.FileClient;

/**
 * File client implementation
 * 
 * @author dkueffer
 * 
 */
public class FileClientImpl implements FileClient {

	@Override
	public boolean uploadFile(File file, String authToken) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Get a input stream of a file by path
	 */
	@Override
	public InputStream getFileByPath(String url, String authToken) {
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				NetworkHelper.getEngine()).build();

		Response res = client.target(url).request()
				.header("auth_token", authToken).get();

		return res.readEntity(InputStream.class);
	}

	/**
	 * Get a file list of the current user
	 */
	@Override
	public String getFilesByUser(String url, String authToken) {
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				NetworkHelper.getEngine()).build();

		Response res = client.target(url).request()
				.header("auth_token", authToken).get();

		return res.readEntity(String.class);
	}

	@Override
	public boolean deleteFile(String path, String authToken) {
		// TODO Auto-generated method stub
		return false;
	}
}
