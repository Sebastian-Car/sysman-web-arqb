package com.sysman.contratos.ejb.impl;

import com.sysman.contratos.ejb.EjbContratosUnoGeneralLocal;
import com.sysman.contratos.ejb.EjbContratosUnoGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class ContratosUno
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContratosUnoGeneral
                implements EjbContratosUnoGeneralRemote,
                EjbContratosUnoGeneralLocal
{
    /**
     * Default constructor.
     */
    public EjbContratosUnoGeneral()
    {
    }

    @Override
    public void insertarSectoresDefault(
        String compania,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.PR_SECTORES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarDetallesActividades(
        String compania,
        long codEstudio,
        long codContrato,
        String tipoContrato,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_COD_ESTUDIO       =>",
                                Long.toString(codEstudio), ", ",
                                "UN_COD_CONTRATO      =>",
                                Long.toString(codContrato), ", ",
                                "UN_TIPO_CONTRATO     =>'", tipoContrato, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.PR_ACTUALIZAR_DETACTIVIDADES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String extraerValores(
        String compania,
        String claseorden,
        long numero,
        BigDecimal valorfinal,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_VALORFINAL        =>",
                                valorfinal.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_EXTRAERVALORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean eliminarOrdendeCompra(
        String compania,
        String claseOrden,
        long numero)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_ELIMINARORDENDECOMPRA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void seleccionarRequisiciones(
        String compania,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.PR_SELECCIONARREQUISICIONES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void insertaPpto(
        String compania,
        String claseOrden,
        long numero,
        String claseDisp,
        long numeroDispSel,
        String fechaSelec,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_CLASEDISP         =>'", claseDisp, "', ",
                                "UN_NUMERODISPSEL     =>",
                                Long.toString(numeroDispSel), ", ",
                                "UN_FECHASELEC        =>'", fechaSelec, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.PR_INSERTAPPTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal calculartotalpagos(
        String compania,
        String claseOrden,
        long numero,
        String claseContable)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_CLASECONTABLE     =>'", claseContable, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_CALCULARTOTALPAGOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String copiarContrato(
        String compania,
        String claseOrden,
        long copiarDe,
        int vigencia,
        long numero,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_COPIARDE          =>",
                                Long.toString(copiarDe), ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_COPIARCONTRATO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal ConsecContrato(
        String compania,
        String claseContrato,
        int anioVigencia,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASECONTRATO     =>'", claseContrato,
                                "', ", "UN_ANIOVIGENCIA      =>",
                                Integer.toString(anioVigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_CONSEC_CONTRATOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean actualizaIvaDetalle(
        String compania,
        String claseOrden,
        long numero,
        BigDecimal porcIvaGlobal,
        String roundValorIvaoc,
        String roundVlrTotaloc,
        String roundValorUnioc,
        BigDecimal digRedoVluniIva,
        BigDecimal digRoundVlrIva,
        BigDecimal digRedonTotal,
        String usuario)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_PORCIVAGLOBAL     =>",
                                porcIvaGlobal.toString(), ", ",
                                "UN_ROUNDVALORIVAOC   =>'", roundValorIvaoc,
                                "', ", "UN_ROUNDVLRTOTALOC   =>'",
                                roundVlrTotaloc, "', ",
                                "UN_ROUNDVALORUNIOC   =>'", roundValorUnioc,
                                "', ", "UN_DIGREDOVLUNIIVA   =>",
                                digRedoVluniIva.toString(), ", ",
                                "UN_DIGROUNDVLRIVA    =>",
                                digRoundVlrIva.toString(), ", ",
                                "UN_DIGREDONTOTAL     =>",
                                digRedonTotal.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_ACTUALIZAIVADETALLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal getTotalValorNovedad(
        String compania,
        String claseOrden,
        long ordendeCompra,
        String claseNovedad)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendeCompra), ", ",
                                "UN_CLASENOVEDAD      =>'", claseNovedad, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_VALORTOTALNOVEDADES",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void importarPrecontractual(
        String compania,
        long numeroOrden,
        String claseOrden,
        long estudioPrevio,
        String usuario,
        String numProceso)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMEROORDEN       =>",
                                Long.toString(numeroOrden), ", ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_ESTUDIOPREVIO     =>",
                                Long.toString(estudioPrevio), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_NUMPROCESO        =>'", numProceso, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.PR_IMPORTARPRECONTRACTUAL",
                        SysmanFunciones.concatenar(parametros));
    }

}