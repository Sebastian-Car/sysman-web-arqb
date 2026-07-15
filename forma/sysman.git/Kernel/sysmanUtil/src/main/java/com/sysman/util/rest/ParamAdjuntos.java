/*-
 * ParamAdjuntos.java
 *
 * 1.0
 * 
 * 7/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que sirve para administrar lo adjuntos
 * 
 * @version 1.0, 7/01/2021
 * @author eamaya
 *
 */

public class ParamAdjuntos {
    String objeto;
    String formato;
    String nombre;

    public ParamAdjuntos() {
        super();
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }
}
