/*
 * FrmcentroutilidadControladorUrlEnum
 *
 * 1.0
 *
 * 09/10/2023
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
public enum FrmtarifasporconceptosControladorUrlEnum {

    URL001("FrmtarifasporconceptosControladorRURL6997", "4001"),
	URL002("FrmtarifasporconceptosControladorURL17101", "663019"),
	URL003("FrmtarifasporconceptosControladorURL59003", "59003"),
	URL004("FrmtarifasporconceptosControladorURL17101", "663011"),
	URL005("FrmtarifasporconceptosControladorURL17101", "1924003"), //param Tercero
	URL006("FrmtarifasporconceptosControladorURL17101", "1924001"),
	URL007("FrmtarifasporconceptosControladorURL1921002", "1921002");
    private final String key;
    private final String value;

    private FrmtarifasporconceptosControladorUrlEnum(String key, String value) {
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
