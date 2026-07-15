/*-
 * SiifReportesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 28/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * 
 * @version 1.0, 28/11/2017
 * @author jcrodriguez
 *
 */
public enum SiifReportesControladorUrlEnum {

    URL1717("SIIFREPORTESURL1717", "4001"), //trae el año

    URL1718("FRMCAEJECUCIONPASCONTROLADORURL1718", "7001"), // trae el mes

    URL1719("FRMCAEJECUCIONPASCONTROLADORURL1719", "1735001"); //Trae el formato y el nombre del reporte

    private final String key;
    private final String value;

    private SiifReportesControladorUrlEnum(String key, String value) {
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
