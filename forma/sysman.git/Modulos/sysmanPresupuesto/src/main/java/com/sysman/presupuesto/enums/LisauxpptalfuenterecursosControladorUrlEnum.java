/**
 * 
 */
package com.sysman.presupuesto.enums;

/**
 * @author dcastiblanco
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LisauxpptalfuenterecursosControladorUrlEnum {
	URL5264("LISAUXPPTALFUENTERECURSOSCONTROLADORURLENUM5264", "25008"),
	
	URL11906("LISAUXPPTALFUENTERECURSOSCONTROLADORURLENUM11906","23010"),
	
	URL3766("LISAUXPPTALFUENTERECURSOSCONTROLADORURLENUM3766", "45014"),
	
	URL4700("LISAUXPPTALFUENTERECURSOSCONTROLADORURLENUM4700", "45016"),
	
	URL5993("LISAUXPPTALFUENTERECURSOSCONTROLADORURLENUM5993", "25012"),
	
	URL12612("LISAUXPPTALFUENTERECURSOSCONTROLADORURLENUM12612","23019");

	private final String key;
    private final String value;

    private LisauxpptalfuenterecursosControladorUrlEnum(String key, String value) {
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

