/*-
 * FrmCriteriosEvaluacionControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para almacenar las Url's de servicio para el
 * controlador FrmCriteriosEvaluacion.
 * 
 * @version 1.0, 8/02/2018
 * @author dnino
 *
 */
public enum FrmCriteriosEvaluacionControladorUrlEnum {

    URL405("FRMCRITERIOSEVALUACIONCONTROLADORURL405", "938002"),

    URL002("FRMCRITERIOSEVALUACIONCONTROLADORURL002", "752013"),

    URL001("FRMCRITERIOSEVALUACIONCONTROLADORURL001", "752011");

    private final String key;
    private final String value;

    private FrmCriteriosEvaluacionControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}