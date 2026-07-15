/*
 * RetencionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RetencionesControladorUrlEnum {

    URL4425("RETENCIONESCONTROLADORURL4425", "4014"),

    URL4190("RETENCIONESCONTROLADORURL4190", "20001"),

    URL4663("RETENCIONESCONTROLADORURL4663", "12003"),

    URL8298("RETENCIONESCONTROLADORURL8298", "12004"),

    URL3567("RETENCIONESCONTROLADORURL3567", "4001"),

    URL5125("RETENCIONESCONTROLADORURL5125", "8001"),

    URL3679("RETENCIONESCONTROLADORURL3679", "16074"),

    URL3947("RETENCIONESCONTROLADORURL3947", "20023"),

    URL3948("RETENCIONESCONTROLADORURL3948", "29060"),

    URL3949("RETENCIONESCONTROLADORURL3949", "29061"),

    URL3950("RETENCIONESCONTROLADORURL3950", "23005"),

    URL4899("RETENCIONESCONTROLADORURL4899", "23048"),

	URL4900("RETENCIONESCONTROLADORURL4900", "13003"),
	
	URL4901("RETENCIONESCONTROLADORURL4901", "34001"),
	
	URL1937002("RETENCIONESCONTROLADORURL1937002", "1937002");
	
    private final String key;
    private final String value;

    private RetencionesControladorUrlEnum(String key, String value) {
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
