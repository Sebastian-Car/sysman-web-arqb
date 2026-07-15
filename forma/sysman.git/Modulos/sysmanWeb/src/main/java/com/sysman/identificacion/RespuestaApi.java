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

package com.sysman.identificacion;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase estandar para generar la respuesta de todos los servicios con
 * el fin de enviar los errores de negocio claramente
 * 
 * @version 1.1, 18/09/2018
 * @author jgomez
 *
 */
@XmlRootElement
public class RespuestaApi {
    /**
     * Código de la respuesta del servicio que debe ser de negocio,
     * por defecto se deja 0 indicando que no se genero error alguno y
     * si envia el cuerpo de la respuesta
     */
    private int codigo;
    /**
     * Mensaje del error de negocio, por defecto se deja OK; lo que
     * indica que no hay error y el código es 0
     */
    private String mensaje;
    /**
     * Se incluye la respuesta del servicio para cuado el codigo es
     * diferente de 0
     */
    private Object cuerpo;

    /**
     * Constructor, el cual por defecto deja el codigo = 0 y mensaje =
     * OK, de tal fin que si el servicio no genera error solo se
     * realiza el set a cuerpo
     */
    public RespuestaApi() {
        cuerpo = null;
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

    /**
     * 
     * @return cuerpo
     */
    public Object getCuerpo() {
        return cuerpo;
    }

    /**
     * 
     * @param cuerpo
     */
    public void setCuerpo(Object cuerpo) {
        this.cuerpo = cuerpo;
    }

}
