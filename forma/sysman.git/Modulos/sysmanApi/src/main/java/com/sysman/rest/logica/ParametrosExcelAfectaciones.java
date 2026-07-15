package com.sysman.rest.logica;

import java.io.Serializable;
import java.util.List;

public class ParametrosExcelAfectaciones implements Serializable {

    private static final long serialVersionUID = 1L;

    private String compania;

    private String nitEntidad;

    private int anio;

    private String mes;

    private String dia;
    
    private String usuario;

//    private List<ParametrosRegRecuadoCausacion> registros;

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getNitEntidad() {
        return nitEntidad;
    }

    public void setNitEntidad(String nitEnntidad) {
        this.nitEntidad = nitEnntidad;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

//    public List<ParametrosRegRecuadoCausacion> getRegistros() {
//        return registros;
//    }
//
//    public void setRegistros(List<ParametrosRegRecuadoCausacion> registros) {
//        this.registros = registros;
//    }

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
