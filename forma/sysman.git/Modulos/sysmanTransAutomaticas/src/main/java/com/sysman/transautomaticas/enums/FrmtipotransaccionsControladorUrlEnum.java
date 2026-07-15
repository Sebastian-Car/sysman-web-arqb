/*
 * FrmprogramacionfinancieraControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.transautomaticas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmtipotransaccionsControladorUrlEnum {

    URL1993("FRMTIPOTRANSACCIONSCONTROLADORURL1993",
                    "15005"),

    URL1994("FRMTIPOTRANSACCIONSCONTROLADORURL1994",
                    "15005"), //cuenta Contable 

    URL1995("FRMTIPOTRANSACCIONSCONTROLADORURL1995",
                    "25053"),

    URL1996("FRMTIPOTRANSACCIONSCONTROLADORURL1996",
                    "25044"),

    URL1997("FRMTIPOTRANSACCIONSCONTROLADORURL1997",
                    "6002");

    private final String key;
    private final String value;

    private FrmtipotransaccionsControladorUrlEnum(String key,
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
