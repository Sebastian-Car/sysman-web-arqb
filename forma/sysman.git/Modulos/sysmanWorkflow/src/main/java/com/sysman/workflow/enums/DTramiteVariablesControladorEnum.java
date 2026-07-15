/*-
 * FrmDTramiteVariablesControladorEnum.java
 *
 * 1.0
 * 
 * 25/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para centralizar las cadenas y parametros
 * utilizados en el controlador:
 * {@link com.sysman.workflow.FrmDTramiteVariablesControlador}
 * 
 * @version 1.0, 25/04/2018
 * @author pespitia
 *
 */
public enum DTramiteVariablesControladorEnum {

    PR_COD_FORM("PR_COD_FORM"),

    PR_D_TRAMITE("PR_D_TRAMITE"),

    PR_NODO("PR_NODO"),

    PR_PROCESO("PR_PROCESO"),

    PR_TIPO_TRAMITE("PR_TIPO_TRAMITE"),

    PR_TRAMITE("PR_TRAMITE"),

    ADJUNTO("ADJUNTO"),

    ADJUNTO_NOM("ADJUNTO_NOM"),

    CODIGO_NODO_VARIABLE("CODIGO_NODO_VARIABLE"),

    CODIGO_PROCESO("CODIGO_PROCESO"),

    D_TRAMITE("D_TRAMITE"),

    ETIQUETA("ETIQUETA"),

    EXISTE("EXISTE"),

    EXTENSION("EXTENSION"),

    ID("ID"),

    NODO("NODO"),

    OBLIGATORIO("OBLIGATORIO"),

    PROCESO("PROCESO"),

    TIPO_MIME("TIPO_MIME"),

    TIPO_TRAMITE("TIPO_TRAMITE"),

    TRAMITE("TRAMITE"),

    VARIABLE("VARIABLE"),
    
    ENVIA_CORREO("ENVIA_CORREO"),
    
    PROCEDENCIA_AUT("PROCEDENCIA_AUT");

    private final String value;

    private DTramiteVariablesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
