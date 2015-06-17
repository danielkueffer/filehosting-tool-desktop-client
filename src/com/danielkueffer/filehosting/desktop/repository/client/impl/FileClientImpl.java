package com.danielkueffer.filehosting.desktop.repository.client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import com.danielkueffer.filehosting.desktop.helper.NetworkHelper;
import com.danielkueffer.filehosting.desktop.repository.client.FileClient;

/**
 * File client implementation
 * 
 * @author dkueffer
 * 
 */
public class FileClientImpl implements FileClient {

	/**
	 * Upload a file to the server
	 */
	@Override
	public String uploadFile(String url, File file, String fileName,
			int parent, String authToken) {

		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				NetworkHelper.getEngine()).build();

		MultipartFormDataOutput mdo = new MultipartFormDataOutput();

		try {
			mdo.addFormData("file", new FileInputStream(file),
					MediaType.APPLICATION_OCTET_STREAM_TYPE);

			mdo.addFormData("my-filename", new String(fileName),
					MediaType.APPLICATION_FORM_URLENCODED_TYPE);

			mdo.addFormData("parent", new String(parent + ""),
					MediaType.APPLICATION_FORM_URLENCODED_TYPE);

			mdo.addFormData("last-modified", new String(file.lastModified()
					+ ""), MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
				mdo) {
		};

		Response res = client
				.target(url)
				.request(MediaType.APPLICATION_JSON)
				.header("auth_token", authToken)
				.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));

		return res.readEntity(String.class);
	}

	/**
	 * Create a folder on the server
	 */
	@Override
	public String createFolder(String url, String folderName, int parent,
			String authToken) {
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				NetworkHelper.getEngine()).build();

		Form form = new Form();
		form.param("folder", folderName);
		form.param("parent", parent + "");

		Response res = client
				.target(url)
				.request(MediaType.APPLICATION_JSON)
				.header("auth_token", authToken)
				.post(Entity.entity(form,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return res.readEntity(String.class);
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
		return false;
	}

	/**
	 * Get a list of the deleted files on the server
	 */
	@Override
	public String getDeletedFilesByUser(String url, String authToken) {
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				NetworkHelper.getEngine()).build();

		Response res = client.target(url).request()
				.header("auth_token", authToken).get();

		return res.readEntity(String.class);
	}

	/**
	 * Update the deleted files on server
	 */
	@Override
	public String updateDeletedFiles(String url, String json, String authToken) {
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				NetworkHelper.getEngine()).build();

		Form form = new Form();
		form.param("json", json);

		Response res = client
				.target(url)
				.request(MediaType.APPLICATION_JSON)
				.header("auth_token", authToken)
				.post(Entity.entity(form,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		return res.readEntity(String.class);
	}
}
