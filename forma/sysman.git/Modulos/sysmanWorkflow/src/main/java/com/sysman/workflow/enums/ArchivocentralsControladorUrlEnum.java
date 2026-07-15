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
 * {@link com.sysman.workflow.ArchivocentralsControlador}
 * 
 * @version 1.0, 19/11/2019
 * @author jgomez
 *
 */
public enum ArchivocentralsControladorUrlEnum {

    URL002("ARCHIVOCENTRAL002", "1036001"),
    
    ;

    private final String key;
    private final String value;

    private ArchivocentralsControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
