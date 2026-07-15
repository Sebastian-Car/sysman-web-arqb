/*
 * FrmInformeElementosProtControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmInformeElementosProtControladorUrlEnum {

    URL4068("FRMINFORMEELEMENTOSPROTCONTROLADORURL0001", "730001"),
    
    URL4069("FRMINFORMEELEMENTOSPROTCONTROLADORURL0003", "730007"),

    URL4931("FRMINFORMEELEMENTOSPROTCONTROLADORURL0017", "62017"),
    
    URL4932("FRMINFORMEELEMENTOSPROTCONTROLADORURL0095", "62095"),

    URL5589("FRMINFORMEELEMENTOSPROTCONTROLADORURL0006", "46004");

    private final String key;
    private final String value;

    private FrmInformeElementosProtControladorUrlEnum(String key,
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
