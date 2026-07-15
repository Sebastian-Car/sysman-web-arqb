/*-
 * ActualizaparametrosretroactivosControladorUrlEnum.java
 *
 * 1.0
 *
 * 18/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 *
 * @version 1.0, 11/02/2019
 * @author mzanguna
 *
 */
public enum ConceptosdiansControladorUrlEnum {

    URL4925("PERIODOSCONTROLADORURL4925", "4028"),

    URL4926("PERIODOSCONTROLADORURL4926", "1770001"),

    URL4927("PERIODOSCONTROLADORURL4927", "1771001"),

    URL4928("PERIODOSCONTROLADORURL4927", "151001");

    private final String key;
    private final String value;

    private ConceptosdiansControladorUrlEnum(String key,
        String value) {
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
