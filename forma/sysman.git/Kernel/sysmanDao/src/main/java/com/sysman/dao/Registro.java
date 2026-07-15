/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sysman.dao;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cmanrique
 */
public class Registro {

    private int indice;
    private Map<String, Object> campos;
    private Map<String, Object> llave;

    public Registro() {
        campos = new HashMap<>();
        llave = new HashMap<>();
    }

    public Registro(Map<String, Object> campos) {
        this.campos = campos;
        llave = new HashMap<>();
    }

    public Registro(int indice, Map<String, Object> campos) {
        this.indice = indice;
        this.campos = campos;
        llave = new HashMap<>();
    }

    public void asignarLlaveOLD(String[] nombre) {
        if (nombre != null) {
            for (int i = 0; i < nombre.length; i++) {
                llave.put(nombre[i], campos.get(nombre[i]));
            }
        }
    }

    public void asignarLlave(String[] nombre) {
        if (nombre != null) {
            for (int i = 0; i < nombre.length; i++) {
                llave.put("KEY_" + nombre[i], campos.get(nombre[i]));
            }
        }
    }

    public Map<String, Object> getCampos() {
        return campos;
    }

    public void setCampos(Map<String, Object> campos) {
        this.campos = campos;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public Map<String, Object> getLlave() {
        return llave;
    }

    public void setLlave(Map<String, Object> llave) {
        this.llave = llave;
    }

}
