package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultadoValidacionDetalle {

    @JsonProperty("CLASE")
    private String clase;

    @JsonProperty("CODIGO")
    private String codigo;

    @JsonProperty("DESCRIPCION")
    private String descripcion;

    @JsonProperty("OBSERVACIONES")
    private String observaciones;

    @JsonProperty("PATHFUENTE")
    private String pathFuente;

    @JsonProperty("FUENTE")
    private String fuente;

    // Getters y Setters

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPathFuente() {
        return pathFuente;
    }

    public void setPathFuente(String pathFuente) {
        this.pathFuente = pathFuente;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
}
