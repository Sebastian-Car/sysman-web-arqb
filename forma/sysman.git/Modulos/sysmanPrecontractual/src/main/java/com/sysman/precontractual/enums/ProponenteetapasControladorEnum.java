/*
 * ProponenteetapasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ProponenteetapasControladorEnum {

    TIPOCONTRATO_LOWER("tipoContrato"),

    CONSECUTIVO_TRANSACCION_LOWER("consecutivoTransaccion"),

    CONSECUTIVO_DETALLE_LOWER("consecutivoDetalle"),

    ID_ETAPA_LOWER("idEtapa"),

    NOMBRE_ETAPA_LOWER("nombreEtapa"),

    RID_LOWER("rid"),

    ANIO_LOWER("anio"),

    CONDICION_LOWER("condicion"),

    ESTADO_VIGENCIA_LOWER("estadoVigencia"),

    ESTADO_ETAPA_LOWER("estadoEtapa"),

    ESTADO_PROCESO_LOWER("estadoProceso"),

    REDON_VAL_UNI_IVA_LOWER("redonValorUnitarioIVA"),

    DIG_RED_VAL_UNI_IVA_LOWER("digRedoValorUnitarioIVA"),

    REDONDEO_TOTAL_LOWER("redondeoTotal"),

    DIG_REDON_TOTAL("digRedonTotal"),

    COTIZAR_INVENTARIO_LOWER("cotizaInventario"),

    DESDE_MONITOR_LOWER("desdeMonitor"),

    PROPONENTE_LOWER("proponente"),

    NOMBRE_PROPONENTE_LOWER("nombreProponente"),

    SUCURSAL_LOWER("sucursal"),

    ID_ESTAPA_LOWER("idEtapa"),

    MODIFICAR("modificar"),

    ESTADO_PROPONENTE_LOWER("estadoProponente"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    FECHAINSCRIPCION("FECHAINSCRIPCION"),

    TRANSACCION("TRANSACCION"),

    PROPONENTE("PROPONENTE"),

    CONSECUTIVODETALLE("CONSECUTIVODETALLE"),

    ESTADOLB("ESTADOLB"),

    ;

    private final String value;

    private ProponenteetapasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
