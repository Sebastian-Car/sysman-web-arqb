/*
 * FrmCategoriasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCategoriasConceptosControladorUrlEnum {

    URL3238("FRMCATEGORIASCONTROLADORURL3238",
                    "1005"),

    URL5717("FRMCATEGORIASCONTROLADORURL5717",
                    "2009"),

    URL6507("FRMCATEGORIASCONTROLADORURL6507",
                    "5009"),

    URL5555("FRMCATEGORIASCONTROLADORURL5555",
                    "1006"),

    URL7777("FRMCATEGORIASCONTROLADORURL7777",
                    "2010"),

    URL9999("FRMCATEGORIASCONTROLADORURL9999",
                    "5010"),

    URL7238("FRMCATEGORIASCONTROLADORURL7238",
                    "766005");

    private final String key;
    private final String value;

    private FrmCategoriasConceptosControladorUrlEnum(String key, String value) {
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
