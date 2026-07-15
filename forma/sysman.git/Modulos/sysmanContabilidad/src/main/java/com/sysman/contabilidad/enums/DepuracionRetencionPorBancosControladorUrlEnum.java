/*-
 * DepuracionRetencionPorBancosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 8/11/2018
 * @author bcardenas
 *
 */
public enum DepuracionRetencionPorBancosControladorUrlEnum {

    URL0001("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL001", "4001"),

    URL0002("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL002", "39076"),

    URL0003("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL003", "39078"),

    URL0004("DEPURACIONRETENCIONPORBANCOSCONTROLADORURL004", "39080");

    private final String key;
    private final String value;

    private DepuracionRetencionPorBancosControladorUrlEnum(String key,
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
