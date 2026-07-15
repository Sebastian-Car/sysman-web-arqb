/*-
 * FrmDetalleDocumentoInscritoControladorEnum.java
 *
 * 1.0
 * 
 * 21/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 21/06/2018
 * @author lbotia
 *
 */
public enum FrmDetalleDocumentoInscritoControladorEnum {

    TIPOEVENTO("TIPOEVENTO"),

    IDEVENTO("IDEVENTO"),

    SUCURSAL("SUCURSAL"),

    CONSECUTIVO("CONSECUTIVO"),

    NUMERO_DCTO("NUMERO_DCTO");

    private final String value;

    private FrmDetalleDocumentoInscritoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
