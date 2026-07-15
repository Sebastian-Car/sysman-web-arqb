/*
 * VidaUtilPlacasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */

public enum VidaUtilPlacasControladorUrlEnum {

    URL0001("VIDAUTILPLACASCONTROLADORURL0001", "141074"),

    URL0002("VIDAUTILPLACASCONTROLADORURL0002", "141072"),

    URL0003("VIDAUTILPLACASCONTROLADORURL0003", "141076"),

    URL9526("VIDAUTILPLACASCONTROLADORURL9526", "59009"),

    URL27810("VIDAUTILPLACASCONTROLADORURL27810", "141070"),

    URL10009("VIDAUTILPLACASCONTROLADORURL10009", "171001"),

    URL11324("VIDAUTILPLACASCONTROLADORURL11324", "141071"),
    
    URL11325("VIDAUTILPLACASCONTROLADORURL11325", "1747001"),
    
    URL11326("VIDAUTILPLACASCONTROLADORURL11326", "");

    private final String key;
    private final String value;

    private VidaUtilPlacasControladorUrlEnum(String key, String value) {
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
