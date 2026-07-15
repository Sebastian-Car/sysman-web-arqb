/*
 * FrmImprimirFactLoteUrlEnum
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
public enum FrmImprimirFactLoteUrlEnum {

    URL5457("FRMIMPRIMIRFACTLOTEURL5457", "4001"),

    URL5904("FRMIMPRIMIRFACTLOTEURL5904", "661033"),

    URL7218("FRMIMPRIMIRFACTLOTEURL7218", "14033"),

    URL6768("FRMIMPRIMIRFACTLOTEURL6768", "14001"),

    URL7818("FRMIMPRIMIRFACTLOTEURL7818", "661050");

    private final String key;
    private final String value;

    private FrmImprimirFactLoteUrlEnum(String key, String value) {
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
