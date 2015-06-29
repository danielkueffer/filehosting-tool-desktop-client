package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.danielkueffer.filehosting.desktop.service.PropertyService;

/**
 * The property service implementation
 * 
 * @author dkueffer
 * 
 */
public class PropertyServiceImpl implements PropertyService {

	private static final String CONFIG_DIR = "config";

	private Properties props;
	private String appConfigDir;
	private String configPath;

	public PropertyServiceImpl(String appConfigDir) {
		this.appConfigDir = appConfigDir;
		this.configPath = this.appConfigDir + "/config/config.properties";

		this.props = new Properties();
		this.createFileIfNotExists();
	}

	/**
	 * Get a property by key
	 */
	@Override
	public String getProperty(String key) {
		InputStream input = null;
		String value = "";

		try {
			input = new FileInputStream(this.configPath);
			this.props.load(input);

			value = this.props.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return value;
	}

	/**
	 * Save a property
	 */
	@Override
	public void saveProperty(String key, String value) {
		OutputStream output = null;

		try {
			output = new FileOutputStream(this.configPath);

			// Set the property
			this.props.setProperty(key, value);

			// Save the property
			this.props.store(output, null);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Create the config directory and file if not existing
	 */
	private void createFileIfNotExists() {

		// Check for config directory
		File dir = new File(this.appConfigDir + "/" + CONFIG_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Check for config file
		File configFile = new File(this.configPath);
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
