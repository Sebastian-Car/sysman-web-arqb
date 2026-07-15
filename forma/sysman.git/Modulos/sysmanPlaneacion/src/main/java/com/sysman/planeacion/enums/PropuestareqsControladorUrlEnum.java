/*-
 * PropuestareqsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.planeacion.enums;

/**
 * 
 * @version 1.0, 8/09/2017
 * @author jcrodriguez
 *
 */
public enum PropuestareqsControladorUrlEnum {
    URL2923("PROPUESTAREQSCONTROLADORURL2923", "544001"),

    URL2625("PROPUESTAREQSCONTROLADORURL2625", "544003"),

    URL2926("PROPUESTAREQSCONTROLADORURL2926", "62050"),

    URL2928("PROPUESTAREQSCONTROLADORURL2928", "14081"),

    URL2930("PROPUESTAREQSCONTROLADORURL2930", "112105"),

    URL2932("PROPUESTAREQSCONTROLADORURL2932", "551002"),

    URL2934("PROPUESTAREQSCONTROLADORURL2934", "71018"),

    URL2936("PROPUESTAREQSCONTROLADORURL2936", "550001"),

    URL2938("PROPUESTAREQSCONTROLADORURL2938", "544004"),

    URL2940("PROPUESTAREQSCONTROLADORURL2940", "544005");

    private final String key;
    private final String value;

    private PropuestareqsControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
