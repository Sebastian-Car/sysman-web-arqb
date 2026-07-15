/*
 * FrmViaticosControladorUrlEnum
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
public enum FrmViaticosControladorUrlEnum {

    URL8063("FRMVIATICOSCONTROLADORURL8063",
                    "769001"),

    URL17338("FRMVIATICOSCONTROLADORURL17338",
                    "931006"),

    URL7394("FRMVIATICOSCONTROLADORURL7394",
                    "62002"),

    URL22984("FRMVIATICOSCONTROLADORURL22984",
                    "761006"),

    URL9023("FRMVIATICOSCONTROLADORURL9023",
                    "931001"),

    URL5050("FRMVIATICOSCONTROLADORURL5050",
                    "931002"),

    URL15119("FRMVIATICOSCONTROLADORURL15119",
                    "931004"),

    URL12916("FRMVIATICOSCONTROLADORURL12916",
                    "931003"),

    URL8727("FRMVIATICOSCONTROLADORURL8727",
                    "768001"),

    URL23235("FRMVIATICOSCONTROLADORURL23235",
                    "931005"),

    URL4444("FRMVIATICOSCONTROLADORURL4444",
                    "71030"),

    URL5555("FRMVIATICOSCONTROLADORURL5555",
                    "210089"),

    URL7777("FRMVIATICOSCONTROLADORURL7777",
                    "463010"),

    URL8787("FRMVIATICOSCONTROLADORURL8787",
                    "764013"),

    URL5858("FRMVIATICOSCONTROLADORURL5858",
                    "761007"),

    URL6262("FRMVIATICOSCONTROLADORURL6262",
                    "51001"),
    
    URL1616("FRMVIATICOSCONTROLADORURL1616",
                    "764013")

    ;

    private final String key;
    private final String value;

    private FrmViaticosControladorUrlEnum(String key, String value) {
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
