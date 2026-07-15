/*
 * ClientConfig
 *
 * 1.0
 *
 * 12/08/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.api.clientwso2.config;

import com.sysman.kernel.api.clientwso2.beans.Token;
import com.sysman.kernel.api.clientwso2.tokens.TokenManager;

import java.util.Map;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Singleton para guardar informacion de configuracion de
 * servicios de API.
 */
public class ClientConfig {

    private static final ClientConfig instance = new ClientConfig();

    private String user;
    private String password;
    private String serviceHost;
    private String tokenURL;
    private Map<String, String> servicesList;
    private Map<String, String> operationsList;
    private String consumerKey;
    private String consumerSecret;
    private Token token;
    private long timeMilis;
    private boolean tokenRequest;

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

    /**
     * @return the token
     */
    public Token getToken() {
        if (token != null) {
            long diferencia = (timeMilis + (token.getExpiresIn() * 1000))
                - System.currentTimeMillis();
            if (diferencia < 120 && diferencia > 0) {
                TokenManager tm = new TokenManager();
                setToken(tm.refrescarToken(token.getRefreshToken()));
            }
            else if (diferencia < 0) {
                TokenManager tm = new TokenManager();
                setToken(tm.getToken());
            }
        }
        return token;
    }

    /**
     * @param token
     * the token to set
     */
    public void setToken(Token token) {
        timeMilis = System.currentTimeMillis();
        this.token = token;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user
     * the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     * the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the tokenRequest
     */
    public boolean isTokenRequest() {
        return tokenRequest;
    }

    /**
     * @param tokenRequest
     * the tokenRequest to set
     */
    public void setTokenRequest(boolean tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

}
