/*-
 * EjbFacturacionGeneralAcuerdos.java
 *
 * 1.0
 * 
 * 28/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralAcuerdosLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralAcuerdosRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class FacturacionGeneralAcuerdos
 */
@Stateless
@LocalBean
public class EjbFacturacionGeneralAcuerdos
                implements EjbFacturacionGeneralAcuerdosRemote,
                EjbFacturacionGeneralAcuerdosLocal {
    /**
     * Default constructor.
     */
    public EjbFacturacionGeneralAcuerdos() {
        // constructor vacio
    }

    @Override
    public String prepararDeudaAcuerdosPago(
        String compania,
        String acuerdoNro,
        String seleccion,
        String tipoCobro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ACUERDONRO        =>'", acuerdoNro, "', ",
                                "UN_SELECCION         =>'", seleccion, "', ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_ACUERDOS.FC_PREPARARDEUDA_ACUERDOSPAGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String generarAcuerdoPreliminar(
        String compania,
        String tipoCobro,
        int anio,
        String tercero,
        String sucursal,
        String deudaCapital,
        String deudaInteres,
        String deudaTotal,
        String cuotaInicial,
        boolean indCondonacion,
        String obsCondonacion,
        String seleccionFact,
        String acuerdoNro,
        double tasa,
        int ncuotas,
        String valorCondonado,
        boolean indSimple,
        double gradiante,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_DEUDACAPITAL      =>'", deudaCapital, "', ",
                                "UN_DEUDAINTERES      =>'", deudaInteres, "', ",
                                "UN_DEUDATOTAL        =>'", deudaTotal, "', ",
                                "UN_CUOTAINICIAL      =>'", cuotaInicial, "', ",
                                "UN_IND_CONDONACION   =>",
                                (indCondonacion ? "-1" : "0"), ", ",
                                "UN_OBS_CONDONACION   =>'", obsCondonacion,
                                "', ", "UN_SELECCION_FACT    =>'",
                                seleccionFact, "', ",
                                "UN_ACUERDONRO        =>'", acuerdoNro, "', ",
                                "UN_TASA              =>",
                                Double.toString(tasa), ", ",
                                "UN_NCUOTAS           =>",
                                Integer.toString(ncuotas), ", ",
                                "UN_VALORCONDONADO    =>'", valorCondonado,
                                "', ", "UN_IND_SIMPLE        =>",
                                (indSimple ? "-1" : "0"), ", ",
                                "UN_GRADIANTE         =>",
                                Double.toString(gradiante), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_ACUERDOS.FC_GENERARACUERDOPRELIMINAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void aprobarAcuerdo(
        String compania,
        String tipoCobro,
        BigInteger acuerdoNro,
        String seleccion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "', ",
                                "UN_ACUERDONRO        =>",
                                acuerdoNro.toString(), ", ",
                                "UN_SELECCION         =>'", seleccion, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_ACUERDOS.PR_APROBARACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void eliminarAcuerdoPagoPorTercero(
        String compania,
        String tipoCobro,
        String tercero,
        String sucursal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_ACUERDOS.PR_ELIMINARACUERDOPAGO_TER",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void facturarAcuerdo(
        String compania,
        int aniocobro,
        String tipocobro,
        String tipoacuerdo,
        BigInteger nroacuerdo,
        int cuota,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOCOBRO         =>",
                                Integer.toString(aniocobro), ", ",
                                "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                                "UN_TIPOACUERDO       =>'", tipoacuerdo, "', ",
                                "UN_NROACUERDO        =>",
                                nroacuerdo.toString(), ", ",
                                "UN_CUOTA             =>",
                                Integer.toString(cuota), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_ACUERDOS.PR_FACTURARACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }
}
