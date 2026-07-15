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
public class Aplicacion implements Serializable {

    private int codigo;
    private String nombre;
    private String area;
    private int diasHabiles;
    private String rutaArchivos;
    private String rutaDocumentos;

    public Aplicacion(int codigo, String nombre, String area, int diasHabiles,
        String rutaArchivos, String rutaDocumentos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.area = area;
        this.diasHabiles = diasHabiles;
        this.rutaArchivos = rutaArchivos;
        this.rutaDocumentos = rutaDocumentos;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getDiasHabiles() {
        return diasHabiles;
    }

    public void setDiasHabiles(int diasHabiles) {
        this.diasHabiles = diasHabiles;
    }

    public String getRutaArchivos() {
        return rutaArchivos;
    }

    public void setRutaArchivos(String rutaArchivos) {
        this.rutaArchivos = rutaArchivos;
    }

    public String getRutaDocumentos() {
        return rutaDocumentos;
    }

    public void setRutaDocumentos(String rutaDocumentos) {
        this.rutaDocumentos = rutaDocumentos;
    }

}
