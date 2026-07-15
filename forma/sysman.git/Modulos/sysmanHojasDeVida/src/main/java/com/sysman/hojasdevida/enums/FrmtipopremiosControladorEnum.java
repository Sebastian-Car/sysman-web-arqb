/*-
º * FrmtipopremiosControladorEnum.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase que contiene las constantes empleadas en el controlador
 * Frmtipopremios.
 * 
 * @version 1.0, 31/01/2018
 * @author dnino
 *
 */
public enum FrmtipopremiosControladorEnum {
    COMPANIA("COMPANIA"),

    ID_TIPO_PREMIO("ID_TIPO_PREMIO");

    private final String value;

    private FrmtipopremiosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
