/*-
 * FrmconceptoviaticosControladorEnum.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * Este enumerado me permite almacenar las palabras clave del
 * controlador FrmconceptoviaticosControlador
 * 
 * @version 1.0, 18/01/2018
 * @author mvenegas
 *
 */
public enum FrmconceptoviaticosControladorEnum {

    TIPOPPTAL("TIPOPPTAL"),

    COMPROBANTEDISPONIBILIDAD("COMPROBANTEDISPONIBILIDAD"),

    COMPROBANTE_DISPON("COMPROBANTE_DISPON"),

    NATURALEZA("NATURALEZA"),

    ID("ID"),

    CUENTA_PPTAL("CUENTA_PPTAL"),

    TIPO_CPTE_CNT("TIPO_CPTE_CNT"),

    CUENTA_CAUSACION_DEBITO("CUENTA_CAUSACION_DEBITO"),

    TIPO_CONCEPTO("TIPO_CONCEPTO"),

    TIPO_CPTE_CNT_LEG("TIPO_CPTE_CNT_LEG"),

    TIPO_CPTE_CNT_TES("TIPO_CPTE_CNT_TES"),

    CUENTA_LEGALIZACION_CREDITO("CUENTA_LEGALIZACION_CREDITO"),

    CUENTA_LEGALIZACION_DEBITO("CUENTA_LEGALIZACION_DEBITO"),

    CUENTA_CAUSACION_CREDITO("CUENTA_CAUSACION_CREDITO"),

    CODIGO_CONCEPTO("CODIGO_CONCEPTO"),

    TIPO_CPTE_PPTAL("TIPO_CPTE_PPTAL");

    private final String value;

    private FrmconceptoviaticosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
