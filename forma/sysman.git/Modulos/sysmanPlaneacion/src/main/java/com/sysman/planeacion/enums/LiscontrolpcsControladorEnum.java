/*
 * LiscontrolpcsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum LiscontrolpcsControladorEnum {

    CODIGOINI("CODIGOINI"),

    REPORTE442("000442InfControlPCm"),

    FECHAINICIAL("fechaInicial"),

    FECHAFINAL("fechaFinal"),

    CODIGOINICIAL("codigoInicial"),

    CODIGOFINAL("codigoFinal"),

    ANO("ano"),

    PR_STRSQL("PR_STRSQL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    PR_FECHAS("PR_FECHAS"),

    PR_RUBROS("PR_RUBROS")

    ;

    private final String value;

    private LiscontrolpcsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
