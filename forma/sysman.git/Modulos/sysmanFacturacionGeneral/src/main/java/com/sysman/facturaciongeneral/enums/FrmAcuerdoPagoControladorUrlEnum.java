/*
 * FrmAcuerdoPagoControladorUrlEnum
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
public enum FrmAcuerdoPagoControladorUrlEnum {

    URL0001("FRMACUERDOPAGOCONTROLADORURL0001", "661019"),

    URL14952("FRMACUERDOPAGOCONTROLADORURL14952", "661017"),

    URL14317("FRMACUERDOPAGOCONTROLADORURL14317", "14001"),
    
    URL0002("FRMACUERDOPAGOCONTROLADORURL0002", "665024"),
    ;

    private final String key;
    private final String value;

    private FrmAcuerdoPagoControladorUrlEnum(String key, String value) {
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
