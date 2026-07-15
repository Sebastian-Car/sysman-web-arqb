/*
 * PredialreldiacaringrereanoControladorEnum
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
public enum PredialreldiacaringrereanoControladorEnum {
    CONCEPTO_DE_DESCUENTO("CONCEPTO DE DESCUENTO"), FIRMA_JEFE_SECCION_RELACIONES_DETALLADAS(
                    "FIRMA JEFE SECCION RELACIONES DETALLADAS"), OTROS("OTROS"), PORCENTAJE_DESCUENTO_PREDIAL(
                                    "porcentaje descuento a predial"), OBSERVACIONES_INFORME_RECAUDO(
                                                    "OBSERVACIONES INFORME RECAUDO"), CONCEPTO_PARA_DESCUENTO_CAR(
                                                                    "CONCEPTO PARA DESCUENTO CAR"), PR_ENCABEZADO_UNO(
                                                                                    "PR_ENCABEZADO_UNO"), PR_ENCABEZADO_DOS(
                                                                                                    "PR_ENCABEZADO_DOS"), PR_ENCABEZADO_TRES(
                                                                                                                    "PR_ENCABEZADO_TRES"), PORCENTAJE_DESCUENTO_CAR(
                                                                                                                                    "porcentaje descuento a car"), CODIGO_BANCO_COMPENSACIONES(
                                                                                                                                                    "CODIGO BANCO COMPENSACIONES"), DESCAPARTE(
                                                                                                                                                                    "descAparte"), PR_FORMS_DESCAPARTE(
                                                                                                                                                                                    "PR_FORMS_DESCAPARTE"), ENCABEZADO(
                                                                                                                                                                                                    "ENCABEZADO"), PAQUETEINICIAL(
                                                                                                                                                                                                                    "PAQUETEINICIAL"), FECHAINICIAL(
                                                                                                                                                                                                                                    "FECHAINICIAL"), FECHAFINAL(
                                                                                                                                                                                                                                                    "FECHAFINAL"), BANCOINICIAL(
                                                                                                                                                                                                                                                                    "BANCOINICIAL"), BANCOFINAL(
                                                                                                                                                                                                                                                                                    "BANCOFINAL"), REPORTE001452(
                                                                                                                                                                                                                                                                                                    "001452PREDIALRELDIACARINGREANO2"), REPORTE001450(
                                                                                                                                                                                                                                                                                                                    "001450PREDIALRELDIACARINGREANO1"), REPORTE001419(
                                                                                                                                                                                                                                                                                                                                    "001419PREDIALRELDIACARANODET"), REPORTE000905(
                                                                                                                                                                                                                                                                                                                                                    "000905PREDIALRELDIAPREDIALANOVTQ"), REPORTE000902(
                                                                                                                                                                                                                                                                                                                                                                    "000902PREDIALRELDIAPREDIALANOVL"), REPORTE000901(
                                                                                                                                                                                                                                                                                                                                                                                    "000901PREDIALRELDIAPREDIALANOPTO"), REPORTE000910(
                                                                                                                                                                                                                                                                                                                                                                                                    "000910PREDIALRELDIACARANO"), REPORTE000908(
                                                                                                                                                                                                                                                                                                                                                                                                                    "000908PREDIALRELDIACARANOPTO"), REPORTE000896(
                                                                                                                                                                                                                                                                                                                                                                                                                                    "000896PREDIALRELDIACARINGREANO3"), REPORTE000894(
                                                                                                                                                                                                                                                                                                                                                                                                                                                    "000894PREDIALRELDIACARINGREANOVTQ"), REPORTE000880(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "000880PREDIALRELDIACARINGREANOPTO"), REPORTE000888(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "000888PREDIALRELDIACARINGREANOVL"), CNIT(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "8912800003"), NOMBREBANCO(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "NOMBREBANCO"), MSM_TRANS_INTERRUMPIDA(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "MSM_TRANS_INTERRUMPIDA"), MSM_INFORME_NO_EXISTE(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "MSM_INFORME_NO_EXISTE"), CODIGOBANCO(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "CODIGOBANCO");

    private final String value;

    private PredialreldiacaringrereanoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
