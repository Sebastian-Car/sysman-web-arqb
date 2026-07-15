/*
 * FrmActualizarFacturaControladorUrlEnum
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
public enum FrmActualizarFacturaControladorUrlEnum {

    URL15659("FRMACTUALIZARFACTURACONTROLADORURL15659",
                    "15048"),

    URL14423("FRMACTUALIZARFACTURACONTROLADORURL14423",
                    "665014"),

    URL14995("FRMACTUALIZARFACTURACONTROLADORURL14995",
                    "661027"),

    URL17525("FRMACTUALIZARFACTURACONTROLADORURL17525",
                    "72002"),

    URL4545("FRMACTUALIZARFACTURACONTROLADORURL4545",
                    "29131"),

    URL9595("FRMACTUALIZARFACTURACONTROLADORURL9595",
                    "39071"),

    URL20100("FRMACTUALIZARFACTURACONTROLADORURL20100",
                    "670001"),

    URL4848("FRMACTUALIZARFACTURACONTROLADORURL4848",
                    "39090");

    private final String key;
    private final String value;

    private FrmActualizarFacturaControladorUrlEnum(String key, String value) {
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
