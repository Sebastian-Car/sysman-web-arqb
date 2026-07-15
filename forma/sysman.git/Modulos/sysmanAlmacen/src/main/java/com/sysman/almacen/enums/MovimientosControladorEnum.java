/*-
 * MovimientosControladorEnum.java
 *
 * 1.0
 * 
 * 3/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 3/05/2017
 * @author jrodrigueza
 *
 */
public enum MovimientosControladorEnum {
    /**
     * Parametro TIPO
     */
    PARAM0("TIPO"),
    /**
     * Parametro CLASE_MOVIMIENTO
     */
    PARAM1("CLASE_MOVIMIENTO"),
    /**
     * Parametro NIT
     */
    PARAM2("NIT"),
    /**
     * Parametro TIPO_PERSONA
     */
    PARAM3("TIPO_PERSONA"),
    /**
     * Parametro CPTO_MOV
     */
    PARAM4("CPTO_MOV"),
    /**
     * Parametro CLASE_MOV
     */
    PARAM5("CLASE_MOV"),
    /**
     * Parametro CEDULA
     */
    PARAM6("CEDULA"),
    /**
     * Parametro NIT
     */
    PARAM7("NIT"),
    /**
     * Parametro ROWNUM
     */
    PARAM8("ROWNUM"),
    /**
     * Parametro TIPOMOVASOCIADO
     */
    PARAM9("TIPOMOVASOCIADO"),
    /**
     * Parametro CLASE_BODEGA
     */
    PARAM10("CLASE_BODEGA"),
    /**
     * Parametro TIPOELEMENTO
     */
    PARAM11("TIPOELEMENTO"),
    /**
     * Parametro IND_TRASPASO
     */
    IND_TRASPASO("IND_TRASPASO"),
    /**
     * Parametro IND_TRASPASO
     */
    PREDECESOR("PREDECESOR");;

    private final String value;

    private MovimientosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
