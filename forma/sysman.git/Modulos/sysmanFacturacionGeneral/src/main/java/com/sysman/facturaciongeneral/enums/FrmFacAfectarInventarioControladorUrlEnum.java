/*
 * FrmFacAfectarInventarioControladorUrlEnum
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
public enum FrmFacAfectarInventarioControladorUrlEnum {

    URL6213("FRMFACAFECTARINVENTARIOCONTROLADORURL6213",
                    "661033"),

    URL7616("FRMFACAFECTARINVENTARIOCONTROLADORURL7616",
                    "62038"),

    URL7210("FRMFACAFECTARINVENTARIOCONTROLADORURL7210",
                    "139017"),

    URL5732("FRMFACAFECTARINVENTARIOCONTROLADORURL5732",
                    "665015");

    private final String key;
    private final String value;

    private FrmFacAfectarInventarioControladorUrlEnum(String key,
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
