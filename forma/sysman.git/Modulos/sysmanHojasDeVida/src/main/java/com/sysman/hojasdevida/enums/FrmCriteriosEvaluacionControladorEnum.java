/*-
 * FrmCriteriosEvaluacionControladorEnum.java
 *
 * 1.0
 * 
 * 19/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumerado reservado para las constantes del archivo
 * FrmCriteriosEvaluacionControlador.
 * 
 * @version 1.0, 19/02/2018
 * @author dnino
 *
 */
public enum FrmCriteriosEvaluacionControladorEnum {

    PARAM0("claseEvaluacion"),

    PARAM1("CLASE_EVALUACION"),

    PARAM2("CRITERIOS DE EVALUACION - ENCUESTAS"),

    PARAM3("CRITERIOS DE EVALUACION - "),

    PARAM4("2"),

    ESCOMPROMISO("ESCOMPROMISO"),

    COPIAR_DE("COPIAR_DE");

    private final String value;

    private FrmCriteriosEvaluacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
