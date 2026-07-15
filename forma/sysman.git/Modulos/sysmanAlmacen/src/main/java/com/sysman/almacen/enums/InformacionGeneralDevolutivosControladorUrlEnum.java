/*
 * InformacionGeneralDevolutivosControladorUrlEnum
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
public enum InformacionGeneralDevolutivosControladorUrlEnum {

    URL4740("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL4740", "112011"),

    URL5626("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL5626", "112013"),

    URL3900("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL3900", "20013"),
    
    URL4523("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL4523", "20015"),
    
    URL179006("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL179006", "179006");
    

    private final String key;
    private final String value;

    private InformacionGeneralDevolutivosControladorUrlEnum(String key,
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
