/*
 * LisauxiliarsaldosControladorUrlEnum
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
public enum LisauxiliarsaldosControladorUrlEnum {

    URL4567("LISAUXILIARSALDOSCONTROLADORURL4567", "15005"),

    URL5040("LISAUXILIARSALDOSCONTROLADORURL5040", "15003"),

    URL5573("LISAUXILIARSALDOSCONTROLADORURL5573", "16108"),

    URL5972("LISAUXILIARSALDOSCONTROLADORURL5972", "16110"),

    URL7965("LISAUXILIARSALDOSCONTROLADORURL7965", "20019"),

    URL7434("LISAUXILIARSALDOSCONTROLADORURL7434", "20017"),

    URL6436("LISAUXILIARSALDOSCONTROLADORURL6436", "14001"),
    URL6904("LISAUXILIARSALDOSCONTROLADORURL6904", "14033"),
    URL1717("LISAUXILIARSALDOSCONTROLADORURL1717", "13028"),//Referencia Inicial
    URL1718("LISAUXILIARSALDOSCONTROLADORURL1718","13030");	//Referencia Final
	

    private final String key;
    private final String value;

    private LisauxiliarsaldosControladorUrlEnum(String key, String value) {
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
