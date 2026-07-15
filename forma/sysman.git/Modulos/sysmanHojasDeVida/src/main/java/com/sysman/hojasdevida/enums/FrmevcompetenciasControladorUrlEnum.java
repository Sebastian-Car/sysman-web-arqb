/**
 * Clase: FrmevcompetenciasControladorUrlEnum.java
 * 
 */

package com.sysman.hojasdevida.enums;

/**
 * @version 1.0, 23/01/2018
 * @author fperez
 *
 */

public enum FrmevcompetenciasControladorUrlEnum {

    URL220("FRMEVCOMPETENCIASCONTROLADORURL220", "753002"),

    URL179("FRMEVCOMPETENCIASCONTROLADORURL179", "755001"),

    URL258("FRMEVCOMPETENCIASCONTROLADORURL258", "753001");

    private final String key;
    private final String value;

    private FrmevcompetenciasControladorUrlEnum(String key, String value)
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
