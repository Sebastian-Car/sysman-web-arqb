/*-
 * UsuariossincodcatastralsControladorEnum.java
 *
 * 1.0
 * 
 * 21/06/2017
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
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum UsuariossincodcatastralsControladorEnum {

    CODIGOINTERNO("CODIGOINTERNO"),

    CODIGOCATASTRAL("CODIGOCATASTRAL"),

    CICLOFILTRO("CICLOFILTRO"),

    SP_USUARIO("SP_USUARIO");

    private final String value;

    private UsuariossincodcatastralsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
