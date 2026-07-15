/*
 * InfTransaccionesSstControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InfTransaccionesSstControladorUrlEnum {

    URL4974("INFTRANSACCIONESSSTCONTROLADORURL4974", "728001"),

    URL4138("INFTRANSACCIONESSSTCONTROLADORURL4138", "727001"),

    URL377("INFTRANSACCIONESSSTCONTROLADORURL377", "727008"),

    URL395("INFTRANSACCIONESSSTCONTROLADORURL395", "739001"),

    URL415("INFTRANSACCIONESSSTCONTROLADORURL415", "739003"),

    URL435("INFTRANSACCIONESSSTCONTROLADORURL435", "728005"),

    URL455("INFTRANSACCIONESSSTCONTROLADORURL455", "1049001"),

    URL474("INFTRANSACCIONESSSTCONTROLADORURL474", "1049003"),

    URL495("INFTRANSACCIONESSSTCONTROLADORURL495", "1678003"),

    URL515("INFTRANSACCIONESSSTCONTROLADORURL515", "1678005");

    private final String key;
    private final String value;

    private InfTransaccionesSstControladorUrlEnum(String key, String value)
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
