/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.logica;

import java.io.Serializable;

/**
 *
 * @author cmanrique
 */
public class Grupo implements Serializable {

    private String codigo;
    private int aplicacion;
    private int nivelGrupo;
    private int nivelUsuario;
    private Aplicacion aplicacionGrupo;
    private boolean modificaComprobante;
    private boolean esAdministrador;

    public Grupo(String codigo, int aplicacion, int nivelGrupo,
        int nivelUsuario, Aplicacion aplicacionGrupo,
        boolean modificaComprobante, boolean esAdministrador) {
        this.codigo = codigo;
        this.aplicacion = aplicacion;
        this.nivelGrupo = nivelGrupo;
        this.nivelUsuario = nivelUsuario;
        this.aplicacionGrupo = aplicacionGrupo;
        this.modificaComprobante = modificaComprobante;
        this.esAdministrador = esAdministrador;

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getAplicacion() {
        return aplicacion;
    }

    public void setAplicacion(int aplicacion) {
        this.aplicacion = aplicacion;
    }

    public int getNivelGrupo() {
        return nivelGrupo;
    }

    public void setNivelGrupo(int nivelGrupo) {
        this.nivelGrupo = nivelGrupo;
    }

    public int getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(int nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public Aplicacion getAplicacionGrupo() {
        return aplicacionGrupo;
    }

    public void setAplicacionGrupo(Aplicacion aplicacionGrupo) {
        this.aplicacionGrupo = aplicacionGrupo;
    }

    public boolean isModificaComprobante() {
        return modificaComprobante;
    }

    public void setModificaComprobante(boolean modificaComprobante) {
        this.modificaComprobante = modificaComprobante;
    }

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

}
