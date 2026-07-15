/*
 * AnalisiscarteracxcControladorUrlEnum
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
public enum AnalisiscarteracxcControladorUrlEnum {

    URL2759("ANALISISCARTERACXCCONTROLADORURL2759", "14036"),

    URL3332("ANALISISCARTERACXCCONTROLADORURL3332", "14033"),
    
    URL16209("FRMCARTERAFINANCIABLESCONTROLADORURL16209", "16209"),
    
    URL16207("FRMCARTERAFINANCIABLESCONTROLADORURL16207", "16207");

    private final String key;
    private final String value;

    private AnalisiscarteracxcControladorUrlEnum(String key, String value) {
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
