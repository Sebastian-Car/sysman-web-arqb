/*
 * ProgramacionmantenimientosControladorUrlEnum
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
public enum ProgramacionmantenimientosControladorUrlEnum {

    URL23799("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL23799", "441002"),

    URL10095("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL10095", "62039"),

    URL7114("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7114", "447003"),

    URL7682("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7682", "62004"),

    URL7683("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7683", "447004"),

    URL7684("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7684", "447005"),

    URL7685("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7685", "447006"),

    URL7686("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7686", "411001"),

    URL7687("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7687", "447007"),

    URL7688("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7688", "447008"),

    URL7689("PROGRAMACIONMANTENIMIENTOSCONTROLADORURL7689", "447009");

    private final String key;
    private final String value;

    private ProgramacionmantenimientosControladorUrlEnum(String key,
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
