package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbContabilidadCeroGeneralRemote {

    String verificarInconsistencias(String compania, int ano)
                    throws SystemException;

    void cambiarNitTerceros(String nueCompania, String nueNit,
        String nueSucursal, String antCompania, String antNit,
        String antSucursal, String usuario) throws SystemException;
    
    String validarCuentaUtilizar(
    		String compania,
            int ano,
            String cuenta,
            boolean validaBloqueo)
                        throws SystemException;

}
