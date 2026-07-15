/*-
 * ParametrosSIGEC.java
 *
 * 1.0
 * 
 * 05/03/2024
 * 
 * Copyright (c) 2016 Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.Date;

/**
 * Clase que envia los parametros al servicio que recibe los SIGEC
 * 
 * @version 1.0, 05/03/2024
 * @author mrosero
 *
 */
public class ParametrosPagoSIGEC {

	/**
	 * @value contribuyente
	 */
	private String contribuyente;

	/**
	 * @value Tipo del código del Liquidacion.
	 */
	private int type;

	/**
	 * @value fecha del pago del Liquidacion.
	 */
	private String paymentDate;

	/**
	 * @value valor pagado del Liquidacion.
	 */
	private int valuePayed;

	/**
	 * @value codigo unico de la Liquidacion de la estampilla.
	 */
	private String liquidatedValueId;

	/**
	 * @value codigo unico del pago de la Liquidacion.
	 */
	private String paidValueId;

	public String getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(String contribuyente) {
		this.contribuyente = contribuyente;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public int getValuePayed() {
		return valuePayed;
	}

	public void setValuePayed(int valuePayed) {
		this.valuePayed = valuePayed;
	}

	public String getLiquidatedValueId() {
		return liquidatedValueId;
	}

	public void setLiquidatedValueId(String liquidatedValueId) {
		this.liquidatedValueId = liquidatedValueId;
	}

	public String getPaidValueId() {
		return paidValueId;
	}

	public void setPaidValueId(String paidValueId) {
		this.paidValueId = paidValueId;
	}

}
