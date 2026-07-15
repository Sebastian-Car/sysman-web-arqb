/*-
 * RespuestaApi.java
 *
 * 1.0
 * 
 * 26/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase para generar la respuesta del servcio envio factura
 * 
 * 
 * @version 1.1, 19/06/2018
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaEnvioFactura {
    /**
     * Código de la respuesta del servicio que debe ser de negocio,
     * por defecto se deja 0 indicando que no se genero error alguno y
     * si envia el cuerpo de la respuesta
     */
    int codigo;
    /**
     * Mensaje del error de negocio, por defecto se deja OK; lo que
     * indica que no hay error y el código es 0
     */
    String mensaje;
    /**
     * Se incluye la respuesta del servicio para cuado el codigo es
     * diferente de 0
     */
    List<Object> cuerpo;

    /**
     * Constructor, el cual por defecto deja el codigo = 0 y mensaje =
     * OK, de tal fin que si el servicio no genera error solo se
     * realiza el set a cuerpo
     */
    public RespuestaEnvioFactura() {

        codigo = 0;
        mensaje = "OK";
    }

    /**
     * 
     * @return codigo
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * 
     * @param codigo
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * 
     * @return mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * 
     * @param mensaje
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<Object> getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(List<Object> cuerpo) {
        this.cuerpo = cuerpo;
    }

}
