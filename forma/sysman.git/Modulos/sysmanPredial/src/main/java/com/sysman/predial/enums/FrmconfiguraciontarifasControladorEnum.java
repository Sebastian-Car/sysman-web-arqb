/*
 * FrmconfiguraciontarifasControladorEnum
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
public enum FrmconfiguraciontarifasControladorEnum {

    CLASEINICIAL("CLASEINICIAL"),

    CLASE_INICIAL("CLASE_INICIAL"),

    CODIGOINICIAL("CODIGOINICIAL"),

    TIPOINICIAL("TIPOINICIAL"),

    TRPCOD("TRPCOD"),

    TRPANO("TRPANO"),

    NUEVES("9999999999"),

    NUMERO_ORDENF("NUMERO_ORDENF"),

    IND_CLASEPREDIO("IND_CLASEPREDIO"),

    IND_ESTRATO("IND_ESTRATO"),

    TIPO_INICIAL("TIPO_INICIAL"),

    TIPO_FINAL("TIPO_FINAL"),

    ESTRATO_INICIAL("ESTRATO_INICIAL"),

    ESTRATO_FINAL("ESTRATO_FINAL"),

    IND_TIPOESTRATO("IND_TIPOESTRATO"),

    TRPRAN("TRPRAN")

    ;

    private final String value;

    private FrmconfiguraciontarifasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
