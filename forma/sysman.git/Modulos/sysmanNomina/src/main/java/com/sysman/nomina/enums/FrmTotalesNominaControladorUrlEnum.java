package com.sysman.nomina.enums;

public enum FrmTotalesNominaControladorUrlEnum {

	   URL537004("FRMTOTALESNOMINACONTROLADORURL537004", "537004"),
	   
	   URL471008("FRMTOTALESNOMINACONTROLADORURL471008", "471008"),
	   
    	URL471049("FRMTOTALESNOMINACONTROLADORURL471049", "471049"),
    	
	   URL471050("FRMTOTALESNOMINACONTROLADORURL471050", "471050");  
		
		private final String key;
	    private final String value;

	    private FrmTotalesNominaControladorUrlEnum(String key, String value)
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
