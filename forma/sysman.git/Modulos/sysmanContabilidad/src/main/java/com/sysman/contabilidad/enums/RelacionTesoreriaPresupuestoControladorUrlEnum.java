/*
 * RelaciondeegresosControladorUrlEnum

 *
 * 1.0
 *
 * 26/06/2016
 *
 * Copyright  Sysman
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

public enum RelacionTesoreriaPresupuestoControladorUrlEnum {

	URL4993("RELACIONDEEGRESOSCONTROLADORURL4993", "94018"),

    URL3743("RELACIONDEEGRESOSCONTROLADORURL3743", "94016");

	private final String key;
	private final String value;

	private  RelacionTesoreriaPresupuestoControladorUrlEnum(String key, String value) {
		this.key   = key; 
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}



}
