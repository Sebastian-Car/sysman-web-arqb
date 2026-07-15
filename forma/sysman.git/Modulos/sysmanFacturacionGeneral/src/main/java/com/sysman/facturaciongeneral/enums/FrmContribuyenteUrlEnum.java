/*
 * FrmContribuyenteUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmContribuyenteUrlEnum {

    URL14950("FRMCONTRIBUYENTEURL14950",
                    "1850001"),

    URL12990("FRMCONTRIBUYENTEURL12990",
                    "2001"),

    URL13818("FRMCONTRIBUYENTEURL13818",
                    "5014"),

    URL14580("FRMCONTRIBUYENTEURL14580",
                    "1848001"),
	
	URL14204("FRMCONTRIBUYENTEURL14204","14204");

    private final String key;
    private final String value;

    private FrmContribuyenteUrlEnum(String key, String value) {
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
