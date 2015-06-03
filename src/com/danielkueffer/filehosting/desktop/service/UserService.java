package com.danielkueffer.filehosting.desktop.service;

import java.io.File;

import com.danielkueffer.filehosting.desktop.repository.pojos.User;

/**
 * The user service
 * 
 * @author dkueffer
 * 
 */
public interface UserService {
	boolean login(String username, String password);

	User getUser();

	File getProfileImage();

	boolean checkServerStatus(String url);
}
