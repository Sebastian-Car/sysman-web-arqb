/*
 * FormulariodianControladorUrlEnum
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
public enum FormulariodianControladorUrlEnum {

    URL13904("FORMULARIODIANCONTROLADORURL13904",
                    " List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),

    URL5542("FORMULARIODIANCONTROLADORURL5542",
                    "4002"),

    URL6208("FORMULARIODIANCONTROLADORURL6208",
                    "7001");

    private final String key;
    private final String value;

    private FormulariodianControladorUrlEnum(String key, String value) {
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
