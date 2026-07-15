/*-
 * FrmevaluadorevaluadosControladorUrl.java
 *
 * 1.0
 * 
 * 25/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Este enumerado me permite llamar los servicios necesarios para el
 * funcionamiento del controlador FrmevaluadorevaluadosControlador
 * 
 * @version 1.0, 25/01/2018
 * @author mvenegas
 *
 */
public enum FrmevaluadorevaluadosControladorUrlEnum {

    URL100("FRMEVALUADOREVALUADOSCONTROLADOR100", "462005"),

    URL102("FRMEVALUADOREVALUADOSCONTROLADOR102", "210097"),

    URL104("FRMEVALUADOREVALUADOSCONTROLADOR104", "463011"),

    URL106("FRMEVALUADOREVALUADOSCONTROLADOR106", "93400G"),

    URL107("FRMEVALUADOREVALUADOSCONTROLADOR107", "938002"),

    URL108("FRMEVALUADOREVALUADOSCONTROLADOR108", "943001");

    private final String key;
    private final String value;

    private FrmevaluadorevaluadosControladorUrlEnum(String key,
        String value) {
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
