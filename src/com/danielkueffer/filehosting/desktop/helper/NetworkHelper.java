package com.danielkueffer.filehosting.desktop.helper;

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
}
