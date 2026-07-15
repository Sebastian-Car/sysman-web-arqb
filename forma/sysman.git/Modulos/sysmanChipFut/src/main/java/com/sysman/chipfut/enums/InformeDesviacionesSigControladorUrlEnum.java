/*-
 * SeguimientoReciprocasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.chipfut.enums;

/**
 * 
 * @version 1.0, 15/12/2018
 * @author ybecerra
 *
 */
public enum InformeDesviacionesSigControladorUrlEnum {
    // Lista Año

    URL204("INFORMEDESVIACIONESSIGCONTROLADORUR204", "4001"),

    URL263("INFORMEDESVIACIONESSIGCONTROLADORUR263", "4027"),

    URL232("INFORMEDESVIACIONESSIGCONTROLADORUR232", "7001"),

    URL289("INFORMEDESVIACIONESSIGCONTROLADORUR289", "7012"),

    URL306("INFORMEDESVIACIONESSIGCONTROLADORUR306", "29019"),

    URL311("INFORMEDESVIACIONESSIGCONTROLADORUR311", "29021");

    private final String key;
    private final String value;

    private InformeDesviacionesSigControladorUrlEnum(String key, String value) {
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
