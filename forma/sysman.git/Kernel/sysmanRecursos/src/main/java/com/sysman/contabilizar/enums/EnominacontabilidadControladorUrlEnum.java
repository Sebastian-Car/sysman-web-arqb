/*
 * CAlmacenContabilidadsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum EnominacontabilidadControladorUrlEnum {
    // Companias -> Sin Parï¿½metros
    URL4410("ENOMINACONTABILIDADCONTROLADORURLENUM4410",
                    "59021"),
    // Procesos -> Parï¿½metros: Compaï¿½ia.
    URL4420("ENOMINACONTABILIDADCONTROLADORURLENUM4420",
                    "537010"),
    // Perï¿½odos -> Parï¿½metros: Compaï¿½ia, Id de proceso, aï¿½o, mes
    URL4430("ENOMINACONTABILIDADCONTROLADORURLENUM4430",
                    "471069"),
    // Aï¿½os -> Parï¿½metro: Compaï¿½ia.
    URL4440("ENOMINACONTABILIDADCONTROLADORURLENUM4440",
                    "471073"),
    // Centro de costo -> Parï¿½metros: Compaï¿½ia, aï¿½o.
    URL4450("ENOMINACONTABILIDADCONTROLADORURLENUM4450",
                    "20001"),
    // Empleado
    URL4460("ENOMINACONTABILIDADCONTROLADORURLENUM4460",
                    "620015"),
    // Mes -> Parametros: Compaï¿½ia y Aï¿½o.
    URL4470("ENOMINACONTABILIDADCONTROLADORURLENUM4470",
                    "7034")

    ;

    private final String key;
    private final String value;

    private EnominacontabilidadControladorUrlEnum(String key, String value)
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
