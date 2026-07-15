/*-
 * TransaccionModelosControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * 
 * @version 1.0, 21/11/2018
 * @author ybecerra
 *
 */
public enum ConfiguracionRetencionesControladorUrlEnum {

    URL161("CONFIGURACIONCONTROLADORURL161", "4001"),

    URL194("CONFIGURACIONCONTROLADORURL194", "12012"),

    URL243("CONFIGURACIONCONTROLADORURL243", "22002"),

    URL283("CONFIGURACIONCONTROLADORURL283", "1754001")

    ;

    private final String key;
    private final String value;

    private ConfiguracionRetencionesControladorUrlEnum(String key,
                    String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
