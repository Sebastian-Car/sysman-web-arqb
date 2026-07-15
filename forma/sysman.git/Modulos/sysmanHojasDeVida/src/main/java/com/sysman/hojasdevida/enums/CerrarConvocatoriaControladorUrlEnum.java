/*-
 * CerrarConvocatoriaControladorUrlEnum.java
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
 * @author spina
 *
 * @version 1.0, 18 de dic. de 2017
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CerrarConvocatoriaControladorUrlEnum {

    URL001("CERRARCONVOCATORIACONTROLADORURL001", "708007"),

    URL002("CERRARCONVOCATORIACONTROLADORURL002", "463041"),

    URL007("CERRARCONVOCATORIACONTROLADORURL007", "708020"),

    URL003("CERRARCONVOCATORIACONTROLADORURL003", "62090"),

    URL004("CERRARCONVOCATORIACONTROLADORURL004", "722012"),

    URL005("CERRARCONVOCATORIACONTROLADORURL005", "722016"),

    URL006("CERRARCONVOCATORIACONTROLADORURL006", "722017"),

    ;

    private final String key;
    private final String value;

    private CerrarConvocatoriaControladorUrlEnum(String key, String value) {
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
