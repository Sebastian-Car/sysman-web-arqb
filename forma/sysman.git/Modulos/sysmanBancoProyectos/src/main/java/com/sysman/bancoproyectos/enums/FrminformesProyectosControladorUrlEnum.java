/*
 * FrminformesProyectosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrminformesProyectosControladorUrlEnum {

    URL7145("FRMINFORMESPROYECTOSCONTROLADORURL7145","94113"),
    URL4852("FRMINFORMESPROYECTOSCONTROLADORURL4852","32015"),
    URL7040("FRMINFORMESPROYECTOSCONTROLADORURL7040","4043"),  
    URL10663("FRMINFORMESPROYECTOSCONTROLADORURL10663","131006"),  
    URL85158("FRMINFORMESPROYECTOSCONTROLADORURL85158","131008"),  
    URL83647("FRMINFORMESPROYECTOSCONTROLADORURL83647","130011"), 
    URL101866("FRMINFORMESPROYECTOSCONTROLADORURL101866","131010"),  
    URL74184("FRMINFORMESPROYECTOSCONTROLADORURL74184","433005"),  
    URL93694("FRMINFORMESPROYECTOSCONTROLADORURL93694","131009"),  
    URL9541("FRMINFORMESPROYECTOSCONTROLADORURL9541","32003"),  
    URL90263("FRMINFORMESPROYECTOSCONTROLADORURL90263","554008"),  
    URL96497("FRMINFORMESPROYECTOSCONTROLADORURL96497","94114"),  
    URL10118("FRMINFORMESPROYECTOSCONTROLADORURL10118","32013"),  
    URL7711("FRMINFORMESPROYECTOSCONTROLADORURL7711","107017"),  
    URL6553("FRMINFORMESPROYECTOSCONTROLADORURL6553","62056"),  
    URL8597("FRMINFORMESPROYECTOSCONTROLADORURL8597","4045");

    private final String key;
    private final String value;

    private  FrminformesProyectosControladorUrlEnum(String key, String value) {
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
