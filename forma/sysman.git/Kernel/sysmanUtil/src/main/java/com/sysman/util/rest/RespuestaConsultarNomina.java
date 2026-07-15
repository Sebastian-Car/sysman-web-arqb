package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RespuestaConsultarNomina {

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
    private RespuestaCuerpoConsultarNomina cuerpo;
    
    
	public RespuestaConsultarNomina() {
		cuerpo = null;
        codigo = 0;
        mensaje = "OK";
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


	public RespuestaCuerpoConsultarNomina getCuerpo() {
		return cuerpo;
	}


	public void setCuerpo(RespuestaCuerpoConsultarNomina cuerpo) {
		this.cuerpo = cuerpo;
	}
	
	
}
