/*-
 * SubEncargosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 20 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 20 de dic. de 2017
 * @author amonroy
 *
 */
public enum SubEncargosControladorUrlEnum {

    URL001("SUBENCARGOSCONTROLADORRURL001", "714001"),

    URL002("SUBENCARGOSCONTROLADORURL002", "715001"),

    URL003("SUBENCARGOSCONTROLADORURL003", "62002"),

    URL004("SUBENCARGOSCONTROLADORURL004", "463003"),

    URL005("SUBENCARGOSCONTROLADORURL005", "613001"),

    URL006("SUBENCARGOSCONTROLADORURL006", "613003");

    private final String key;
    private final String value;

    private SubEncargosControladorUrlEnum(String key, String value) {
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
