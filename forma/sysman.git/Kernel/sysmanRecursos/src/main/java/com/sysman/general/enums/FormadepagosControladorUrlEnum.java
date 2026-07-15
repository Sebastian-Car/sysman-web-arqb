/*
 * FormadepagosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FormadepagosControladorUrlEnum {

    URL7918("FORMADEPAGOSCONTROLADORURL7918", "186001"), URL6228(
                    "FORMADEPAGOSCONTROLADORURL6228",
                    "186002"), URL18625("FORMADEPAGOSCONTROLADORURL18625",
                                    "186003"), URL12241(
                                                    "FORMADEPAGOSCONTROLADORURL12241",
                                                    "82004"), URL001(
                                                                    "FORMADEPAGOSCONTROLADORURL001",
                                                                    "1704001");

    private final String key;
    private final String value;

    private FormadepagosControladorUrlEnum(String key, String value) {
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
