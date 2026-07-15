package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialUnoLocal;
import com.sysman.predial.ejb.EjbPredialUnoRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialUno
 * 
 * @author eamaya
 * @version 2.0 , Refactoring concatenacion
 */
@Stateless
@LocalBean
public class EjbPredialUno implements EjbPredialUnoRemote, EjbPredialUnoLocal {
    /**
     * Default constructor.
     */
    public EjbPredialUno() {
    }

    @Override
    public void anularAbonoSaldoCredito(
        String compania,
        String docnum,
        String precod,
        String user,
        int preano,
        boolean pagado)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_DOCNUM           =>'", docnum, "', ",
                                "UN_PRECOD           =>'", precod, "', ",
                                "UN_USER             =>'", user, "', ",
                                "UN_PREANO           =>",
                                Integer.toString(preano), ", ",
                                "UN_PAGADO            =>",
                                pagado ? "-1" : "0", "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_ANULAR_ABONO_SALDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int consultarVigenciaValidaParaPago(
        String compania,
        String predio)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PREDIO            =>'", predio, "'" };

        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.FC_PAGOANO_VALIDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void actualizarUltimaVigenciaPaga(
        String compania,
        String codigoinicial,
        String codigofinal,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOINICIAL     =>'", codigoinicial,
                                "', ", "UN_CODIGOFINAL       =>'",
                                codigofinal, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_MTO_ULTIMAVIGENCIAPAGA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void distribuirCuotasAcuerdosyRecibosDePago(
        String compania,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOINICIAL     =>'", codigoinicial,
                                "', ", "UN_CODIGOFINAL       =>'", codigofinal,
                                "', ", "UN_USUARIO =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_VERIFICARDISTCUOTAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarAcuerdos(
        String compania,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_CODIGOINICIAL =>'", codigoinicial, "', ",
                                "UN_CODIGOFINAL   =>'" + codigofinal, "', ",
                                "UN_USUARIO =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_ACTUALIZARACUERDOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String verificarCuotasCanceladas(
        String compania,
        boolean indfechapago,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_INDFECHAPAGO  =>",
                                indfechapago ? "-1" : "0", ", ",
                                "UN_CODIGOINICIAL =>'", codigoinicial, "', ",
                                "UN_CODIGOFINAL   =>'", codigofinal, "',",
                                "UN_USUARIO =>'", usuario, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.FC_MANTENIMIENTOACUERDOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void registrarRecaudoAbonos(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal valorRecibo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USER              =>'", user, "', ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString()
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_ABONO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarRecaudoCuotas(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal valorRecibo,
        BigDecimal nroCuota,
        int vigencia)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USER              =>'", user, "', ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString(), ", ",
                                "UN_NRO_CUOTA         =>", nroCuota.toString(),
                                ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia)
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_CUOTA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarRecaudoAcuerdos(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal valorRecibo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USER              =>'", user, "', ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString()
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_ACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarReciboAbonoAAcuerdo(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal totalRecibo,
        String paquete)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USER              =>'", user, "', ",
                                "UN_TOTAL_RECIBO      =>",
                                totalRecibo.toString(), ", ",
                                "UN_PAQUETE           =>'", paquete, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_REC_ABONO_ACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarRecaudoUnicoVigencia(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        BigDecimal valorRecibo,
        int vigencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString(), ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia)
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_UNICO_ANO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarRecaudoEnVigencia(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        BigDecimal valorRecibo,
        int vigenciaFinal)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString(), ", ",
                                "UN_VIGENCIA_FINAL    =>",
                                Integer.toString(vigenciaFinal)
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_VIGENCIAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarRecaudoUnicoVigencia(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        BigDecimal valorRecibo,
        int vigencia,
        String paquete,
        String user)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString(), ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_PAQUETE           =>'", paquete, "', ",
                                "UN_USER              =>'", user, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_UNICOANO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarRecaudo(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        BigDecimal valorRecibo,
        int vigenciaFinal,
        String paquete,
        String user)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RECIBO            =>'", recibo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_FECHA_RECAUDO     =>'", fechaRecaudo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_VALOR_RECIBO      =>",
                                valorRecibo.toString(), ", ",
                                "UN_VIGENCIA_FINAL    =>",
                                Integer.toString(vigenciaFinal), ", ",
                                "UN_PAQUETE           =>'", paquete, "', ",
                                "UN_USER              =>'", user, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void activarPredialEnCobro(
        String compania,
        String precod,
        String user,
        String numproceso,
        String numorden)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PRECOD            =>'", precod, "', ",
                                "UN_USER              =>'", user, "', ",
                                "UN_NUMPROCESO        =>'", numproceso, "', ",
                                "UN_NUMORDEN          =>'", numorden, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.PR_ACTIVAR_PREDIAL_COBRO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long actFacturadosPrescripcion(
        String compania,
        String codigo,
        String numpredial,
        int prescripcion,
        String resolucion,
        int desde,
        int hasta,
        String observacion,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_NUMPREDIAL        =>'", numpredial, "', ",
                                "UN_PRESCRIPCION      =>",
                                Integer.toString(prescripcion), ", ",
                                "UN_RESOLUCION        =>'", resolucion, "', ",
                                "UN_DESDE             =>",
                                Integer.toString(desde), ", ",
                                "UN_HASTA             =>",
                                Integer.toString(hasta), ", ",
                                "UN_OBSERVACION       =>'", observacion, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.FC_ACT_FACTURADOS_PRESCRIPCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public long modificarEstrato(
        String codigo,
        String estrato,
        String nuevoestrato,
        String formato,
        String nuevoformato,
        String tabla,
        String tablai,
        String compania,
        String usuario,
        String descripcion)
                    throws SystemException {

        String[] parametros = { "UN_CODIGO            =>'", codigo, "', ",
                                "UN_ESTRATO           =>'", estrato, "', ",
                                "UN_NUEVOESTRATO      =>'", nuevoestrato, "', ",
                                "UN_FORMATO           =>'", formato, "', ",
                                "UN_NUEVOFORMATO      =>'", nuevoformato, "', ",
                                "UN_TABLA             =>'", tabla, "', ",
                                "UN_TABLAI            =>'", tablai, "', ",
                                "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_DESCRIPCION       =>'", descripcion, "'"
        };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.FC_ACT_MODIFIESTRASOCIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public long insertModifiEstraSocio(
        String tabla,
        String campos,
        String valoresinsert)
                    throws SystemException {

        String[] parametros = { "UN_TABLA             =>'", tabla, "', ",
                                "UN_CAMPOS            =>'", campos, "', ",
                                "UN_VALORESINSERT     =>'", valoresinsert, "'"
        };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.FC_INSERT_MODIFIESTRASOCIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public int consultarEncabezadoDeColumna(
        String codigo,
        String estrato,
        String nuevoEstrato,
        String formato,
        String nuevoFormato,
        String tabla,
        String tablai,
        String compania,
        String usuario,
        String descripcion)
                    throws SystemException {
        String[] parametros = { "UN_CODIGO            =>'", codigo, "', ",
                                "UN_ESTRATO           =>'", estrato, "', ",
                                "UN_NUEVOESTRATO      =>'", nuevoEstrato, "', ",
                                "UN_FORMATO           =>'", formato, "', ",
                                "UN_NUEVOFORMATO      =>'", nuevoFormato, "', ",
                                "UN_TABLA             =>'", tabla, "', ",
                                "UN_TABLAI            =>'", tablai, "', ",
                                "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_DESCRIPCION       =>'", descripcion, "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM1.FC_ACT_MODIFIESTRASOCIO",
                        SysmanFunciones.concatenar(parametros), Types.INTEGER);
    }

    @Override
    public String verificarPrediosFacturados(
        String compania,
        String codigoInicial,
        String codigoFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODIGOINICIAL     =>'", codigoInicial,
                                    "', ", "UN_CODIGOFINAL       =>'",
                                    codigoFinal, "'" };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PREDIAL_COM1.FC_VERIFICARPREDIOSFACTURADOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}
