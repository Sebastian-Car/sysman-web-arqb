/*-
 * frmgruposControladorEnum.java
 *
 * 1.0
 * 
 * 26/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 26/01/2018
 * @author fperez
 *
 */
public enum FrmgruposControladorEnum {

    COMPANIA("COMPANIA"), GRUPO("GRUPO"), CLASE_EVALUACION(
                    "CLASE_EVALUACION"), CLASEEVALUACION(
                                    "CLASEEVALUACION"), CODIGO(
                                                    "CODIGO"), NOMBRE("NOMBRE");

    private final String value;

    private FrmgruposControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
