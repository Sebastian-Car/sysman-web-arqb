/*-
 * AuxiliaresOblgFechaPagoControladorEnum.java
 *
 * 1.0
 * 
 * 30/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 30/01/2019
 * @author bcardenas
 *
 */
public enum AuxiliaresOblgFechaPagoControladorEnum {
    CENTROINICIAL("CENTROINICIAL"), TERCEROINICIAL(
                    "TERCEROINICIAL"), CODIGOINICIAL(
                                    "CODIGOINICIAL"), TIPOINICIAL(
                                                    "TIPOINICIAL"), REFERENCIAINICIAL(
                                                                    "REFERENCIAINICIAL");

    private final String value;

    private AuxiliaresOblgFechaPagoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
