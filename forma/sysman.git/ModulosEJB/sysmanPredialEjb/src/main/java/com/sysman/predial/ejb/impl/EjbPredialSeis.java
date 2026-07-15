package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialSeisLocal;
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialSeis
 *
 * @version 2, 10/06/2017 jrodriguezr Se realiza refactorización de
 * concatenados
 */
@Stateless
@LocalBean

public class EjbPredialSeis
                implements EjbPredialSeisRemote, EjbPredialSeisLocal
{
    /**
     * Default constructor.
     */
    public EjbPredialSeis()
    {
        // Sin sentencias
    }

    @Override
    public String imprimirCuotaAcuerdoDePago(
        String codigoacuerdo,
        String compania,
        String usuario,
        int fechavencida,
        boolean anulado,
        boolean cancelado,
        int pagocuotaanterior,
        long cuota,
        int facturacioncuotaanterior,
        int controlarrecibos,
        String codigopredio)
                    throws SystemException {
        String[] parametros = { "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ", "UN_COMPANIA          =>'", compania,
                                "', ", "UN_USUARIO           =>'", usuario,
                                "', ", "UN_FECHAVENCIDA      =>",
                                Integer.toString(fechavencida), ", ",
                                "UN_ANULADO           =>",
                                (anulado ? "-1" : "0"), ", ",
                                "UN_CANCELADO         =>",
                                (cancelado ? "-1" : "0"), ", ",
                                "UN_PAGOCUOTAANTERIOR =>",
                                Integer.toString(pagocuotaanterior), ", ",
                                "UN_CUOTA             =>", Long.toString(cuota),
                                ", ", "UN_FACTURACIONCUOTAANTERIOR =>",
                                Integer.toString(facturacioncuotaanterior),
                                ", ", "UN_CONTROLARRECIBOS  =>",
                                Integer.toString(controlarrecibos), ", ",
                                "UN_CODIGOPREDIO      =>'", codigopredio, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_IMPRIMIRCUOTAACUERDODEPAGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public int verificarFechaLimiteCuota(
        String codigoacuerdo,
        long cuota)
                    throws SystemException {
        String[] parametros = { "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ", "UN_CUOTA             =>",
                                Long.toString(cuota), "" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_VERIFICARFEHALIMITECUOTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int verificarPagoCuotaAnterior(
        String codigoacuerdo)
                    throws SystemException {
        String[] parametros = { "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "'" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_VERIFICARPAGOCUOTAANTERIOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);

    }

    @Override
    public int verificarFacCuotaAnterior(
        String codigoacuerdo,
        long cuota)
                    throws SystemException {
        String[] parametros = { "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ", "UN_CUOTA             =>",
                                Long.toString(cuota), "" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_VERIFICARFACCUOTAANT",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int verificarAnulacionReciboPendiente(
        String codigoacuerdo,
        String codigopredio,
        String compania,
        long cuota)
                    throws SystemException {
        String[] parametros = { "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ", "UN_CODIGOPREDIO      =>'", codigopredio,
                                "', ", "UN_COMPANIA          =>'", compania,
                                "', ", "UN_CUOTA             =>",
                                Long.toString(cuota), "" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_VERIFICARANULRECPENDIENTE",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String obtenerCuotasRecibos(
        String acuerdo,
        String predio,
        String recibo)
                    throws SystemException {
        String[] parametros = { "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_RECIBO            =>'", recibo, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_CUOTASRECIBOSIMP",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public long reemplazarTarifas(
        String compania,
        int anoremplazado,
        int anoanterior,
        String usuario,
        BigDecimal incremento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOREMPLAZADO     =>",
                                Integer.toString(anoremplazado), ", ",
                                "UN_ANOANTERIOR       =>",
                                Integer.toString(anoanterior), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_INCREMENTO        =>",
                                incremento.toString(), "" };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_REMPLAZATARIFAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public String importarIGACTipoUno(
        String compania,
        String usuario,
        Date fechacorte,
        boolean indTotal,
        String nombrecompania)
                    throws SystemException {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_IND_TOTAL         =>",
                                    (indTotal ? "-1" : "0"), ", ",
                                    "UN_NOMBRECOMPANIA    =>'", nombrecompania,
                                    "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM6.FC_IMPORTAR_IGAC_TIPO_UNO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String validarTipoResolucion(
        String primeralinea)
                    throws SystemException {
        String[] parametros = { "UN_PRIMERALINEA      =>'", primeralinea, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_VALIDATIPORESOLUCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void distribuirAcuerdoAcacias(
        String compania,
        String codigopredio,
        String codigoacuerdo,
        int preanoi,
        int preano,
        String tabla)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOPREDIO      =>'", codigopredio, "', ",
                                "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ", "UN_PREANOI           =>",
                                Integer.toString(preanoi), ", ",
                                "UN_PREANO            =>",
                                Integer.toString(preano), ", ",
                                "UN_TABLA             =>'", tabla, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_DISTRIBUIRACUERDOACACIAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarAcuerdosFacturados(
        String compania,
        String tabla,
        int codigo,
        String codigoacuerdo,
        String campos,
        BigInteger cuota,
        String condicion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TABLA             =>'", tabla, "', ",
                                "UN_CODIGO            =>",
                                Integer.toString(codigo), ", ",
                                "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ", "UN_CAMPOS            =>'", campos,
                                "', ", "UN_CUOTA             =>",
                                cuota.toString(), ", ",
                                "UN_CONDICION         =>'", condicion, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_ACTUALIZARACUFACTURADOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long activarReserva(
        String compania,
        String codigo,
        double porcentaje,
        int vigencia,
        String ordenpredial,
        String usuario,
        String resolucion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_PORCENTAJE        =>",
                                Double.toString(porcentaje), ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_ORDENPREDIAL      =>'", ordenpredial, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_RESOLUCION        =>'", resolucion, "'" };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.FC_ACTIVARESERVA",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public void cancelarReserva(
        String compania,
        String codigo,
        int vigencia,
        String ordenpredial,
        String usuario,
        String resolucion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_ORDENPREDIAL      =>'", ordenpredial, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_RESOLUCION        =>'", resolucion, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_CANCELARRESERVA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reversarPagosExcedentes(
        String compania,
        String numFactura,
        String codPredio,
        String codAcuerdo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUM_FACTURA       =>'", numFactura, "', ",
                                "UN_COD_PREDIO        =>'", codPredio, "', ",
                                "UN_COD_ACUERDO       =>'", codAcuerdo, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_REVERSARPAGOSEXCEDENTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reversarPagosCuotasAcuerdos(
        String compania,
        String numFactura,
        String codPredio,
        String codAcuerdo,
        String usuario,
        boolean abonoaacuerdo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUM_FACTURA       =>'", numFactura, "', ",
                                "UN_COD_PREDIO        =>'", codPredio, "', ",
                                "UN_COD_ACUERDO       =>'", codAcuerdo, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_ABONOAACUERDO     =>",
                                (abonoaacuerdo ? "-1" : "0"), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_REVERSARPAGOSCUOTASACUERDOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reversarPagoFinal(
        String compania,
        String numFactura,
        String codPredio,
        int preano,
        int preanoi,
        String pagBan,
        String paquete,
        Date fechapago,
        String usuario)
                    throws SystemException {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NUM_FACTURA       =>'", numFactura,
                                    "', ", "UN_COD_PREDIO        =>'",
                                    codPredio, "', ", "UN_PREANO            =>",
                                    Integer.toString(preano), ", ",
                                    "UN_PREANOI           =>",
                                    Integer.toString(preanoi), ", ",
                                    "UN_PAG_BAN           =>'", pagBan, "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_FECHAPAGO         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechapago),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM6.PR_REVERSARPAGOFINAL",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public void reversarAbonos(
        String compania,
        String numFactura,
        String codPredio,
        String usuario,
        int preano,
        int preanoi)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUM_FACTURA       =>'", numFactura, "', ",
                                "UN_COD_PREDIO        =>'", codPredio, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_PREANO            =>",
                                Integer.toString(preano), ", ",
                                "UN_PREANOI           =>",
                                Integer.toString(preanoi), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_REVERSARABONOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void cancelarReservasUsuario(
        String compania,
        String codigo,
        int vigencia,
        String ordenpredial,
        String usuario,
        String resolucion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_ORDENPREDIAL      =>'", ordenpredial, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_RESOLUCION        =>'", resolucion, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM6.PR_CANCELAR_RESERVAS_USUARIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal reversarPagoPazYSalvo(
        String compania,
        String referencia,
        Date fecha,
        String paquete,
        String banco,
        String numcupones,
        String acumulado,
        String usuario)
                    throws SystemException {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_REFERENCIA        =>'", referencia,
                                    "', ", "UN_FECHA             =>'",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "', ", "UN_PAQUETE           =>'", paquete,
                                    "', ", "UN_BANCO             =>'", banco,
                                    "', ", "UN_NUMCUPONES        =>'",
                                    numcupones, "', ",
                                    "UN_ACUMULADO         =>'", acumulado,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM6.FC_REVERSARPAGO_PAZYSALVO",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public void habilitarExento(
        String compania,
        int nivelUsuario,
        String usuario,
        boolean indExeImpuesto,
        boolean indExeCar,
        boolean indExeOtros,
        int anioDesde,
        int anioHasta,
        String codpredio,
        String numeroOrden,
        String codResolucion,
        Date fecResolucion,
        String elaboradaPor,
        String firmadaPor,
        String observacion)
                    throws SystemException {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NIVEL_USUARIO     =>",
                                    Integer.toString(nivelUsuario), ", ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_IND_EXE_IMPUESTO  =>",
                                    indExeImpuesto ? "-1" : "0", ", ",
                                    "UN_IND_EXE_CAR       =>",
                                    indExeCar ? "-1" : "0", ", ",
                                    "UN_IND_EXE_OTROS     =>",
                                    indExeOtros ? "-1" : "0", ", ",
                                    "UN_ANIO_DESDE        =>",
                                    Integer.toString(anioDesde), ", ",
                                    "UN_ANIO_HASTA        =>",
                                    Integer.toString(anioHasta), ", ",
                                    "UN_CODPREDIO         =>'", codpredio,
                                    "', ", "UN_NUMERO_ORDEN      =>'",
                                    numeroOrden, "', ",
                                    "UN_CODRESOLUCION     =>'", codResolucion,
                                    "', ", "UN_FECRESOLUCION     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecResolucion),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELABORADAPOR      =>'", elaboradaPor,
                                    "', ", "UN_FIRMADAPOR        =>'",
                                    firmadaPor, "', ",
                                    "UN_OBSERVACION       =>'", observacion,
                                    "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM6.PR_HABILITAR_EXENTO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public void deshabilitarExento(
        String compania,
        int nivelUsuario,
        String usuario,
        boolean indExeImpuesto,
        boolean indExeCar,
        boolean indExeOtros,
        int anioDesde,
        int anioHasta,
        String codPredio,
        String numeroOrden,
        String codResolucion,
        Date fecResolucion,
        String elaboradapor,
        String firmadapor,
        String observacion)
                    throws SystemException {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NIVEL_USUARIO     =>",
                                    Integer.toString(nivelUsuario), ", ",
                                    "UN_USUARIO           =>'", usuario + "', ",
                                    "UN_IND_EXE_IMPUESTO  =>",
                                    indExeImpuesto ? "-1" : "0", ", ",
                                    "UN_IND_EXE_CAR       =>",
                                    indExeCar ? "-1" : "0", ", ",
                                    "UN_IND_EXE_OTROS     =>",
                                    indExeOtros ? "-1" : "0", ", ",
                                    "UN_ANIO_DESDE        =>",
                                    Integer.toString(anioDesde), ", ",
                                    "UN_ANIO_HASTA        =>",
                                    Integer.toString(anioHasta), ", ",
                                    "UN_CODPREDIO         =>'",
                                    codPredio + "', ",
                                    "UN_NUMERO_ORDEN      =>'",
                                    numeroOrden, "', ",
                                    "UN_CODRESOLUCION     =>'",
                                    codResolucion, "', ",
                                    "UN_FECRESOLUCION     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecResolucion),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELABORADAPOR      =>'", elaboradapor,
                                    "', ", "UN_FIRMADAPOR        =>'",
                                    firmadapor, "', ",
                                    "UN_OBSERVACION       =>'", observacion,
                                    "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM6.PR_DESHABILITAR_EXENTO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

}