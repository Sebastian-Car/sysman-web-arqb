/*-
 * ExperienciaLaboralsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general.enums;


/**
 * @author amonroy
 * 
 * @version 1.0, 18 de dic. de 2017
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SolicitudRapidaControladorUrlEnum {

    URL4058("PERIODOTRABAJOCONTROLADORURL4058", "537004"),

    URL4735("PERIODOTRABAJOCONTROLADORURL4735", "471008"),

    URL7274("PERIODOTRABAJOCONTROLADORURL7274", "471050"),

    URL5723("PERIODOTRABAJOCONTROLADORURL5723", "471049"),
    
    URL5724("PERIODOTRABAJOCONTROLADORURL5724", "1008004");

    private final String key;
    private final String value;

    private SolicitudRapidaControladorUrlEnum(String key, String value) {
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
