package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmactualizarauxiliareseningresoControladorUrlEnum {

	URL2656("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL2656", "4001"),   // Años
	URL5449("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL5449","23058"),   // Auxiliar compañia-año	
    URL5450("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL23019","23001"),  // Auxiliar compañia-año
    URL8835("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL8835","20082"),	// centro costo compañia -año 
    URL9520("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL9520","20013"),	// centro costo compañia -año
    URL5584("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL5584","13047"),	// referencias compañia - año  
    URL5585("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL5585","13001"),	// referencias compañia - año
	URL34043("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL34043","34072"),	// Fuente recurso compañia-año 
	URL34045("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL34045","34001"),	// Fuente recurso compañia-año  
    URL3725("FRMACTUALIZARAUXILIARESENINGRESOCONTROLADORURLURL3725", "45111");  // 94109 V_PLAN_PRESUPUESTAL compañia - año - naturaleza



    

    private final String key;
    private final String value;

    private FrmactualizarauxiliareseningresoControladorUrlEnum(String key, String value)
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