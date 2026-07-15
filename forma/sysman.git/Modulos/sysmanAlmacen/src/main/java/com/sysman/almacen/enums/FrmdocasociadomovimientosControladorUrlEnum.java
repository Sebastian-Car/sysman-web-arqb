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
public enum FrmdocasociadomovimientosControladorUrlEnum {

    URL3238("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL3238", "157001"),

    URL4991("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL4991", "82025"),

    URL4055("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL4055", "82027"),

    URL5683("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL5683", "109011"),

    // URL5787("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL5787",
    // "41006"),
    URL5787("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL5787", "41022"),

    URL5588("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL5588", "157003"),

    URL3388("FRMDOCASOCIADOMOVIMIENTOSCONTROLADORURL3388", "157004"),
    
    URL41024("MOVIMIENTOSCONTROLADORURLENUM41024", "41024"),
    
    URL41027("MOVIMIENTOSCONTROLADORURLENUM41027", "41027");

    private final String key;
    private final String value;

    private FrmdocasociadomovimientosControladorUrlEnum(String key,
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
