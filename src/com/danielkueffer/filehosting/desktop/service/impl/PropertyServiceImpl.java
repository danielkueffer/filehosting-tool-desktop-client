package com.danielkueffer.filehosting.desktop.service.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.danielkueffer.filehosting.desktop.Main;
import com.danielkueffer.filehosting.desktop.service.PropertyService;

/**
 * The property service implementation
 * 
 * @author dkueffer
 * 
 */
public class PropertyServiceImpl implements PropertyService {

	private static final String CONFIG_PATH = "src/" + Main.PATH
			+ "resources/config/config.properties";

	private Properties props;

	public PropertyServiceImpl() {
		this.props = new Properties();
	}

	/**
	 * Get a property by key
	 */
	@Override
	public String getProperty(String key) {
		InputStream input = null;
		String value = "";

		try {
			input = new FileInputStream(CONFIG_PATH);
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
			output = new FileOutputStream(CONFIG_PATH);

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
}
