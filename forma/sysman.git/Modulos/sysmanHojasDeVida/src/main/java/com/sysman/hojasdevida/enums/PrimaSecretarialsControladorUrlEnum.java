/*-
 * PrimaSecretarialsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 21 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 21 de dic. de 2017
 * @author amonroy
 *
 */
public enum PrimaSecretarialsControladorUrlEnum {

    URL001("PRIMASECRETARIALSCONTROLADORURL001", "686001"), // INSERT

    URL002("PRIMASECRETARIALSCONTROLADORURL002", "686002"), // UPDATE

    URL003("PRIMASECRETARIALSCONTROLADORURL003", "68600D"), // DELETE

    URL004("PRIMASECRETARIALSCONTROLADORURL004", "686003"), // PAGINADO

    URL005("PRIMASECRETARIALSCONTROLADORURL005", "70000G"),

    URL006("PRIMASECRETARIALSCONTROLADORURL006", "70000G");

    private final String key;
    private final String value;

    private PrimaSecretarialsControladorUrlEnum(String key, String value) {
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
