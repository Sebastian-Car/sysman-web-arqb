/*-
 * CalculoPredial.java
 *
 * 1.0
 * 
 * 6 ago. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que permite el manejo de los atributos de la deuda de un
 * predio
 * 
 * @version 1.0, 15/05/2020
 * @author eamaya
 *
 */

public class DeudaPredio {

    String idCompania;
    String codigoPredio;
    int descuentoley;
    String user;

    public String getIdCompania() {
        return idCompania;
    }

    public void setIdCompania(String idCompania) {
        this.idCompania = idCompania;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public int getDescuentoley() {
        return descuentoley;
    }

    public void setDescuentoley(int descuentoley) {
        this.descuentoley = descuentoley;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
