/*
 * FichaTecnicaInmuebleUrlEnum
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
public enum FichaTecnicaInmuebleUrlEnum {

    URL2929("FICHATECNICAINMUEBLEURL2929",
                    "137006"),

    URL3030("FICHATECNICAINMUEBLEURL3030",
                    "137008"),

    URL3131("FICHATECNICAINMUEBLEURL3131",
                    "137004"),

    URL3232("FICHATECNICAINMUEBLEURL3232",
                    "136006"),

    URL3333("FICHATECNICAINMUEBLEURL3232",
                    "136008"),

    URL3434("FICHATECNICAINMUEBLEURL3232",
                    "136004"),

    URL3535("FICHATECNICAINMUEBLEURL3535",
                    "137012"),

    URL3636("FICHATECNICAINMUEBLEURL3636",
                    "137014"),

    URL3737("FICHATECNICAINMUEBLEURL3737",
                    "137010"),
    
    URL3838("FICHATECNICAINMUEBLEURL3838",
                    "136012"),

    URL3939("FICHATECNICAINMUEBLEURL3939",
                    "136014"),
    
    URL4040("FICHATECNICAINMUEBLEURL4040",
                    "136010");
    
    
    private final String key;
    private final String value;

    private FichaTecnicaInmuebleUrlEnum(String key, String value) {
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
