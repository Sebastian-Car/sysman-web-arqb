package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbReportesLocal {

    String resolverConsulta(
        String compania,
        String reporte,
        String usuario)
                        throws SystemException;
    
    void configurarParametros(
        String compania,
        String reporte,
        String usuario)
                        throws SystemException;
   

   
}