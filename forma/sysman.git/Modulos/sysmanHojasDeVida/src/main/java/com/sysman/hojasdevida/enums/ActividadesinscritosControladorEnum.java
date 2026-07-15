/*-
 * ActividadesinscritosControladorEnum.java
 *
 * 1.0
 * 
 * 3/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para almacenar las constantes del archivo
 * ActividadesinscritosControlador.
 * 
 * @version 1.0, 3/02/2018
 * @author dnino
 *
 */
public enum ActividadesinscritosControladorEnum {

    TIPOEVENTO("TIPOEVENTO"),

    IDEVENTO("IDEVENTO"),

    SUCURSAL("SUCURSAL"),

    FECHAINICIAL("FECHAINICIAL"),

    BENEFICIARIO("BENEFICIARIO"),

    NUMERO_DCTO("NUMERO_DCTO")

    ;

    private final String value;

    private ActividadesinscritosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
