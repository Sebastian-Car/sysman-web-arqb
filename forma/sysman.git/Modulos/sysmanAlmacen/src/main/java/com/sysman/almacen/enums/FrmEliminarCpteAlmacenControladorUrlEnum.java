package com.sysman.almacen.enums;

public enum FrmEliminarCpteAlmacenControladorUrlEnum {

    URL4001("ELIMINARCOMPROBANTECONTROLADORURL4001", "4001"), 
    
    URL139003("ELIMINARCOMPROBANTECONTROLADORURL139003", "139003"), 
    
    URL41002("ELIMINARCOMPROBANTECONTROLADORURL41002", "41002"), 
    
    URL119014("ELIMINARCOMPROBANTECONTROLADORURL119014", "119014");
	
	

    private final String key;
    private final String value;

    private FrmEliminarCpteAlmacenControladorUrlEnum(String key, String value)
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
