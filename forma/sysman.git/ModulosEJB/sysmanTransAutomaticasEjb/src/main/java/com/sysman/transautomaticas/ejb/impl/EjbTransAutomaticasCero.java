/*-
 * EjbTransAutomaticasCero.java
 *
 * 1.0
 * 
 * 26 sept. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.transautomaticas.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.transautomaticas.ejb.EjbTransAutomaticasCeroLocal;
import com.sysman.transautomaticas.ejb.EjbTransAutomaticasCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * 
 * Session Bean implementation class TransAutomaticasCero
 * 
 */

@Stateless
@LocalBean
public class EjbTransAutomaticasCero implements EjbTransAutomaticasCeroRemote,
                EjbTransAutomaticasCeroLocal {

    /**
     * 
     * Default constructor.
     * 
     */

    public EjbTransAutomaticasCero() {
    }

    @Override
    public void validarOrden(
        String compania,
        int anio,
        String tipo,
        String numero,
        String orden)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_ORDEN             =>'", orden, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_TRANS_AUTOMATICAS.PR_VALIDARORDEN",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String generarConsecutivoOrden(
        String compania,
        int anio,
        String tipo,
        String numero,
        String tabla)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_TABLA             =>'", tabla, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_TRANS_AUTOMATICAS.FC_CONSECUTIVOORDEN",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void copiarTransaccionModelo(
        String compania,
        int ano,
        String tipo,
        String numero,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_TRANS_AUTOMATICAS.PR_COPIARTRANSACCION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String validarTransaccion(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO_MODELO     =>'", numeroModelo,
                                    "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(),
                                    ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_TRANS_AUTOMATICAS.FC_VALIDAR_TRANSACCION",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        } catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String modificarTransaccionesRete(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO_MODELO     =>'", numeroModelo,
                                    "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_TRANS_AUTOMATICAS.FC_UPDATETRANSACCIONES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        } catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String calcularTransaccion(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO_MODELO     =>'", numeroModelo,
                                    "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_TRANS_AUTOMATICAS.FC_CALCULARTRANSACCION",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        } catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void controlarTransaccion(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO_MODELO     =>'", numeroModelo,
                                "', ",
                                "UN_NUMERO            =>", numero.toString()
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_TRANS_AUTOMATICAS.PR_CONTROLAR_TRANSACCION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void guardarAfectacion(
        String compania,
        int anio,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String detalles,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", (numero).toString(), ", ",
                                "UN_NUMERO_MODELO     =>'", numeroModelo,"', ",
                                "UN_DETALLES          =>'", detalles,"', ",
                                "UN_USUARIO           =>'", usuario,"'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_TRANS_AUTOMATICAS.PR_GUARDARAFECTACION",
                        SysmanFunciones.concatenar(parametros));
    }
    
}