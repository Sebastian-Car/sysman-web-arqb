/*-
 * EjbAyudalinea.java
 *
 * 1.0
 * 
 * 29/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */     

package com.sysman.ayudas.ejb.impl;
import com.sysman.ayudas.ejb.EjbAyudalineaLocal;
import com.sysman.ayudas.ejb.EjbAyudalineaRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;


/**
 * Session Bean implementation class Ayudalinea
 */
@Stateless
@LocalBean

public class EjbAyudalinea implements EjbAyudalineaRemote, EjbAyudalineaLocal {
    /**
     * Default constructor.
     */
    public EjbAyudalinea() {
    }
    
    @Override
    public String cargarTareas(int proceso, String cadenaplan, String usuario)
                           throws SystemException {
                   String[] parametros = { "UN_PROCESO          =>'", Integer.toString(proceso), "', ",
                                                   "UN_CADENA        =>'", cadenaplan, "', ",
                                                   "UN_USUARIO           =>'", usuario, "'"
                   };
                   try {
                           return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                                           ConectorPool.ESQUEMA_SYSMAN,
                                           "PCK_AYUDAS.FC_CARGAR_TAREAS",
                                           SysmanFunciones.concatenar(parametros),
                                           Types.CLOB));
                   } catch (IOException | SQLException e) {
                           throw new SystemException(e);
                   }
                   
           }

    
}
