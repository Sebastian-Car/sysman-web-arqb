/*-
 * TiporespuestaspqrsControladorEnum.java
 *
 * 1.0
 * 
 * 20/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 *
 */
public enum UsuariosinmedidorControladorEnum {
    
    PARAM2("DIGITOS"),
    PARAM1("LOCALIZACION"),
    PARAM0("CICLOINI"),
    TABLA("SP_USUARIO");

    private final String value;

    private UsuariosinmedidorControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
