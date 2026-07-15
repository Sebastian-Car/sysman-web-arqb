/*-
 * ConfiguracionSIFSEControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.sia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/12/2018
 * @author bcardenas
 *
 */
public enum ConfiguracionSIFSEControladorUrlEnum {

    URL0001("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "4001"),

    URL0002("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "45053"),

    URL0003("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "1760001"),

    URL0004("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "1761001"),

    URL0005("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "1031027"),

    URL0006("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "1031028");

    private final String key;
    private final String value;

    private ConfiguracionSIFSEControladorUrlEnum(String key,
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
