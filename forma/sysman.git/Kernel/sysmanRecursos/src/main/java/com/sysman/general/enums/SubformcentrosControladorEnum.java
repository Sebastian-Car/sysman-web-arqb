/*-
 * SubformcentrosControladorEnum.java
 *
 * 1.0
 * 
 * 6/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 6/04/2017
 * @author amonroy
 *
 */
public enum SubformcentrosControladorEnum {

    PARAM3("ANOQR"), PARAM2("DETALLE_COMPROBANTE_CNT"), PARAM1(
                    "MESFINALQR"), PARAM0("MESINICIALQR");

    private final String value;

    private SubformcentrosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
