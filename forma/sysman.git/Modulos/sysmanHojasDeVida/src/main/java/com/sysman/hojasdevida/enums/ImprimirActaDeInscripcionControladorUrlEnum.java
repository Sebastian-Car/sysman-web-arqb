/*-
 * ImprimirActaDeInscripcionControladorUrlEnum.java
 *
 * 1.0
 * 
 * 26/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase que permite gestionar los servicios que emplea el formulario
 * Imprimir Acta de inscripcion.
 * 
 * @version 1.0, 26/01/2018
 * @author dnino
 *
 */
public enum ImprimirActaDeInscripcionControladorUrlEnum {

    URL230("IMPRIMIRACTADEINSCRIPCIONCONTROLADORURL230",
                    "708001");

    private final String key;
    private final String value;

    private ImprimirActaDeInscripcionControladorUrlEnum(String key,
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
