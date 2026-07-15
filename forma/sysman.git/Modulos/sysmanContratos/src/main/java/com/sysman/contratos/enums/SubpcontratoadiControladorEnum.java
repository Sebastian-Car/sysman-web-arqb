/*
 * SubpcontratoadiControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum SubpcontratoadiControladorEnum {

    TABLA("D_ORDENDECOMPRA"),
    PARAM13("ORDENDECOMPRAINI"),
    PARAM12("CLASEORDENINI"),
    PARAM11("KEY_CODIGO"),
    PARAM10("KEY_ORDENDECOMPRA"),
    PARAM9("KEY_CLASEORDEN"),
    PARAM8("KEY_COMPANIA"),
    PARAM7("ITEMORIGEN"),
    PARAM6("TIPOORIGEN"),
    PARAM5("NUMEROORIGEN"),
    PARAM4("ORDENDECOMPRA"),
    PARAM3("TIPO"), 
    PARAM1("TIPOAFECTADO"),  
    PARAM2("NUMEROAFECTADO"),  
    PARAM0("TIPOPPTO");

    private final String value;

    private  SubpcontratoadiControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
