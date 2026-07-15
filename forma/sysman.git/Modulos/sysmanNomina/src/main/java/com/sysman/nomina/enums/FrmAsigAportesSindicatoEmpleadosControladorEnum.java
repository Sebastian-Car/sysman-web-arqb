/*-
 * FrmproyectosnominasControladorEnum.java
 *
 * 1.0
 * 
 * 7/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumerado para el controlador FrmproyectosnominasControlador.
 * 
 * @version 1.0, 7/02/2018
 * @author fperez
 *
 */
public enum FrmAsigAportesSindicatoEmpleadosControladorEnum {

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    CLASE_ID_DE_FONDO("CLASE_ID_DE_FONDO"),

    FORMA_DESCUENTO("FORMA_DESCUENTO"),

    NOMBRE_FONDO("NOMBRE_FONDO");

    private final String value;

    private FrmAsigAportesSindicatoEmpleadosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
