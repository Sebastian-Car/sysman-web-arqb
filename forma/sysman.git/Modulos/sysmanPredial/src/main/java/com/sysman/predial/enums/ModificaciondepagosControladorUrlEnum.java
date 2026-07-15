/*
 * ModificaciondepagosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ModificaciondepagosControladorUrlEnum {

    URL7117("MODIFICACIONDEPAGOSCONTROLADORURL7117",
                    "374011"),

    URL9216("MODIFICACIONDEPAGOSCONTROLADORURL9216",
                    "374015"),

    URL10767("MODIFICACIONDEPAGOSCONTROLADORURL10767",
                    "410001"),

    URL10052("MODIFICACIONDEPAGOSCONTROLADORURL10052",
                    "367122"),

    URL6535("MODIFICACIONDEPAGOSCONTROLADORURL6535",
                    "375004"),

    URL7932("MODIFICACIONDEPAGOSCONTROLADORURL7932",
                    "374013"),

    URL1341("MODIFICACIONDEPAGOSCONTROLADORURL1341",
                    "407001"),

    URL1342("MODIFICACIONDEPAGOSCONTROLADORURL1342",
                    "407002"),

    URL1343("MODIFICACIONDEPAGOSCONTROLADORURL1343",
                    "407003"),

    URL1344("MODIFICACIONDEPAGOSCONTROLADORURL1344",
                    "407004"),

    URL1350("MODIFICACIONDEPAGOSCONTROLADORURL1350",
                    "374017"),

    URL1351("MODIFICACIONDEPAGOSCONTROLADORURL1351",
                    "410003"),

    URL1352("MODIFICACIONDEPAGOSCONTROLADORURL1352",
                    "410004"),

    URL1353("MODIFICACIONDEPAGOSCONTROLADORURL1353",
                    "410004"),

    URL1345("MODIFICACIONDEPAGOSCONTROLADORURL1345",
                    "407005");

    private final String key;
    private final String value;

    private ModificaciondepagosControladorUrlEnum(String key, String value) {
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
