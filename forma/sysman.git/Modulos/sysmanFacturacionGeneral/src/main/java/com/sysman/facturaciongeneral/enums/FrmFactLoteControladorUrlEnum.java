/*
 * FrmFacEstadoControladorUrlEnum
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
public enum FrmFactLoteControladorUrlEnum {

    URL8921("FRMRANGOPRODUCCIONDIANONTROLADORURL8921",
                    "1848005"),

    URL2154("FRMRANGOPRODUCCIONDIANONTROLADORURL2154",
                    "39091"),

    URL9512("FRMRANGOPRODUCCIONDIANONTROLADORURL9512",
                    "47029"),

    URL3578("FRMRANGOPRODUCCIONDIANONTROLADORURL3578",
                    "661052"),

    URL7354("FRMRANGOPRODUCCIONDIANONTROLADORURL7354",
                    "14193"),

    URL2054("FRMRANGOPRODUCCIONDIANONTROLADORURL2054",
                    "1852001"),

    URL2486("FRMRANGOPRODUCCIONDIANONTROLADORURL2486",
                    "663030"),

    URL3564("FRMRANGOPRODUCCIONDIANONTROLADORURL3564",
                    "661053"),

    URL3651("FRMRANGOPRODUCCIONDIANONTROLADORURL3651",
                    "36012"),

    URL2974("FRMRANGOPRODUCCIONDIANONTROLADORURL2974",
                    "661054"),

    URL4587("FRMRANGOPRODUCCIONDIANONTROLADORURL4587",
                    "661055"),

    URL4987("FRMRANGOPRODUCCIONDIANONTROLADORURL4987",
                    "661056"),

    URL7452("FRMRANGOPRODUCCIONDIANONTROLADORURL7452",
                    "661057"),

    URL5768("FRMRANGOPRODUCCIONDIANONTROLADORURL5768",
                    "661058"),

    URL5769("FRMRANGOPRODUCCIONDIANONTROLADORURL5769",
                    "661059"),

    URL5770("FRMRANGOPRODUCCIONDIANONTROLADORURL5770",
                    "661060"),

    URL5771("FRMRANGOPRODUCCIONDIANONTROLADORURL5771",
                    "661061"),

    URL5772("FRMRANGOPRODUCCIONDIANONTROLADORURL5772",
                    "661062"),

    URL5773("FRMRANGOPRODUCCIONDIANONTROLADORURL5773",
                    "661063"),
    
    URL661076("FRMRANGOPRODUCCIONDIANONTROLADORURL5773",
            "661076"),
    
    URL1848009("FRMFACTLOTECONTROLADORURLENUMURL1848009",
    		"1848009"),
    
    URL661077("FRMFACTLOTECONTROLADORURLENUMURL661077",
    		"661077"),
    
    URL661078("FRMFACTLOTECONTROLADORURLENUMURL661078",
    		"661078"),
    
    URL39116("FRMFACTLOTECONTROLADORURLENUMURL39116",
    		"39116"),
    URL661064("FRMFACTLOTECONTROLADORURLENUMURL661064", "661064"),
    ;

    private final String key;
    private final String value;

    private FrmFactLoteControladorUrlEnum(String key, String value) {
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
