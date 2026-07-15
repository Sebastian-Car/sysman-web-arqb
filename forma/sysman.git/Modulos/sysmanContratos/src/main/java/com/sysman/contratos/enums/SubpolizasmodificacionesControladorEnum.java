/*-
 * SubpolizasmodificacionesControladorEnum.java
 *
 * 1.0
 * 
 * 15/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubpolizasmodificacionesControladorEnum {

    FECHAAPROBACIONRESPONSABILIDAD("FECHAAPROBACIONRESPONSABILIDAD"),

    FECHAAPROBACIONCUMPLIMIENTO("FECHAAPROBACIONCUMPLIMIENTO"),

    VLRACTUAL("VLRACTUAL"),

    OBSERVACIONES("OBSERVACIONES"),

    VIGENCIAHASTA("VIGENCIAHASTA"),

    VIGENCIADESDE("VIGENCIADESDE"),

    FECHAEXPEDICION("FECHAEXPEDICION"),

    POLIZAS("POLIZAS"),

    TIPO("TIPO"),

    ORDENDECOMPRA("ORDENDECOMPRA"),

    TIPODESC("TIPODESC"),

    ASEGURADORA("ASEGURADORA"),

    NOMBREASEGURADORA("NOMBREASEGURADORA"),

    VALORASEGURADO("VALORASEGURADO"),

    VLRANTERIOR("VLRANTERIOR");

    private final String value;

    private SubpolizasmodificacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
