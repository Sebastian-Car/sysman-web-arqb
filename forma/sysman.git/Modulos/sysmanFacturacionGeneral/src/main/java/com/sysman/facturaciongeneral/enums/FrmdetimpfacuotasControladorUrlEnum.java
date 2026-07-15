/*
 * FrmdetimpfacuotasControladorUrlEnum
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
public enum FrmdetimpfacuotasControladorUrlEnum {

	URL661045("FRMDETIMPFACUOTASCONTROLADORURLENUM661045", "661045"),

	URL14180("FRMDETIMPFACUOTASCONTROLADORURLENUM14180", "14180");

	private final String key;
	private final String value;

	private FrmdetimpfacuotasControladorUrlEnum(String key, String value) {
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
