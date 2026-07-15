package com.sysman.general.enums;

public enum FrmCarterafinanciablesControladorUrlEnum {
	
	URL001("LIBRODIARIOOFICIALCONTROLADORURL9410", "20013"),
	
	URL002("LISRESULTADOSCONTROLADORURL6648", "16067"),
	
	URL003("LISRESULTADOSCONTROLADORURL7929", "16071"),
	
	URL16209("FRMCARTERAFINANCIABLESCONTROLADORURL16209", "16209"),
	
    URL16207("FRMCARTERAFINANCIABLESCONTROLADORURL16207", "16207"), 
    
    URL14036("FRMCARTERAFINANCIABLESCONTROLADORURL14036","14036");

	
	
    private final String key;
    private final String value;

    private FrmCarterafinanciablesControladorUrlEnum(String key, String value) {
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
   