package com.sysman.sia.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbSiaCeroRemote {

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