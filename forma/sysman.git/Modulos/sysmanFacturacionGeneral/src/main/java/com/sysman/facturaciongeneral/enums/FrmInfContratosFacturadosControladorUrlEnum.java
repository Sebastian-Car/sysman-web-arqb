/*
 * FrmInfContratosFacturadosControladorUrlEnum
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
public enum FrmInfContratosFacturadosControladorUrlEnum {

    URL6562("FRMINFCONTRATOSFACTURADOSCONTROLADORURL6562",
                    "664003"),

    URL7892("FRMINFCONTRATOSFACTURADOSCONTROLADORURL7892",
                    "14031"),

    URL7442("FRMINFCONTRATOSFACTURADOSCONTROLADORURL7442",
                    "14001"),

    URL5656("FRMINFCONTRATOSFACTURADOSCONTROLADORURL5656",
                    "664001");

    private final String key;
    private final String value;

    private FrmInfContratosFacturadosControladorUrlEnum(String key,
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
