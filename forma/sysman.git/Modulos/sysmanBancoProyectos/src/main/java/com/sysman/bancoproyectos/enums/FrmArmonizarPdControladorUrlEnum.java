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
 * @author jhernandez
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmArmonizarPdControladorUrlEnum {

    URL001("FRMARMONIZARPDCONTROLADORURL001", "4001");

    private final String key;
    private final String value;

    private FrmArmonizarPdControladorUrlEnum(String key, String value) {
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
