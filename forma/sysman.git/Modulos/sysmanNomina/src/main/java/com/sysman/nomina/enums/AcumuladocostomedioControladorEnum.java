/*-
 * AcumuladocostomedioControladorEnum.java
 *
 * 1.0
 * 
 * 09/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 09/01/2018
 * @author spina
 *
 */
public enum AcumuladocostomedioControladorEnum {

    PARAM0("PARAM0");

    private final String value;

    private AcumuladocostomedioControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
