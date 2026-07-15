/*-
 * PertinenciaNovedadControladorUrlEnum.java
 *
 * 1.0
 * 
 * 26/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 26/12/2018
 * @author bcardenas
 *
 */
public enum PertinenciaNovedadControladorUrlEnum {

    URL0001("PERTINENCIANOVEDADCONTROLADORURL0001", "130048"),

    URL0002("PERTINENCIANOVEDADCONTROLADORURL0002", "130050"),

    URL0004("PERTINENCIANOVEDADCONTROLADORURL0004", "1762001"),
    
    URL1986001("PERTINENCIANOVEDADCONTROLADORURL1986001", "1986001"),
    
    URL1987001("PERTINENCIANOVEDADCONTROLADORURL1987001", "1987001"),
    
    URL176200D("PERTINENCIANOVEDADCONTROLADORURL176200D", "176200D");

    private final String key;
    private final String value;

    private PertinenciaNovedadControladorUrlEnum(String key, String value) {
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
