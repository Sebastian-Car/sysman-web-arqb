/*
 * CambiorutaControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum CambiorutaControladorEnum {
    TG_NOMBRE5("TG_NOMBRE5"),
    SI("SI"),
    FIMM("FIMM"),
    ULTIMO("ULTIMO"),
    PRIMERO("PRIMERO"),
    CODIGOINICIAL("CODIGOINICIAL"),
    CODIGOFINAL("CODIGOFINAL"),
    TB_TB1373("TB_TB1373"), 
    TB_TB1376("TB_TB1376"),
    TB_TB1377("TB_TB1377"),
    TB_TB1379("TB_TB1379"),
    TB_TB1375("TB_TB1375"),
    TB_TB1374("TB_TB1374"),
    TB_TB1380("TB_TB1380"),
    TB_TB1378("TB_TB1378"),
    SEGUNDOAPELLIDO("SEGUNDOAPELLIDO"),
    PRIMERAPELLIDO("PRIMERAPELLIDO"),
    PREFACTURANDO("PREFACTURANDO"),
    PR_MANEJA_PREFACTURACION("PR_MANEJA_PREFACTURACION"),
    
    NOMBRES("NOMBRES");

    private final String value;

    private  CambiorutaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
