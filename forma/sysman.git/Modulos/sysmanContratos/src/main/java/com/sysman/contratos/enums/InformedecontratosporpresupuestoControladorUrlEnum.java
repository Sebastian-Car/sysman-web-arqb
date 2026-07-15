/*
 * InformedecontratosporpresupuestoControladorUrlEnum
 *
 * 1.0
 *
 * 10/08/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum InformedecontratosporpresupuestoControladorUrlEnum {

    URL5597("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5597", "73012"),

    URL5598("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5598", "73014"),

    URL5599("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5599", "430001"),

    URL5600("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5600", "430003"),

    URL5601("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5601", "200010"),

    URL5602("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5602", "14036"),

    URL5603("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5603", "14038"),

    URL5604("INFORMEDECONTRATOSPORPRESUPUESTOCONTROLADORURL5604", "73028")

    ;

    private final String key;
    private final String value;

    private InformedecontratosporpresupuestoControladorUrlEnum(String key,
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
