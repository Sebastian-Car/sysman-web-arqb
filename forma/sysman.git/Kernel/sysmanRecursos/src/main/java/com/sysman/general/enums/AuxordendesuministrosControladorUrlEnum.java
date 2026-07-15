/*
 * AuxordendesuministrosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AuxordendesuministrosControladorUrlEnum {

    URL71413("AUXORDENDESUMINISTROSCONTROLADORURL71413", "62001"),

    URL7551("AUXORDENDESUMINISTROSCONTROLADORURL7551", "109006"),

    URL7961("AUXORDENDESUMINISTROSCONTROLADORURL7961", "109004"),

    URL23440("AUXORDENDESUMINISTROSCONTROLADORURL23440", "109007"),

    URL24444("AUXORDENDESUMINISTROSCONTROLADORURL24444", "113001"),

    URL16994("AUXORDENDESUMINISTROSCONTROLADORURL16994", "112001"),

    URL12517("AUXORDENDESUMINISTROSCONTROLADORURL12517", "109005"),

    URL23150("AUXORDENDESUMINISTROSCONTROLADORURL23150", "82002"),

    URL31004("AUXORDENDESUMINISTROSCONTROLADORURL31004", "109008"),

    URL9178("AUXORDENDESUMINISTROSCONTROLADORURL9178", "82003"),

    URL47717("AUXORDENDESUMINISTROSCONTROLADORURL47717", "110001"),

    URL9139("AUXORDENDESUMINISTROSCONTROLADORURL9139", "113002"),

    URL41121("AUXORDENDESUMINISTROSCONTROLADORURL41121", "193001");

    private final String key;
    private final String value;

    private AuxordendesuministrosControladorUrlEnum(String key, String value) {
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
