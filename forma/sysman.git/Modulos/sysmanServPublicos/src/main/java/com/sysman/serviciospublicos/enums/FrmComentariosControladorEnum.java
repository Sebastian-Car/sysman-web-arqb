/*-
 * FrmComentariosControladorEnum.java
 *
 * 1.0
 * 
 * 30/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmComentariosControladorEnum {
    PARAM0("COMENTARIOS");

    private final String value;

    private FrmComentariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
