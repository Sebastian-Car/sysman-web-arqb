/*
 * ActualizarSaldosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SeguimientoMensualControladorEnum {


    URL0001("SEGUIMIENTOMENSUAL0001",
            "32003"),
    URL0002("SEGUIMIENTOMENSUAL0002",
            "32013"),
	URL0003("SEGUIMIENTOMENSUAL0003","4043"),
	
	URL0004("SEGUIMIENTOMENSUAL0003","7005");


    private final String key;
    private final String value;

    private  SeguimientoMensualControladorEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
