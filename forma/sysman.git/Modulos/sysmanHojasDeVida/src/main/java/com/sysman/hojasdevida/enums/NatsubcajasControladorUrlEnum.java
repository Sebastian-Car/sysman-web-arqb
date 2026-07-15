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
 * @author jguerrero
 * 
 * @version 1.0, 14 de dic. de 2017
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NatsubcajasControladorUrlEnum {

    URL001("NATSUBCAJASCONTROLADOR001", "710001"),

    URL002("NATSUBCAJASCONTROLADOR002", "710002"),

    URL003("NATSUBCAJASCONTROLADOR003", "710003"),

    URL004("NATSUBCAJASCONTROLADOR004", "644002"),

    URL005("NATSUBCAJASCONTROLADOR005", "710006"),

    URL006("NATSUBCAJASCONTROLADOR006", "710007"),

    URL007("NATSUBCAJASCONTROLADOR007", "710009");

    private final String key;
    private final String value;

    private NatsubcajasControladorUrlEnum(String key, String value) {
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
