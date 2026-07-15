/*
 * PredialultimospagosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum PredialultimospagosControladorUrlEnum {

    URL4841("PREDIALULTIMOSPAGOSCONTROLADORURL4841", "367162"),

    URL4842("PREDIALULTIMOSPAGOSCONTROLADORURL4842", "367164"),

    URL4843("PREDIALULTIMOSPAGOSCONTROLADORURL4843", "367166"),

    URL4844("PREDIALULTIMOSPAGOSCONTROLADORURL4844", "367168"),

    URL4845("PREDIALULTIMOSPAGOSCONTROLADORURL4845", "381009"),;

    private final String key;
    private final String value;

    private PredialultimospagosControladorUrlEnum(String key,
        String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
