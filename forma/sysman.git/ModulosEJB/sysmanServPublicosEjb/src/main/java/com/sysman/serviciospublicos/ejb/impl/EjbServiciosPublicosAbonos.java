/*-
 * EjbServiciosPublicosAbonos.java
 *
 * 1.0
 * 
 * 11/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosAbonosLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosAbonosRemote;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosAbonos
 * 
 * @version 2.0, 10/06/2017, <strong>pespitia</strong>:<br>
 * Implementacion de la funcion SysmanFunciones.concatenar
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosAbonos
                implements EjbServiciosPublicosAbonosRemote,
                EjbServiciosPublicosAbonosLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosAbonos() {
        // Constructor vacio
    }

    @Override
    public void actualizarRangos(
        String compania)
                    throws SystemException {
        StringBuilder builder = new StringBuilder();
        builder.append("UN_COMPANIA          =>'");
        builder.append(compania);
        builder.append("'");

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_ABONOS.PR_RANGOUSUARIOSCICLO",
                        builder.toString());
    }

}