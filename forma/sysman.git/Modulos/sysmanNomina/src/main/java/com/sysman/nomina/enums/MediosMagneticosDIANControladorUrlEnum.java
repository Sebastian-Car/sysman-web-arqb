/*
 * MediosMagneticosDIANControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MediosMagneticosDIANControladorUrlEnum {

    URL11537("MEDIOSMAGNETICOSDIANCONTROLADORURL11537",
                    "471039"),

    URL13652("MEDIOSMAGNETICOSDIANCONTROLADORURL13652",
                    "471039"),

    URL11062("MEDIOSMAGNETICOSDIANCONTROLADORURL11062",
                    "537008"),

    URL13125("MEDIOSMAGNETICOSDIANCONTROLADORURL13125",
                    "471032"),

    URL12121("MEDIOSMAGNETICOSDIANCONTROLADORURL12121",
                    "471026"),

    URL131313("MEDIOSMAGNETICOSDIANCONTROLADORURL131313",
                    "471026"),

    URL4545("MEDIOSMAGNETICOSDIANCONTROLADORURL4545",
                    "1051001"),

    URL4646("MEDIOSMAGNETICOSDIANCONTROLADORURL4646",
                    "1051002"),

    URL4747("MEDIOSMAGNETICOSDIANCONTROLADORURL4747",
                    "1051003"),

    URL4848("MEDIOSMAGNETICOSDIANCONTROLADORURL4747",
                    "1051004"),

    URL4949("MEDIOSMAGNETICOSDIANCONTROLADORURL4949",
                    "1051005")

    ;

    private final String key;
    private final String value;

    private MediosMagneticosDIANControladorUrlEnum(String key, String value) {
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
