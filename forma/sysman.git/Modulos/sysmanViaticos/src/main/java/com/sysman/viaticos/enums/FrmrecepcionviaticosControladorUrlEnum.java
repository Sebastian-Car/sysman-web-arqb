/*
 * SalarioMinimoViaticosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmrecepcionviaticosControladorUrlEnum {

    URL4615("FRMRECEPCIONVIATICOSCONTROLADORURL4615",
                    "761022"),

    URL35421("FRMRECEPCIONVIATICOSCONTROLADORURL35421",
                    "76100R"),

    URL56214("FRMRECEPCIONVIATICOSCONTROLADORURL56214",
                    "76100U"),

    URL5852("FRMRECEPCIONVIATICOSCONTROLADORURL5852",
                    "1001"),

    URL55124("FRMRECEPCIONVIATICOSCONTROLADORURL55124",
                    "2001"),

    URL87512("FRMRECEPCIONVIATICOSCONTROLADORURL55124",
                    "5002")

    ;

    private final String key;
    private final String value;

    private FrmrecepcionviaticosControladorUrlEnum(String key, String value) {
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
