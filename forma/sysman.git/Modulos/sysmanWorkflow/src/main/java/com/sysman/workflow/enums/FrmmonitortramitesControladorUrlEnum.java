/*
 * FrmTipoTramitesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.workflow.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmmonitortramitesControladorUrlEnum {

    URL001("FRMMONITORTRAMITESCONTROLADORURL001", "1042001"),
    
    URL002("FRMMONITORTRAMITESCONTROLADORURL001", "1737003"),
    
    URL003("FRMMONITORTRAMITESCONTROLADORURL001", "1042016"),;

    private final String key;
    private final String value;

    private FrmmonitortramitesControladorUrlEnum(String key, String value) {
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
