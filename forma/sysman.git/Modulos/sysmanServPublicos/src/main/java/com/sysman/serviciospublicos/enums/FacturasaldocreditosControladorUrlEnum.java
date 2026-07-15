/*
 * FacturasaldocreditosControladorUrlEnum
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
public enum FacturasaldocreditosControladorUrlEnum {

    URL1213("FACTURASALDOCREDITOSCONTROLADORURL1213",
                    "251002"),

    URL35414("FACTURASALDOCREDITOSCONTROLADORURL35414",
                    "309004"),

    URL20824("FACTURASALDOCREDITOSCONTROLADORURL20824",
                    "215002"),

    URL19510("FACTURASALDOCREDITOSCONTROLADORURL19510",
                    "251001"),

    URL21346("FACTURASALDOCREDITOSCONTROLADORURL21346",
                    "309005"),

    URL11018("FACTURASALDOCREDITOSCONTROLADORURL11018",
                    "309001"),

    URL22842("FACTURASALDOCREDITOSCONTROLADORURL22842",
                    "345001"),

    URL39459("FACTURASALDOCREDITOSCONTROLADORURL39459",
                    "251003"),

    URL31099("FACTURASALDOCREDITOSCONTROLADORURL31099",
                    "227009"),
    
    URL32099("FACTURASALDOCREDITOSCONTROLADORURL32099",
                    "227009"),

    URL45620("FACTURASALDOCREDITOSCONTROLADORURL45620",
                    "251004"),

    URL32534("FACTURASALDOCREDITOSCONTROLADORURL32534",
                    "251005"),

    URL42882("FACTURASALDOCREDITOSCONTROLADORURL42882",
                    "214044"),

    URL21857("FACTURASALDOCREDITOSCONTROLADORURL21857",
                    "227008"),

    URL23473("FACTURASALDOCREDITOSCONTROLADORURL23473",
                    "213066"),
    
    URL2222("FACTURASALDOCREDITOSCONTROLADORURL2222",
                    "251006"),
    
    URL3535("FACTURASALDOCREDITOSCONTROLADORURL3535",
                    "309006");

    private final String key;
    private final String value;

    private FacturasaldocreditosControladorUrlEnum(String key, String value) {
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
