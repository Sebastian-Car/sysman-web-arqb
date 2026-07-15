/*
 * FrmInversionxAnoControladorUrlEnum
 *
 * 1.0
 *
 * 20/09/2017
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
public enum FrmInversionxAnoControladorUrlEnum {

    URL4310("FRMINVERSIONXANOCONTROLADORURL4310", "4001"),

    URL4311("FRMINVERSIONXANOCONTROLADORURL4311", "4027"),

    URL4312("FRMINVERSIONXANOCONTROLADORURL4312", "576013"),

    URL4313("FRMINVERSIONXANOCONTROLADORURL4313", "576012"),

    ;

    private final String key;
    private final String value;

    private FrmInversionxAnoControladorUrlEnum(String key,
        String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
