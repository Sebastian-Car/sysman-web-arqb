/*-
 * CobroPersuasivoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 17/05/2017
 * @author jrodrigueza
 *
 */
public enum CobroPersuasivoControladorUrlEnum {
    URL2559("COBROPERSUASIVOCONTROLADOR2559", "213046"),

    URL2779("COBROPERSUASIVOCONTROLADOR2779", "213048"),

    URL2999("COBROPERSUASIVOCONTROLADOR2999", "214040"),

    URL3149("COBROPERSUASIVOCONTROLADOR3149", "104019");

    private final String key;
    private final String value;

    private CobroPersuasivoControladorUrlEnum(String key,
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
