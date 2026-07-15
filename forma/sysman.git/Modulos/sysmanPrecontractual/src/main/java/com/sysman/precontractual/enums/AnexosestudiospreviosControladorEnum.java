/*-
 * AnexosestudiospreviosControladorEnum.java
 *
 * 1.0
 * 
 * 22/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * 
 * @version 1.0, 22/08/2017
 * @author jcrodriguez
 *
 */
public enum AnexosestudiospreviosControladorEnum {

    ARCHIVO("ARCHIVO"),

    TITULO("TITULO"),

    CONSECUTIVODETALLE("CONSECUTIVODETALLE"),

    COD_ESTUDIO("COD_ESTUDIO"),

    MENU190207("190207"),

    TRANSACCION("TRANSACCION"),

    TIPOCONTRATO("TIPOCONTRATO"),

    ARCHIVOS_D_TRANSACCION("ARCHIVOS_D_TRANSACCION");

    private final String value;

    private AnexosestudiospreviosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
