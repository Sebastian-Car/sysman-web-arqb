package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmInventarioFisicoControladorUrlEnum {

	URL135009("FrmInventarioFisicoControladorUrlEnum135009","135009"),
	
	URL119033("FrmInventarioFisicoControladorUrlEnum119033","119033"),
	
	URL112199("FrmInventarioFisicoControladorUrlEnum112199","112199");

    private final String key;
    private final String value;

    private FrmInventarioFisicoControladorUrlEnum(String key, String value) {
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
