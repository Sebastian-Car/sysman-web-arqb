/*
 * FrmtarifasintrecargoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmtarifasintrecargoControladorUrlEnum {

    URL7175("FRMTARIFASINTRECARGOCONTROLADORURL7175",
                    "215027"),

    URL5929("FRMTARIFASINTRECARGOCONTROLADORURL5929",
                    "252001");

    private final String key;
    private final String value;

    private FrmtarifasintrecargoControladorUrlEnum(String key, String value) {
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
