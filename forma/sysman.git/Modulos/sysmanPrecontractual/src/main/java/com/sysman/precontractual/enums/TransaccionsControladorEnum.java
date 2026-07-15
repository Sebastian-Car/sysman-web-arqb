/*
 * TransaccionsControladorEnum
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
public enum TransaccionsControladorEnum {

    TRANSACCIONPAR("TRANSACCIONPAR"),

    TIPO_CONTRATO("TIPO_CONTRATO"),

    CODESTUDIO("CODESTUDIO"),

    VARIABLE("VARIABLE"),

    ANOTX("ANOTX"),

    CONSECUTIVODETALLE("CONSECUTIVODETALLE"),

    COTIZA_ELEMENTOS("COTIZA_ELEMENTOS"),

    ESTADOETAPA("ESTADOETAPA"),

    ESTUDIOPREVIO("ESTUDIOPREVIO"),

    HEREDAPROPONENTE("HEREDAPROPONENTE"),

    IDELEMENTO("IDELEMENTO"),

    IDETAPA("IDETAPA"),

    NOMBRE_ESTUDIO("NOMBRE_ESTUDIO"),

    NOMESTADOVIG("NOMESTADOVIG"),

    NOM_TIPO("NOM_TIPO"),

    NUM_PROCESO("NUM_PROCESO"),

    TRANSACCION("TRANSACCION"),

    TRANSACCIONITEMINVENTARIO("TRANSACCIONITEMINVENTARIO"),

    TB_TB2229("TB_TB2229"),

    TB_TB560("TB_TB560"),

    CONSECUTIVODETALLESTR("consecutivoDetalle"),

    CONSECUTIVOTRANSACCION("consecutivoTransaccion"),

    FORMATOFECHA("dd/MM/yyyy"),

    DESDEMONITOR("desdeMonitor"),

    ESTADOETAPASTR("estadoEtapa"),

    IDETAPASTR("idEtapa"),

    NOMBREETAPA("nombreEtapa"),

    TIPOCONTRATO("tipoContrato"),

    TRANSACCIONSTR("transaccion"),

    HORA("HORA"),

    FECHAINICIALLB("FECHAINICIALLB"),

    FECHAFINALLB("FECHAFINALLB"),

    NOM_ESTADOETAPA("NOM_ESTADOETAPA"),

    ELEMENTOS("ELEMENTOS"),

    VALUNITOTAL("VALUNITOTAL"),

    VALIVATOTAL("VALIVATOTAL"),

    VALDESCTOTAL("VALDESCTOTAL"),

    VALTOTAL("VALTOTAL"),

    NOM_ESTADO("NOM_ESTADO"),

    ORDENACTUAL("ORDENACTUAL");

    private final String value;

    private TransaccionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
