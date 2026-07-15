/*
 * TipocomprobantesControladorUrlEnum
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
public enum TipocomprobantesControladorUrlEnum {

    URL0001("TIPOCOMPROBANTESCONTROLADORURL0001",
                    "117001"),

    URL0002("TIPOCOMPROBANTESCONTROLADORURL0002",
                    "11700C"),

    URL0003("TIPOCOMPROBANTESCONTROLADORURL0003",
                    "11700U"),

    URL0004("TIPOCOMPROBANTESCONTROLADORURL0004",
                    "11700D"),

    URL2886("TIPOCOMPROBANTESCONTROLADORURL2886",
                    "15051"),

    URL21259("TIPOCOMPROBANTESCONTROLADORURL21259",
                    "447011");

    private final String key;
    private final String value;

    private TipocomprobantesControladorUrlEnum(String key, String value) {
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
