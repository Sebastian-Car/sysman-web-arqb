package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Pojo requerido para obtener los datos requeridos para el proceso
 * 
 * @version 1.0, 28/01/2020
 * @author eamaya
 * 
 */

public class ParametrosRegRecuadoCausacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fechaDeReporte;

    private String codigo;

    private double valor;

    private String cuentaBancaria;

    private String observacion;
    
    private String reciboUnico;

    public String getFechaDeReporte() {
        return fechaDeReporte;
    }

    public void setFechaDeReporte(String fechaDeReporte) {
        this.fechaDeReporte = fechaDeReporte;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

	public String getReciboUnico() {
		return reciboUnico;
	}

	public void setReciboUnico(String reciboUnico) {
		this.reciboUnico = reciboUnico;
	}

}
