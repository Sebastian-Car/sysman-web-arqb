/*
 * ExistenciadevxdepccControladorUrlEnum
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
public enum FrmealmacencontabilidadmsiifControladorUrlEnum {

    URL5238("FRMEALMACENCONTABILIDADMSIIFONTROLADORURL5238", "4002"),
    URL5248("FRMEALMACENCONTABILIDADMSIIFONTROLADORURL5248", "7016"),
    URL9268("FRMEALMACENCONTABILIDADMSIIFONTROLADORURL9268", "119027"),
    URL4269("FRMEALMACENCONTABILIDADMSIIFCONTROLADORURL4269", "119028");

    private final String key;
    private final String value;

    private FrmealmacencontabilidadmsiifControladorUrlEnum(String key,
        String value) {
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
