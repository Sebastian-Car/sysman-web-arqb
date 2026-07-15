/*
 * FrmrptdivisionmunicipalsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmrptdivisionmunicipalsControladorUrlEnum {

    URL2404("FRMRPTDIVISIONMUNICIPALSCONTROLADORURL2404", "1001"),

    URL7952("FRMRPTDIVISIONMUNICIPALSCONTROLADORURL7952", ""),

    URL6389("FRMRPTDIVISIONMUNICIPALSCONTROLADORURL6389", ""),

    URL8788("FRMRPTDIVISIONMUNICIPALSCONTROLADORURL8788", ""),

    URL3342("FRMRPTDIVISIONMUNICIPALSCONTROLADORURL3342", "5001"),

    URL2773("FRMRPTDIVISIONMUNICIPALSCONTROLADORURL2773", "2005");

    private final String key;
    private final String value;

    private FrmrptdivisionmunicipalsControladorUrlEnum(String key,
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
