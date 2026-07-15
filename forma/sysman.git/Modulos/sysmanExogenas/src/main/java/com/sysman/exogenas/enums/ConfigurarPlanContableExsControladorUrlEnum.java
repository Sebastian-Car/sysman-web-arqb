/*-
 * ConfigurarPlanContableExsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 3 dic. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.exogenas.enums;

/**
 * 
 * @version 1.0, 3 dic. 2018
 * @author ybecerra
 *
 */
public enum ConfigurarPlanContableExsControladorUrlEnum {

    URL153("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM153", "29145"),

    URL177("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM177", "1757004"),

    URL203("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM203", "4001"),

    URL228("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM228", "49001"),

    URL268("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM268", "1756001"),

    URL427("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM427", "16172"),
    
    URL409("CONFIGURACIONPLANCONTABLEEXCONTROLADORURLENUM409", "16180");

    private final String key;
    private final String value;

    private ConfigurarPlanContableExsControladorUrlEnum(String key,
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
