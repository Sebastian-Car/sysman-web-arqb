/*
 * CertificadoPazSalvosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CertificadoPazSalvosControladorUrlEnum {

    URL7846("CERTIFICADOPAZSALVOSCONTROLADORURL7846", "214026"),

    URL18553("CERTIFICADOPAZSALVOSCONTROLADORURL18553", ""),

    URL8507("CERTIFICADOPAZSALVOSCONTROLADORURL8507", "104021"),

    URL18554("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "342009"), // INSERT

    URL18555("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "342008"), // UPDATE

    URL18556("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "342011"), // DELETE

    URL18557("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "342012"), // PAGINADO

    URL18558("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "342014"),

    URL18559("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "213081"),

    URL18560("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "342015"),

    URL18561("CERTIFICADOPAZSALVOSCONTROLADORURL18553", "104022");// CODIGO
                                                                  // RUTA

    private final String key;
    private final String value;

    private CertificadoPazSalvosControladorUrlEnum(String key, String value) {
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
