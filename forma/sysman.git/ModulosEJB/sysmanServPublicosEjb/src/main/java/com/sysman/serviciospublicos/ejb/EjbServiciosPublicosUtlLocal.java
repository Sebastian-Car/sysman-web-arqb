/*-
 * EjbServiciosPublicosUtlLocal.java
 *
 * 1.0
 * 
 * 26/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

/**
 * @version 1.0, 26/07/2017
 * @author jrodrigueza
 */
@Local
public interface EjbServiciosPublicosUtlLocal {

    boolean obtenerCicloCalculado(String compania, int ciclo)
                    throws SystemException;

    boolean validarEmpresaExterna(String compania, int ciclo, String codigoRuta,
        String empresaAseoExt, String codigoExterno) throws SystemException;

    void cargarNovedadesConvenio(String compania, String codigoInterno,
        int ciclo, int ano, String periodo, String nit, String total,
        String cuotasPactadas, String cuotaAPagar, String capital,
        String interes, String otros, String usuario) throws SystemException;

}
