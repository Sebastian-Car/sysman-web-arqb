/*-
 * FrmtiponovedadtecnicasControladorEnum.java
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
public enum FrmtiponovedadtecnicasControladorEnum {

    PARAM0("PARAM0"),

    CODIGO("CODIGO"),

    TIPOLB("TIPOLB");

    private final String value;

    private FrmtiponovedadtecnicasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
