/*
 * FrmcarteraresumenporvigenciasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmcarteraresumenporvigenciasControladorEnum {
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"), PR_NITCOMPANIA("PR_NITCOMPANIA"), PR_TCODIGOFINAL(
                    "PR_TCODIGOFINAL"), PR_TCODIGOINICIAL("PR_TCODIGOINICIAL"), PR_NOMBRECOMPANIA(
                                    "PR_NOMBRECOMPANIA"), PR_VISIBLE(
                                                    "PR_VISIBLE"), REPORTE000841(
                                                                    "000841infcarteraresumenporvigencias"), CODIGO_INICIAL(
                                                                                    "CODIGO_INICIAL");

    private final String value;

    private FrmcarteraresumenporvigenciasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
