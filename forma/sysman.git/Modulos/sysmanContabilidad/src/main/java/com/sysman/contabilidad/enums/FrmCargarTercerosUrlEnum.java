/*
 * FrmCargarTercerosUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCargarTercerosUrlEnum {

	URL0001("FrmCargarTercerosURLENUM59003", "59003"), //COMPANIA
	URL0002("FrmCargarTercerosURLENUM209001", "209011"), //TIPO IDENTIFICACION
	URL0003("FrmCargarTercerosURLENUM19001", "19001"), // CLASE TERCERO
	URL0004("FrmCargarTercerosURLENUM1001", "1009"), //PAISES
	URL0005("FrmCargarTercerosURLENUM2012", "2012"),//DEPARTAMENTO
	URL0006("FrmCargarTercerosURLENUM5015", "5015"), //CIUDAD
	URL0007("FrmCargarTercerosURLENUM22001", "22001"),//REGIMEN
	URL0008("FrmCargarTercerosURLENUM1848008", "1848008");//REPSONSABILIDADFISCAL

    private final String key;
    private final String value;

    private FrmCargarTercerosUrlEnum(String key, String value) {
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
