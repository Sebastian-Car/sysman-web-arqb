/*-
 * PlantillasUrlEnum.java
 *
 * 1.0
 * 
 * 5/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plantillas.enums;

/**
 * Enumeraci&oacute;n que permitie clasificar las URLs empleadas para
 * acceder a los servicios asociados con plantillas.
 * 
 * @version 1.0, 5/06/2018
 * @author jrodrigueza
 *
 */
public enum PlantillasUrlEnum {
    /**
     * 104067 getModeloplantillasTodosLosDatosQuery
     */
    DSS_104067("getModeloplantillasTodosLosDatosQuery", "104067"),
    /**
     * 58002 getAplicacionesGeneralQuery
     */
    DSS_58002("getAplicacionesGeneralQuery", "58002");

    /**
     * Cadena que contiene la clave del enumerado
     */
    private final String key;
    /**
     * Valor del enumerado.
     */
    private final String value;

    /**
     * 
     * @param key
     * una clave
     * @param value
     * un valor
     */
    private PlantillasUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
