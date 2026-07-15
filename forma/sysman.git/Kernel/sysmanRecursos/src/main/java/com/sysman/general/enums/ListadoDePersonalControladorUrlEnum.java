/*
 * ListadoDePersonalControladorUrlEnum
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
public enum ListadoDePersonalControladorUrlEnum {

    URL17358("LISTADODEPERSONALCONTROLADORURL17358",
                    "627006"),

    URL18188("LISTADODEPERSONALCONTROLADORURL18188",
                    "627008"),

    URL19115("LISTADODEPERSONALCONTROLADORURL19115",
                    "622003"),

    URL15163("LISTADODEPERSONALCONTROLADORURL15163",
                    "62011"),

    URL14307("LISTADODEPERSONALCONTROLADORURL14307",
                    "62007"),

    URL11242("LISTADODEPERSONALCONTROLADORURL11242",
                    "463008"),

    URL20803("LISTADODEPERSONALCONTROLADORURL20803",
                    "623001"),

    URL12567("LISTADODEPERSONALCONTROLADORURL12567",
                    "618002"),

    URL23100("LISTADODEPERSONALCONTROLADORURL23100",
                    "638006"),

    URL21621("LISTADODEPERSONALCONTROLADORURL21621",
                    "623003"),

    URL22548("LISTADODEPERSONALCONTROLADORURL22548",
                    "638004"),

    URL16687("LISTADODEPERSONALCONTROLADORURL16687",
                    "639005"),

    URL19913("LISTADODEPERSONALCONTROLADORURL19913",
                    "622005"),

    URL13392("LISTADODEPERSONALCONTROLADORURL13392",
                    "618004"),

    URL10010("LISTADODEPERSONALCONTROLADORURL10010",
                    "463006"),

    URL16130("LISTADODEPERSONALCONTROLADORURL16130",
                    "639003"),

    URL23723("LISTADODEPERSONALCONTROLADORURL23723",
                    "736002"),
    
    URL46374("LISTADODEPERSONALCONTROLADORURL46374",
                    "118026");

    private final String key;
    private final String value;

    private ListadoDePersonalControladorUrlEnum(String key, String value) {
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
