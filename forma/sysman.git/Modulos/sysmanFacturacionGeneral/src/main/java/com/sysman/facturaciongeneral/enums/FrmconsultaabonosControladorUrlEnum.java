/*
 * FacturacionconceptosControladorUrlEnum
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
public enum FrmconsultaabonosControladorUrlEnum {

    URL23432("FRMCONSULTAABONOSCONTROLADORURLENUM23432", "670002"),

    URL9923("FRMCONSULTAABONOSCONTROLADORURLENUM9923", "670003"),

    URL19054("FRMCONSULTAABONOSCONTROLADORURLENUM9054", "1017002"),

    URL36973("FRMCONSULTAABONOSCONTROLADORURLENUM36973", "1017001"),

    URL17101("FRMCONSULTAABONOSCONTROLADORURLENUM17101", "670004"),

    URL15596("FRMCONSULTAABONOSCONTROLADORURLENUM15596", "213227"),

    URL15005("FRMCONSULTAABONOSCONTROLADORURLENUM15005", ""),

    URL8740("FRMCONSULTAABONOSCONTROLADORURLENUM8740", " ");

    private final String key;
    private final String value;

    private FrmconsultaabonosControladorUrlEnum(String key, String value) {
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
