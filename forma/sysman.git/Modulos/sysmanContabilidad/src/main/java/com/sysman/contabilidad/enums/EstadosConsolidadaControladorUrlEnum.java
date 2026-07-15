/*
 * ESTADOSCONSOLIDADAControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EstadosConsolidadaControladorUrlEnum {

    URL5742("ESTADOSCONSOLIDADACONTROLADORURL5742", //Ano
                    "4001"),

    URL6376("ESTADOSCONSOLIDADACONTROLADORURL6376", // Mes Inicial
                    "7001"),

    URL5173("ESTADOSCONSOLIDADACONTROLADORURL5173", //Mes Final
                    "7020"),

    URL4054("ESTADOSCONSOLIDADACONTROLADORURL4054",
                    "4001"),

    URL4553("ESTADOSCONSOLIDADACONTROLADORURL4553",
                    "16005");

    private final String key;
    private final String value;

    private EstadosConsolidadaControladorUrlEnum(String key, String value) {
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
