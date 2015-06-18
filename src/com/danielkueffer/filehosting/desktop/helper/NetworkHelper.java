package com.danielkueffer.filehosting.desktop.helper;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import com.danielkueffer.filehosting.desktop.enums.PropertiesKeys;
import com.danielkueffer.filehosting.desktop.service.PropertyService;

/**
 * The network helper
 * 
 * @author dkueffer
 * 
 */
public class NetworkHelper {

	private static PropertyService propertyService;

	/**
	 * Set the propertyService
	 * 
	 * @param propertyService
	 */
	public static void setPropertyService(PropertyService propertyService) {
		NetworkHelper.propertyService = propertyService;
	}

	/**
	 * Get the ApacheHttpClient4Engine with or without PROXY enabled
	 * 
	 * @return
	 */
	public static ApacheHttpClient4Engine getEngine() {

		ApacheHttpClient4Engine engine = null;

		boolean proxyEnabled = new Boolean(
				propertyService.getProperty(PropertiesKeys.PROXY_ENABLED
						.getValue()));

		// PROXY enabled
		if (proxyEnabled) {

			// Get and set the field values
			String proxyServer = propertyService
					.getProperty(PropertiesKeys.PROXY_SERVER.getValue());
			String proxyPort = propertyService
					.getProperty(PropertiesKeys.PROXY_PORT.getValue());

			HttpHost proxy = new HttpHost(proxyServer,
					Integer.parseInt(proxyPort));
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

			engine = new ApacheHttpClient4Engine(httpClient);
		} else {
			// PROXY disabled
			engine = new ApacheHttpClient4Engine();
		}

		return engine;
	}

	/**
	 * Return the path of the folder as in the database entry of the file
	 * 
	 * @param fullPath
	 * @param homeFolderPath
	 * @return
	 */
	public static String getRelativePath(String fullPath, String homeFolderPath) {

		String parentPath = "";
		try {
			URL parentURL = Paths.get(fullPath).toUri().toURL();
			URL homeFolderURL = Paths.get(homeFolderPath).toUri().toURL();

			parentPath = parentURL.getPath().replace(homeFolderURL.getPath(),
					"");

			// Decode the URL
			parentPath = URLDecoder.decode(parentPath, "UTF-8");

			// Remove last slash of the string
			if (parentPath.length() > 0
					&& parentPath.charAt(parentPath.length() - 1) == '/') {
				parentPath = parentPath.substring(0, parentPath.length() - 1);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return parentPath;
	}
}
