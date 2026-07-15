package com.sysman.rest.logica;

import java.io.Serializable;
import java.util.List;

/**
 * Pojo requerido para obtener los datos requeridos para el proceso
 * 
 * @version 1.0, 28/01/2020
 * @author eamaya
 * 
 */

public class ParametrosRecuadoCausacion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int loteNum;

    private String compania;

    private String nitEntidad;

    private int anio;

    private String tipoCobro;

    private String usuario;

    private List<ParametrosRegRecuadoCausacion> registros;
    
    public int getLoteNum() {
        return loteNum;
    }

    public void setLoteNum(int loteNum) {
        this.loteNum = loteNum;
    }

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

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<ParametrosRegRecuadoCausacion> getRegistros() {
        return registros;
    }

    public void setRegistros(List<ParametrosRegRecuadoCausacion> registros) {
        this.registros = registros;
    }

}
