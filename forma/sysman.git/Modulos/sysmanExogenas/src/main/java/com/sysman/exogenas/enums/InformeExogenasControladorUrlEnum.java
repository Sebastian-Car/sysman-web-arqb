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
public enum InformeExogenasControladorUrlEnum {

    URL169("INFORMEEXOGENASCONTROLADORURL169", "4001"),

    URL195("INFORMEEXOGENASCONTROLADORURL195", "7001"),

    URL221("INFORMEEXOGENASCONTROLADORURL221", "7012"),

    URL253("INFORMEEXOGENASCONTROLADORURL253", "1750001");

    private final String key;
    private final String value;

    private InformeExogenasControladorUrlEnum(String key,
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
