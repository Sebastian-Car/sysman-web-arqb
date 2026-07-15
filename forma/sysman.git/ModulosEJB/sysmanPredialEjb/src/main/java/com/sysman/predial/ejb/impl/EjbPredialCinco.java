package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCincoLocal;
import com.sysman.predial.ejb.EjbPredialCincoRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialCinco
 */
@Stateless
@LocalBean

public class EjbPredialCinco
                implements EjbPredialCincoRemote, EjbPredialCincoLocal
{
    /**
     * Default constructor.
     *
     * @version 2, 10/06/2017 jrodriguezr Se realiza refactorización
     * de concatenados
     */
    public EjbPredialCinco()
    {
        // Sin Sentencias
    }

    @Override
    public void actualizarAbonoPrioridad(
        String compania,
        String numeroOrden,
        String acuerdo,
        String predio,
        long cuota,
        long valorcuota,
        long valorabono,
        String strtipo,
        long numcuotas)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_CUOTA             =>", Long.toString(cuota),
                                ", ", "UN_VALORCUOTA        =>",
                                Long.toString(valorcuota), ", ",
                                "UN_VALORABONO        =>",
                                Long.toString(valorabono), ", ",
                                "UN_STRTIPO           =>'", strtipo, "', ",
                                "UN_NUMCUOTAS         =>",
                                Long.toString(numcuotas), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.PR_CALCABONOENACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void distribuirAbonoEnAcuerdo(
        String compania,
        String acuerdo,
        String predio,
        long cuota,
        String strtipo,
        long numcuotas)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_CUOTA             =>", Long.toString(cuota),
                                ", ",
                                "UN_STRTIPO           =>'", strtipo, "', ",
                                "UN_NUMCUOTAS         =>",
                                Long.toString(numcuotas), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.PR_DISTRIBABONOENACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void totalizarAbonoEnAcuerdo(
        String compania,
        String acuerdo,
        String predio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.PR_TOTALIZARABONOENACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String imprimirFactAbonoEnAcuerdo(
        String numeroOrden,
        String acuerdo,
        String predio,
        long cuota,
        String strtipo,
        boolean anulacion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_CUOTA             =>", Long.toString(cuota),
                                ", ",
                                "UN_STRTIPO           =>'", strtipo, "', ",
                                "UN_ANULACION         =>",
                                (anulacion ? "-1" : "0"), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.FC_IMPRIMIRFACTABONOENACUERDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void facturarAbonoEnAcuerdo(
        String numeroOrden,
        String acuerdo,
        String predio,
        long cuota,
        BigDecimal valorCuota,
        BigDecimal valoRabono,
        String strtipo,
        boolean anulacion,
        boolean facturar,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_CUOTA             =>", Long.toString(cuota),
                                ", ",
                                "UN_VALOR_CUOTA       =>",
                                valorCuota.toString(), ", ",
                                "UN_VALO_RABONO       =>",
                                valoRabono.toString(), ", ",
                                "UN_STRTIPO           =>'", strtipo, "', ",
                                "UN_ANULACION         =>",
                                (anulacion ? "-1" : "0"), ", ",
                                "UN_FACTURAR          =>",
                                (facturar ? "-1" : "0"), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.PR_FACTURARABONOENACUERDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void planoMoroso(
        String compania,
        BigDecimal valor,
        Date fechaCorte,
        boolean sinCedula,
        String nombreCompania)
                    throws SystemException {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_VALOR             =>", valor.toString(),
                                    ", ",
                                    "UN_FECHA_CORTE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_SIN_CEDULA        =>",
                                    (sinCedula ? "-1" : "0"), ", ",
                                    "UN_NOMBRE_COMPANIA   =>'", nombreCompania,
                                    "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM5.PR_PLANO_MOROSO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String importarAsobancaria(
        String compania,
        BigDecimal tamanio,
        String cadena,
        int rango,
        String usuario,
        String numeroOrden,
        String modulo,
        String codigoBanco,
        String nombreArchivo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TAMANIO           =>", tamanio.toString(),
                                ", ", "UN_CADENA            =>", cadena, ", ",
                                "UN_RANGO             =>",
                                Integer.toString(rango), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_NUMEROORDEN       =>'", numeroOrden, "', ",
                                "UN_MODULO            =>'", modulo, "', ",
                                "UN_CODIGOBANCO       =>'", codigoBanco, "', ",
                                "UN_NOMBREARCHIVO     =>'", nombreArchivo,
                                "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.FC_IMPORTARASOBANCARIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String establecerTablaAsobancaria(
        String compania,
        String usuario,
        BigDecimal factMultiplicacion,
        int linea,
        String lineaArchivo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_FACTMULTIPLICACIO =>",
                                factMultiplicacion.toString(), ", ",
                                "UN_LINEA             =>",
                                Integer.toString(linea), ", ",
                                "UN_LINEA_ARCHIVO     =>'", lineaArchivo, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.FC_ESTABLECERTABLAINSERASOBANC",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String validarFacturaMF(
        String compania,
        String noFactura,
        String fechaPago,
        String numeroOrden,
        String modulo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NOFACTURA         =>'", noFactura, "', ",
                                "UN_FECHAPAGO         =>'", fechaPago, "', ",
                                "UN_NUMEROORDEN       =>'", numeroOrden, "', ",
                                "UN_MODULO            =>'", modulo, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM5.FC_VALIDARFACTURAMF",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

}