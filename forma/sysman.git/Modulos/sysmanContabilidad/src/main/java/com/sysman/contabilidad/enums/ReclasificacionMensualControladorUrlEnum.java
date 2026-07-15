/*
 * ReclasificacionMensualControladorUrlEnum
 *
 * 1.0
 *
 * 26/06/2023
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ReclasificacionMensualControladorUrlEnum {

    URL12212("RECLASIFICACIONMENSUALCONTROLADORURL12212","16033"),  
    URL10958("RECLASIFICACIONMENSUALCONTROLADORURL10958","4001"),  
    URL11528("RECLASIFICACIONMENSUALCONTROLADORURL11528","15009"); 

    private final String key;
    private final String value;

    private  ReclasificacionMensualControladorUrlEnum(String key, String value) {
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
