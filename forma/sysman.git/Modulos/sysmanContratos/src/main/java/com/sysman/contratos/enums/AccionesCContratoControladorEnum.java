/*-
 * AccionesCContratoControladorEnum.java
 *
 * 1.0
 * 
 * 2/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 2/08/2017
 * @author pespitia
 *
 */
public enum AccionesCContratoControladorEnum {

    CODIGOINI("CODIGOINI");

    private final String value;

    private AccionesCContratoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
