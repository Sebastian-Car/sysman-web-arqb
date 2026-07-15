/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.componentes;

import java.util.Map;

/**
 *
 * @author jrodrigueza
 */
public class Direccionador {

    private String ruta;
    private Map<String, Object> parametros;
    private String numForm;

    public Direccionador() {
        //
    }

    public Direccionador(String numForm, String ruta,
        Map<String, Object> parametros) {
        this.parametros = parametros;
        this.ruta = ruta;
        this.numForm = numForm;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(Map<String, Object> parametros) {
        this.parametros = parametros;
    }

    public String getNumForm() {
        return numForm;
    }

    public void setNumForm(String numForm) {
        this.numForm = numForm;
    }

}
