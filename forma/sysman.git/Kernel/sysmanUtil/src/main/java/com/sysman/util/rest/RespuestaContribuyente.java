/*-
 * RespuestaAutoServicio.java
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

/*-
 * RespuestaApi.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @version 1.1, 11/12/2020
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaContribuyente {
    /**
     * Código de la respuesta del servicio que debe ser de negocio,
     * por defecto se deja 0 indicando que no se genero error alguno y
     * si envia el cuerpo de la respuesta
     */
    private long codigo;
    /**
     * Mensaje del error de negocio, por defecto se deja OK; lo que
     * indica que no hay error y el código es 0
     */
    private String mensaje;
    /**
     * Se incluye la respuesta del servicio para cuado el codigo es
     * diferente de 0
     */
    private RespuestaCuerpoContribuyente cuerpo;

    /**
     * Constructor, el cual por defecto deja el codigo = 0 y mensaje =
     * OK, de tal fin que si el servicio no genera error solo se
     * realiza el set a cuerpo
     */
    public RespuestaContribuyente() {
        cuerpo = null;
        codigo = 0;
        mensaje = "OK";
    }

    /**
     * 
     * @return codigo
     */
    public long getCodigo() {
        return codigo;
    }

    /**
     * 
     * @param codigo
     */
    public void setCodigo(long codigo) {
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

    public RespuestaCuerpoContribuyente getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(RespuestaCuerpoContribuyente cuerpo) {
        this.cuerpo = cuerpo;
    }

}
