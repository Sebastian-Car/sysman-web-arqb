/*-
 * ImprimirhvcarreraControladorUrlEnum.java
 *
 * 1.0
 *
 * 14/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 *
 * @version 1.0, 14/12/2017
 * @author lcortes
 *
 */
public enum ImprimirhvcarreraControladorUrlEnum {

    URL001("IMPRIMIRHVCARRERACONTROLADORURL001", "685009"),

    URL002("IMPRIMIRHVCARRERACONTROLADORURL002", "685023");

    private final String key;
    private final String value;

    private ImprimirhvcarreraControladorUrlEnum(String key, String value) {
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
