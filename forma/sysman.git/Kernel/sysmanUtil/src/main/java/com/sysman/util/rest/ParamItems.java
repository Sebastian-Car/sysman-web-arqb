/*-
 * ParamItems.java
 *
 * 1.0
 * 
 * 5/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * Clase que administra los parametros del servicio items
 * 
 * @version 1.0, 5/01/2021
 * @author eamaya
 *
 */
public class ParamItems {
    /**
     * 
     */
    private List<ParamItem> items;

    /**
     * @return the items
     */
    public List<ParamItem> getItems() {
        return items;
    }

    /**
     * @param items
     * the items to set
     */
    public void setItems(List<ParamItem> items) {
        this.items = items;
    }
}
