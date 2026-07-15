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
public enum ConfPagoaTercerosControladorUrlEnum {

    URL2154("CONFPAGOATERCEROSCONTROLADORURL2154", "36007"),

    URL2157("CONFPAGOATERCEROSCONTROLADORURL2157", "151040"),

    URL3541("CONFPAGOATERCEROSCONTROLADORURL3541", "151042"),

    URL8764("CONFPAGOATERCEROSCONTROLADORURL8764", "14185");

    private final String key;
    private final String value;

    private ConfPagoaTercerosControladorUrlEnum(String key,
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
