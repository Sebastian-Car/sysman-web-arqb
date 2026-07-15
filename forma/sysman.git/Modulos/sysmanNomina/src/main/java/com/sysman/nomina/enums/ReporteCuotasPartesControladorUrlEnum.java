/*
 * ReporteCuotasPartesControladorUrlEnum
 *
 * 1.0
 *
 * 24/10/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ReporteCuotasPartesControladorUrlEnum {

    URL7540("REPORTECUOTASPARTESCONTROLADORURL7540", "471002"),

    URL7541("REPORTECUOTASPARTESCONTROLADORURL7541", "7024"),

    URL7542("REPORTECUOTASPARTESCONTROLADORURL7542", "471003"),

    URL7543("REPORTECUOTASPARTESCONTROLADORURL7543", "537001"),

    URL7544("REPORTECUOTASPARTESCONTROLADORURL7544", "210104"),

    ;

    private final String key;
    private final String value;

    private ReporteCuotasPartesControladorUrlEnum(String key, String value)
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
