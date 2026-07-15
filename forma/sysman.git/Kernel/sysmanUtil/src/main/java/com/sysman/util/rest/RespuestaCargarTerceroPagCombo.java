package com.sysman.util.rest;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RespuestaCargarTerceroPagCombo {
    private String conteo;
// se debe crear con la lista el conteo y el total segun la respuesta asi como esta no sirve para lo que necesita
    private String totales;

    private List<Map<String, Object>> datos;

	public String getConteo() {
		return conteo;
	}

	public void setConteo(String conteo) {
		this.conteo = conteo;
	}

	public String getTotales() {
		return totales;
	}

	public void setTotales(String totales) {
		this.totales = totales;
	}

	public List<Map<String, Object>> getDatos() {
		return datos;
	}

	public void setDatos(List<Map<String, Object>> datos) {
		this.datos = datos;
	}

}
