/*
 * InformedecontratosporpresupuestoControladorEnum
 *
 * 1.0
 *
 * 10/08/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum InformedecontratosporpresupuestoControladorEnum {

    CODIGOINI("CODIGOINI"),

    ID("ID"),

    RUBROINICIAL("RUBROINICIAL"),

    NUMEROPPTO("NUMEROPPTO"),

    ANOINICIAL("ANOINICIAL"),

    ANOFINAL("ANOFINAL"),

    NIT("NIT"),

    TECEROINICIAL("TECEROINICIAL"),

    TB_TB2133("TB_TB2133"),

    TB_TB2132("TB_TB2132"),

    TB_TB2129("TB_TB2129"),

    TB_TB2128("TB_TB2128"),

    PR_TITULO_INFORME_CONTRATOS("PR_TITULO_INFORME_CONTRATOS"),

    PR_STRSQL("PR_STRSQL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    PR_CARGO_INFORME_CONTRATOS("PR_CARGO_INFORME_CONTRATOS"),

    TIPO("TIPO");

    private final String value;

    private InformedecontratosporpresupuestoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
