/*
 * RDepreciacionMesPeriodoEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum RDepreciacionMesPeriodoEnum {
    CADENA("D,N,E"),
    PARAM2("PARAM2"),  
    PARAM1("PARAM1"),  
    ELEMENTOINICIAL("ELEMENTOINICIAL"),
    CODIGOELEMENTO("CODIGOELEMENTO"),
    CONDICION("condicion"),
    GRUPO("grupo"),
    PARAMETRO("parametro"),
    OPCION1("10040316"),
    OPCION2("1007030203"),
    OPCION3("1007030202"),
    OPCION4("1007030201"),
    NIOMBREREPORTE("000549IDepreciarMesPeriodo"),
    PARAMETROSISTEMA("DIGITOS AGRUPACION INVENTARIO"),
    SYSDATE("SYSDATE"),
    FECHA("fecha"),
    PR_STRSQL("PR_STRSQL"),
    PR_FORMS_RDEPRECIACIONMESPERIODO_MES("PR_FORMS_RDEPRECIACIONMESPERIODO_MES"),
    PR_FORMS_RDEPRECIACIONMESPERIODO_ANO("PR_FORMS_RDEPRECIACIONMESPERIODO_ANO"),
    PR_AGRUPADO("PR_AGRUPADO"),
    NOMBRELARGO("NOMBRELARGO"),
    PARAM0("PARAM0");

    private final String value;

    private  RDepreciacionMesPeriodoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
