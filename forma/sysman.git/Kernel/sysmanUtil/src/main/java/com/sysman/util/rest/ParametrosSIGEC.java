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
import java.util.List;

/**
 * Clase que envia los parametros al servicio que recibe los SIGEC
 * 
 * @version 1.0, 05/03/2024
 * @author mrosero
 *
 */
public class ParametrosSIGEC {
	
	
	/**
	 * @value contribuyente
	 */
	private String contribuyente;
	
	/**
	 * @value platform
	 */
	private int platform;

	/**
	 * @value Identificación del Acto/Documento
	 */
	private String actDocumentCode;

	/**
	 * @value Valor en pesos del Acto/Documento
	 */
	private int generatorFactValue;

	/**
	 * @value Tipo de identificación del sujeto pasivo
	 */
	private String payerDocumentParametricTypeCode;

	/**
	 * @value Número de documento del sujeto pasivo.
	 */
	private String taxpayerDocumentNumber;

	/**
	 * @value Nombre del sujeto pasivo.
	 */
	private String taxpayerName;

	/**
	 * @value Fecha de inicio del Acto/Documento
	 */
	private String generatorFactStartDate;

	/**
	 * @value Fecha final del Acto/Documento
	 */
	private String generatorFactEndDate;
	
	private List<ParametroCuerpoEnvioFactura> facturas;

	/**
	 * @value Tipo del código del Acto/Documento.
	 */
	private String parametricActDocumentCodeType;

	public String getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(String contribuyente) {
		this.contribuyente = contribuyente;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	public String getActDocumentCode() {
		return actDocumentCode;
	}

	public void setActDocumentCode(String actDocumentCode) {
		this.actDocumentCode = actDocumentCode;
	}

	public int getGeneratorFactValue() {
		return generatorFactValue;
	}

	public void setGeneratorFactValue(int generatorFactValue) {
		this.generatorFactValue = generatorFactValue;
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

	public String getTaxpayerName() {
		return taxpayerName;
	}

	public void setTaxpayerName(String taxpayerName) {
		this.taxpayerName = taxpayerName;
	}

	public String getGeneratorFactStartDate() {
		return generatorFactStartDate;
	}

	public void setGeneratorFactStartDate(String generatorFactStartDate) {
		this.generatorFactStartDate = generatorFactStartDate;
	}

	public String getGeneratorFactEndDate() {
		return generatorFactEndDate;
	}

	public void setGeneratorFactEndDate(String generatorFactEndDate) {
		this.generatorFactEndDate = generatorFactEndDate;
	}

	public String getParametricActDocumentCodeType() {
		return parametricActDocumentCodeType;
	}

	public void setParametricActDocumentCodeType(String parametricActDocumentCodeType) {
		this.parametricActDocumentCodeType = parametricActDocumentCodeType;
	}

	/**
	 * @return the facturas
	 */
	public List<ParametroCuerpoEnvioFactura> getFacturas() {
		return facturas;
	}

	/**
	 * @param facturas the facturas to set
	 */
	public void setFacturas(List<ParametroCuerpoEnvioFactura> facturas) {
		this.facturas = facturas;
	}
	

}
