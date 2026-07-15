/*
 * ActualizarubicacionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ActualizarubicacionControladorUrlEnum {

    URL5392("ACTUALIZARUBICACIONCONTROLADORURL5392","Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"SP_USUARIO\","),  
    URL3757("ACTUALIZARUBICACIONCONTROLADORURL3757","213018"),  
    URL4087("ACTUALIZARUBICACIONCONTROLADORURL4087","213015"),  
    URL3315("ACTUALIZARUBICACIONCONTROLADORURL3315","214005"),
    URL3327("ACTUALIZARUBICACIONCONTROLADORURL3327","213017");
    private final String key;
    private final String value;

    private  ActualizarubicacionControladorUrlEnum(String key, String value) {
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
