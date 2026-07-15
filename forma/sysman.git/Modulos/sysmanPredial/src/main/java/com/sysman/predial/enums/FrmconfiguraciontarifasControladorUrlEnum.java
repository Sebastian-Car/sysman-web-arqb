/*
 * FrmconfiguraciontarifasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmconfiguraciontarifasControladorUrlEnum {

    URL5207("FRMCONFIGURACIONTARIFASCONTROLADORURL5207", "380004"),

    URL5672("FRMCONFIGURACIONTARIFASCONTROLADORURL5672", "367037"),

    URL12011("FRMCONFIGURACIONTARIFASCONTROLADORURL12011", "379005"),

    URL13271("FRMCONFIGURACIONTARIFASCONTROLADORURL13271", "376004"),

    URL8239("FRMCONFIGURACIONTARIFASCONTROLADORURL8239", "367039"),

    URL4814("FRMCONFIGURACIONTARIFASCONTROLADORURL4814", "380003"),

    URL11071("FRMCONFIGURACIONTARIFASCONTROLADORURL11071", "379003");

    private final String key;
    private final String value;

    private FrmconfiguraciontarifasControladorUrlEnum(String key,
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
