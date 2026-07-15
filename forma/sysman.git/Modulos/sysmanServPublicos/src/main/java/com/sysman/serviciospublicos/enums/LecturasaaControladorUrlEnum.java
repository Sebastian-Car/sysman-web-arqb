/*
 * LecturasaaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum LecturasaaControladorUrlEnum {

    URL4403("LECTURASAACONTROLADORURL4403", "213136"),

    URL4404("LECTURASAACONTROLADORURL4404", "213134"),

    URL4405("LECTURASAACONTROLADORURL4405", "213138"),

    URL4406("LECTURASAACONTROLADORURL4406", "213148"),
    
    URL4407("LECTURASAACONTROLADORURL4407", "213150"),
    
    URL4408("LECTURASAACONTROLADORURL4408", "213151"),
    
    URL4409("LECTURASAACONTROLADORURL4409", "118003"),
    ;

    private final String key;
    private final String value;

    private LecturasaaControladorUrlEnum(String key, String value)
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
