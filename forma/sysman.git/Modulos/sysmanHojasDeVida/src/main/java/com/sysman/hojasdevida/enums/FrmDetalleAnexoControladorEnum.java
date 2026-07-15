/*-
 * FrmDetalleAnexoControladorEnum.java
 *
 * 1.0
 * 
 * 19/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 19/06/2018
 * @author lbotia
 *
 */
public enum FrmDetalleAnexoControladorEnum {

    ANO("ANO"),

    TIPO("TIPO"),

    TIPOACT("TIPOACT"),

    ACTIVIDADES("ACTIVIDADES"),

    NOMBRE_ARCHIVO("NOMBRE_ARCHIVO"),

    CONSECUTIVO("CONSECUTIVO"),

    ACTIVIDAD("ACTIVIDAD");

    private final String value;

    private FrmDetalleAnexoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}