/*-
 * FrmVisualizarAcuerdosControladorEnum.java
 *
 * 1.0
 * 
 * 21/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * 
 * @version 1.0, 21/05/2019
 * @author bcardenas
 *
 */
public enum FrmVisualizarAcuerdosControladorEnum {

    CLASE_PROYECTO("45"),

    NUMERO_FACTURA("NUMERO_FACTURA"),

    ID("ID"),

    CODIGO_PROYECTO("CODIGO_PROYECTO"),

    APLICACION("APLICACION"),

    FACTURA("FACTURA"),

    VALIDAR("VALIDAR");

    private final String value;

    private FrmVisualizarAcuerdosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
