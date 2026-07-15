/*-
 * SeleccionarClaseVpControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 6/03/2019
 * @author bcardenas
 *
 */
public enum SeleccionarClaseVpControladorUrlEnum {

    URL1032("SELECCIONARCLASEVPCONTROLADORURL1032", "1032001");

    private final String key;
    private final String value;

    private SeleccionarClaseVpControladorUrlEnum(String key,
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
