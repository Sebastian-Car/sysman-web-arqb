/*
 * FrmConceptosBaseAportesControladorUrlEnum
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
public enum FrmConceptosBaseAportesControladorUrlEnum {

    URL5329("FRMCONCEPTOSBASEAPORTESCONTROLADORURL5329",
                    "151001"),

    URL4855("FRMCONCEPTOSBASEAPORTESCONTROLADORURL4855",
                    "151001");

    private final String key;
    private final String value;

    private FrmConceptosBaseAportesControladorUrlEnum(String key,
        String value) {
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
