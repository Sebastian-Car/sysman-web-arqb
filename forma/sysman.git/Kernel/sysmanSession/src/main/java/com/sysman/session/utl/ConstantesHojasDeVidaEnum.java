/*-
 * ConstantesHojasDeVidaEnum.java
 *
 * 1.0
 * 
 * 25/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.session.utl;

/**
 * Enumeracion que permite clasificar cada uno de los parametros de
 * sesion utilizados en el modulo de <code>HOJAS DE VIDA</code>.
 * 
 * @version 1.0, 25/01/2018
 * @author crodriguez
 *
 */
public enum ConstantesHojasDeVidaEnum {

    CLASE_EVALUACION("claseEvaluacion");

    private final String value;

    private ConstantesHojasDeVidaEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
