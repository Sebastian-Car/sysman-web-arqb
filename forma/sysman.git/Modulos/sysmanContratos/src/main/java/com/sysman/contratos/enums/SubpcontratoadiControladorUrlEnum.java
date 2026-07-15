/*
 * SubpcontratoadiControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum SubpcontratoadiControladorUrlEnum {

    URL15084("SUBPCONTRATOADICONTROLADORURL15084","113032"), 
    URL91505("SUBPCONTRATOADICONTROLADORURL91505","113035"), 
    URL54786("SUBPCONTRATOADICONTROLADORURL54786","113036"), 
    URL72648("SUBPCONTRATOADICONTROLADORURL72648","11300D"), 
    URL98566("SUBPCONTRATOADICONTROLADORURL98566","113034"), 
    URL25327("SUBPCONTRATOADICONTROLADORURL25327","113030"),  
    URL12742("SUBPCONTRATOADICONTROLADORURL12742","25018"),  
    URL39635("SUBPCONTRATOADICONTROLADORURL39635","11300D"),  
    URL10595("SUBPCONTRATOADICONTROLADORURL10595","62034"), 
    URL10542("SUBPCONTRATOADICONTROLADORURL10542","200005"),
    URL13391("SUBPCONTRATOADICONTROLADORURL13391","38032"),  
    URL29171("SUBPCONTRATOADICONTROLADORURL29171","113031"),  
    URL11804("SUBPCONTRATOADICONTROLADORURL11804","112079"),  
    URL11810("SUBPCONTRATOADICONTROLADORURL11810","112077"),
	URL11342("SUBPCONTRATOADICONTROLADORURL113042","113042");

    private final String key;
    private final String value;

    private  SubpcontratoadiControladorUrlEnum(String key, String value) {
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
