/*
 * RegistropagobancospazsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RegistropagobancospazsControladorUrlEnum {

    URL56774("REGISTROPAGOBANCOSPAZSCONTROLADORURL56774",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tablaUpd,"),

    URL54626("REGISTROPAGOBANCOSPAZSCONTROLADORURL54626",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tablaIpPagosDetPaz,"),

    URL53221("REGISTROPAGOBANCOSPAZSCONTROLADORURL53221",
                    "415002"),

    URL8509("REGISTROPAGOBANCOSPAZSCONTROLADORURL8509",
                    "375004"),

    URL58404("REGISTROPAGOBANCOSPAZSCONTROLADORURL58404",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tablaUpd,"),

    URL7123("REGISTROPAGOBANCOSPAZSCONTROLADORURL7123",
                    "415001"),

    URL6070("REGISTROPAGOBANCOSPAZSCONTROLADORURL6070",
                    "409002"), 
    
    URL9087("REGISTROPAGOBANCOSPAZSCONTROLADORURL9087",
                    "394007"),
    
    URL4646("REGISTROPAGOBANCOSPAZSCONTROLADORURL4646",
                    "394008");

    private final String key;
    private final String value;

    private RegistropagobancospazsControladorUrlEnum(String key, String value) {
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
