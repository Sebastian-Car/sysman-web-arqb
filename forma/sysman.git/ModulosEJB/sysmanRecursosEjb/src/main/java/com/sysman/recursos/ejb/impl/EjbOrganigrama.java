/*-
 * EjbOrganigrama.java
 *
 * 1.0
 * 
 * 2 jul. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbOrganigramaLocal;
import com.sysman.recursos.ejb.EjbOrganigramaRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class Organigrama
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbOrganigrama
                implements EjbOrganigramaRemote, EjbOrganigramaLocal {
    /**
     * Default constructor.
     */
    public EjbOrganigrama() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public String jsonOrganigramaBmanga(
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ORGANIGRAMA.FC_JSON_ORGANIGRAMA",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String jsonOrgBmangaRecu(
        String compania,
        String codigopadre)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOPADRE       =>'", codigopadre, "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ORGANIGRAMA.FC_JSON_ORGANIGRAMARECU",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
}