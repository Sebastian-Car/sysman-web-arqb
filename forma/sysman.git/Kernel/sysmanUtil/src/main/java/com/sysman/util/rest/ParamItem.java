/*-
 * ParamItem.java
 *
 * 1.0
 * 
 * 5/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que administra los parametros del cuerpo del servicio items
 * 
 * @version 1.0, 5/01/2021
 * @author eamaya
 *
 */
public class ParamItem {
    /**
     * Propiedad que identifica el codigode barras del producto
     */
    private String codigoProducto;
    /**
     * Propiedad que identifica el nombre del usuario que crea el
     * producto
     */
    private String createdBy;
    /**
     * Propiedad que identifica descripcion del producto
     */
    private String descripcionProducto;
    /**
     * Propiedad que identifica tipo de unidad de medidad usada para
     * el producto
     */
    private String unidadMedida;
    /**
     * Propiedad que identifica el valor asignado al producto
     */
    private String valorItem;

    /**
     * @return the codigoProducto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * @param codigoProducto
     * the codigoProducto to set
     */
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     * the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the descripcionProducto
     */
    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    /**
     * @param descripcionProducto
     * the descripcionProducto to set
     */
    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    /**
     * @return the unidadMedida
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * @param unidadMedida
     * the unidadMedida to set
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    /**
     * @return the valorItem
     */
    public String getValorItem() {
        return valorItem;
    }

    /**
     * @param valorItem
     * the valorItem to set
     */
    public void setValorItem(String valorItem) {
        this.valorItem = valorItem;
    }

}
