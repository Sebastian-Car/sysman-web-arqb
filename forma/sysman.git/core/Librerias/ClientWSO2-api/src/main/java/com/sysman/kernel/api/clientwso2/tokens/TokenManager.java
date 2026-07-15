/*
 * TokenManager
 *
 * 1.0
 *
 * 12/08/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.api.clientwso2.tokens;

import com.sysman.kernel.api.clientwso2.beans.Token;
import com.sysman.kernel.api.clientwso2.config.ClientConfig;
import com.sysman.kernel.api.clientwso2.connectors.HttpClient;
import com.sysman.kernel.api.clientwso2.converters.JsonConverter;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase que nos permite gestionar informacion del Token de acceso.
 */
public class TokenManager {

    private HttpClient httpClient;
    private static final Logger logger = Logger.getLogger(TokenManager.class);

    public TokenManager() {
        httpClient = new HttpClient();
    }

    /**
     * Permite obtener token de acceso del Token API de acuerdo a las
     * credenciales suministradas y existentes en WSO2
     */
    public Token getToken() {
        String submitUrl = ClientConfig.getInstance().getTokenURL();
        String consumerKey = ClientConfig.getInstance().getConsumerKey();
        String consumerSecret = ClientConfig.getInstance().getConsumerSecret();
        String username = ClientConfig.getInstance().getUser();
        String password = ClientConfig.getInstance().getPassword();
        try {
            String applicationToken = consumerKey + ":" + consumerSecret;
            BASE64Encoder base64Encoder = new BASE64Encoder();
            applicationToken = "Basic "
                + base64Encoder.encode(applicationToken.getBytes()).trim();

            String payload = "grant_type=password&username=" + username
                + "&password=" + password;
            HttpResponse httpResponse = httpClient.doPost(submitUrl,
                            applicationToken, payload,
                            "application/x-www-form-urlencoded");
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            String jsonAccessToken = httpClient
                            .getResponsePayload(httpResponse);
            return JsonConverter.toToken(jsonAccessToken);
        }
        catch (IOException e) {
            logger.error(e);
            return null;
        }
    }

    public Token refrescarToken(String refreshToken) {
        String submitUrl = ClientConfig.getInstance().getTokenURL();
        String consumerKey = ClientConfig.getInstance().getConsumerKey();
        String consumerSecret = ClientConfig.getInstance().getConsumerSecret();
        String username = ClientConfig.getInstance().getUser();
        String password = ClientConfig.getInstance().getPassword();
        try {
            String applicationToken = consumerKey + ":" + consumerSecret;
            BASE64Encoder base64Encoder = new BASE64Encoder();
            applicationToken = "Basic "
                + base64Encoder.encode(applicationToken.getBytes()).trim();

            String payload = "grant_type=refresh_token&username=" + username
                + "&password=" + password + "&refresh_token=" + refreshToken;
            HttpResponse httpResponse = httpClient.doPost(submitUrl,
                            applicationToken, payload,
                            "application/x-www-form-urlencoded");
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return getToken();
            }
            String jsonAccessToken = httpClient
                            .getResponsePayload(httpResponse);
            return JsonConverter.toToken(jsonAccessToken);
        }
        catch (IOException e) {
            logger.error(e);
            return null;
        }
    }
}
