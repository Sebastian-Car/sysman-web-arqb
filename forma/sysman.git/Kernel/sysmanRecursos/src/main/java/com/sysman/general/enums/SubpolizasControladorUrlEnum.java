/*
 * SubpolizasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubpolizasControladorUrlEnum {

    URL3689("SUBPOLIZASCONTROLADORURL3689", "198001"),

    URL3968("SUBPOLIZASCONTROLADORURL3968", "95001"),

    URL3969("SUBPOLIZASCONTROLADORURL3968", "104048"),

    /**
     * 104019 getModeloplantillasPagFormatoPorTipoQuery
     */
    URL20274("SUBPOLIZASCONTROLADORURL20274", "104019"),

    /**
     * 195011 updatePolizasIndicadorImpreso
     */
    URL35890("SUBPOLIZASCONTROLADORURL35890", "195011"),
    
    URL35891("SUBPOLIZASCONTROLADORURL35890", "4070");
    

    private final String key;
    private final String value;

    private SubpolizasControladorUrlEnum(String key, String value) {
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
