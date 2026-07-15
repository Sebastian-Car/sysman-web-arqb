package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbAlmacenDosLocal;
import com.sysman.almacen.ejb.EjbAlmacenDosRemote;
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
 * Session Bean implementation class AlmacenDos
 *
 * @version 2, 10/06/2017 jrodriguezr Se realiza refactorización de
 * concatenados
 */
@Stateless
@LocalBean
public class EjbAlmacenDos implements EjbAlmacenDosRemote, EjbAlmacenDosLocal {
    /**
     * Default constructor.
     */
    public EjbAlmacenDos() {
        // Sin sentencias
    }

    @Override
    public long generarConsecutivoMov(
        String tablauno,
        String tablados,
        String criterio,
        String campo)
                    throws SystemException {
        String[] parametros = { "UN_TABLAUNO          =>'", tablauno, "', ",
                                "UN_TABLADOS          =>'", tablados, "', ",
                                "UN_CRITERIO          =>'", criterio, "', ",
                                "UN_CAMPO             =>'", campo, "'" };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_GENCONSECGEN",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public long generarConsecutivoDevolutivo(
        String tipomovimiento,
        String compania,
        String clase,
        int modulo)
                    throws SystemException {
        String[] parametros = { "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                "', ", "UN_COMPANIA          =>'", compania,
                                "', ", "UN_CLASE             =>'", clase, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), "" };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_CONSECENTDEV_ACTIVOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public long cambiarTipoActivo(
        String compania,
        String tipomovimiento,
        BigInteger consecutivo,
        Date fecha,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                    "', ", "UN_CONSECUTIVO       =>",
                                    consecutivo.toString(), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'" };
            return (long) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM2.FC_CAMBIARTIPOACTIVO",
                            SysmanFunciones.concatenar(parametros),
                            Types.BIGINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean reversarDocumento(
        String compania,
        String tipomovasociado,
        long movasociado,
        String codigoelemento,
        BigDecimal cantidad)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOMOVASOCIADO   =>'", tipomovasociado,
                                "', ", "UN_MOVASOCIADO       =>",
                                Long.toString(movasociado),
                                ", ", "UN_CODIGOELEMENTO    =>'",
                                codigoelemento, "', ",
                                "UN_CANTIDAD          =>", cantidad.toString(),
                                "" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_REVERSADOCUMENTOAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String consultarClaseBodega(
        String compania,
        String dependencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_DEPENDENCIA       =>'", dependencia, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_GETCLASEBODEGA(",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String actualizarInventario(
        String strelement,
        BigDecimal dif,
        String nombrecampo,
        String strcompania)
                    throws SystemException {
        String[] parametros = { "UN_STRELEMENT        =>'", strelement, "', ",
                                "UN_DIF               =>", dif.toString(), ", ",
                                "UN_NOMBRECAMPO       =>'", nombrecampo, "', ",
                                "UN_STRCOMPANIA       =>'", strcompania, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_ACTRES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String consultarResponsableDep(
        String strdependencia,
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_STRDEPENDENCIA    =>'", strdependencia,
                                "', ", "UN_COMPANIA          =>'", compania,
                                "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_RETRESPONSABLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public long actualizarDocAsociado(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                "', ", "UN_MOVIMIENTO        =>",
                                Long.toString(movimiento),
                                "" };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_ACTUALIZA_DOC_ASOCIADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public void generarInventarioInicial(
        String compania,
        String tipoDocAsociado,
        long numeroDocAsociado,
        String tipoMovEntrada,
        String tipoMovSalida,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO_DOC_ASOCIADO =>'", tipoDocAsociado,
                                "', ",
                                "UN_NUMERO_DOC_ASOCIA =>",
                                Long.toString(numeroDocAsociado), ", ",
                                "UN_TIPO_MOV_ENTRADA  =>'", tipoMovEntrada,
                                "', ",
                                "UN_TIPO_MOV_SALIDA   =>'", tipoMovSalida,
                                "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.PR_GENERA_INVENTARIO_INICIAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String consultarVidaUtilPlaca(
        String compania,
        long placa)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PLACA             =>", Long.toString(placa),
                                "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_VIDAUTILRESTANTE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String generarComprobanteAlmacen(
        String compania,
        String claseOrden,
        long numero,
        String usuario,
        String dependencia,
        String recalcularDevolu)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                String.valueOf(numero), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_DEPENDENCIA       =>'", dependencia, "', ",
                                "UN_RECALCULARDEVOLU  =>'", recalcularDevolu,
                                "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_GENERARCOMPROBANTEALM",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean revisarPlacasGeneradas(
        String compania,
        String claseOrden,
        long numero,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>",
                                String.valueOf(numero), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM2.FC_REVISARPLACASGENERADAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

}