/*
 * FrmconceptosadicalmacensControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmconceptosadicalmacensControladorUrlEnum {

    URL12243("FRMCONCEPTOSADICALMACENSCONTROLADORURL12243", "663024"),

    URL14304("FRMCONCEPTOSADICALMACENSCONTROLADORURL14304", "663025"),

    URL8159("FRMCONCEPTOSADICALMACENSCONTROLADORURL8159", "29127");

    private final String key;
    private final String value;

    private FrmconceptosadicalmacensControladorUrlEnum(String key,
        String value) {
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
