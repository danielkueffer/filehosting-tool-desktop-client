package com.danielkueffer.filehosting.desktop.repository.client;

/**
 * The user client
 * 
 * @author dkueffer
 * 
 */
public interface UserClient {
	String login(String url, String username, String password);
	
	boolean logout(String authToken);
	
	String getUserByUsername(String username, String authToken);
	
	String checkServerStatus(String url);
}
