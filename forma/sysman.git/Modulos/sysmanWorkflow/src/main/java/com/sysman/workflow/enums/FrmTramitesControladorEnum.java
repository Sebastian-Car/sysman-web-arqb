/*-
 * FrmTramitesControladorEnum.java
 *
 * 1.0
 * 
 * 19/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumeracion que contiene los parametros utilizados en el llamado de
 * los DSS.
 * 
 * @version 1.0, 19/04/2018
 * @author pespitia
 *
 */
public enum FrmTramitesControladorEnum {

    PR_ROWKEY("PR_ROWKEY"),

    PR_VER_DESDE_MONITOR("PR_VER_DESDE_MONITOR"),

    CODIGO_FORMULARIO("CODIGO_FORMULARIO"),

    DIRECCION_PROCEDENCIA("DIRECCION_PROCEDENCIA"),

    NODO_ACTUAL("NODO_ACTUAL"),

    NODO_A_NOM("NODO_A_NOM"),

    NODO_ORIGEN("NODO_ORIGEN"),

    NODO_O_NOM("NODO_O_NOM"),

    PROCESO("PROCESO"),

    PROCESOS("PROCESOS"),

    TIPO_TRAMITE("TIPO_TRAMITE"),
    
    TIPOTRAMITE("TIPOTRAMITE"),

    TRAMITE("TRAMITE"),

    USUARIO_INTERNO("USUARIO_INTERNO"),
    
    PAIS_ORIGEN("PAIS_ORIGEN"),
    
    DEPARTAMENTO_ORIGEN("DEPARTAMENTO_ORIGEN"),
    
    CIUDAD_ORIGEN("CIUDAD_ORIGEN"),
    
    PROCEDENCIA("PROCEDENCIA"),
    
    NUMERO("NUMERO"),
    
    CODIGO("CODIGO"),
    
    KEY_CODIGO("KEY_CODIGO"),
    
    NUMERACION_UNICA("NUMERACION_UNICA"),
    
    CODIGO_CALIDAD("CODIGO_CALIDAD"),
	
	CODIGO_URL("CODIGO_URL"),
	
	DEPARTAMENTO_EMAIL_RADICA_TRAMITE_SI("52"),
	
	COMPANIA_EMAIL_RADICA_TRAMITE_SI("001"),
	
	PROCESO_EMAIL_RADICA_TRAMITE_NO("00000"),
	
	;
    private final String value;

    private FrmTramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
