/*
 * PredialRelDiaPagBancControladorUrlEnum
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
public enum PredialRelDiaPagBancControladorUrlEnum {

    URL4841("PREDIALRELDIAPAGBANCCONTROLADORURL4841", "375004"),

    URL4842("PREDIALRELDIAPAGBANCCONTROLADORURL4842", "375006"),

    URL4843("PREDIALRELDIAPAGBANCCONTROLADORURL4843", "381008"),;

    private final String key;
    private final String value;

    private PredialRelDiaPagBancControladorUrlEnum(String key,
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
