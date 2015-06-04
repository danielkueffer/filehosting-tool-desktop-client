package com.danielkueffer.filehosting.desktop.enums;

/**
 * Keys for the config.properties file
 * 
 * @author dkueffer
 * 
 */
public enum PropertiesKeys {

	SERVER_ADDRESS("serverAddress"),
	USERNAME("username"),
	PASSWORD("password");

	private final String value;

	private PropertiesKeys(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
