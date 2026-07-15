/*-
 * FrmListadoFacturacionControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 19/12/2023
 * @author mperez
 *
 */
public enum FrmListFacEntreFechasControladorUrlEnum {

    URL14006("FRMLISTFACENTREFECHASCONTROLADORURL14006", "14006"),

    URL14010("FRMLISTFACENTREFECHASCONTROLADORURL14010", "14010"),

    URL663001("FRMLISTFACENTREFECHASCONTROLADORURL663001", "663001"),

    URL663003("FRMLISTFACENTREFECHASCONTROLADORURL663003", "663003"),
	
	URL1915003("FRMLISTFACENTREFECHASCONTROLADORURL1915003", "1915003"),
	
	URL14122("FRMLISTFACENTREFECHASCONTROLADORURL14122", "14122"),
	
	URL14202("FRMLISTFACENTREFECHASCONTROLADORURL14122", "14202"); 

    private final String key;
    private final String value;

    private FrmListFacEntreFechasControladorUrlEnum(String key, String value) {
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
