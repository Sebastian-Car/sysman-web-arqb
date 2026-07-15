/*-
 * UrlServiceCache.java
 *
 * 1.0
 * 
 * 2/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cache.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 2/05/2017
 * @author cmanrique
 *
 */
public enum UrlServiceCache {

    SYSMANDSUNIST("-1001"),

    SYSMANIRISST("-2001"),

    SYSMANAUDITORIA("-4001")

    ;

    private final String codeUrl;

    private UrlServiceCache(String codeUrl)
    {
        this.codeUrl = codeUrl;
    }

    /**
     * @return the codeUrl
     */
    public String getCodeUrl()
    {
        return codeUrl;
    }

}
