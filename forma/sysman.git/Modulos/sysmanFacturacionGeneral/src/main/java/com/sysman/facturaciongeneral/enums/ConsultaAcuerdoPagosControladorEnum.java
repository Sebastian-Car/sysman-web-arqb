/*-
 * ConsultaAcuerdoPagosControladorEnum.java
 *
 * 1.0
 * 
 * 27 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ConsultaAcuerdoPagosControladorEnum {

    CODACUERDO("CODACUERDO"),

    TIPOACUERDO("TIPOACUERDO"),

    TIPOCOBRO("TIPOCOBRO"),

    SF_ACUERDO_PAGO("SF_ACUERDO_PAGO"),

    SF_DETALLE_ACUERDO("SF_DETALLE_ACUERDO");

    private final String value;

    private ConsultaAcuerdoPagosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
