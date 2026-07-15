/*
 * LfinanciablesdeudaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LfinanciablesdeudaControladorUrlEnum {

    URL10337("LFINANCIABLESDEUDACONTROLADORURL10337", "213085"),

    URL9338("LFINANCIABLESDEUDACONTROLADORURL9338", "213083"),

    URL7439("LFINANCIABLESDEUDACONTROLADORURL7439", "227031"),

    URL8695("LFINANCIABLESDEUDACONTROLADORURL8695", "227032"),

    URL11448("LFINANCIABLESDEUDACONTROLADORURL11448", "214063"),

    URL6835("LFINANCIABLESDEUDACONTROLADORURL6835", "227029"),

    URL8100("LFINANCIABLESDEUDACONTROLADORURL8100", "227030"),

    URL8101("LFINANCIABLESDEUDACONTROLADORURL8100", "213140");

    private final String key;
    private final String value;

    private LfinanciablesdeudaControladorUrlEnum(String key, String value) {
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
