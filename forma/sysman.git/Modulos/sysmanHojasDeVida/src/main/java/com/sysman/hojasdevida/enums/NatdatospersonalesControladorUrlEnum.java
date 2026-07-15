/*
 * NatdatospersonalesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NatdatospersonalesControladorUrlEnum {

    URL13088("NATDATOSPERSONALESCONTROLADORURL13088",
                    "1001"),

    URL18785("NATDATOSPERSONALESCONTROLADORURL18785",
                    "5001"),

    URL13664("NATDATOSPERSONALESCONTROLADORURL13664",
                    "209001"),

    URL14298("NATDATOSPERSONALESCONTROLADORURL14298",
                    "2001"),

    URL20843("NATDATOSPERSONALESCONTROLADORURL20843",
                    "631002"),

    URL17634("NATDATOSPERSONALESCONTROLADORURL17634",
                    "1001"),

    URL15724("NATDATOSPERSONALESCONTROLADORURL15724",
                    "5001"),

    URL16717("NATDATOSPERSONALESCONTROLADORURL16717",
                    "5001"),

    URL10877("NATDATOSPERSONALESCONTROLADORURL10877",
                    "2001"),

    URL1345("NATDATOSPERSONALESCONTROLADORURL1345",
                    "2001"),

    URL1858("NATDATOSPERSONALESCONTROLADORURL1858",
                    "2001"),

    URL22799("NATDATOSPERSONALESCONTROLADORURL22799",
                    "20046"),

    URL22219("NATDATOSPERSONALESCONTROLADORURL22219",
                    "36002"),

    URL21460("NATDATOSPERSONALESCONTROLADORURL21460",
                    "540003"),

    URL11638("NATDATOSPERSONALESCONTROLADORURL11638",
                    "5001"),

    URL19718("NATDATOSPERSONALESCONTROLADORURL19718",
                    "615001"),

    URL24079("NATDATOSPERSONALESCONTROLADORURL24079",
                    "14130"),

    URL10355("NATDATOSPERSONALESCONTROLADORURL10355",
                    "1001"),

    URL12562("NATDATOSPERSONALESCONTROLADORURL12562",
                    "1001"),

    URL23462("NATDATOSPERSONALESCONTROLADORURL23462",
                    "614001"),

    URL21907("NATDATOSPERSONALESCONTROLADORURL21907",
                    "633001"),

    URL20266("NATDATOSPERSONALESCONTROLADORURL20266",
                    "637001"),

    URL20267("NATDATOSPERSONALESCONTROLADORURL20267", "210116"),
    
    
    URL20269("NATDATOSPERSONALESCONTROLADORURL20269", "459007"),
    
    URL20270("NATDATOSPERSONALESCONTROLADORURL20270", "647001"),
	
	
    URL20271("NATDATOSPERSONALESCONTROLADORURL20271", "1808005");

    private final String key;
    private final String value;

    private NatdatospersonalesControladorUrlEnum(String key, String value) {
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
