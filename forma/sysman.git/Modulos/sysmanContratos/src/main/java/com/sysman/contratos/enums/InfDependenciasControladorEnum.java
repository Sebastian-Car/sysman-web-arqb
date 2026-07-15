/*
 * InfDependenciasControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum InfDependenciasControladorEnum {

    ANIOFINAL("anioFinal"),

    ANIOINICIAL("anioInicial"),

    DEPENDENCIA("dependencia"),

    COMPANIA("compania"),

    PIVOT2("pivotConsulta2"),

    ANIO("ANIO"),

    VALOR("VALOR TOTAL CONTRATADO"),

    DEPENDENCIAS4("800004Dependencias"),

    DEPENDENCIAS5("800005Dependencias"),

    DEPENDENCIAS6("800006Dependencias"),

    DEPENDENCIAS7("800007Dependencias"),

    INFORME("informe.xls");

    private final String value;

    private InfDependenciasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}