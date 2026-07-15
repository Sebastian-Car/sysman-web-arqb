/*-
 * ConstanteResponse.java
 *
 * 1.0
 * 
 * 14/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 14/08/2017
 * @author cmanrique
 *
 */
public enum ConstanteResponse {

    CONTENT_DISP_ATT_FILENAME("Content-disposition",
                    "attachment; filename=");

    private String name;
    private String value;

    private ConstanteResponse(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
