package com.sysman.exc.kernel.api.clientwso2.connectors;

import java.util.ResourceBundle;

public class PropertiesConfigUtil {

    public static final ResourceBundle RECURSO = ResourceBundle
                    .getBundle("configExc");

    public static String getValueFromConfigP(String llave) {
        return RECURSO.getString(llave);
    }

}
