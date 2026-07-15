/*
 * PagossaldosControladorUrlEnum
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
public enum PagossaldosControladorUrlEnum {

    URL7694("PAGOSSALDOSCONTROLADORURL7694","367124"),  
    URL8678("PAGOSSALDOSCONTROLADORURL8678","367128"),
    URL8690("PAGOSSALDOSCONTROLADORURL8678","385015"),
    URL7020("PAGOSSALDOSCONTROLADORURL7020","375001"), 
    URL7030("PAGOSSALDOSCONTROLADORURL7020","381007"),
    URL7362("PAGOSSALDOSCONTROLADORURL7362","4013"),
    URL1978("PAGOSSALDOSCONTROLADORURL1978", "386005"),
    URL1979("PAGOSSALDOSCONTROLADORURL1979", "386004"),
    URL1980("PAGOSSALDOSCONTROLADORURL1980", "386006"),
    URL1981("PAGOSSALDOSCONTROLADORURL1981", "386007");

    private final String key;
    private final String value;

    private  PagossaldosControladorUrlEnum(String key, String value) {
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
