/*-
 * FrmPagoEfectuadosControladoEnum.java
 *
 * 1.0
 * 
 * 5/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 5/07/2018
 * @author lbotia
 *
 */
public enum FrmPagoEfectuadosControladorEnum {

    CODIGO("CODIGO"),

    NOMBRE("NOMBRE"),

    NIT("NIT")

    ;

    private final String value;

    private FrmPagoEfectuadosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
