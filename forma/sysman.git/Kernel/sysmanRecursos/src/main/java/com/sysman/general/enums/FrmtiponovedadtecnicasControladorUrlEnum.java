/*-
 * FrmtiponovedadtecnicasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 5/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 5/03/2018
 * @author lbotia
 *
 */
public enum FrmtiponovedadtecnicasControladorUrlEnum {

    URL0001("", "");

    private final String key;
    private final String value;

    private FrmtiponovedadtecnicasControladorUrlEnum(String key, String value) {
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
