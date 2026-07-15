/* DevolutivoPorGrupoDependenciaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * 
 * @version 1.0, 18/10/2018
 * @author asana
 *
 */
public enum  DocumentosCorreccionValorControladorUrlEnum {

        URL3217("DEVOLUTIVOSPORGRUPOCONTROLADORURL3217", "1745001"),
        
        URL3218("DEVOLUTIVOSPORGRUPOCONTROLADORURL3218", "1745003"),
	
        URL3219("DEVOLUTIVOSPORGRUPOCONTROLADORURL3219", "141112"),
	
        URL3220("DEVOLUTIVOSPORGRUPOCONTROLADORURL3220", "141114");

    private final String key;
    private final String value;

    private  DocumentosCorreccionValorControladorUrlEnum(String key, String value)
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
