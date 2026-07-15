/*-
 * FrmDTramitesControladorEnum.java
 *
 * 1.0
 * 
 * 24/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para centralizar las cadenas y parametros
 * utilizados en el controlador:
 * {@link com.sysman.workflow.FrmDTramitesControlador}
 * 
 * @version 1.0, 24/04/2018
 * @author pespitia
 *
 */
public enum FrmDTramitesControladorEnum {

    PR_NODO_ORIGEN("PR_NODO_ORIGEN"),

    PR_NODO_ORIGEN_NOM("PR_NODO_ORIGEN_NOM"),

    PR_PROCESO("PR_PROCESO"),

    PR_PROCESO_NOM("PR_PROCESO_NOM"),

    PR_TIPOTRAMITE("PR_TIPOTRAMITE"),

    PR_TIPOTRAMITE_NOM("PR_TIPOTRAMITE_NOM"),

    PR_TRAMITE("PR_TRAMITE"),

    NODO("NODO"),

    PROCESO("PROCESO"),

    RACI("RACI"),

    ROL("ROL"),
    
    FECHA_REAL("FECHA_REAL"),
    
    FECHA_PRORROGA("FECHA_PRORROGA"),
    
    PRORROGA("PRORROGA"),

	DEPENDENCIA("DEPENDENCIA");
    
    private final String value;

    private FrmDTramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
