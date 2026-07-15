/*-
 * PrimaSecretarialsControladorEnum.java
 *
 * 1.0
 * 
 * 21 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 21 de dic. de 2017
 * @author amonroy
 *
 */
public enum PrimaSecretarialsControladorEnum {

    NAT_PRIMA_SERVICIOS("NAT_PRIMA_SERVICIOS"),

    PS_CODIGOPERSONA("PS_CODIGOPERSONA"),

    TIPOACTOADTIVO("TIPOACTOADTIVO");

    private final String value;

    private PrimaSecretarialsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
