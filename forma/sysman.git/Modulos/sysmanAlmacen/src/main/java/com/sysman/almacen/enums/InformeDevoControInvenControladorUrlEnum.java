/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author dcastiblanco
 ** Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InformeDevoControInvenControladorUrlEnum {
	
	URL9785("INFORMEDEVOCONTROINVENCONTROLADORURL9785", "112011"),

    URL9786("INFORMEDEVOCONTROINVENCONTROLADORURL9786", "112013");

     private final String key;
    private final String value;

    private InformeDevoControInvenControladorUrlEnum(String key,
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
