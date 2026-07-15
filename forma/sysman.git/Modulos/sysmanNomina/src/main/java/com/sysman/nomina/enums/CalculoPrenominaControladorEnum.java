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
 * @version 1.0, 18/08/2017
 * @author pespitia
 *
 */
public enum CalculoPrenominaControladorEnum {

    NOMBRETIPOPARAMETRO("NOMBRETIPOPARAMETRO"),

    TIPOPARAMETRO("TIPOPARAMETRO"),

    MODULO("MODULO");

    private final String value;

    private CalculoPrenominaControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
