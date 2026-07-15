package com.sysman.nomina.enums;

public enum DuplicarConceptosDocentesControladorUrlEnum {
	
    URL001("PERIODOTRABAJOCONTROLADORURL6834", "462001"),
    
    URL6890("HISTORICOSCONTROLADORURL6890",
            "210031"), ;  
	
	private final String key;
    private final String value;

    private DuplicarConceptosDocentesControladorUrlEnum(String key, String value)
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
