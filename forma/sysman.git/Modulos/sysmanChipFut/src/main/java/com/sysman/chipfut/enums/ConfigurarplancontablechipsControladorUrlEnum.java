/*
 * AcummensualControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfigurarplancontablechipsControladorUrlEnum {

    URL3665("ACUMMENSUALCONTROLADORURL3665", "16124"),

    URL20888("ACUMMENSUALCONTROLADORURL20888", "16126"),

    URL14271("ACUMMENSUALCONTROLADORURL14271", "46002"),

    URL10355("ACUMMENSUALCONTROLADORURL10355", "34001"),

    URL1578("ACUMMENSUALCONTROLADORURL1578", "222002"),

    URL1512("ACUMMENSUALCONTROLADORURL1512", "4001"),

    URL1687("ACUMMENSUALCONTROLADORURL1512", "14154"),

    URL19121("ACUMMENSUALCONTROLADORURL19121", "16130"),

    URL8935("ACUMMENSUALCONTROLADORURL8395", "51002"),

    URL15847("ACUMMENSUALCONTROLADORURL8395", "16131"),

    URL739("CONFIGURARPLANCONTABLECHIPURL739", "16175");

    private final String key;
    private final String value;

    private ConfigurarplancontablechipsControladorUrlEnum(String key,
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
