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

public enum UbicacionElementoControladorUrlEnum {

    URL0001("VIDAUTILPLACASCONTROLADORURL0001", "1003"), //País

    URL0002("VIDAUTILPLACASCONTROLADORURL0002", "2007"), //Departamento

    URL0003("VIDAUTILPLACASCONTROLADORURL0003", "5007"), //Ciudad

    URL9526("VIDAUTILPLACASCONTROLADORURL9526", "119021"), //Actualizar  en D_Movimiento

    URL27810("VIDAUTILPLACASCONTROLADORURL27810", "141122"), // Actualizar en Devolutivo

    URL10009("VIDAUTILPLACASCONTROLADORURL10009", "1714005"),

    URL11324("VIDAUTILPLACASCONTROLADORURL11324", "");

    private final String key;
    private final String value;

    private UbicacionElementoControladorUrlEnum(String key, String value) {
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
