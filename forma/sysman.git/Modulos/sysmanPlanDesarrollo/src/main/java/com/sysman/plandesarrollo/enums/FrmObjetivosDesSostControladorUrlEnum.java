/*-
 * FrmObjetivosDesSostControladorUrlEnum.java
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
 * Enumeracion que permite clasificar cada uno de los servicios DSS
 * requeridos por el formulario.
 * 
 * @version 1.0, 16/12/2019
 * @author jrodrigueza
 *
 */
public enum FrmObjetivosDesSostControladorUrlEnum {

    /** A&ntilde;os hasta el actual en orden descendente. */
    URL4002("OBJETIVOSDESSOST_VIGENCIAS", "4002"),

    /** Metas de proyecto para la vigencia especificada. */
    URL552052("OBJETIVOSDESSOST_METAS", "552052")

    ;

    /** Clave que identifica al enumerado. */
    private final String key;

    /** C&oacute;digo del DSS asociado. */
    private final String value;

    /** Define la estructura del enumeado */
    private FrmObjetivosDesSostControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /** Obtiene la clave del enumerado. */
    public String getKey() {
        return key;
    }

    /** Obtiene el DSS asociado. */
    public String getValue() {
        return value;
    }
}
