/*-
 * ImprimirHojasDeVidaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.hojasdevida.ImprimirHojasDeVidaControlador}
 * 
 * @version 1.0, 13/12/2017
 * @author pespitia
 *
 */
public enum ImprimirHojasDeVidaControladorUrlEnum {

    URL0002("IMPRIMIRHOJASDEVIDACONTROLADORURL0002", "685007"),

    URL0001("IMPRIMIRHOJASDEVIDACONTROLADORURL0001", "685005");

    private final String key;
    private final String value;

    private ImprimirHojasDeVidaControladorUrlEnum(String key,
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
