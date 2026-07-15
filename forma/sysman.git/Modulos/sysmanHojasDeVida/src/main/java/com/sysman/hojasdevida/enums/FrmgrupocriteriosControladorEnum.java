/*-
 * frmgrupocriteriosControladorEnum.java
 *
 * 1.0
 * 
 * 29/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 29/01/2018
 * @author fperez
 *
 */
public enum FrmgrupocriteriosControladorEnum {

    COMPANIA("COMPANIA"),

    GRUPO("GRUPO"),

    CLASE_EVALUACION("CLASEEVALUACION"),

    CODIGO("CODIGO"),

    CODIGO_CRITERIO("CODIGO_CRITERIO"),

    NOMBRE("NOMBRE");

    private final String value;

    private FrmgrupocriteriosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
