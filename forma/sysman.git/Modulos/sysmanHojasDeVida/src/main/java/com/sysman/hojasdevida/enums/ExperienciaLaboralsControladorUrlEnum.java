/*-
 * ExperienciaLaboralsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author amonroy
 * 
 * @version 1.0, 18 de dic. de 2017
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ExperienciaLaboralsControladorUrlEnum {

    URL001("EXPERIENCIALABORALSCONTROLADORURL001", "2001"),

    URL002("EXPERIENCIALABORALSCONTROLADORURL002", "5001"),

    URL003("EXPERIENCIALABORALSCONTROLADORURL003", "1001");

    private final String key;
    private final String value;

    private ExperienciaLaboralsControladorUrlEnum(String key, String value) {
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
