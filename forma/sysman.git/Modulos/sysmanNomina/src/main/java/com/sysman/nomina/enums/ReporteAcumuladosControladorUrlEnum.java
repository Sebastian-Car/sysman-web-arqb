/*
 * ReporteAcumuladosControladorUrlEnum
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
public enum ReporteAcumuladosControladorUrlEnum {

    URL9975("REPORTEACUMULADOSCONTROLADORURL9975","210027"),  
    URL5200("REPORTEACUMULADOSCONTROLADORURL5200","471002"),  
    URL6761("REPORTEACUMULADOSCONTROLADORURL6761","471009"),  
    URL5624("REPORTEACUMULADOSCONTROLADORURL5624","471004"),  
    URL6048("REPORTEACUMULADOSCONTROLADORURL6048","7024"),  
    URL9007("REPORTEACUMULADOSCONTROLADORURL9007","537001");

    private final String key;
    private final String value;

    private  ReporteAcumuladosControladorUrlEnum(String key, String value) {
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
