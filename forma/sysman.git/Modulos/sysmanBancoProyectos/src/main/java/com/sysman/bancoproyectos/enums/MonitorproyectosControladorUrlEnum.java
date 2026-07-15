/*
 * MonitorproyectosControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum MonitorproyectosControladorUrlEnum {

    URL15084("MONITORPROYECTOSCONTROLADORURL15084","32036"),
    URL10369("MONITORPROYECTOSCONTROLADORURL10369","32038"),  
    URL11690("MONITORPROYECTOSCONTROLADORURL11690","62066"),  
    URL13792("MONITORPROYECTOSCONTROLADORURL13792","32039"),  
    URL10977("MONITORPROYECTOSCONTROLADORURL10977","4043");

    private final String key;
    private final String value;

    private  MonitorproyectosControladorUrlEnum(String key, String value) {
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
