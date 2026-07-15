/*-
 * VacacionesControladorEnum.java
 *
 * 1.0
 * 
 * 31/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * @version 1.0, 31/10/2017
 * @author jcrodriguez
 *
 */
public enum VacacionesControladorEnum {
    FECHA_ACTO("FECHA_ACTO"),

    PROCESONOMINA("PROCESONOMINA"),

    ID_DE_CONCEPTO("ID_DE_CONCEPTO"),

    PREGUNTAR("PREGUNTAR"),

    ENDINERO("ENDINERO"),

    NUMPERIODOS("NUMPERIODOS"),

    DIAS("DIAS"),

    CEDULA("CEDULA"),

    CONTAR_SABADOS_COMO_DIA_HABIL("CONTAR SABADOS COMO DIA HABIL"),

    DIASDINERO("DIASDINERO"),

    DIASHABILES("DIASHABILES"),

    DIASPENDIENTESDEDISFRUTE("DIASPENDIENTESDEDISFRUTE"),

    FECHAFINAL("FECHAFINAL"),

    FECHAPAGO("FECHAPAGO"),

    FECHA_FINAL("FECHA_FINAL"),
    
    FECHA_INI("FECHAINI"),
    
    FECHA_FIN("FECHAFIN"),
    
    ID_EMPLEADO("IDEMPLEADO"),      

    FECHA_INICIO("FECHA_INICIO"),

    FINAL_DISFRUTE("FINAL_DISFRUTE"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    ID_DE_PROCESO("ID_DE_PROCESO"),

    INICIO_DISFRUTE("INICIO_DISFRUTE"),

    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

    TB_TB2666("TB_TB2666"),

    FORMATO("dd/MM/yyyy HH:mm:ss"),

    NUMERO_DOCUMENTO("NUMERO_DCTO"),

    PAGAR_VACACIONES("PAGAR EL DIA 31 EN VACACIONES"),

    CUENTACONTABLE("CUENTACONTABLE");

    private final String value;

    private VacacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
