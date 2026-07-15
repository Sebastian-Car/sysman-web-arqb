/*-
 * SubdetallecomprobantepptalsControladorEnum.java
 *
 * 1.0
 * 
 * 24/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 24/04/2017
 * @author jrodrigueza
 */
public enum SubdetallecomprobantepptalsControladorEnum {

    PARAM0("COMPANIA"), PARAM1("ANO"), PARAM2("CLASE"), PARAM3("TIPO");

    private final String value;

    private SubdetallecomprobantepptalsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
