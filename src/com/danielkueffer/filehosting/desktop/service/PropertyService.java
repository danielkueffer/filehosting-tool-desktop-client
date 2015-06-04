package com.danielkueffer.filehosting.desktop.service;

/**
 * The property service
 * 
 * @author dkueffer
 * 
 */
public interface PropertyService {
	String getProperty(String key);

	void saveProperty(String key, String value);
}
