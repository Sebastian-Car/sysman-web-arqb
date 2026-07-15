/*
 * FrmestadoetapasControladorEnum
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
public enum FrmestadoetapasControladorEnum {

    IDETAPA("IDETAPA"),

    ETAPAINI("ETAPAINI"),

    CONTRATOINICIAL("CONTRATOINICIAL"),

    AC("AC"),

    REPORTE421("000421EstadoEtapas"),

    COND_ESTADO_A(" AND D_TRANSACCION.ESTADO = 'A' "),

    CND_ESTADO_P(" AND D_TRANSACCION.ESTADO = 'P' "),

    COND_ESTADO_DEFAULT(" AND D_TRANSACCION.ESTADO = 'C' "),

    TIPOCONTRATOLOWER("tipoContrato"),

    CONTRATOINICIALLOWER("contratoInicial"),

    CONTRATOFINALLOWER("contratoFinal"),

    ETAPAINICIALLOWER("etapaInicial"),

    ETAPAFINALLOWER("etapaFinal"),

    ESTADOTRASACCION("estadoTransaccion"),

    NITLOWER("Nit."),

    PR_STRSQL("PR_STRSQL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    PR_NITCOMPANIA("PR_NITCOMPANIA");

    private final String value;

    private FrmestadoetapasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
