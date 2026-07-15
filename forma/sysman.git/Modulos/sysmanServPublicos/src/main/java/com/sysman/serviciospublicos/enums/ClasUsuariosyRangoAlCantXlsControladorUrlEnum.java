/*-
 * ClasUsuariosyRangoAlCantXlsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15 de may. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 15 de may. de 2017
 * @author jeguerrero
 *
 */
public enum ClasUsuariosyRangoAlCantXlsControladorUrlEnum {
    URL3992("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "227001"),

    URL3141("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "227002"),

    URL3142("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "227003"),

    URL3143("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "227004"),

    URL3144("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "227005"),

    URL3145("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "317001"),

    URL3146("ClasUsuariosyRangoAlCantXlsControladorUrlEnum", "317002");

    private final String key;
    private final String value;

    private ClasUsuariosyRangoAlCantXlsControladorUrlEnum(String key,
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
