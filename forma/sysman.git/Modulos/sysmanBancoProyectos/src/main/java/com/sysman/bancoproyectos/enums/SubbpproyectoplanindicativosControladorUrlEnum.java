/*-
 * SubbpproyectoplanindicativosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 27/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 27/09/2017
 * @author jcrodriguez
 *
 */
public enum SubbpproyectoplanindicativosControladorUrlEnum {

    URL1023("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1023", "503013"),

    URL1025("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1025", "433009"),

    URL1027("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1027", "561002"),

    URL1029("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1029", "554011"),

    URL1031("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1031", "554013"),

    URL1033("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1033", "552016"),

    URL1035("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1035", "111020"),

    URL1037("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1037", "552020"),

    URL1039("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1039", "433010"),

    URL1041("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1041", "206012"),

    URL1043("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1043", "513008"),

    URL1045("SUBBPPROYECTOPLANINDICATIVOSCONTROLADORURL1045", "554017");
    private final String key;
    private final String value;

    private SubbpproyectoplanindicativosControladorUrlEnum(String key, String value)
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
