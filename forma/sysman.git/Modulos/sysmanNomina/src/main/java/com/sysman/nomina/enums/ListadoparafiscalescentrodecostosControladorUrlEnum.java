/*
 * ListadoparafiscalescentrodecostosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ListadoparafiscalescentrodecostosControladorUrlEnum {

    URL4547("LISTADOPARAFISCALESCENTRODECOSTOSCONTROLADORURL4547", "537003"),

    URL3084("LISTADOPARAFISCALESCENTRODECOSTOSCONTROLADORURL3084", "471008"),

    URL3738("LISTADOPARAFISCALESCENTRODECOSTOSCONTROLADORURL3738", "471018");

    private final String key;
    private final String value;

    private ListadoparafiscalescentrodecostosControladorUrlEnum(String key, String value)
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
