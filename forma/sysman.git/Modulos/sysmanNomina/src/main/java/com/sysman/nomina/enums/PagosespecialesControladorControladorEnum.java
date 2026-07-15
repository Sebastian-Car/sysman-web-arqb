/*-
 * ActualizaparametrosretroactivosControladorEnum.java
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
 * Enumeracion que permite clasificar cada uno de los parametros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeracion.
 *
 * @version 1.0, 28/03/2019
 * @author mzanguna
 *
 */
public enum PagosespecialesControladorControladorEnum {

    CATEGORIA("ID_DE_CATEGORIA"),

    CODPAGO("CODPAGO"),

    DETALLEPAGOESPECIAL("DETALLEPAGOESPECIAL"),

    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

    ANO("ANIO"),

    CATESCALAFON("CATEGORIAESCALAFONREF")

    ;

    private final String value;

    private PagosespecialesControladorControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
