/*-
 * FrmObjetivosDesSostControladorEnum.java
 *
 * 1.0
 * 
 * 16/12/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * usados en el controlador del formulario.
 * 
 * @version 1.0, 16/12/2019
 * @author jrodrigueza
 *
 */
public enum FrmObjetivosDesSostControladorEnum {

    /** Parametro ID */
    ID("ID"),

    /** Parametro VIGENCIA_ODS */
    VIGENCIA_ODS("VIGENCIA_ODS"),

    /** Parametro CODIGO_ODS */
    CODIGO_ODS("CODIGO_ODS"),

    /** Parametro CODIGO_META */
    CODIGO_META("CODIGO_ODS"),

    /** Parametro DESCRIPCION_META */
    DESCRIPCION_META("DESCRIPCION_META"),

    /** Parametro PORCENTAJE_PARTICIPACION */
    PORCENTAJE_PARTICIPACION("PORCENTAJE_PARTICIPACION")

    ;

    /**
     * Nombre de parametro.
     */
    private final String value;

    /** Define la estructura del enumeado */
    private FrmObjetivosDesSostControladorEnum(String value) {
        this.value = value;
    }

    /** Obtiene el valor del enumerado. */
    public String getValue() {
        return value;
    }
}
