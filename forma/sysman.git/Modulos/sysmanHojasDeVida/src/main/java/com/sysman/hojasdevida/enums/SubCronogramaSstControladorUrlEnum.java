/*
 * SubCronogramaSstControladorUrlEnum
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
public enum SubCronogramaSstControladorUrlEnum {

    URL7012("SUBCRONOGRAMASSTCONTROLADORURL7012", ""),

    URL8834("SUBCRONOGRAMASSTCONTROLADORURL8834", ""),

    URL6285("SUBCRONOGRAMASSTCONTROLADORURL6285", "726001"),

    URL7741("SUBCRONOGRAMASSTCONTROLADORURL7741", "210060"),

    URL8293("SUBCRONOGRAMASSTCONTROLADORURL8293", ""),

    URL10478("SUBCRONOGRAMASSTCONTROLADORURL10478", ""),

    URL5807("SUBCRONOGRAMASSTCONTROLADORURL5807", "725001"),

    URL9928("SUBCRONOGRAMASSTCONTROLADORURL9928", ""),

    URL9378("SUBCRONOGRAMASSTCONTROLADORURL9378", "");

    private final String key;
    private final String value;

    private SubCronogramaSstControladorUrlEnum(String key, String value)
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
