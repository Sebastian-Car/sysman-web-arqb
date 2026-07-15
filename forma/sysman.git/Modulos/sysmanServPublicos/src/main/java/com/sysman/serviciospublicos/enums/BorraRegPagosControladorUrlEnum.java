/*
 * BorraRegPagosControladorUrlEnum
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
public enum BorraRegPagosControladorUrlEnum {

    URL3581("BORRAREGPAGOSCONTROLADORURL3581", "345001"),

    URL2857("BORRAREGPAGOSCONTROLADORURL2857", "228001"),

    URL2858("BORRAREGPAGOSCONTROLADORURL2857", "213032");

    private final String key;
    private final String value;

    private BorraRegPagosControladorUrlEnum(String key, String value) {
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
