package com.sysman.contabilidad.enums;

public enum FrmLisMovDiarioIngresosControladorEnum {
    
 /**
  * @version 1.0 30/05/2018
  * @author bcardenas
  */  

    NOMBREINFORME("001774LisMovDiarioIngresos"),
    
    
    PR_COMPANIA("PR_COMPANIA"),
    
    
    PR_FECHA("PR_FECHA");
    
    
    private final String value;

    private FrmLisMovDiarioIngresosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}