/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author dcastiblanco
 *  Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 *
 */
public enum MovimientoElementoControladorEnum {
	
	 PARAM0("CODIGO");

    private final String value;

    private MovimientoElementoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}