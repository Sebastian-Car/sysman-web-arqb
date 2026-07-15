/*-
 * InformePagoAportesParafiscalesControladorEnum.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * 
 * @version 1.0, 27/02/2018
 * @author crodriguez
 *
 */
public enum InformePagoAportesParafiscalesControladorEnum {

    ID_PROCESO("ID_PROCESO"),

    PROCESO("PROCESO");

    private final String value;

    private InformePagoAportesParafiscalesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
