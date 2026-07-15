/*
 * NovedadesPlanoAseoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NovedadesPlanoAseoControladorUrlEnum {

    URL4775("NOVEDADESPLANOASEOCONTROLADORURL4775", "214095"),

    URL4776("NOVEDADESPLANOASEOCONTROLADORURL4776", "213215"),

    URL4777("NOVEDADESPLANOASEOCONTROLADORURL4777", "319008"),

    URL4778("NOVEDADESPLANOASEOCONTROLADORURL4778", "295004"),

    URL4779("NOVEDADESPLANOASEOCONTROLADORURL4779", "295005"),

    URL4780("NOVEDADESPLANOASEOCONTROLADORURL4780", "295006");

    private final String key;
    private final String value;

    private NovedadesPlanoAseoControladorUrlEnum(String key, String value) {
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
