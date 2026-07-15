package com.sysman.circularunica.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbCircularUnicaCeroGeneralRemote
{

    void prepararCodigos(
        String compania,
        int anioIni,
        int anioFin,
        String usuario,
        int opcion) throws SystemException;

    String actualizarVigenciaPlanPptal(
        String compania,
        int anioIni,
        int anioFin,
        String usuario) throws SystemException;
    
    String cargarConfiguracionPptal(
			String tabla, 
			String cadena, 
			String usuario,
			String compania,
			int ano
			) throws SystemException;
}