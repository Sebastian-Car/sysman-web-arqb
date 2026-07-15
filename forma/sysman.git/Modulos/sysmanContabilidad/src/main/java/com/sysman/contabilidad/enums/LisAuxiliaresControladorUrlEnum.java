/*
 * LisAuxiliaresControladorUrlEnum
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
public enum LisAuxiliaresControladorUrlEnum {

    URL5222("LISAUXILIARESCONTROLADORURL5222", "16008"),

    URL5971("LISAUXILIARESCONTROLADORURL5971", "16010"),

    URL4272("LISAUXILIARESCONTROLADORURL4272", "15003"),

    URL3488("LISAUXILIARESCONTROLADORURL3488", "15005"),
    
    URL7965("LISAUXILIARESCONTROLADORURL7965", "20019"),

    URL7434("LISAUXILIARESCONTROLADORURL7434", "20017"),
    
	URL6436("LISAUXILIARESCONTROLADORURL6436", "14001"),

	URL6904("LISAUXILIARESCONTROLADORURL6904", "14031");

    private final String key;
    private final String value;

    private LisAuxiliaresControladorUrlEnum(String key, String value) {
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
