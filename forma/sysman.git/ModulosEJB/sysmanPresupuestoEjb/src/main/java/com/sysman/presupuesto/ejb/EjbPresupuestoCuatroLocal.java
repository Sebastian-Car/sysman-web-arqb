/*-
 * EjbPresupuestoCuatroLocal.java
 *
 * 1.0
 * 
 * 4 jul. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbPresupuestoCuatroLocal {

    void congelarSaldosMan(String compania, int anio, String tipo,
        BigInteger comprobanteIni, BigInteger comprobanteFin)
                    throws SystemException;
    
    void insertaPpto(
            String compania,
            String claseOrden,
            String numero,
            String claseDisp,
            long numeroDispSel,
            String tercero,
            String sucursal,
            String usuario) throws SystemException;
    
    void cargarComprobanteDetallePptal(
    		String compania, String cadenaplan,
            String usuario
    		)throws SystemException;
    
    void cargarTipoClasificador(
    		String compania, String cadenaplan,
            String usuario
    		)throws SystemException;


	void cargarPmrFuente(
			String compania, 
			String cadena, 
			String usuario)
			throws SystemException;
	
    boolean actualizarClasificadoresPptal(
            String compania,
            int ano,
            String usuario)
                        throws SystemException;
    

    String generarlibroRegistrosSql(
    		String  strsql)
                        throws SystemException;
    
      void  actualizarAuxiliaresIngreso(
			String compania, 
			String anio, 
			String rubro,
			String centrocostoini, 
			String centrocostofin, 
			String auxiliarini,
			String auxiliarfin,
			String referenciaini,
			String referenciafin,
			String fuenteini, 
			String fuentefin)  throws SystemException;
      
      
      void cargarMovimientosPptales(
          String compania, String cadenah,
          String cadenad, String usuario
                      )throws SystemException;

    

}
