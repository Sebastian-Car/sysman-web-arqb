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
public enum FrmproyectosnominasControladorEnum {

    COMPANIA("COMPANIA"), ID_DE_PROYECTO("ID_DE_PROYECTO");

    private final String value;

    private FrmproyectosnominasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
