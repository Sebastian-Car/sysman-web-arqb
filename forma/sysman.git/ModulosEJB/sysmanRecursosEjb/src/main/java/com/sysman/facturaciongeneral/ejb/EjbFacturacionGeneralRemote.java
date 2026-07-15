/*-
 * EjbFacturacionGeneralCeroRemote.java
 *
 * 1.0
 * 
 * 10/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbFacturacionGeneralRemote {

	boolean validarManejaInventario(
			String compania,
			int ano,
			String tipocobro)
					throws SystemException;

	String cargarRecaudoCausacion(String compania, String cadenaplano,
			String tipocobro, int anio, String tercero, String usuario, int loteNum)
					throws SystemException;

	String recaudarFactura(
			String compania,
			String tipoFactura,
			BigInteger numeroFactura,
			String observacion,
			String cuenta,
			Date fecha,
			int anio,
			boolean diferida,
			String usuario) throws SystemException;

	boolean interfazarFactura(
			String compania, 
			String tipofactura,
			BigInteger nofactura, 
			Date fechapago, 
			boolean vermensaje,
			boolean manejainventario, 
			String usuario) throws SystemException;
	
	String facturarConceptosLiquida(
			    String compania,
		        int ano,
			    String tipoFactura,
			    String tercero,
			    String concepto,
			    String descripcion,	
		    	String usuario)
		                    throws SystemException;
	  
}