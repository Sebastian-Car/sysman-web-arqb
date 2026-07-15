/*-
 * InformeContraloriaControladorEnum.java
 *
 * 1.0
 * 
 * 25/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 25/09/2017
 * @author jcrodriguez
 *
 */
public enum InformeContraloriaControladorEnum {
    OBJETO("OBJETO"),

    AVANCE("AVANCE"),

    VALOREJECUTADO("VALOREJECUTADO"),

    VIGENCIAFIN("VIGENCIAFIN"),

    FECHAREGISTRO("FECHAREGISTRO"),

    VALORTOTAL("VALORTOTAL"),

    NOMBREPROYECTO("NOMBREPROYECTO"),

    CODIGOBPIM("CODIGOBPIM"),

    CONSULTA800049("800049informeContraloria"),

    FICHA_EBI("FICHA EBI");

    private final String value;

    private InformeContraloriaControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
