/*
 * ComprobantecntsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeraci&oacute;n que permite clasificar cada uno de los
 * par&aacute;metros identificados en el refactoring, para ser
 * convertidos Map <String,String> y disponibles en dicha
 * enumeraci&oacute;n.
 */
public enum ComprobantecntsControladorEnum {

    TIPO("TIPO"),

    TIPOCOBRO("TIPOCOBRO"),

    REGIMEN("REGIMEN"),

    COMPROBANTE("COMPROBANTE"),

    TIPOCOMPROBANTE("TIPOCOMPROBANTE"),

    BASEA("BASEA"),

    CUENTABANCO("CUENTABANCO"),

    REGISTRO("REGISTRO"),

    SYSDATE("SYSDATE"),

    AUTORETENEDOR("AUTORETENEDOR"),

    DEBITOSAFECTADOS("DEBITOSAFECTADOS"),

    CREDITOSAFECTADOS("CREDITOSAFECTADOS"),

    CONCEPTO_SF("CONCEPTO_SF"),

    NUMEROCONTRATO("NUMEROCONTRATO"),

    DETALLE_COMPROBANTE_CNT("DETALLE_COMPROBANTE_CNT"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    TIPOCOBROCONCEPTO("TIPOCOBROCONCEPTO"),

    FECHA_VCN_DOC("FECHA_VCN_DOC"),

    PORCIVA("PORCIVA"),

    FECHAPAGADOGN("FECHAPAGADOGN"),
    
    FECHACONSIGNACION("FECHACONSIGNACION"),

    TIPOCONTRATO("TIPOCONTRATO"),

    TEXTO("TEXTO"),

    IMPRESO("IMPRESO"),

    VLRAGIRAR("VLRAGIRAR"),

    VLR_BASEIVA("VLR_BASEIVA"),

    NRO_DOCUMENTO("NRO_DOCUMENTO"),

    VLR_BASE("VLR_BASE"),

    PR_ACCION1_EN_ORDEN_DE_PAGO("PR_ACCION1_EN_ORDEN_DE_PAGO"),

    ACCION_EN_ORDEN_DE_PAGO("ACCION EN ORDEN DE PAGO"),

    ACCION1_EN_ORDEN_DE_PAGO("ACCION1 EN ORDEN DE PAGO"),

    VLR_DOCUMENTO("VLR_DOCUMENTO"),
    /**
     * Par&aacute;metro CUENTAPPTAL
     */
    CUENTAPPTAL("CUENTAPPTAL"),
    /**
     * Par&aacute;metro RUBRO_PPTAL
     */
    RUBRO_PPTAL("RUBRO_PPTAL"),

    LEY1450("LEY1450"),

    LEY1819("LEY1819"),
    
    FECHA_SOLICITUD("FECHA_SOLICITUD"),
    
    NUMEROFACTURA("NUMEROFACTURA"),
    
    FECHAINI("FECHAINI"),
    
    FECHAFIN("FECHAFIN"),
    
    NITCOMPANIA("NITCOMPANIA"),
    
    FECHA_VENCIMIENTO("FECHA_VENCIMIENTO"),
    
    FECHA("FECHA"),
    
    NOMBRETERCERO("NOMBRETERCERO"),
    
    BASEGRAVAVLEDETALLE("\"baseGravable\":0.0,"),
    
    IMPUESTOIVA("\"porcentajeIva\":0.0,"),
    
    VALORIMPUESTOIVA("\"valorIva\":0.0,"),
    
    IMPUESTOICA("\"porcentajeIca\":0.0,"),
    
    VALORIMPUESTOICA("\"valorIca\":0.0,"),
    
    IMPUESTOIMPOCONSUMO("\"porcentajeImpConsumo\":0.0,"),
    
    VALORIMPUESTOIMPOCONSUMO("\"valorImpConsumo\":0.0,"),
    
    NUMERO("NUMERO"),
    
    TERCERO("TERCERO"),
	
    CMPTE_AFECTADO("CMPTE_AFECTADO"),
    
    VALOR ("VALOR"),
    
    VALOR_DEBITO("VALOR_DEBITO"), 
    
    TIPOSIGEC("TIPOSIGEC"),
    
    EQUIV_SIGEC("EQUIV_SIGEC"),
    
    SIGEC("SIGEC"),
    
    TIPO_SIGEC("TIPO_SIGEC"),
    
    URL_SERVICIO_SOAP( "URL SERVICIO SOAP"),
    MANEJA_FACTURACION_ELECTRONICA_EXTERNA("MANEJA FACTURACION ELECTRONICA EXTERNA"),
    USUARIO_FACT_ELECTRONICA_EXTERNA("USUARIO FACT ELECTRONICA EXTERNA"),
    CLAVE_FACT_ELECTRONICA_EXTERNA("CLAVE FACT ELECTRONICA EXTERNA"), 
    
    TIPO_MEDIO_PAGO("TIPO_MEDIO_PAGO"), 
    
    TIPO_PAGO("TIPO_PAGO"), 
    
    DESCRIPCION("DESCRIPCION"), 
    
    ELABORO("ELABORO"), 
    
    ELABORO_FECHA("ELABORO_FECHA"), 
    
    CUDE("CUDE"), 
    
    TIPO_DOC("TIPO_DOC"), 
    
    NOTA("NOTA");
	
    private final String value;

    private ComprobantecntsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
