/*-
 * ConfigurarIpcControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 15/03/2019
 * @author bcardenas
 *
 */
public enum ConfigurarIpcControladorUrlEnum {

    URL0003("CONFIGURARIPCCONTROLADORURL0003", "4001"),

    URL0004("CONFIGURARIPCCONTROLADORURL004", "7049"),

    URL0007("CONFIGURARIPCCONTROLADORURL007", "7047");

    private final String key;
    private final String value;

    private ConfigurarIpcControladorUrlEnum(String key,
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
