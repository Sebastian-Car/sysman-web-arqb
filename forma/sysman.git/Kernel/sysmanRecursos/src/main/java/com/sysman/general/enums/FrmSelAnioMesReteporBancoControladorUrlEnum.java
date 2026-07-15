/*
 * FrmSelAnioMesReteporBancoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmSelAnioMesReteporBancoControladorUrlEnum {

    URL4300("FRMSELANIOMESRETEPORBANCOCONTROLADORURL4300",
                    "7005"),

    URL3765("FRMSELANIOMESRETEPORBANCOCONTROLADORURL3765",
                    "4001");

    private final String key;
    private final String value;

    private FrmSelAnioMesReteporBancoControladorUrlEnum(String key,
        String value) {
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
