/*
 * ProgramacionProyInversionControladorUrlEnum
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
public enum ProgramacionProyInversionControladorUrlEnum {
    /**
     * 34001 getFuenterecursosPagPorcodigonombreinicialQuery
     */
    URL6560("PROGRAMACIONPROYINVERSIONCONTROLADORURL6560", "34001"),

    /**
     * 34003 getFuenterecursosPagPorcodigonombrefinalQuery
     */
    URL7693("PROGRAMACIONPROYINVERSIONCONTROLADORURL7693", "34003");

    private final String key;
    private final String value;

    private ProgramacionProyInversionControladorUrlEnum(String key,
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
