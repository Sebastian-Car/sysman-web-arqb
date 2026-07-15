/*-
 * FrminfdescuentosacasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 4/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrminfdescuentosacasControladorUrlEnum {

    URL001("FRMINFDESCUENTOSACASCONTROLADORURL001", "367037"),

    URL002("FRMINFDESCUENTOSACASCONTROLADORURL002", "367039");

    private final String key;
    private final String value;

    private FrminfdescuentosacasControladorUrlEnum(String key, String value) {
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
