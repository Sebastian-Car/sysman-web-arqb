/*
 * ReporteTarifasAcueductoUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ReporteTarifasAcueductoUrlEnum {

    URL14772("REPORTETARIFASACUEDUCTOURL14772",
                    "317001"),

    URL8298("REPORTETARIFASACUEDUCTOURL8298",
                    "227033"),

    URL9696("REPORTETARIFASACUEDUCTOURL9696",
                    "227036"),

    URL9078("REPORTETARIFASACUEDUCTOURL9078",
                    "227001"),

    URL7670("REPORTETARIFASACUEDUCTOURL7670",
                    "227001"),

    URL10543("REPORTETARIFASACUEDUCTOURL10543",
                    "227005");

    private final String key;
    private final String value;

    private ReporteTarifasAcueductoUrlEnum(String key, String value)
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
