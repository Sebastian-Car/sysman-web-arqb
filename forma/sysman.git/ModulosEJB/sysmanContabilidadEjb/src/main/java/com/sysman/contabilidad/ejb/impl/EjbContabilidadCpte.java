/*-
 * EjbContabilidadCpte.java
 *
 * 1.0
 * 
 * 4/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadCpteLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadCpteRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilidadCpte
 * 
 * @version 2.0 asana Refactorización de concatenados.
 */
@Stateless
@LocalBean
public class EjbContabilidadCpte
                implements EjbContabilidadCpteRemote, EjbContabilidadCpteLocal {
    /**
     * Default constructor.
     */
    public EjbContabilidadCpte() {
        // No tiene sentencias
    }

    @Override
    public boolean validarEquivPptales(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException {
        byte salida;
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(), ""
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_VALIDAR_EQUIV_PPTALES",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean prepararNiif(
        String compania,
        int anio,
        int modulo,
        String tipomovimiento,
        BigInteger numero,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_MODULO            =>",
                               Integer.toString(modulo), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(),
                               ",",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_PREPARAR_NIIF",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean contabilizarNiif(
        String compania,
        int modulo,
        Date fechacreacion,
        Date fecha,
        Date fechavncdoc,
        Date fechapagadodogn,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        String descripcion,
        String texto,
        String tercero,
        String sucursal,
        BigDecimal vlrdocumento,
        String codusuario,
        BigDecimal vlrbase,
        BigDecimal vlrbaseiva,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal vlragirar,
        BigDecimal debitosafectados,
        BigDecimal creditosafectados,
        double porciva,
        String centroCosto,
        String auxiliar,
        String fuenteRecurso,
        String referencia)
                    throws SystemException {
        byte salida;
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_MODULO            =>",
                                   Integer.toString(modulo), ", ",
                                   "UN_FECHACREACION     =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacreacion),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAVNCDOC       =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechavncdoc),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAPAGADODOGN   =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechapagadodogn),
                                   "','DD/MM/YYYY'), ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                   "', ",
                                   "UN_NUMERO            =>", numero.toString(),
                                   ", ",
                                   "UN_DESCRIPCION       =>'", descripcion,
                                   "', ",
                                   "UN_TEXTO             =>'", texto, "', ",
                                   "UN_TERCERO           =>'", tercero, "', ",
                                   "UN_SUCURSAL          =>'", sucursal, "', ",
                                   "UN_VLRDOCUMENTO      =>",
                                   vlrdocumento.toString(), ", ",
                                   "UN_CODUSUARIO        =>'", codusuario,
                                   "', ",
                                   "UN_VLRBASE           =>",
                                   vlrbase.toString(), ", ",
                                   "UN_VLRBASEIVA        =>",
                                   vlrbaseiva.toString(), ", ",
                                   "UN_DEBITO            =>", debito.toString(),
                                   ", ",
                                   "UN_CREDITO           =>",
                                   credito.toString(), ", ",
                                   "UN_VLRAGIRAR         =>",
                                   vlragirar.toString(), ", ",
                                   "UN_DEBITOSAFECTADOS  =>",
                                   debitosafectados.toString(), ", ",
                                   "UN_CREDITOSAFECTADOS =>",
                                   creditosafectados.toString(), ", ",
                                   "UN_PORCIVA           =>",
                                   Double.toString(porciva), ", ",
                                   "UN_CENTRO_COSTO      =>'", centroCosto,
                                   "', ",
                                   "UN_AUXILIAR          =>'", auxiliar, "', ",
                                   "UN_FUENTE_RECURSO    =>'", fuenteRecurso,
                                   "', ",
                                   "UN_REFERENCIA        =>'", referencia, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD_CPTE.FC_CONTABILIZAR_NIIF",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean validarPacEgreso(
        String compania,
        int modulo,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        String clase,
        BigDecimal vlrdocumento,
        BigDecimal vlragirar,
        boolean indimpresion)
                    throws SystemException {
        byte salida;
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_MODULO            =>",
                               Integer.toString(modulo), ", ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(),
                               ", ",
                               "UN_CLASE             =>'", clase, "', ",
                               "UN_VLRDOCUMENTO      =>",
                               vlrdocumento.toString(), ", ",
                               "UN_VLRAGIRAR         =>", vlragirar.toString(),
                               ", ",
                               "UN_INDIMPRESION      =>",
                               (indimpresion ? "-1" : "0"), ""
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_VALIDAR_PAC_EGRESO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean actualizarNroDocDetalleCnt(
        String compania,
        int modulo,
        String nrodocumento,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        BigDecimal debitosafectados,
        BigDecimal creditosafectados,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_MODULO            =>",
                               Integer.toString(modulo), ", ",
                               "UN_NRODOCUMENTO      =>'", nrodocumento, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(),
                               ", ",
                               "UN_DEBITOSAFECTADOS  =>",
                               debitosafectados.toString(), ", ",
                               "UN_CREDITOSAFECTADOS =>",
                               creditosafectados.toString(), ",",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_ACT_NRODOC_DETALLE_CNT",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean borrarDetComproNiif(
        String compania,
        int modulo,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException {
        byte salida;
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_MODULO            =>",
                               Integer.toString(modulo), ", ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString()
        };

        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_BORRAR_DETCOMP_NIIF",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String verificarCompACopiar(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_VERIFICAR_COMP_COPIAR",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public boolean verificarTieneDetalle(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException {
        byte salida;
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString()
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_VERIFICAR_TIENE_DETALLE",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarValorDocNoCero(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        BigDecimal vlrbase,
        BigDecimal valorbase,
        BigDecimal vlrbaseiva,
        BigDecimal valoriva,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(),
                               ", ",
                               "UN_VLRBASE           =>", vlrbase.toString(),
                               ", ",
                               "UN_VALORBASE         =>", valorbase.toString(),
                               ", ",
                               "UN_VLRBASEIVA        =>", vlrbaseiva.toString(),
                               ", ",
                               "UN_VALORIVA          =>", valoriva.toString(),
                               ",",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.PR_ACT_VALOR_DOC_NOCERO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void actualizarTerceroDet(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        String terceroini,
        String tercerofin,
        String sucursalini,
        String sucursalfin,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(),
                               ", ",
                               "UN_TERCEROINI        =>'", terceroini, "', ",
                               "UN_TERCEROFIN        =>'", tercerofin, "', ",
                               "UN_SUCURSALINI       =>'", sucursalini, "', ",
                               "UN_SUCURSALFIN       =>'", sucursalfin, "',",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.PR_ACT_TERCERO_DET",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void insertarComprobanteCNTRet(
        String compania,
        int anio,
        int modulo,
        String tipomovimiento,
        BigInteger numero,
        BigDecimal vlrbaseiva,
        BigDecimal vlrbase,
        String tipocobro,
        String conceptosf,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_MODULO            =>",
                               Integer.toString(modulo), ", ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ",
                               "UN_NUMERO            =>", numero.toString(),
                               ", ",
                               "UN_VLRBASEIVA        =>", vlrbaseiva.toString(),
                               ", ",
                               "UN_VLRBASE           =>", vlrbase.toString(),
                               ", ",
                               "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                               "UN_CONCEPTOSF        =>'", conceptosf, "',",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.PR_INSERT_CPTE_CNTRET",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public BigInteger enumerarComprobanteCnt(
        String compania,
        int anio,
        String tipo,

        BigInteger numero,
        String centroCosto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_CENTRO_COSTO      =>'", centroCosto,
                                "'"
        };
        return new BigInteger((String) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR));
    }

}