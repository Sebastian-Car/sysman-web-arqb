/*-
 * GenerarAutoservicio.java
 *
 * 1.0
 * 
 * 28/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.negocio.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 28/05/2018
 * @author jgomez
 *
 */
public enum GeneraArchivoEnum {
    CEDULA("cedula"),

    PROCESO("PROCESO"),

    CLASE_SOLICITUD("CLASE_SOLICITUD"),

    REPORTE("REPORTE"),

    PLANTILLA("PLANTILLA"),

    PDF("PDF"),

    WORD("WORD"),

    EXCEL("EXCEL"),

    EMPLEADOINI("empleadoIni"),

    EMPLEADOFIN("empleadoFin"),

    CENTROCOSTOINI("centroCostoIni"),

    CENTROCOSTOFIN("centroCostoFin"),

    VACIO(" "),

    PR_TITULO("PR_TITULO"),

    TIPO_PERMISO("TIPO_PERMISO"),

    ID_DEMPLEADO("ID_DEMPLEADO"),

    USUARIO_SISTEMA("USUARIO_SISTEMA"),

    JEFE_DIRECTO("JEFE_DIRECTO"),

    SUCURSAL_JEFE_DIRECTO("SUCURSAL_JEFE_DIRECTO"),

    ID_DE_CARGO("ID_DE_CARGO"),
    /**
     * Campo HORA_INICIO
     */
    HORA_INICIO("HORA_INICIO"),
    /**
     * Campo HORA_FINAL
     */
    HORA_FINAL("HORA_FINAL")

    ;

    private final String value;

    private GeneraArchivoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
