/*
 * EscalafonsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EscalafonsControladorUrlEnum {

    URL12675("ESCALAFONSCONTROLADORURL12675", "4001"),

    URL10619("ESCALAFONSCONTROLADORURL10619", "607007"),

    URL6934("ESCALAFONSCONTROLADORURL6934", "607005"),

    URL8232("ESCALAFONSCONTROLADORURL8232", "607006"),

    URL5714("ESCALAFONSCONTROLADORURL5714", "607001"),

    URL10635("ESCALAFONSCONTROLADORURL10635", "607003"),

    URL10637("ESCALAFONSCONTROLADORURL10637", "607004"),

    URL10638("ESCALAFONSCONTROLADORURL10638", "463001"),

    URL10639("ESCALAFONSCONTROLADORURL10639", "463005"),

    URL10640("ESCALAFONSCONTROLADORURL10640", "607011"),
    
    URL257("ESCALAFONSCONTROLADORURL247", "1775001"),
    
    URL5217("ESCALAFONSCONTROLADORURL5217", "607017"),
    
    URL607022("ESCALAFONSCONTROLADORURL5217", "607022");

    private final String key;
    private final String value;

    private EscalafonsControladorUrlEnum(String key, String value) {
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
