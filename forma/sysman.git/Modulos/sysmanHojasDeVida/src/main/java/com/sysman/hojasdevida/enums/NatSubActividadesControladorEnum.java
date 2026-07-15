/*
 * EstratosfgControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum NatSubActividadesControladorEnum {

    PARAM0("FECHAINICIO"),
    
    PARAM1("FECHAFIN"),
    
    PARAM2("AC_ANOS"),
    
    PARAM3("AC_MESES");

    private final String value;

    private NatSubActividadesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
