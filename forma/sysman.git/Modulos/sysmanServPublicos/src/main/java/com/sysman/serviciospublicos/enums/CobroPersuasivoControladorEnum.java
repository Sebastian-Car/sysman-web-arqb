/*-
 * CobroPersuasivoControladorEnum.java
 *
 * 1.0
 * 
 * 17/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 17/05/2017
 * @author jrodrigueza
 *
 */
public enum CobroPersuasivoControladorEnum {

    PARAM0("RUTA_INICIAL"), PARAM1("TIPO");

    private final String value;

    private CobroPersuasivoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
