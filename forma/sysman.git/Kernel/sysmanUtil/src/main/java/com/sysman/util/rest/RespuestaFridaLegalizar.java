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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Respuesta al servicio de legalizar factura
 * 
 * @version 1.0, 8/01/2021
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaFridaLegalizar {
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
    Object cuerpo;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(Object cuerpo) {
        this.cuerpo = cuerpo;
    }

}
