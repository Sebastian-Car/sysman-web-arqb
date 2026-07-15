/*-
 * FrmsubfuentesfinanciacionsControladorEnum.java
 *
 * 1.0
 * 
 * 22/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmsubfuentesfinanciacionsControladorEnum {

    FUENTE("FUENTE"),

    CODIGOFUENTE("CODIGOFUENTE");

    private final String value;

    private FrmsubfuentesfinanciacionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
