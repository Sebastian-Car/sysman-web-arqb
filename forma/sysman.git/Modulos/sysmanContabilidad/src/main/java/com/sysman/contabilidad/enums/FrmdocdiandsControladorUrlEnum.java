/*
 * ComprobantecntsControladorUrlEnum
 *
 * 1.0
 *
 * 01/01/2022
 *CPEREZ
 * Copyright Stefanini Sysman
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
public enum FrmdocdiandsControladorUrlEnum {

    URL9848("FrmdocdiandsControladorUrlEnum185600C",
                    "185600C"),

    URL5474("FrmdocdiandsControladorUrlEnum1856003",
                    "1856003"),

    URL3561("FrmdocdiandsControladorUrlEnum1856004",
                    "1856004"),

    URL8245("FrmdocdiandsControladorUrlEnum666012",
                    "666012"),

    URL2735("FrmdocdiandsControladorUrlEnum665025",
                    "665025"),

    URL1987("FrmdocdiandsControladorUrlEnum1857003",
                    "1857003"),

    URL7456("FrmdocdiandsControladorUrlEnum185700C",
                    "185700C"),

    URL3562("FrmdocdiandsControladorUrlEnum1857004",
                    "1857004"),

    URL665023("FrmdocdiandsControladorUrlEnum665023",
                    "665023"),
    URL9457("FRMRANGOPRODUCCIONDIANONTROLADORURL9457",
            "1851001"),  
	URL1895025("COMPROBANTECNTSCONTROLADORURL1895024","1895025"),
	
	URL1895028("COMPROBANTECNTSCONTROLADORURL1895024","1895028");

    private final String key;
    private final String value;

    private FrmdocdiandsControladorUrlEnum(String key, String value) {
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
