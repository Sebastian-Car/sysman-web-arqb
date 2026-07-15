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
 * @version 1.0, 3 dic. 2018
 * @author ybecerra
 *
 */
public enum InformeCgrsControladorUrlEnum {

    URL182("INFORMECGRSCONTROLADORURLENUM182", "4001"),
    
    URL215("INFORMECGRSCONTROLADORURLENUM215", "7001"),
    
    URL240("INFORMECGRSCONTROLADORURLENUM240", "7012"),
    
    URL270("INFORMECGRSCONTROLADORURLENUM270", "1750001");

    private final String key;
    private final String value;

    private InformeCgrsControladorUrlEnum(String key,
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
