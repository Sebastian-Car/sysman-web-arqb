package com.sysman.circularunica.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbCircularUnicaCeroLocal
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
    
    String generarProcesoSiaSql(
    		String  strsql)
                        throws SystemException;
}