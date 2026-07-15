/*
 * FlujotesoreriaControladorUrlEnum
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
public enum FlujotesoreriaControladorUrlEnum {

    URL5406("FLUJOTESORERIACONTROLADORURL5406",
                    "4001"),

    URL5760("FLUJOTESORERIACONTROLADORURL5760",
                    "23040"),

    URL9260("FLUJOTESORERIACONTROLADORURL9260",
                    " List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);");

    private final String key;
    private final String value;

    private FlujotesoreriaControladorUrlEnum(String key, String value) {
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
