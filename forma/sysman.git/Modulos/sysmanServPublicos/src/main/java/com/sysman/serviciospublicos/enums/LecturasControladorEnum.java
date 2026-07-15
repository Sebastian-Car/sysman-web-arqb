/*-
 * LecturasControladorEnum.java
 *
 * 1.0
 * 
 * 8/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum LecturasControladorEnum {
    /**
     * Parametro CLASEPROBLEMA
     */
    PARAM0("CLASEPROBLEMA"),
    /**
     * Parametro PROBLEMA
     */
    PARAM1("PROBLEMA"),
    /**
     * Parametro PROBLEMA_ANT
     */
    PARAM2("PROBLEMA_ANT"),
    /**
     * Parametro NOMBREAFORADOR
     */
    PARAM3("NOMBREAFORADOR"),
    /**
     * Parametro FECHALECTURAAFORO
     */
    PARAM4("FECHALECTURAAFORO"),
    /**
     * Parametro SP_USUARIO_PROBLEMA
     */
    PARAM5("SP_USUARIO_PROBLEMA"),
    /**
     * Parametro LECTURAAFORO
     */
    PARAM6("LECTURAAFORO"),
    /**
     * Parametro LECTURA
     */
    PARAM7("LECTURA"),
    /**
     * Parametro SIGUIENTE
     */
    PARAM8("SIGUIENTE"),
    /**
     * Parametro SOLUCION
     */
    PARAM9("SOLUCION");

    private final String value;

    private LecturasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}