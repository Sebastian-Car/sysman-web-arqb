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
 * @version 1.0, 02/11/2021
 * @author gfigueredo
 *
 */
@XmlRootElement
public class RespuestaFridaLegalizarNotas {
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
    RespuestaFridaLegalizarCuerpoNotas cuerpo;

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

    public RespuestaFridaLegalizarCuerpoNotas getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(RespuestaFridaLegalizarCuerpoNotas cuerpo) {
        this.cuerpo = cuerpo;
    }

}
