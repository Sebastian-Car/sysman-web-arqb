/*-
 * FrmequivalenciasControlador.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * Enumeracion que permite clasificar las cadenas del controlador
 * FrmequivalenciasControlador.
 * 
 * @version 1.0, 27/02/2018
 * @author lbotia
 *
 */
public enum FrmprogramacionfisicasControladorEnum {

    ID_PLAN("ID_PLAN"),

    VIGENCIA("VIGENCIA"),

    VIGENCIA_INICIAL("VIGENCIA_INICIAL"),

    ES_NUEVO("ES_NUEVO"),

    EJECUTADO_TR1_FIN("EJECUTADO_TR1_FIN"),

    EJECUTADO_TR2_FIN("EJECUTADO_TR2_FIN"),

    EJECUTADO_TR3_FIN("EJECUTADO_TR3_FIN"),

    EJECUTADO_TR4_FIN("EJECUTADO_TR4_FIN"),

    PROGRAMADO_TR1_FIN("PROGRAMADO_TR1_FIN"),

    PROGRAMADO_TR2_FIN("PROGRAMADO_TR2_FIN"),

    PROGRAMADO_TR3_FIN("PROGRAMADO_TR3_FIN"),

    PROGRAMADO_TR4_FIN("PROGRAMADO_TR4_FIN"),

    CANTIDADPROGRAMADA("CANTIDADPROGRAMADA"),

    CANTIDADEJECUTADA("CANTIDADEJECUTADA"),

    EJECUCION("EJECUCION"),

    EJECUCIONUNO("EJECUCIONUNO"),

    EJECUCIONDOS("EJECUCIONDOS"),

    EJECUCIONTRES("EJECUCIONTRES"),

    EJECUCIONCUATRO("EJECUCIONCUATRO"),

    FECHA_FINAL_FIN("FECHA_FINAL_FIN"),

    FECHA_INICIAL_FIN("FECHA_INICIAL_FIN");

    private final String value;

    private FrmprogramacionfisicasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
