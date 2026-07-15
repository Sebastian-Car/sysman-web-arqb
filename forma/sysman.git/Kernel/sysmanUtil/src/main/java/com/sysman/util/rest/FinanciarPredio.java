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
 * @version 1.0, 20/05/2020
 * @author eamaya
 *
 */

public class FinanciarPredio {

    String idCompania;
    String codPredio;
    int[] aniosFinanciar;
    String user;
    int indAplicaLey1175;

    public String getIdCompania() {
        return idCompania;
    }

    public void setIdCompania(String idCompania) {
        this.idCompania = idCompania;
    }

    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public int[] getAniosFinanciar() {
        return aniosFinanciar;
    }

    public void setAniosFinanciar(int[] aniosFinanciar) {
        this.aniosFinanciar = aniosFinanciar;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIndAplicaLey1175() {
        return indAplicaLey1175;
    }

    public void setIndAplicaLey1175(int indAplicaLey1175) {
        this.indAplicaLey1175 = indAplicaLey1175;
    }

}
