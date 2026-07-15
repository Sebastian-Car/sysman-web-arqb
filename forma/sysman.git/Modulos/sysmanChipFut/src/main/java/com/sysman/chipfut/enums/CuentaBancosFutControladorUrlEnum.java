/*
 * CuentaBancosFutControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CuentaBancosFutControladorUrlEnum {

    URL10734("CUENTABANCOSFUTCONTROLADORURL10734",
                    "4001"),

    URL9986("CUENTABANCOSFUTCONTROLADORURL9986",
                    "636001"),

    URL12575("CUENTABANCOSFUTCONTROLADORURL12575",
                    "16147"),

    URL13153("CUENTABANCOSFUTCONTROLADORURL13153",
                    "36002"),

    URL13750("CUENTABANCOSFUTCONTROLADORURL13750",
                    "36002"),

    URL11151("CUENTABANCOSFUTCONTROLADORURL11151",
                    "34001"),

    URL11859("CUENTABANCOSFUTCONTROLADORURL11859",
                    "34001"),

    URL12869("CUENTABANCOSFUTCONTROLADORURL12869",
                    "16147"),

    URL10317("CUENTABANCOSFUTCONTROLADORURL10317",
                    "4001"),

    URL9594("CUENTABANCOSFUTCONTROLADORURL9594",
                    "55002"),

    URL96521("CUENTABANCOSFUTCONTROLADORURL96521",
                    "55004");

    private final String key;
    private final String value;

    private CuentaBancosFutControladorUrlEnum(String key, String value) {
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
