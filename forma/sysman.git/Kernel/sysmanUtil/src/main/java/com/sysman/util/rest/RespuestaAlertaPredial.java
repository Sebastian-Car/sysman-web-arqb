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
public class RespuestaAlertaPredial {

    private String predio;
    private long codigo;
    private String mensaje;

    /**
     * Constructor
     */
    public RespuestaAlertaPredial(String predio, long codigo,
        String mensaje) {
        super();
        this.predio = predio;
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
