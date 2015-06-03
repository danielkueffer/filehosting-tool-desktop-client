package com.danielkueffer.filehosting.desktop.repository.client;

/**
 * The user client
 * 
 * @author dkueffer
 * 
 */
public interface UserClient {
	boolean login(String url, String username, String password);
	
	boolean logout();
	
	String getUserByUsername(String username);
	
	String checkServerStatus(String url);
}
