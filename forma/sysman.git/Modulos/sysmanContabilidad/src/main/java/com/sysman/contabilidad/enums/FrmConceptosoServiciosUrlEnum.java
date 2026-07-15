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
public enum FrmConceptosoServiciosUrlEnum {

    URL001("FrmConceptosoServiciosURL4001", "4001"),

    URL002("FrmConceptosoServiciosURL16203", "16221"),
    
    URL003("FrmConceptosoServiciosURL21001", "21001"),
    
    URL004("FrmConceptosoServiciosURL8001", "8001"),
	
	URL005("FrmConceptosoServiciosURL12006", "12005"),

    URL006("FrmConceptosoServiciosURL1930001", "1930001"),

    URL007("FrmConceptosoServiciosURL1932002", "1932002");
    
	
    private final String key;
    private final String value;

    private FrmConceptosoServiciosUrlEnum(String key, String value) {
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
