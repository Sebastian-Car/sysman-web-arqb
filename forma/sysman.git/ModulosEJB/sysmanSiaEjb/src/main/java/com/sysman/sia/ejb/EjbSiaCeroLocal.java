package com.sysman.sia.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbSiaCeroLocal {

    void configurarCuentasDesc(
        String compania,
        int anio,
        String usuario)
                    throws SystemException;

	String subirConsolidadoSia(
		String tabla, 
		String datos, 
		String usuario, 
		String sobrescribir)
				   throws SystemException;
}