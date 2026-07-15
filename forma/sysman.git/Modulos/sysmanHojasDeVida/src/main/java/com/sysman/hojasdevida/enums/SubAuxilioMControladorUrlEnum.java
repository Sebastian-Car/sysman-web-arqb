/*-
 * ImprimirHojasDeVidaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.hojasdevida.ImprimirHojasDeVidaControlador}
 * 
 * @version 1.0, 13/12/2017
 * @author pespitia
 *
 */
public enum SubAuxilioMControladorUrlEnum {

    URL0002("RPTQUINQUENIOCONTROLADORURLENUMURL0002", "685023"),

    URL0003("RPTQUINQUENIOCONTROLADORURLENUMURL0003", "700001"),

    URL0001("RPTQUINQUENIOCONTROLADORURLENUMURL0001", "685021");

    private final String key;
    private final String value;

    private SubAuxilioMControladorUrlEnum(String key,
        String value)
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
