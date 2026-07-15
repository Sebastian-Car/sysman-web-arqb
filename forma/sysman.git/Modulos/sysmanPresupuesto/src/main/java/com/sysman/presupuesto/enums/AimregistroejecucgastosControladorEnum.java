/*
 * AimregistroejecucgastosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum AimregistroejecucgastosControladorEnum {

    ANIO("ANIO"), ID("ID"), APROPIACIONVIGENTE("apropiacionVigente"), COMPROMISOSACUM("compromisosAcum"), OBLIGACIONESACUM(
                    "obligacionesAcum"), PAGOSACUM("pagosAcum"), SYSDATE("SYSDATE"), COMPROMISOSMES("compromisosMes"), OBLIGACIONESMES(
                                    "obligacionesMes"), PAGOSMES("pagosMes"), MES("mes"), CUENTAINICIALL("cuentaInicial"), CUENTAFINAL(
                                                    "cuentaFinal"), NIVEL("nivel"), MILES("miles"), NOMBREREOPRTE1(
                                                                    "000977REGISTROEJECUCGASTOS036AIMEQ"), NOMBREREPORTE2(
                                                                                    "000971REGISTROEJECUCGASTOS036AIM"), MSM_TRANS_INTERRUMPIDA(
                                                                                                    "MSM_TRANS_INTERRUMPIDA"), FIRMA1(
                                                                                                                    "FIRMA1 EN RESOLUCION 036 ESPECIAL"), FIRMA2(
                                                                                                                                    "FIRMA2 EN RESOLUCION 036 ESPECIAL"), FIRMA3(
                                                                                                                                                    "FIRMA3 EN RESOLUCION 036 ESPECIAL"), CARGO1(
                                                                                                                                                                    "CARGO1 EN RESOLUCION 036 ESPECIAL"), CARGO2(
                                                                                                                                                                                    "CARGO2 EN RESOLUCION 036 ESPECIAL"), CARGO3(
                                                                                                                                                                                                    "CARGO3 EN RESOLUCION 036 ESPECIAL"), PR_CONTRALORIADEPARTAMENTAL(
                                                                                                                                                                                                                    "PR_CONTRALORIADEPARTAMENTAL"), PR_NOMBRECOMPANIA(
                                                                                                                                                                                                                                    "PR_NOMBRECOMPANIA"), PR_NOMBREMES(
                                                                                                                                                                                                                                                    "PR_NOMBREMES"), CONTRALORIA_DEPARTAMENTAL(
                                                                                                                                                                                                                                                                    "CONTRALORIA DEPARTAMENTAL"), PR_FIRMARESOLUCION1(
                                                                                                                                                                                                                                                                                    "PR_FIRMARESOLUCION1"), PR_FIRMARESOLUCION2(
                                                                                                                                                                                                                                                                                                    "PR_FIRMARESOLUCION2"), PR_FIRMARESOLUCION3(
                                                                                                                                                                                                                                                                                                                    "PR_FIRMARESOLUCION3"), PR_CARGORESOLUCION1(
                                                                                                                                                                                                                                                                                                                                    "PR_CARGORESOLUCION1"), PR_CARGORESOLUCION2(
                                                                                                                                                                                                                                                                                                                                                    "PR_CARGORESOLUCION2"), PR_CARGORESOLUCION3(
                                                                                                                                                                                                                                                                                                                                                                    "PR_CARGORESOLUCION3"), CUENTAINICIAL(
                                                                                                                                                                                                                                                                                                                                                                                    "CUENTAINICIAL");

    private final String value;

    private AimregistroejecucgastosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
