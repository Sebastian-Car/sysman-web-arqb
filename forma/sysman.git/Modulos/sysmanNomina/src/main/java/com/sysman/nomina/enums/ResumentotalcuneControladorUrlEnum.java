/*-
 * ResumentotalcuneControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 18/08/2017
 * @author pespitia
 *
 */
public enum ResumentotalcuneControladorUrlEnum {

    URL0001("KARDEX", "620019"),

    URL0002("NOMINACUNE", "1881001"),

    URL0003("EMPLE", "1881002"),

    URL0004("CUNEDATOS", "1881004"),

    URL0005("NIE008", "1881005"),

    URL0006("DET", "1881006"),

    URL0007("PAR31", "630005"),

    URL0008("NIE176", "1881007"),

    URL0009("CERTNOM", "1882001"), 
    
    URL1881008("LISTADONOMINAS","1881008");

    private final String key;
    private final String value;

    private ResumentotalcuneControladorUrlEnum(String key,
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
