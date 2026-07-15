/*-
 * InformesDefinitivosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 23/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 23/08/2018
 * @author bcardenas
 *
 */
public enum InformesDefinitivosControladorUrlEnum {

    URL0001("INFORMEDESFINITIVOSCONTROLADORURL0001", "471002"),

    URL0002("INFORMEDESFINITIVOSCONTROLADORURL0002", "471058"),

    URL0003("INFORMEDESFINITIVOSCONTROLADORURL0003", "471020"),

    URL0004("INFORMEDESFINITIVOSCONTROLADORURL0004", "620012");

    private final String key;
    private final String value;

    private InformesDefinitivosControladorUrlEnum(String key,
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
