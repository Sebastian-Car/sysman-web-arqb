package com.sysman.nomina.enums;

public enum SubeConceptosHistControladorUrlEnum {
	
	
    URL6298("SUBENOVEDADESCONTROLADORURL6298", "7027"),

    URL5617("SUBENOVEDADESCONTROLADORURL5617", "471008"),

    URL7130("SUBENOVEDADESCONTROLADORURL7130", "471048"),
	
	URL004("FRMMONITORTRAMITESCONTROLADORURL001", "51004");

    private final String key;
    private final String value;

    private SubeConceptosHistControladorUrlEnum(String key, String value)
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
