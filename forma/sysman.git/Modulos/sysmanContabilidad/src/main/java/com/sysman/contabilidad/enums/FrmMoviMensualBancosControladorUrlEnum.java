/*-
 * FrmMoviMensualBancosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 02/02/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 02/02/2023
 * @author mperez
 *
 */
public enum FrmMoviMensualBancosControladorUrlEnum {

	URL4391("RELACIONPAGODESCUENTOSCONTROLADORURL4391", "4001"),
	
	URL3960("RELACIONPAGODESCUENTOSCONTROLADORURL3960", "7013") ;

	private final String key;
    private final String value;

    private FrmMoviMensualBancosControladorUrlEnum(String key, String value) {
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
