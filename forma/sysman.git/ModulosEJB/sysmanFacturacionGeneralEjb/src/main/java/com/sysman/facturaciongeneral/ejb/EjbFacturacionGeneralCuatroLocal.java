/*-
 * EjbFacturacionGeneralCuatroLocal.java
 *
 * 1.0
 * 
 * 11 oct. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

/**
 * Session Bean implementation class FacturacionGeneralCuatro
 *
 * 
 */
@Local
public interface EjbFacturacionGeneralCuatroLocal {
    boolean manejarInterfazContableNoFacturado(
        String compania,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        boolean manejainventario,
        String usuario)
                    throws SystemException;

    void registrarMovimiento(
        String compania,
        String tipomov,
        long nromov,
        Date fecha,
        String usuario)
                    throws SystemException;

    void eliminarFacturaDiferida(String compania, String tipoabono,
        BigInteger abono, BigInteger numerofactura, int anio, String usuario)
                    throws SystemException;

    String manejarInterfazContableAbono(String compania, String tipocobro,
        BigInteger numero, String tipoabono, BigInteger numeroabono,
        int cuotabono, Date fecha, String tercero, String sucursal,
        boolean manejainventario, String cuenta, BigDecimal vlrabono,
        String usuario) throws SystemException;

	String devolverFacturas(
			String compania, 
			int anio, 
			BigInteger factura, 
			String tipocobro, 
			BigInteger numerocobro,
			boolean facturado, 
			boolean recaudo, 
			String cuenta, 
			Date fecha,
			String usuario) throws SystemException;
	
	String actualizarTablaFacturas(
			String facturas) throws SystemException;
	
    BigDecimal consecutivoobj(
    		String compania,
    		int anio,
            String codigo
           )
                    throws SystemException;
    
    String cargarCobros(
			String compania, 
			int anio,
			String tipoCobro,
			String cadena,
			Date fechaComprobante,
			Date fechaVencimiento,
			String usuario) throws SystemException;
    
    void factutacionLote(
    		String compania, 
			int anio,
			String tipoCobro,
			String cobroInicial,
			String cobroFinal,
            String usuario
    		)throws SystemException;

}
