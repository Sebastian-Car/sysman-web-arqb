/*-
 * InventarioparqueautomotorControladorEnum.java
 *
 * 1.0
 *
 * 24/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.mantenimientoactivos.enums;

/**
 *
 * @version 1.0, 24/08/2017
 * @author spina
 *
 */
public enum InventarioparqueautomotorControladorEnum {
    PAIS("PAIS"),

    VEHICULOSEGURO("VEHICULOSEGURO"),

    VEHICULOTANQUE("VEHICULOTANQUE"),

    VEHICULOGASES("VEHICULOGASES"),

    VEHICULO_PARTES("VEHICULO_PARTES"),

    ACCIDENTALIDAD("ACCIDENTALIDAD"),

    FOTOSACCIDENTES("FOTOSACCIDENTES"),

    FOTOSVEHICULOS("FOTOSVEHICULOS"),

    ARCHIVO("ARCHIVO"),

    CAPTANQUECOMBUSTIBLE("CAPTANQUECOMBUSTIBLE"),

    REGISTRO("REGISTRO"),

    NOMBRECONDUCTOR("NOMBRECONDUCTOR"),

    NOMBRECOMODATARIA("NOMBRECOMODATARIA"),

    FECHAFINALM("FECHAFINALM"),

    FECHAINICIALM("FECHAINICIALM"),

    TB_TB49("TB_TB49"),

    PARESTADO("PARESTADO"),

    NITASEGURADORA("NITASEGURADORA"),

    NIT("NIT"),

    CONDUCTOR("CONDUCTOR"),

    INVENTARIO_PARQUE_AUTOMOTOR("INVENTARIO_PARQUE_AUTOMOTOR"),

    SEPARADOR("SEPARADOR"),

    RUTA("RUTA"),

    KEY_ARCHIVO("KEY_ARCHIVO"),

    SERIE_ELEMENTO("SERIE_ELEMENTO")

    ;

    private final String value;

    private InventarioparqueautomotorControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
