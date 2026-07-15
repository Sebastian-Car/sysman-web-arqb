/*-
 * FrmDetalleListadoElegiblesControladorEnum.java
 *
 * 1.0
 * 
 * 13/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 13/07/2018
 * @author lbotia
 *
 */
public enum FrmDetalleListadoElegiblesControladorEnum {

    NUMERO_DCTO("NUMERO_DCTO"),

    CONSECUTIVO("CONSECUTIVO"),

    NRO_CONVOCATORIA("NRO_CONVOCATORIA"),

    SUCURSAL("SUCURSAL")

    ;

    private final String value;

    private FrmDetalleListadoElegiblesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
