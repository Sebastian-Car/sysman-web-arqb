package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum EjecucionGastosComparativaControladorUrlEnum {

	URL0016("EJECUCIONGASTOSCOMPARATIVACONTROLADORURL0016","7016"),
	URL0007("EJECUCIONGASTOSCOMPARATIVACONTROLADORURL0016","4007"),
	URL0034("EJECUCIONGASTOSCOMPARATIVACONTROLADORURL0016","94034"),
	URL0036("EJECUCIONGASTOSCOMPARATIVACONTROLADORURL0016","94036")
	
	;

    private final String key;
    private final String value;

    private EjecucionGastosComparativaControladorUrlEnum(String key, String value)
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