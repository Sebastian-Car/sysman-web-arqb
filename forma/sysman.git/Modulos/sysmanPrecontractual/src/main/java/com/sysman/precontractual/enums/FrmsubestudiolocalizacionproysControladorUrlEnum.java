/*
 * FrmsubestudiolocalizacionproysControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmsubestudiolocalizacionproysControladorUrlEnum {

    URL4340("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL4340", "1002"),

    URL4681("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL4681", "2001"),

    URL7148("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL7148", "107015"),

    URL5934("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL5934", "107015"),

    URL5422("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL5422", "5003"),

    URL001("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL001", "521001"),

    URL002("FRMSUBESTUDIOLOCALIZACIONPROYSCONTROLADORURL002", "521003");

    private final String key;
    private final String value;

    private FrmsubestudiolocalizacionproysControladorUrlEnum(String key,
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
