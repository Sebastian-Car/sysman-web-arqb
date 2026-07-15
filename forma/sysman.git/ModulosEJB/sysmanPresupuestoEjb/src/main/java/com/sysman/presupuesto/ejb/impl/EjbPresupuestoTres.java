/*-
 * EjbPresupuestoTres.java
 *
 * 1.0
 *
 * 26/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PresupuestoTres
 *
 * -- Modificado por lcortes 10/06/2017. Implementacion metodo
 * concatenar de la clase SysmanFunciones para el envio de parametros
 * a los diferentes procedimientos y funciones.
 */
@Stateless
@LocalBean
public class EjbPresupuestoTres
                implements EjbPresupuestoTresRemote, EjbPresupuestoTresLocal {
    @EJB
    private EjbPresupuestoTresGeneralRemote ejbPresupuestoTresGeneral;

    /**
     * Default constructor.
     */
    public EjbPresupuestoTres() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public long enumerar(
        String compania,
        int ano,
        String tipo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA => '", compania, "', ",
                                "UN_ANO      => ",
                                Integer.toString(ano), ", ",
                                "UN_TIPO     => '", tipo, "'" };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_ENUMERAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public boolean terceroRegistraEmbargo(
        String compania,
        int modulo,
        String tercero,
        String sucursal,
        String tipoComprobante)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_MODULO          =>",
                                Integer.toString(modulo), ", ",
                                "UN_TERCERO         =>'", tercero, "', ",
                                "UN_SUCURSAL        =>'", sucursal, "', ",
                                "UN_TIPOCOMPROBANTE =>'", tipoComprobante,
                                "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_TERCEROREGEMBARGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean configurarParametroDisRes(
        String compania,
        int ano,
        String tipocomprobante,
        BigInteger numero,
        Date fecha,
        String clase,
        int modulo)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          => '", compania,
                                    "', ", "UN_ANO               => ",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPOCOMPROBANTE   => '",
                                    tipocomprobante, "', ",
                                    "UN_NUMERO            => ",
                                    numero.toString(), ", ",
                                    "UN_FECHA             => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CLASE             => '", clase, "', ",
                                    "UN_MODULO            => ",
                                    Integer.toString(modulo) };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO3.FC_CONFIGURARPARAMDISRES",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e.getMessage(), e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public void eliminaNovedad(
        String compania,
        String claseOrden,
        BigInteger ordenDeCompra,
        String claseT,
        String tipoT,
        BigInteger numero)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_CLASEORDEN          =>'", claseOrden, "', ",
                                "UN_ORDENDECOMPRA       =>",
                                ordenDeCompra.toString(), ", ",
                                "UN_CLASET              =>'", claseT, "', ",
                                "UN_TIPOT               =>'", tipoT, "', ",
                                "UN_NUMERO              =>",
                                numero.toString() };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_ELIMINANOVEDAD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal calcularSaldoNeto(String compania, int anio, String tipo,
        BigInteger comprobante, int consecutivo, String cuenta,
        BigDecimal valorDebito, BigDecimal valorCredito)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                               "UN_ANO          =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPO         =>'", tipo, "', ",
                               "UN_COMPROBANTE  =>",
                               comprobante.toString(), ", ",
                               "UN_CONSECUTIVO  =>",
                               Integer.toString(consecutivo), ", ",
                               "UN_CUENTA       =>'", cuenta, "',",
                               "UN_VALOR_DEBITO =>",
                               valorDebito.toString(), ", ",
                               "UN_VALOR_CREDITO =>",
                               valorCredito.toString() };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_CALCULARSALDONETO",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);
    }

    @Override
    public void comprobanteAcopiar(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        String tercero,
        String sucursal,
        Date fecha,
        BigInteger numerocopiar,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NUMEROCOPIAR      =>",
                                    numerocopiar.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO3.PR_COMPROBANTEACOPIAR",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigInteger generarRegistroConTipo(
        String compania,
        int ano,
        String tipo,
        String tercero,
        String sucursal,
        String tipoComprobante,
        BigInteger numeroComprobante,
        String objeto,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_TIPO_COMPROBANTE  =>'", tipoComprobante,
                                "', ", "UN_NUMERO_COMPROBANTE =>",
                                numeroComprobante.toString(), ", ",
                                "UN_OBJETO            =>'", objeto, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return new BigInteger((String) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_GENERAR_REGISTRO_CON_TIPO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR));
    }

    @Override
    public BigDecimal consolidarCompaniasPptales(
        String compania,
        int ano,
        String usuario,
        int nivel)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_NIVEL             =>",
                                Integer.toString(nivel), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_CONSOLIDACIONPPTAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void eliminarCuentaPresupuestal(
        String compania,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_ELIMINARCUENTAPPTAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int actualizarValorSolicitado(
        String compania,
        String itemAfectado,
        int vigenciaItemAfectado,
        BigDecimal valorDigitado,
        int codigoItem,
        int tipoSolicitud,
        String accion,
        BigDecimal valorAntiguo,
        String codigo,
        int ano,
        BigDecimal valorRubro,
        String fuente,
        String centroCosto,
        String referencia,
        int solicitudAfect)
                    throws SystemException {
        return ejbPresupuestoTresGeneral.actualizarValorSolicitado(compania,
                        itemAfectado, vigenciaItemAfectado, valorDigitado,
                        codigoItem, tipoSolicitud, accion, valorAntiguo, codigo,
                        ano, valorRubro, fuente, centroCosto, referencia,solicitudAfect, 0);
    }

    @Override
    public void afectarSolicitud(
        String compania,
        long solicitudAfectada,
        long solicitudNueva,
        int tipoSolicitudNueva)
                    throws SystemException {
        ejbPresupuestoTresGeneral.afectarSolicitud(compania, solicitudAfectada,
                        solicitudNueva, tipoSolicitudNueva);
    }

    @Override
    public void congelarSaldoDetalle(
        String compania,
        int anio,
        String tipo,
        long comprobanteinicial,
        long comprobantefinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_COMPROBANTEINICIAL =>",
                                Long.toString(comprobanteinicial), ", ",
                                "UN_COMPROBANTEFINAL  =>",
                                Long.toString(comprobantefinal)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_CONGELARSALDOENDETALLE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarTerDeoSolicitud(
        String compania,
        long numero,
        String tercero,
        String dependencia)
                    throws SystemException {
        ejbPresupuestoTresGeneral.actualizarTerDeoSolicitud(compania, numero,
                        tercero, dependencia);
    }

    @Override
    public boolean esOrdenador(
        String compania,
        String cedula)
                    throws SystemException {

        return ejbPresupuestoTresGeneral.esOrdenador(compania, cedula);

    }

    @Override
    public boolean actualizarSolicitudesNoAprobadas(
        String compania,
        long solicitud,
        String aprobacion)
                    throws SystemException {

        return ejbPresupuestoTresGeneral.actualizarSolicitudesNoAprobadas(
                        compania, solicitud, aprobacion);

    }

    @Override
    public void revisarAfectacionesPpto(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_REVISAR_AFECTACIONES_PPTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String cargarPlanVigencia(
        String planVig,
        String usuario,
        int opcion)
                    throws SystemException {
        String[] parametros = { "UN_PLANVIG           =>", planVig, ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_OPCION            =>",
                                Integer.toString(opcion), ""
        };        
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
        		 "PCK_PRESUPUESTO3.FC_CARGAR_PLAN_VIG",
                SysmanFunciones.concatenar(parametros),
                Types.VARCHAR);
    }

    @Override
    public void registrarPrSiif(
        String compania,
        String plano,
        int ano,
        int plantilla,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PLANO             =>", plano, ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PLANTILLA         =>",
                                Integer.toString(plantilla), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_REGISTRARPRSIIF",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public int validarAprociacion(
        String compania,
        String codigo,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO               =>'",codigo, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ""
        };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_VAL_APROPIACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }


}