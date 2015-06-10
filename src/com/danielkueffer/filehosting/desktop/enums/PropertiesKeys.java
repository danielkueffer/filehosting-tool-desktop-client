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
	PASSWORD("password"),
	HOME_FOLDER("homeFolder"),
	PROXY_ENABLED("proxyEnabled"),
	PROXY_SERVER("proxyServer"),
	PROXY_PORT("proxyPort"),
	PROXY_AUTHENTICATION("proxyAuthentication"),
	PROXY_USERNAME("proxyUsername"),
	PROXY_PASSWORD("proxyPassword");

	private final String value;

	private PropertiesKeys(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
