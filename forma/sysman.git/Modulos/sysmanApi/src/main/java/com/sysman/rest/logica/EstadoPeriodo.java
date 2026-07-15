package com.sysman.rest.logica;

import java.io.Serializable;
import java.util.List;

import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.rest.enums.EnumAuxiliaresVarios;


/**
 * Pojo para la obtenecion del estado del periodo
 */
public class EstadoPeriodo implements Serializable {
	private String estado ;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constructor vacio
     */
    public EstadoPeriodo() {
    }
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
    

}
