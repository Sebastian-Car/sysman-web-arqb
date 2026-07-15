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
public class ParametrosLiquidacionSIGEC {

	/**
	 * @value contribuyente
	 */
	private String contribuyente;

	/**
	 * @value Tipo del código del Liquidacion.
	 */
	private int type;

	/**
	 * @value Identificación del Acto/Documento
	 */
	private String actDocumentCode;

	/**
	 * @value codigo de la estampilla.
	 */
	private Object stampNumber;

	/**
	 * @value valor de la estampilla.
	 */
	private int liquidatedValue;

	/**
	 * @value codigo unico de la Liquidacion de la estampilla.
	 */
	private String liquidatedValueId;

	/**
	 * @value Tipo de identificación del sujeto pasivo
	 */
	private String payerDocumentParametricTypeCode;

	/**
	 * @value Número de documento del sujeto pasivo.
	 */
	private String taxpayerDocumentNumber;

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

	public String getActDocumentCode() {
		return actDocumentCode;
	}

	public void setActDocumentCode(String actDocumentCode) {
		this.actDocumentCode = actDocumentCode;
	}

	public Object getStampNumber() {
		return stampNumber;
	}

	public void setStampNumber(Object stampNumber) {
		this.stampNumber = stampNumber;
	}

	public int getLiquidatedValue() {
		return liquidatedValue;
	}

	public void setLiquidatedValue(int liquidatedValue) {
		this.liquidatedValue = liquidatedValue;
	}

	public String getLiquidatedValueId() {
		return liquidatedValueId;
	}

	public void setLiquidatedValueId(String liquidatedValueId) {
		this.liquidatedValueId = liquidatedValueId;
	}

	public String getPayerDocumentParametricTypeCode() {
		return payerDocumentParametricTypeCode;
	}

	public void setPayerDocumentParametricTypeCode(String payerDocumentParametricTypeCode) {
		this.payerDocumentParametricTypeCode = payerDocumentParametricTypeCode;
	}

	public String getTaxpayerDocumentNumber() {
		return taxpayerDocumentNumber;
	}

	public void setTaxpayerDocumentNumber(String taxpayerDocumentNumber) {
		this.taxpayerDocumentNumber = taxpayerDocumentNumber;
	}

}
