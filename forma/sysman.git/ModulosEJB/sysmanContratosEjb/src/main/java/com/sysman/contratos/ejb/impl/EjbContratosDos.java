/*-
 * EjbContratosDos.java
 *
 * 1.0
 * 
 * 11/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos.ejb.impl;

import com.sysman.contratos.ejb.EjbContratosDosLocal;
import com.sysman.contratos.ejb.EjbContratosDosRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContratosDos
 */
@Stateless
@LocalBean

public class EjbContratosDos
                implements EjbContratosDosRemote, EjbContratosDosLocal {
    /**
     * Default constructor.
     */
    public EjbContratosDos() {
    }

    @Override
    public void registrarCesion(
        String compania,
        long numero,
        String tipoContrato,
        long numeroAfectado,
        String tipoAfectado,
        String usuario,
        String mensaje,
        String nitTercero,
        String sucursalCesion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_TIPOCONTRATO    =>'", tipoContrato, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_MENSAJE           =>'", mensaje, "', ",
                                "UN_NITTERCERO        =>'", nitTercero, "', ",
                                "UN_SUCURSALCESION    =>'", sucursalCesion, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.PR_REGISTRARCESION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void afectarItem(
        String compania,
        long numero,
        String tipoContrato,
        long numeroAfectado,
        String tipoAfectado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_TIPOCONTRATO    =>'", tipoContrato, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.PR_AFECTARITEM",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean enviarNominaCesion(
        String compania,
        long numeroAfectado,
        String tipoAfectado,
        String nitCesion,
        Date fechaInicial,
        Date fechaFinal,
        BigDecimal valorTotal,
        String nombreCesion,
        String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NUMEROAFECTADO    =>",
                                    Long.toString(numeroAfectado), ", ",
                                    "UN_TIPOAFECTADO      =>'", tipoAfectado,
                                    "', ", "UN_NITCESION         =>'",
                                    nitCesion, "', ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY'), ",
                                    "UN_VALORTOTAL        =>",
                                    valorTotal.toString(), ", ",
                                    "UN_NOMBRECESION      =>'", nombreCesion,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTRATOS_COM2.FC_NOMINACESION",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean actDespNovedadContr(
        String compania,
        String strTipoT,
        String claseOrden,
        long numero,
        long novedad,
        String nvlPEjecucion,
        Date fechaInicial,
        Date fechaFinal,
        Date fechaVencimiento,
        String valorTotal,
        String diasContrato)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_STRTIPOT          =>'", strTipoT, "', ",
                                    "UN_CLASEORDEN        =>'", claseOrden,
                                    "', ", "UN_NUMERO            =>",
                                    Long.toString(numero), ", ",
                                    "UN_NOVEDAD            =>",
                                    Long.toString(novedad), ", ",
                                    "UN_NVLPEJECUCION     =>'", nvlPEjecucion,
                                    "', ", "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAVENCIMIENTO        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                    		fechaVencimiento),
                                    "','DD/MM/YYYY'), ",
                                    "UN_VALORTOTAL        =>'", valorTotal,
                                    "', ", "UN_DIASCONTRATO      =>'",
                                    diasContrato, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTRATOS_COM2.FC_ACTUADESPNOVEDADCONTR",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean eliminarNovedadContrato(
        String compania,
        String cTipoT,
        String cClaseT,
        String claseOrden,
        long numero,
        long novedad,
        long ordenCompra)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CTIPOT            =>'", cTipoT, "', ",
                                "UN_CCLASET           =>'", cClaseT, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_NOVEDAD           =>",
                                Long.toString(novedad), ", ",
                                "UN_ORDENCOMPRA       =>",
                                Long.toString(ordenCompra)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.FC_ELIMINNOVEDCONTRAT",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal actualizarPSubcontrato(
        String compania,
        long ordendeSuministro,
        long codigo,
        long cantidadAnterior,
        long cantidad,
        long ordenDeCompra,
        long codigoOrden,
        String tipoAfectado,
        long numeroAfectado,
        String claseOrden,
        String claseOrdenAnt,
        long numeroOrdenAnt,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ORDENDESUMINISTRO =>",
                                Long.toString(ordendeSuministro), ", ",
                                "UN_CODIGO            =>",
                                Long.toString(codigo), ", ",
                                "UN_CANTIDADANTERIOR  =>",
                                Long.toString(cantidadAnterior),
                                ", ", "UN_CANTIDAD          =>",
                                Long.toString(cantidad),
                                ", ", "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordenDeCompra), ", ",
                                "UN_CODIGOORDEN       =>",
                                Long.toString(codigoOrden), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_CLASEORDENANT     =>'", claseOrdenAnt,
                                "', ", "UN_NUMEROORDENANT    =>",
                                Long.toString(numeroOrdenAnt), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.FC_ACTUALIZAR_PSUBCONTRATO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void modificarCantidadContrato(
        String compania,
        String claseOrden,
        long ordenDeCompra,
        long codigo,
        String campo,
        String valorAnterior,
        String valorAfectar,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordenDeCompra), ", ",
                                "UN_CODIGO            =>",
                                Long.toString(codigo), ", ",
                                "UN_CAMPO             =>'", campo, "', ",
                                "UN_VALORANTERIOR     =>'", valorAnterior,
                                "', ", "UN_VALORAFECTAR      =>'", valorAfectar,
                                "', ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.PR_MODCANTIDAD_CONTRATO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void insertarRubrosOrdenPpto(
        String compania,
        String clase,
        long numero,
        String tipoPpto,
        long numeroPpto,
        String fechaSelec,
        String usuario,
        int contador,
        String tercero,
        String sucursal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASE             =>'", clase, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_TIPOPPTO          =>'", tipoPpto, "', ",
                                "UN_NUMEROPPTO        =>",
                                Long.toString(numeroPpto), ", ",
                                "UN_FECHASELEC        =>'", fechaSelec, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CONTADOR          =>",
                                Integer.toString(contador), ", ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.PR_INSERTARRUBROSPPTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarCumplimientoActividades(
        String compania,
        long codContrato,
        String tipoContrato,
        long codigoNovedad,
        String tipoNovedad,
        long codigoActa,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_COD_CONTRATO      =>",
                                Long.toString(codContrato), ", ",
                                "UN_TIPO_CONTRATO     =>'", tipoContrato, "', ",
                                "UN_CODIGO_NOVEDAD    =>",
                                Long.toString(codigoNovedad), ", ",
                                "UN_TIPO_NOVEDAD      =>'", tipoNovedad, "', ",
                                "UN_CODIGO_ACTA       =>",
                                Long.toString(codigoActa), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM2.PR_ACTUALIZARCUMPLIMIENTOACT",
                        SysmanFunciones.concatenar(parametros));
    }

}