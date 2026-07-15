/*
 * FrmrptsectoresdnpControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmrptsectoresdnpControladorEnum {
    TG_NO_EXISTE("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"),

    REPORTE000293("000293rptSectoresDnpsubsectores"),

    PR_STRSQL("PR_STRSQL"),

    REPORTE000292("000292rptSectoresDNP"),

    CODIGODNP("CODIGODNP"),

    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA");

    private final String value;

    private FrmrptsectoresdnpControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
