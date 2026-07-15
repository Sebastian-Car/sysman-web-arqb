/*
* ClientConfig
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.exc.kernel.api.clientwso2.config;

import java.util.Map;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Singleton para guardar informacion de configuracion de servicios de API.
 */
public class ClientConfig {
 
	private static final ClientConfig instance = new ClientConfig();

	private String serviceHost;
	private String tokenURL;
	private Map<String,String> servicesList;
	private Map<String,String> operationsList;
	private String consumerKey;
	private String consumerSecret;

	private ClientConfig() {

	}

	public static ClientConfig getInstance() {
		return instance;
	}

	public String getServiceHost() {
		return serviceHost;
	}

	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	public String getTokenURL() {
		return tokenURL;
	}

	public void setTokenURL(String tokenURL) {
		this.tokenURL = tokenURL;
	}

	public Map<String, String> getServicesList() {
		return servicesList;
	}

	public void setServicesList(Map<String, String> servicesList) {
		this.servicesList = servicesList;
	}

	public Map<String, String> getOperationsList() {
		return operationsList;
	}

	public void setOperationsList(Map<String, String> operationsList) {
		this.operationsList = operationsList;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
}
