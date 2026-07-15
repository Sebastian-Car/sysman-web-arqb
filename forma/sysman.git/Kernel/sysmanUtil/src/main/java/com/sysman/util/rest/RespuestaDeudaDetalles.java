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

import com.google.gson.JsonObject;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase que permite el manejo de los atributos de la respuesta del
 * calculo predial
 * 
 * @version 1.0, 6 ago. 2019
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaDeudaDetalles {

    private long total;
    private int ano;
    private JsonObject conceptos;

    /**
     * Constructor
     */
    public RespuestaDeudaDetalles() {
        super();
        conceptos = null;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public JsonObject getConceptos() {
        return conceptos;
    }

    public void setConceptos(JsonObject conceptos) {
        this.conceptos = conceptos;
    }

}
