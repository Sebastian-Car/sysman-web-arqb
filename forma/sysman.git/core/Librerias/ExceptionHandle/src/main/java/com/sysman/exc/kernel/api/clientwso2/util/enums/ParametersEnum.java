/*-
 * ParametersEnum.java
 *
 * 1.0
 * 
 * 1/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.exc.kernel.api.clientwso2.util.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 1/08/2017
 * @author cmanrique
 *
 */
public enum ParametersEnum {

    URL("url"), SERVICE("service"), PARAMETERS("parameters"), RESPONSE(
                    "response");

    private final String text;

    private ParametersEnum(String text) {
        this.text = text;
    }

    /**
     * @return the message
     */
    public String getText() {
        return text;
    }

}
