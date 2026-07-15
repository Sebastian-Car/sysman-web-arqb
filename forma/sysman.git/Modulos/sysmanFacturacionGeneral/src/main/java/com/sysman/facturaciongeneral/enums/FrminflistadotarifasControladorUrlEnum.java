/*
 * FrminflistadotarifasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum FrminflistadotarifasControladorUrlEnum {

	URL5822("FRMINFLISTADOTARIFASCONTROLADORURL5822", "4001"),

	URL5520("FRMINFLISTADOTARIFASCONTROLADORURL5520", "668013"), // Carga tarifa inicial
	URL5521("FRMINFLISTADOTARIFASCONTROLADORURL5521", "668011"), // Carga tarifa final

	URL3649("FRMINFLISTADOTARIFASCONTROLADORURL3649", "667011"), // carga estrato inicial

	URL3650("FRMINFLISTADOTARIFASCONTROLADORURL3650", "667009"); // cargar estrato final

	private final String key;
	private final String value;

	private FrminflistadotarifasControladorUrlEnum(String key, String value) {
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
