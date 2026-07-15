/*-
 * ConfigurarPlanContableExsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 3 dic. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cgr.enums;

/**
 * 
 * @version 1.0, 5 abr. 2019
 * @author ybecerra
 *
 */
public enum ConfigurarContratosControladorUrlEnum {

    URL155("CONFIGURARCONTRATOSCONTROLADORURLENUM155", "82111"),
    
    URL161("CONFIGURARCONTRATOSCONTROLADORURLENUM161", "82113"),
    
    URL184("CONFIGURARCONTRATOSCONTROLADORURLENUM184", "1775003"),
    
    URL206("CONFIGURARCONTRATOSCONTROLADORURLENUM206","1783001"),
    
    URL230("CONFIGURARCONTRATOSCONTROLADORURLENUM230","1784001");
    

    private final String key;
    private final String value;

    private ConfigurarContratosControladorUrlEnum(String key,
        String value) {
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
