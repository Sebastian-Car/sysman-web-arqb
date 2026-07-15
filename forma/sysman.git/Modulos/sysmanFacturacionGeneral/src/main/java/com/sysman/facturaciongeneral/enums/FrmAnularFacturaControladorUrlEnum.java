/*
 * TarifasfgControladorUrlEnum
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
public enum FrmAnularFacturaControladorUrlEnum {

    URL6170("FRMANULARFACTURACONTROLADORURL6170",
                    "661031"),

    URL4507("FRMANULARFACTURACONTROLADORURL4507",
                    "661029");

    private final String key;
    private final String value;

    private FrmAnularFacturaControladorUrlEnum(String key, String value) {
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
