/*
 * RevisionincapacidadesControladorUrlEnum
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
public enum RevisionincapacidadesControladorUrlEnum {

    URL4973("REVISIONINCAPACIDADESCONTROLADORURL4973",
                    "471002"),

    URL6850("REVISIONINCAPACIDADESCONTROLADORURL6850",
                    "471002"),

    URL4237("REVISIONINCAPACIDADESCONTROLADORURL4237",
                    "537007"),

    URL5429("REVISIONINCAPACIDADESCONTROLADORURL5429",
                    "471039"),

    URL7340("REVISIONINCAPACIDADESCONTROLADORURL7340",
                    "471039"),
    
    URL5454("REVISIONINCAPACIDADESCONTROLADORURL5454",
                    "471026"),
    
    URL6868("REVISIONINCAPACIDADESCONTROLADORURL6868",
                    "471026");
    

    private final String key;
    private final String value;

    private RevisionincapacidadesControladorUrlEnum(String key, String value) {
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
