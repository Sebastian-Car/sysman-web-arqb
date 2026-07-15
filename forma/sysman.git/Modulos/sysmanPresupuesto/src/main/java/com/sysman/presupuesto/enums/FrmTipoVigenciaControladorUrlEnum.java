package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmTipoVigenciaControladorUrlEnum {

	URL1891001("FRMTIPOVIGENCIACONTROLADORURL1891001","1891001"),
	
	URL195800G("FRMTIPOVIGENCIACONTROLADORURL195800G","195800G")
	
	;

    private final String key;
    private final String value;

    private FrmTipoVigenciaControladorUrlEnum(String key, String value)
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