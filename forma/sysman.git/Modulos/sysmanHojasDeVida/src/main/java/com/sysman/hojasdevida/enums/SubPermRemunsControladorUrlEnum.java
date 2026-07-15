/*-
 * ImprimirHojasDeVidaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.hojasdevida.ImprimirHojasDeVidaControlador}
 * 
 * @version 1.0, 13/12/2017
 * @author pespitia
 *
 */
public enum SubPermRemunsControladorUrlEnum {

    URL0002("SUBPERMREMUNSCONTROLADORURLENUMURL0002", "626019"),

    URL0003("SUBPERMREMUNSCONTROLADORURLENUMURL0003", "700001"),

    URL0001("SUBPERMREMUNSCONTROLADORURLENUMURL0001", "627004"),

    URL0004("SUBPERMREMUNSCONTROLADORURLENUMURL0004", "626021"),

    URL0005("SUBPERMREMUNSCONTROLADORURLENUMURL0005", "62600D"),

    URL0006("SUBPERMREMUNSCONTROLADORURLENUMURL0006", "627012"),

    URL0008("SUBPERMREMUNSCONTROLADORURLENUMURL0006", "62600C"),

    URL0007("SUBPERMREMUNSCONTROLADORURLENUMURL0007", "707003"),

    URL0009("SUBPERMREMUNSCONTROLADORURLENUMURL0009", "471002"),

    URL0010("SUBPERMREMUNSCONTROLADORURLENUMURL0010", "7031"),

    URL0011("SUBPERMREMUNSCONTROLADORURLENUMURL0011", "471031"),

    URL0012("SUBPERMREMUNSCONTROLADORURLENUMURL0012", "626022"),

    URL0013("SUBPERMREMUNSCONTROLADORURLENUMURL0013", "");

    private final String key;
    private final String value;

    private SubPermRemunsControladorUrlEnum(String key,
        String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
