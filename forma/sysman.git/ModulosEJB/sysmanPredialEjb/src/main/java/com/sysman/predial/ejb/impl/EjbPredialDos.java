package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialDosLocal;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialDos
 * 
 * @version 2.0, 10/06/2017, <strong>pespitia</strong>:<br>
 * Implementacion de la funcion SysmanFunciones.concatenar
 */
@Stateless
@LocalBean
public class EjbPredialDos implements EjbPredialDosRemote, EjbPredialDosLocal {
    /**
     * Default constructor.
     */
    public EjbPredialDos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public String modificarRecibos(
        String compania,
        String modificacion,
        String tiporecibo,
        String tipomodificacion,
        Date fechamodificacion,
        String codigo,
        String factura,
        Date fecha,
        String banco,
        String paquete,
        BigDecimal valor,
        String anular,
        String activar,
        String campoanterior,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_MODIFICACION      =>'", modificacion, "', ",
                             "UN_TIPORECIBO        =>'", tiporecibo, "', ",
                             "UN_TIPOMODIFICACION  =>'", tipomodificacion,
                             "', ", "UN_FECHAMODIFICACION =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechamodificacion),
                             "','DD/MM/YYYY'), ", "UN_CODIGO            =>'",
                             codigo, "', ", "UN_FACTURA           =>'", factura,
                             "', ", "UN_FECHA             =>",
                             fecha != null ? "TO_DATE('" +
                                 SysmanFunciones.convertirAFechaCadena(fecha) +
                                 "','DD/MM/YYYY') " : null,
                             ",UN_BANCO             =>'",
                             banco, "', ", "UN_PAQUETE           =>'", paquete,
                             "', ", "UN_VALOR             =>", valor.toString(),
                             ", ", "UN_ANULAR            =>'", anular, "', ",
                             "UN_ACTIVAR           =>'", activar, "', ",
                             "UN_CAMPOANTERIOR     =>'", campoanterior, "', ",
                             "UN_USUARIO           =>'", usuario, "' "
            };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM2.FC_MODIFICARRECIBOS",
                            SysmanFunciones.concatenar(par),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void corregirCupones(
        String compania,
        Date fecha,
        String banco,
        String paquete,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ", "UN_BANCO             =>'",
                             banco, "', ", "UN_PAQUETE           =>'", paquete,
                             "'", "UN_USUARIO           =>'", usuario, "' " };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM2.PR_ARREGLARCUPONES",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String modificarValores(
        String compania,
        String tiporecibo,
        String tipomodificacion,
        String banco,
        String codigo,
        String factura,
        Date fecha,
        BigDecimal valor,
        String paquete,
        Date prefec,
        String pagBan,
        String paqueters,
        String activar,
        String acuerdo,
        String ncAcuerdo,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_TIPORECIBO        =>'", tiporecibo, "', ",
                             "UN_TIPOMODIFICACION  =>'", tipomodificacion,
                             "', ", "UN_BANCO             =>'", banco, "', ",
                             "UN_CODIGO            =>'", codigo, "', ",
                             "UN_FACTURA           =>'", factura, "', ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ",
                             "UN_VALOR             =>", valor.toString(), ", ",
                             "UN_PAQUETE           =>'", paquete, "', ",
                             "UN_PREFEC            =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(prefec),
                             "','DD/MM/YYYY'), ",
                             "UN_PAG_BAN           =>'", pagBan, "', ",
                             "UN_PAQUETERS         =>'", paqueters, "', ",
                             "UN_ACTIVAR           =>'", activar, "', ",
                             "UN_ACUERDO           =>'", acuerdo, "', ",
                             "UN_NC_ACUERDO        =>'", ncAcuerdo, "', ",
                             "UN_USUARIO           =>'", usuario, "' " };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM2.FC_CAMBIARVALORES",
                            SysmanFunciones.concatenar(par),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String actualizarUltimaVigenciaCancelada(
        String compania,
        String codInicial,
        String codFinal,
        String modcod,
        String usuario,
        String descripcion)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CODINICIAL        =>'", codInicial, "', ",
                         "UN_CODFINAL          =>'", codFinal, "', ",
                         "UN_MODCOD            =>'", modcod, "', ",
                         "UN_USUARIO           =>'", usuario, "', ",
                         "UN_DESCRIPCION       =>'", descripcion, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.FC_ACT_ULTVIGCANCELADA",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarCodigoAnterior(
        String compania,
        String codigoAnterior,
        String codigoNuevo)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CODIGO_ANTERIOR   =>'", codigoAnterior, "', ",
                         "UN_CODIGO_NUEVO      =>'", codigoNuevo, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.PR_VALIDARCODIGOANT",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void realizarTrasladoNuevoCodigo(
        String compania,
        String codigoAnterior,
        String codigoNuevo,
        String usuario,
        boolean opcionRegistro)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CODIGO_ANTERIOR   =>'", codigoAnterior, "', ",
                         "UN_CODIGO_NUEVO      =>'", codigoNuevo, "', ",
                         "UN_USUARIO           =>'", usuario, "', ",
                         "UN_OPCION_REGISTRO   =>",
                         opcionRegistro ? "-1" : "0", "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.PR_HAGATRASLADO",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void registroDePagoVigenciaAnterior(
        String compania,
        String v50,
        String vDamnificados,
        String fechacorte,
        String codpredio,
        String codigobanco,
        int anofin,
        String nrorecibo,
        BigDecimal totalpagadoin,
        String tarifaap,
        String trpcod,
        String observacionesin,
        String usuario)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_V_50              =>'", v50, "', ",
                         "UN_V_DAMNIFICADOS    =>'", vDamnificados, "', ",
                         "UN_FECHACORTE        =>'", fechacorte, "', ",
                         "UN_CODPREDIO         =>'", codpredio, "', ",
                         "UN_CODIGOBANCO       =>'", codigobanco, "', ",
                         "UN_ANOFIN            =>", Integer.toString(anofin),
                         ", ", "UN_NRORECIBO         =>'", nrorecibo, "', ",
                         "UN_TOTALPAGADO     =>", totalpagadoin.toString(),
                         ", ", "UN_TARIFAAP          =>'", tarifaap, "', ",
                         "UN_TRPCOD            =>'", trpcod, "', ",
                         "UN_OBSERVACIONES   =>'", observacionesin, "', ",
                         "UN_USUARIO           =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.PR_REGISTRARPAGOANOANTE",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void actualizarAvaluoAnterior(
        String compania,
        String resolucion,
        int preano,
        String codigo,
        int ultimoAnioin,
        BigDecimal avaluo,
        String trpcod)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_RESOLUCION        =>'", resolucion, "', ",
                         "UN_PREANO            =>", Integer.toString(preano),
                         ", ", "UN_CODIGO            =>'", codigo, "', ",
                         "UN_ULTIMO_ANIO     =>",
                         Integer.toString(ultimoAnioin), ", ",
                         "UN_AVALUO            =>", avaluo.toString(), ", ",
                         "UN_TRPCOD            =>'", trpcod, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.PR_AUSUBAVALUOSDOS",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public String insertarCopropietarios(
        String compania,
        String pais,
        String departamento,
        String municipio,
        String resolucion,
        BigInteger consecutivo,
        int ano,
        String codigo,
        String usuario)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_PAIS              =>'", pais, "', ",
                         "UN_DEPARTAMENTO      =>'", departamento, "', ",
                         "UN_MUNICIPIO         =>'", municipio, "', ",
                         "UN_RESOLUCION        =>'", resolucion, "', ",
                         "UN_CONSECUTIVO       =>", consecutivo.toString(),
                         ", ", "UN_ANO               =>", Integer.toString(ano),
                         ", ", "UN_CODIGO            =>'", codigo, "', ",
                         "UN_USUARIO           =>'", usuario, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.FC_CLICKPROPIETARIOS",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public long insertarUsuariosResolucionIgac(
        String resolucion,
        String pais,
        String departamento,
        String municipio,
        int ano,
        String codigo,
        String usuario,
        String compania)
                    throws SystemException {
        String[] par = { "UN_RESOLUCION        =>'", resolucion, "', ",
                         "UN_PAIS              =>'", pais, "', ",
                         "UN_DEPARTAMENTO      =>'", departamento, "', ",
                         "UN_MUNICIPIO         =>'", municipio, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_CODIGO            =>'", codigo, "', ",
                         "UN_USUARIO           =>'", usuario, "', ",
                         "UN_COMPANIA          =>'", compania, "'" };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.FC_ACEPTARPROPIETARIOS",
                        SysmanFunciones.concatenar(par),
                        Types.BIGINT);
    }

    @Override
    public String actualizarNumeroDeOrdenPredios(
        String compania,
        String codigo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_USUARIO           =>'", usuario, "' " };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.FC_ACTUALIZA_NUM_ORDEN",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String insertarUsuariosDesdeResoluciones(
        String departamento,
        String municipio,
        String resolucion,
        String radicacion,
        String compania,
        String usuario,
        int pagoAno,
        BigDecimal pagVal,
        String pagBan,
        String numCom,
        String codpadre,
        String pagfec,
        BigDecimal trppor)
                    throws SystemException {

        String[] par = { "UN_DEPARTAMENTO    =>'", departamento, "', ",
                         "UN_MUNICIPIO         =>'", municipio, "', ",
                         "UN_RESOLUCION        =>'", resolucion, "', ",
                         "UN_RADICACION        =>'", radicacion, "', ",
                         "UN_COMPANIA          =>'", compania, "', ",
                         "UN_USUARIO           =>'", usuario, "', ",
                         "UN_PAGO_ANO          =>", Integer.toString(pagoAno),
                         ", ",
                         "UN_PAG_VAL           =>", pagVal.toString(), ", ",
                         "UN_PAG_BAN           =>'", pagBan, "', ",
                         "UN_NUM_COM           =>'", numCom, "', ",
                         "UN_CODPADRE          =>'", codpadre, "', ",
                         "UN_PAGFEC            =>'", pagfec, "', ",
                         "UN_TRPPOR            =>", trppor.toString(), "" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.FC_REGISTRAR",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public void incrementarAvaluos(
        String compania,
        String codigo,
        String numeroOrden,
        BigDecimal avaluo,
        BigDecimal avaluoAno,
        int ano)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CODIGO            =>'", codigo, "', ",
                         "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                         "UN_AVALUO            =>", avaluo.toString(), ", ",
                         "UN_AVALUO_ANO        =>", avaluoAno.toString(), ", ",
                         "UN_ANO               =>", Integer.toString(ano), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM2.PR_INCREMENTARAVALUOS",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public String armaConsultaEstadoCuenta(
        String compania,
        String nit,
        String codPredio,
        String propietario,
        boolean porUsuario,
        boolean porPredio,
        String numOrden,
        String nombreCompania)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NIT               =>'", nit, "', ",
                                    "UN_CODPREDIO         =>'", codPredio,
                                    "', ",
                                    "UN_PROPIETARIO       =>'", propietario,
                                    "', ",
                                    "UN_PORUNUSUARIO      =>",
                                    porUsuario ? "-1" : "0", ", ",
                                    "UN_PORPREDIO         =>",
                                    porPredio ? "-1" : "0", ", ",
                                    "UN_NUMORDEN          =>'", numOrden, "', ",
                                    "UN_NOMBRECOMPANIA    =>'", nombreCompania,
                                    "'" };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM2.FC_ARMACONSULTAESTADOCUENTA",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}