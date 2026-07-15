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

package com.sysman.exogenas.enums;

/**
 * 
 * @version 1.0, 3 dic. 2018
 * @author ybecerra
 *
 */
public enum CuentasConfiguradasControladorUrlEnum {

    URL175("CUENTASCONFIGURADASCONTROLADORURL175", "39081"),
    
    URL183("CUENTASCONFIGURADASCONTROLADORURL183", "39083"),
    
    URL197("CUENTASCONFIGURADASCONTROLADORURL197", "49001"),
        
    URL193("CUENTASCONFIGURADASCONTROLADORURL193", "39085");

    private final String key;
    private final String value;

    private CuentasConfiguradasControladorUrlEnum(String key,
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
