/*-
 * FrmDTramiteVariablesControladorEnum.java
 *
 * 1.0
 * 
 * 25/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para centralizar las cadenas y parametros
 * utilizados en el controlador:
 * {@link com.sysman.workflow.ArchivoCentralsControladoEnum}
 * 
 * @version 1.0, 19/11/2019
 * @author jgomez
 *
 */
public enum ArchivoCentralsControladoEnum {


    TABLA("ARCHIVO_CENTRAL");

    private final String value;

    private ArchivoCentralsControladoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
