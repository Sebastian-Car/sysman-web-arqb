/*
 * FrmConsultasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmConsultasControladorUrlEnum {

    URL4789("FRMCONSULTASCONTROLADORURL4789", "471008"),

    URL5769("FRMCONSULTASCONTROLADORURL5769", "471059"),

    URL5201("FRMCONSULTASCONTROLADORURL5201", "7027"),

    URL292("FRMCONSULTASCONTROLADORURL292", "104059"),

    URL647("FRMCONSULTASCONTROLADORURL647", "59020"),

    URL288("FRMCONSULTASCONTROLADORURL288", "210096");

    private final String key;
    private final String value;

    private FrmConsultasControladorUrlEnum(String key, String value) {
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
