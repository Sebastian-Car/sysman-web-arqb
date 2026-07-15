/*-
 * ImprimirHvPrestacionesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 14 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author amonroy
 * 
 * @version 1.0, 14 de dic. de 2017
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RequisitosposesionsControladorUrlEnum {

    URL001("REQUISITOSPOSESIONSCONTROLADORURLENUMURL001", "696001"),

    URL002("REQUISITOSPOSESIONSCONTROLADORURLENUMURL002", "696003"),

    URL003("REQUISITOSPOSESIONSCONTROLADORURLENUMURL003", "696002"),

    URL004("REQUISITOSPOSESIONSCONTROLADORURLENUMURL004", "696004");

    private final String key;
    private final String value;

    private RequisitosposesionsControladorUrlEnum(String key, String value) {
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
