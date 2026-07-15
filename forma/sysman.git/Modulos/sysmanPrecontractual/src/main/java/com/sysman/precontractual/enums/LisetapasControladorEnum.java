/*
 * LisetapasControladorEnum
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
public enum LisetapasControladorEnum {

    TIPOCONTRATO("TIPOCONTRATO"),

    ETAPAINICIAL("ETAPAINICIAL"),

    TIPOCONTRATOLOWER("tipoContrato"),

    ETAPAINICIALLOWER("etapaInicial"),

    ETAPAFINALLOWER("etapaFinal"),

    REPORTE410("000410LisEtapasxTipoTTP"),

    PR_STRSQL("PR_STRSQL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    PR_NITCOMPANIA("PR_NITCOMPANIA");

    private final String value;

    private LisetapasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
