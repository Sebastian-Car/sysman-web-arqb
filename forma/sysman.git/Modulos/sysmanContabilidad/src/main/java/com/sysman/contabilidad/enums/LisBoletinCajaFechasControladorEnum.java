/*
 * LisBoletinCajaFechasControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum LisBoletinCajaFechasControladorEnum {   

    FECHAINI("FECHAINI"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),
    INFORME1("000769BoletindeCaja3"),
    INFORME2("000770RelCompEmitidos"),
    INFORME3("000768BoletindeCaja1"),
    INFORME4("000754FlujoDeCaja"),
    FECHAINICIAL("fechaInicial"),
    SYSDATE("SYSDATE"),
    CUENTAINI("CUENTAINI"),
    FECHAFINAL("fechaFinal"),
    MSM_INFORME_NO_EXISTE("MSM_INFORME_NO_EXISTE");


    private final String value;

    private  LisBoletinCajaFechasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
