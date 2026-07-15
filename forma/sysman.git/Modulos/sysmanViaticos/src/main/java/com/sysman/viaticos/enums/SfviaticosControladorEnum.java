/*-
 * SfviaticosControladorEnum.java
 *
 * 1.0
 *
 * 19/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SfviaticosControladorEnum {

    CODIGO_CONCEPTO("CODIGO_CONCEPTO"),

    NIT("NIT"),

    VALDIASSINPER("VALDIASSINPER"),

    VALDIASPER("VALDIASPER"),

    VALOR_ABONADO("VALOR_ABONADO"),

    SALDO("SALDO"),

    TIPO_VIATICO("TIPO_VIATICO"),

    ESCALAFON("ESCALAFON"),

    CATEGORIA("CATEGORIA")

    ;

    private final String value;

    private SfviaticosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
