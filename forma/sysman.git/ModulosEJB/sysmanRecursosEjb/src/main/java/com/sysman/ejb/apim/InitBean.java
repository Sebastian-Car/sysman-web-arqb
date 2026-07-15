package com.sysman.ejb.apim;

import com.sysman.kernel.api.clientwso2.beans.Token;
import com.sysman.kernel.api.clientwso2.config.ClientConfig;
import com.sysman.kernel.api.clientwso2.tokens.TokenManager;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Session Bean implementation class InitBean
 */
@Startup
@Singleton
@LocalBean
public class InitBean {

    /**
     * Default constructor.
     */
    private InitBean() {
    }

    @PostConstruct
    public void inicializar() {

        ResourceBundle apimProperties = ResourceBundle.getBundle("apimanager");
        ClientConfig clientConfig = ClientConfig.getInstance();

        boolean tokenRequest = Boolean
                        .parseBoolean(apimProperties.getString("tokenRequest"));

        clientConfig.setTokenRequest(tokenRequest);
        if (tokenRequest) {
            clientConfig.setConsumerKey(
                            apimProperties.getString("consumerKey"));
            clientConfig.setConsumerSecret(
                            apimProperties.getString("consumerSecret"));
            clientConfig.setTokenURL(apimProperties.getString("tokenURL"));

            clientConfig.setUser(apimProperties.getString("user"));
            clientConfig.setPassword(apimProperties.getString("password"));
            TokenManager tokenManager = new TokenManager();
            Token token = tokenManager.getToken();
            clientConfig.setToken(token);
        }
    }

}
