/*-
 * ReclacificacionCierrePptalControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 15/02/2019
 * @author bcardenas
 *
 */
public enum ReclasificacionCierrePptalControladorUrlEnum {

    URL0001("RECLASIFICACIONCIERREPPTALCONTROLADORURL0001", "4001"),

    URL0002("RECLASIFICACIONCIERREPPTALCONTROLADORURL0002", "1031032"),

    URL45031("RECLASIFICACIONCIERREPPTALCONTROLADORURL45031", "45031"),

    URL34038("RECLASIFICACIONCIERREPPTALCONTROLADORURL34038", "34038"),

    URL13026("RECLASIFICACIONCIERREPPTALCONTROLADORURL13026", "13026"),

    URL20040("RECLASIFICACIONCIERREPPTALCONTROLADORURL20040", "20040"),

    URL23028("RECLASIFICACIONCIERREPPTALCONTROLADORURL25028", "23028"),

    URL1031("RECLASIFICACIONCIERREPPTALCONTROLADORURL1031", "1031034"),
    
    URL0003("RECLASIFICACIONCIERREPPTALCONTROLADORURL0002", "1031035");

    private final String key;
    private final String value;

    private ReclasificacionCierrePptalControladorUrlEnum(String key,
        String value) 
    {
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
