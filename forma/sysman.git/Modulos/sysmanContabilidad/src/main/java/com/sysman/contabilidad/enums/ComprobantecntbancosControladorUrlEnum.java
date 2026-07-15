/*-
 * ComprobantecntbancosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 4/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 4/05/2017
 * @author jguerrero
 *
 */
public enum ComprobantecntbancosControladorUrlEnum {
	
    URL4980("COMPROBANTECNTBANCOSURL4980", "16097"),

    URL7958("COMPROBANTECNTBANCOSURLURL7958", "16099"),

    URL7075("COMPROBANTECNTBANCOSURLURL7075", "39066"),

    URL4372("COMPROBANTECNTBANCOSURLURL4372", "87001"),

    URL4373("COMPROBANTECNTBANCOSURLURL4372", "38030"),

    URL4374("COMPROBANTECNTBANCOSURLURL4372", "87002"),

    URL4375("COMPROBANTECNTBANCOSURLURL4372", "53005"),

    URL4376("COMPROBANTECNTBANCOSURLURL4372", "60001"),
    
    URL295("COMPROBANTECNTBANCOSURLURL295", "1763001"),
    
    URL4860("COMPROBANTECNTBANCOSURLENUM4858", "13026"),
    
    URL13603("FACTURACIONCONCEPTOSCONTROLADORURL13603", "20059"),
    
    URL39128("COMPROBANTECNTBANCOSURLENUM39128", "39128"),
    
    URL16237("COMPROBANTECNTBANCOSURLENUM16237", "16237");

    private final String key;
    private final String value;

    private ComprobantecntbancosControladorUrlEnum(String key, String value) {
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
