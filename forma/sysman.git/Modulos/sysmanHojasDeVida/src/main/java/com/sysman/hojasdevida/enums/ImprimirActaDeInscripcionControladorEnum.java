/*-
 * ImprimirActaDeInscripcionControlador.java
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
 * Enumerado que almacena las constantes empleadas en el archivo
 * ImprimirActaDeInscripcionControlador.
 * 
 * @version 1.0, 26/01/2018
 * @author dnino
 *
 */
public enum ImprimirActaDeInscripcionControladorEnum {
    NRO_CONVOCATORIA("NRO_CONVOCATORIA"),

    REPORTE("001671ActaInscripcion");

    private final String value;

    private ImprimirActaDeInscripcionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}