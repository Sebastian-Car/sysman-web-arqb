/*
 * TipoClaseEventoSstsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmEvaluacionesSubDetPrincipalControladorUrlEnum {

    URL15084("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL15084", "991001"),

    URL58413("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL58413", "991008"),

    URL4217("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL4217", "947008"),

    URL8574("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL8574", "991002"),

    URL275("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL275", "991010");

    private final String key;
    private final String value;

    private FrmEvaluacionesSubDetPrincipalControladorUrlEnum(String key, String value)
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
