/*
 * AuxiliarfacturacionconceptosControladorEnum
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
public enum AuxiliarfacturacionconceptosControladorEnum {

    FORMATO_CALIDAD("FORMATO CALIDAD"), 
    TB_TB1800("TB_TB1800"),
    TB_TB3158("TB_TB3158"),
    SI("SI"),
    ATRASO("ATRASO"),
    VALORES("VALORES"),
    CONCEPTOINICIAL("conceptoInicial"),
    PR_CICLO("PR_CICLO"),
    CONCEPTOFINAL("conceptoFinal"),
    CONDICIONCICLO("condicionCiclo"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),
    MSM_INFORME_VAR_NO_EXISTE("MSM_INFORME_VAR_NO_EXISTE"),
    PR_PERIODOS("PR_PERIODOS"),
    FORMULARIO001226("001226LAuxiliarFacturacionConcepto"),
    FORMULARIO001237("001237LAuxiliarFacturacionConceptoCOS");

    private final String value;

    private  AuxiliarfacturacionconceptosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
