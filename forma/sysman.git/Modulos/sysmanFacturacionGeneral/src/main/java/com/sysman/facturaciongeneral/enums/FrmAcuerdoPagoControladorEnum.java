/*
 * FrmAcuerdoPagoControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmAcuerdoPagoControladorEnum {

    PAR_NUMFACSELECCIONADOS("numFacSeleccionados"),

    PAR_SUCURSAL("sucursal"),

    PAR_TERCERO("tercero"),

    PAR_ANIO("anio"),

    PAR_DEUDATOTAL("deudaTotal"),

    PAR_DEUDAINTERES("deudaInteres"),

    PAR_DEUDACAPITAL("deudaCapital"),

    PAR_NUMACUERDO("numAcuerdo"),

    PAR_TIPOCOBRO("tipoCobro"),

    MSM_PROCESO_EJECUTADO("MSM_PROCESO_EJECUTADO"),

    NIT("NIT"),

    TIPO("TIPO");

    private final String value;

    private FrmAcuerdoPagoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
