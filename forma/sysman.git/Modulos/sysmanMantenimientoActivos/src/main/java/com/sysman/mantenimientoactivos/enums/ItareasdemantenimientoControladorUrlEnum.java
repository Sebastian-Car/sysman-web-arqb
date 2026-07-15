/*
 * ItareasdemantenimientoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ItareasdemantenimientoControladorUrlEnum {

    URL4325("ITAREASDEMANTENIMIENTOCONTROLADORURL4325", "443004"),

    URL5100("ITAREASDEMANTENIMIENTOCONTROLADORURL5100", "112086"),

    URL5887("ITAREASDEMANTENIMIENTOCONTROLADORURL5887", "112088"),

    URL3763("ITAREASDEMANTENIMIENTOCONTROLADORURL3763", "443002"),

    URL7484("ITAREASDEMANTENIMIENTOCONTROLADORURL7484", "444003"),

    URL6801("ITAREASDEMANTENIMIENTOCONTROLADORURL6801", "444001");

    private final String key;
    private final String value;

    private ItareasdemantenimientoControladorUrlEnum(String key, String value) {
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
