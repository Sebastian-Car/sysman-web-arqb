/*
 * MonitorNovedadesProyectosControladorUrlEnum
 *
 * 1.0
 *
 * 26/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MonitorNovedadesProyectosControladorUrlEnum {

    URL3410("MONITORNOVEDADESPROYECTOSCONTROLADORURL3410", "130020"),

    URL3411("MONITORNOVEDADESPROYECTOSCONTROLADORURL3411", "32041"),

    URL3412("MONITORNOVEDADESPROYECTOSCONTROLADORURL3412", "62068"),

    URL3413("MONITORNOVEDADESPROYECTOSCONTROLADORURL3413", "218012"),

    URL3414("MONITORNOVEDADESPROYECTOSCONTROLADORURL3414", "218014"),

    URL3415("MONITORNOVEDADESPROYECTOSCONTROLADORURL3415", "130028"),

    URL3416("MONITORNOVEDADESPROYECTOSCONTROLADORURL3416", "552045"),

    URL3417("MONITORNOVEDADESPROYECTOSCONTROLADORURL3417", "554023"),

    URL3418("MONITORNOVEDADESPROYECTOSCONTROLADORURL3418", "552046"),

    URL3419("MONITORNOVEDADESPROYECTOSCONTROLADORURL3419", "130034"),

    URL3420("MONITORNOVEDADESPROYECTOSCONTROLADORURL3419", "130036")

    ;

    private final String key;
    private final String value;

    private MonitorNovedadesProyectosControladorUrlEnum(String key,
        String value)
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
