/*-
 * EsfactoresporestproysControladorUrlEnum.java
 *
 * 1.0
 * 
 * 24/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * del refactoring.
 * 
 * @version 1.0, 24/08/2017
 * @author pespitia
 *
 */
public enum EtapaPreDocControladorUrlEnum {

    URL0001("ETAPAPREDOCCONTROLADORURL0001",
                    "490001"),

    URL0002("ETAPAPREDOCCONTROLADORURL0002",
                    "49000C"),

    URL0003("ETAPAPREDOCCONTROLADORURL0003",
                    "49000U"),

    URL0004("ETAPAPREDOCCONTROLADORURL0004",
                    "49000D"),
    
    URL0005("ETAPAPREDOCCONTROLADORURL0005",
                    "104040")

    ;

    private final String key;
    private final String value;

    private EtapaPreDocControladorUrlEnum(String key, String value) {
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
