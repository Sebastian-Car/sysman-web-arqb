package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum LisEjecPptalIngresosCompFlContorladorUrlEnum {

	URL0016("LISEJECPPTALINGRESOSCOMPFLCONTORLADORURL0016","7016"),
	URL0007("LISEJECPPTALINGRESOSCOMPFLCONTORLADORURL0007","4007"),
	URL0023("LISEJECPPTALINGRESOSCOMPFLCONTORLADORURL0022","94022"),
	URL0025("LISEJECPPTALINGRESOSCOMPFLCONTORLADORURL0024","94024")
	
	;

    private final String key;
    private final String value;

    private LisEjecPptalIngresosCompFlContorladorUrlEnum(String key, String value)
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