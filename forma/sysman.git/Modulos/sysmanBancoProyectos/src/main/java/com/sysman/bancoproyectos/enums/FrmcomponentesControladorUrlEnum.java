/*
 * FrmcomponentesControladorUrlEnum
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
public enum FrmcomponentesControladorUrlEnum {

    URL8556("FRMCOMPONENTESCONTROLADORURL8556",
                    "553004"),

    URL0001("FRMCOMPONENTESCONTROLADORURL8556",
                    "206017"),

    URL9703("FRMCOMPONENTESCONTROLADORURL9703",
                    "4042"),

    URL10244("FRMCOMPONENTESCONTROLADORURL10244",
                    "206004"),

    URL8991("FRMCOMPONENTESCONTROLADORURL8991",
                    "561001"),

    URL1111("FRMCOMPONENTESCONTROLADORURL1111",
                    "32012"),

    URL6969("FRMCOMPONENTESCONTROLADORURL6969",
                    "513003");

    private final String key;
    private final String value;

    private FrmcomponentesControladorUrlEnum(String key, String value) {
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
