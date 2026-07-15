/*
 * SfconceptosControladorUrlEnum
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
public enum SfconceptosControladorUrlEnum {

    URL31616("SFCONCEPTOSCONTROLADORURL31616", "663009"),

    URL19055("SFCONCEPTOSCONTROLADORURL19055", "663010"),

    URL44206("SFCONCEPTOSCONTROLADORURL44206", "663013"),

    URL10092("SFCONCEPTOSCONTROLADORURL10092", "23001"),

    URL10742("SFCONCEPTOSCONTROLADORURL10742", "20003"),

    URL11903("SFCONCEPTOSCONTROLADORURL11903", "29127"),

    URL11419("SFCONCEPTOSCONTROLADORURL11419", "21001"),

    URL55481("SFCONCEPTOSCONTROLADORURL55481", "29139"),
    
    URL55784("SFCONCEPTOSCONTROLADORURL55784", "13026"),
    
    URL58412("SFCONCEPTOSCONTROLADORURL55481", "34001"),

    URL47769("SFCONCEPTOSCONTROLADORURL47769", "155001"),

    URL9670("SFCONCEPTOSCONTROLADORURL9670", "4001"),

    URL2227("SFCONCEPTOSCONTROLADORURL2227", "20023"),

    URL2251("SFCONCEPTOSCONTROLADORURL2251", "23005"),
    
    URL2252("SFCONCEPTOSCONTROLADORURL2252", "1884036"),
    
    URL195001("SFCONCEPTOSCONTROLADORURL1950", "1950001");

    private final String key;
    private final String value;

    private SfconceptosControladorUrlEnum(String key, String value)
    {
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
