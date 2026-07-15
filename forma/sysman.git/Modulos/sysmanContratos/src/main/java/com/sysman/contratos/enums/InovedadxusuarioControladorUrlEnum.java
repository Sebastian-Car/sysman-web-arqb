/*-
 * InovedadxusuarioControladorUrlEnum.java
 *
 * 1.0
 * 
 * 10/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * Enumeracion que permite clasificar los identificadores con su
 * correspondiente DSS.
 * 
 * @version 1.0, 10/08/2017
 * @author pespitia
 *
 */
public enum InovedadxusuarioControladorUrlEnum {

    URL0001("INFCLASECONTRATOTIPOGASTOCONTROLADORURL0001", "73016");

    private final String key;
    private final String value;

    private InovedadxusuarioControladorUrlEnum(String key,
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
