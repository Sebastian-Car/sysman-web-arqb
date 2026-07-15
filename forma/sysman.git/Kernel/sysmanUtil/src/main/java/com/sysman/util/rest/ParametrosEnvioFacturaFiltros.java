/*-
 * ParametrosEnvioFactura.java
 *
 * 1.0
 * 
 * 4/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que administra los parametros del servicio envio factura con
 * filtros
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametrosEnvioFacturaFiltros {

    private int codigo;

    private String mensaje;

    private ParametroCuerpoEnvioFactura cuerpo;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public ParametroCuerpoEnvioFactura getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(ParametroCuerpoEnvioFactura cuerpo) {
        this.cuerpo = cuerpo;
    }

}
