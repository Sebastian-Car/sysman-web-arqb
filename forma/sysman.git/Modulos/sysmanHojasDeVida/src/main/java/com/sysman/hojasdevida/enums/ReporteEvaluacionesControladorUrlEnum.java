/*-
 * ReporteEvaluacionesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * 
 * @version 1.0, 31/01/2018
 * @author crodriguez
 *
 */
public enum ReporteEvaluacionesControladorUrlEnum {

	URL260("REPORTEEVALUACIONESCONTROLADOR260", "947001"),

	URL288("REPORTEEVALUACIONESCONTROLADOR288", "947003"),

	URL313("REPORTEEVALUACIONESCONTROLADOR313", "939005"),

	URL338("REPORTEEVALUACIONESCONTROLADOR338", "939030"),

	URL365("REPORTEEVALUACIONESCONTROLADOR365", "939032"),

	URL388("REPORTEEVALUACIONESCONTROLADOR388", "939034"),

	URL415("REPORTEEVALUACIONESCONTROLADOR415", "752003"),

	URL439("REPORTEEVALUACIONESCONTROLADOR439", "752005"),

	URL466("REPORTEEVALUACIONESCONTROLADOR466", "939001"),

	URL491("REPORTEEVALUACIONESCONTROLADOR491", "939009"),

	URL516("REPORTEEVALUACIONESCONTROLADOR516", "939003"),

	URL540("REPORTEEVALUACIONESCONTROLADOR540", "939011"),

	URL565("REPORTEEVALUACIONESCONTROLADOR565", "938002")

	;

	private final String key;
	private final String value;

	private ReporteEvaluacionesControladorUrlEnum(String key, String value) {
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
