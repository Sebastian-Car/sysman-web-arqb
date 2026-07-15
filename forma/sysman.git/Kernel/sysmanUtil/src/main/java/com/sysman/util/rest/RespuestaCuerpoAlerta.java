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

import java.util.List;

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
public class RespuestaCuerpoAlerta {

    private List<RespuestaAlertaPredial> alertas;

    /**
     * Constructor
     */
    public RespuestaCuerpoAlerta() {
        super();
        alertas = null;

    }

    public List<RespuestaAlertaPredial> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<RespuestaAlertaPredial> alertas) {
        this.alertas = alertas;
    }

}
