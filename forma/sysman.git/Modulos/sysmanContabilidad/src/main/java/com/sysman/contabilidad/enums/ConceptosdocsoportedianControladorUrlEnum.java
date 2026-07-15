package com.sysman.contabilidad.enums;

public enum ConceptosdocsoportedianControladorUrlEnum {
	
    URL21760("CONCEPTOSDOCSOPORTEDIANCONTROLADOREURLENUM21760", "4001"),
    	
	URL1895001("CONCEPTOSDOCSOPORTEDIANCONTROLADOREURLENUM1895001","1895001"),
	
	URL1895020("CONCEPTOSDOCSOPORTEDIANCONTROLADOREURLENUM1895020","1895019");
	
    private final String key;
    private final String value;

    private ConceptosdocsoportedianControladorUrlEnum(String key,
        String value) {
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
