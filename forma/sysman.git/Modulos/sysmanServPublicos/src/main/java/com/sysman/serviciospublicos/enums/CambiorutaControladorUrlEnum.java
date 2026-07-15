/*
 * CambiorutaControladorUrlEnum
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
public enum CambiorutaControladorUrlEnum {

    URL4633("CAMBIORUTACONTROLADORURL4633","213042"),  
    URL5809("CAMBIORUTACONTROLADORURL5809"," rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),  
    URL7588("CAMBIORUTACONTROLADORURL7588","214036"),
    URL3484("CAMBIORUTACONTROLADORURL3484","213039"),  
    URL3050("CAMBIORUTACONTROLADORURL3050","214035"),  
    URL5469("CAMBIORUTACONTROLADORURL5469","213043"),
    URL5429("CAMBIORUTACONTROLADORURL5429","213045");

    private final String key;
    private final String value;

    private  CambiorutaControladorUrlEnum(String key, String value) {
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
