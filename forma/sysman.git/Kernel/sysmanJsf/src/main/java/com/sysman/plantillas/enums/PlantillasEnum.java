/*-
 * PlantillasEnum.java
 *
 * 1.0
 * 
 * 5/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plantillas.enums;

/**
 * Enumeraci&oacute;n que permite clasificar cada uno de los
 * par&aacute;metros identificados en los utiltiarios usados para
 * generar plantillas.
 * 
 * @version 1.0, 5/06/2018
 * @author jrodrigueza
 *
 */
public enum PlantillasEnum {
    /**
     * constante MANEJA_ESTILOS
     */
    MANEJA_ESTILOS("MANEJA_ESTILOS"),
    /**
     * constante CONSULTA
     */
    CONSULTA("CONSULTA"),
    /**
     * constante FORMATO
     */
    FORMATO("FORMATO"),
    /**
     * constante ETIQUETA
     */
    ETIQUETA("ETIQUETA"),
    /**
     * constante NOMBRETIPO
     */
    NOMBRETIPO("NOMBRETIPO"),
    /**
     * constante PLANTILLA
     */
    PLANTILLA("PLANTILLA"),
    /**
     * constante MODO_CREACION
     */
    MODO_CREACION("MODO_CREACION"),
    /**
     * cadena salidas
     */
    STR_SALIDAS("salidas"),
    /**
     * cadena nombresArchivos
     */
    STR_NOMBRES_ARCHIVOS("nombresArchivos"),
    /**
     * cadena valorEtiqueta
     */
    STR_VALOR_ETIQUETA("valorEtiqueta"),
    /**
     * constante ENLACE_PRINCIPAL
     */
    ENLACE_PRINCIPAL("ENLACE_PRINCIPAL"),
    /**
     * constante ENLACE_SECUNDARIO
     */
    ENLACE_SECUNDARIO("ENLACE_SECUNDARIO"),
    /**
     * constante CONDICION
     */
    CONDICION("CONDICION"),
    /**
     * constante RUTA_ARCHIVOS
     */
    RUTA_ARCHIVOS("RUTA_ARCHIVOS");

    /**
     * cadena que representa el valor asociado a un enumerado.
     */
    private final String value;

    /**
     * Constructor que recibe el valor para cada enumerado
     * seg&uacute;n corresponda.
     * 
     * @param value
     */
    private PlantillasEnum(String value) {
        this.value = value;
    }

    /**
     * @return the valor
     */
    public String getValue() {
        return value;
    }

}
