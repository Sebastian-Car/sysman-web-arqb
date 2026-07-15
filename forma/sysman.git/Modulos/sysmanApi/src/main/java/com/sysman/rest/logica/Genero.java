package com.sysman.rest.logica;

import java.io.Serializable;
import java.util.List;

import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.rest.enums.EnumAuxiliaresVarios;


/**
 * Pojo para la obtenecion del genero
 */
public class Genero implements Serializable {
	private String codigo;
	private String nombre;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constructor vacio
     */
    public Genero() {

    }
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
