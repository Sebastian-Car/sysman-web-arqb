/*-
 * FrmequivalenciasControladorUrlEnums.java
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
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 27/02/2018
 * @author lbotia
 *
 */
public enum PiPlanDesarrolloControladorUrlEnum {

    URL0001("PIPLANDESARROLLOCONTROLADORURL0001", "552043"),

    URL0002("PIPLANDESARROLLOCONTROLADORURL0002", "552042"),
    
    URL0003("PIPLANDESARROLLOCONTROLADORURL0003", "55200D"),
    
    URL0004("PIPLANDESARROLLOCONTROLADORURL0004", "552041"),
    
    URL0005("PIPLANDESARROLLOCONTROLADORURL0005", "62005"),
    
    URL0006("PIPLANDESARROLLOCONTROLADORURL0006", "203002"),
    
    URL0007("PIPLANDESARROLLOCONTROLADORURL0007", "4001"),
    
    URL0008("PIPLANDESARROLLOCONTROLADORURL0008", "554021"),
    
    URL0009("PIPLANDESARROLLOCONTROLADORURL0009", "554022");

    private final String key;
    private final String value;

    private PiPlanDesarrolloControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
