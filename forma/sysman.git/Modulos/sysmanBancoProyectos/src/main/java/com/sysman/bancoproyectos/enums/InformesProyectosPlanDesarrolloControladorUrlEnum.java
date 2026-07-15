/*-
 * InformesProyectosPlanDesarrolloControladorUrlEnum.java
 *
 * 1.0
 * 
 * 25/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 25/09/2017
 * @author jcrodriguez
 *
 */
public enum InformesProyectosPlanDesarrolloControladorUrlEnum {
    URL4823("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4823", "62064"),

    URL4824("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4824", "4043"),

    URL4826("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4826", "32032"),

    URL4828("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4828", "32034"),

    URL4830("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4830", "430016"),

    URL4832("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4832", "430018"),

    URL4834("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4834", "118018"),

    URL4836("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4836", "430023"),

    URL4838("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4838", "430025"),

    URL4840("INFORMESPROYECTOSPLANDESARROLLOCONTROLADORURL4840", "430027");
    private final String key;
    private final String value;

    private InformesProyectosPlanDesarrolloControladorUrlEnum(String key, String value)
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
