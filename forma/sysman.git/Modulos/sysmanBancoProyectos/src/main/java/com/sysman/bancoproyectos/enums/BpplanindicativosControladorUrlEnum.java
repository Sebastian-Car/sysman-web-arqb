/*
 * BpplanindicativosControladorUrlEnum
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
public enum BpplanindicativosControladorUrlEnum {

    URL6037("BPPLANINDICATIVOSCONTROLADORURL6037", "4001"),

    URL6590("BPPLANINDICATIVOSCONTROLADORURL6590", "4001"),

    URL8303("BPPLANINDICATIVOSCONTROLADORURL8303", "203001"),

    URL7829("BPPLANINDICATIVOSCONTROLADORURL7829", "62053"),

    URL7140("BPPLANINDICATIVOSCONTROLADORURL7140", "553001"),

    URL7141("BPPLANINDICATIVOSCONTROLADORURL7141", "554001"),

    URL7142("BPPLANINDICATIVOSCONTROLADORURL7142", "554002"),

    URL7143("BPPLANINDICATIVOSCONTROLADORURL7143", "554003"),

    URL7144("BPPLANINDICATIVOSCONTROLADORURL7144", "62052"),

    URL7145("BPPLANINDICATIVOSCONTROLADORURL7145", "552001");

    private final String key;
    private final String value;

    private BpplanindicativosControladorUrlEnum(String key, String value) {
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
