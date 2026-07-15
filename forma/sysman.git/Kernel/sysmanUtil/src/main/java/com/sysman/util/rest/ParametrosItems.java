/*-
 * ParametrosItems.java
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

import java.util.List;

/**
 * Clase que administra los parametros de los items
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametrosItems {

    private double baseGravable;

    private double cantidad;

    private ParametrosItemsCargos cargo;

    private String codigoproducto;

    private String descripcionproducto;

    private ParametrosItemsDescuento descuento;

    private String descuentoItem;

    private List<ParametrosItemsImpuestos> impuestos;

    private double porcentajeIca;

    private double porcentajeImpConsumo;

    private double porcentajeIva;

    private String tipoDescuento;

    private double totalitem;

    private String unidadmedida;

    private double valorIca;

    private double valorImpConsumo;

    private double valorIva;

    public double getBaseGravable() {
        return baseGravable;
    }

    public void setBaseGravable(double baseGravable) {
        this.baseGravable = baseGravable;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public ParametrosItemsCargos getCargo() {
        return cargo;
    }

    public void setCargo(ParametrosItemsCargos cargo) {
        this.cargo = cargo;
    }

    public String getCodigoproducto() {
        return codigoproducto;
    }

    public void setCodigoproducto(String codigoproducto) {
        this.codigoproducto = codigoproducto;
    }

    public String getDescripcionproducto() {
        return descripcionproducto;
    }

    public void setDescripcionproducto(String descripcionproducto) {
        this.descripcionproducto = descripcionproducto;
    }

    public ParametrosItemsDescuento getDescuento() {
        return descuento;
    }

    public void setDescuento(ParametrosItemsDescuento descuento) {
        this.descuento = descuento;
    }

    public String getDescuentoItem() {
        return descuentoItem;
    }

    public void setDescuentoItem(String descuentoItem) {
        this.descuentoItem = descuentoItem;
    }

    public List<ParametrosItemsImpuestos> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<ParametrosItemsImpuestos> impuestos) {
        this.impuestos = impuestos;
    }

    public double getPorcentajeIca() {
        return porcentajeIca;
    }

    public void setPorcentajeIca(double porcentajeIca) {
        this.porcentajeIca = porcentajeIca;
    }

    public double getPorcentajeImpConsumo() {
        return porcentajeImpConsumo;
    }

    public void setPorcentajeImpConsumo(double porcentajeImpConsumo) {
        this.porcentajeImpConsumo = porcentajeImpConsumo;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public double getTotalitem() {
        return totalitem;
    }

    public void setTotalitem(double totalitem) {
        this.totalitem = totalitem;
    }

    public String getUnidadmedida() {
        return unidadmedida;
    }

    public void setUnidadmedida(String unidadmedida) {
        this.unidadmedida = unidadmedida;
    }

    public double getValorIca() {
        return valorIca;
    }

    public void setValorIca(double valorIca) {
        this.valorIca = valorIca;
    }

    public double getValorImpConsumo() {
        return valorImpConsumo;
    }

    public void setValorImpConsumo(double valorImpConsumo) {
        this.valorImpConsumo = valorImpConsumo;
    }

    public double getValorIva() {
        return valorIva;
    }

    public void setValorIva(double valorIva) {
        this.valorIva = valorIva;
    }

	public double getValorunitario() {
        return valorunitario;
    }

    public void setValorunitario(double valorunitario) {
        this.valorunitario = valorunitario;
    }

    private double valorunitario;

}
