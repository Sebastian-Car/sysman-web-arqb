/*
 * ResumenTotalPersonalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ResumenTotalPersonalControladorUrlEnum {

    URL6134("RESUMENTOTALPERSONALCONTROLADORURL6134",
                    "471039"),

    URL7301("RESUMENTOTALPERSONALCONTROLADORURL7301",
                    "471039"),

    URL4521("RESUMENTOTALPERSONALCONTROLADORURL4521",
                    "471002"),

    URL11015("RESUMENTOTALPERSONALCONTROLADORURL11015",
                    "471001"),

    URL12191("RESUMENTOTALPERSONALCONTROLADORURL12191",
                    "462004"),

    URL5330("RESUMENTOTALPERSONALCONTROLADORURL5330",
                    "471002"),

    URL5555("RESUMENTOTALPERSONALCONTROLADORURL5555",
                    "471041"),

    URL4444("RESUMENTOTALPERSONALCONTROLADORURL4444",
                    "471041"),

    URL4545("RESUMENTOTALPERSONALCONTROLADORURL4545",
                    "210054"), 
    
    URL4646("RESUMENTOTALPERSONALCONTROLADORURL4646",
                    "210055"), 
    
    URL4747("RESUMENTOTALPERSONALCONTROLADORURL4747",
                    "210056");

    private final String key;
    private final String value;

    private ResumenTotalPersonalControladorUrlEnum(String key, String value) {
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
