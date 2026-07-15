/*
 * PrequisicionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PrequisicionsControladorUrlEnum {

    URL9318("PREQUISICIONSCONTROLADORURL9318",
                    "62051"),

    URL10274("PREQUISICIONSCONTROLADORURL10274",
                    "112061"),

    URL7838("PREQUISICIONSCONTROLADORURL7838",
                    "62048"),

    URL14876("PREQUISICIONSCONTROLADORURL14876",
                    "193005"),

    URL11359("PREQUISICIONSCONTROLADORURL11359",
                    "193004"),

    URL5848("PREQUISICIONSCONTROLADORURL5848",
                    "193002"),

    URL9257("PREQUISICIONSCONTROLADORURL9257",
                    "551002"),

    URL8712("PREQUISICIONSCONTROLADORURL8712",
                    "62002"),

    URL8888("PREQUISICIONSCONTROLADORURL8888",
                    "193003"),

    URL9597("PREQUISICIONSCONTROLADORURL9597",
                    "62047"),
    

    URL7777("PREQUISICIONSCONTROLADORURL7777",
                    "62002"),

    ;

    private final String key;
    private final String value;

    private PrequisicionsControladorUrlEnum(String key, String value) {
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
