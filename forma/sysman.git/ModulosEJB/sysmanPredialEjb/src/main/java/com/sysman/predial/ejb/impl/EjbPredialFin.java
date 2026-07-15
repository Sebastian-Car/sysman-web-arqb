package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialFinLocal;
import com.sysman.predial.ejb.EjbPredialFinRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialFin
 * 
 * @author ybecerra
 * @version 2, 10/06/2017, Implementacion metodo concatenar de la
 * clase SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones
 */
@Stateless
@LocalBean
public class EjbPredialFin implements EjbPredialFinRemote, EjbPredialFinLocal {
    /**
     * Default constructor.
     */
    public EjbPredialFin() {
    }

    @Override
    public void crearAcuedoDePago(
        String compania,
        String acuerdo,
        int periodo,
        String tAcuerdo,
        String tFacturadosacu)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "', ",
                               "UN_ACUERDO   =>'", acuerdo, "', ",
                               "UN_PERIODO   =>",
                               Integer.toString(periodo), ", ",
                               "UN_T_ACUERDO    =>'", tAcuerdo, "', ",
                               "UN_T_FACTURADOSACU =>'", tFacturadosacu, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_CALCULARACUERDOVIG",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void liquidaInteresRecargo(
        String compania,
        String codigoacuerdo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_CODIGOACUERDO   =>'", codigoacuerdo, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_LIQUIDAINTERESRECARGO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void liquidarInteresAcuerdo(
        String compania,
        String codigoacuerdo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_CODIGOACUERDO    =>'", codigoacuerdo, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_LIQUIDAINTERESACUERDO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void revertirInteresPagoAnticipado(
        String compania,
        String codigoacuerdo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "', ",
                               "UN_CODIGOACUERDO    =>'", codigoacuerdo, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_REVIERTEINTERESPAGANT",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public String calcularCuotasAcuerdo(
        String compania,
        String codigoacuerdo,
        boolean anulado,
        String usuario,
        Date fechacorte,
        String codigo)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA             =>'", compania,
                                   "', ",
                                   "UN_CODIGOACUERDO        =>'", codigoacuerdo,
                                   "', ", "UN_ANULADO       =>",
                                   anulado ? "-1" : "0", ", ",
                                   "UN_USUARIO         =>'", usuario, "', ",
                                   "UN_FECHACORTE      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacorte),
                                   "','DD/MM/YYYY'), ",
                                   "UN_CODIGO            =>'", codigo, "'"

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_FIN.FC_CALCULARCUOTASACUERDO",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int calcularRecargosAcuerdo(
        String compania,
        String codigoacuerdo,
        String interescompuesto,
        Date fechacorte)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA           =>'", compania, "', ",
                                   "UN_CODIGOACUERDO      =>'", codigoacuerdo,
                                   "', ", "UN_INTERESCOMPUESTO  =>'",
                                   interescompuesto, "', ",
                                   "UN_FECHACORTE        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacorte),
                                   "','DD/MM/YYYY')" };
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_FIN.FC_CALCULARRECARGOSACUERDO",
                            SysmanFunciones.concatenar(parametro),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String consultarTasaInteresVigente(
        String compania)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA =>'", compania, "'"

        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.FC_CONSULTARTASAINTERESVIGENTE",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public void manejarPagoAnticipado(
        String compania,
        String codigoAcuerdo,
        int acuerdo,
        int recargo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA           =>'", compania, "', ",
                               "UN_CODIGO_ACUERDO     =>'", codigoAcuerdo,
                               "', ",
                               "UN_ACUERDO            =>",
                               Integer.toString(acuerdo), ", ",
                               "UN_RECARGO           =>",
                               Integer.toString(recargo), ""

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_MANEJAPAGOANTICIPADO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public int crearAcuerdo(
        String compania,
        String nombrecompania,
        String predio,
        int periodo,
        String numeroorden,
        String idres,
        String nombreres,
        String direccionres,
        String telefonores,
        int ncuotas,
        BigDecimal interes,
        BigDecimal recargo,
        String resolucion,
        String usuario,
        String recsoporte,
        boolean aplicadscesp,
        boolean preeliminar,
        boolean indabonoinicial,
        String vlrabonoinicial,
        String nitcompania,
        boolean acuerdopasto)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_NOMBRECOMPANIA    =>'", nombrecompania,
                               "', ", "UN_PREDIO     =>'", predio, "', ",
                               "UN_PERIODO            =>",
                               Integer.toString(periodo), ", ",
                               "UN_NUMEROORDEN       =>'", numeroorden, "', ",
                               "UN_IDRES             =>'", idres, "', ",
                               "UN_NOMBRERES         =>'", nombreres, "', ",
                               "UN_DIRECCIONRES      =>'", direccionres, "', ",
                               "UN_TELEFONORES       =>'", telefonores, "', ",
                               "UN_NCUOTAS           =>",
                               Integer.toString(ncuotas), ", ",
                               "UN_INTERES           =>", interes.toString(),
                               ", ", "UN_RECARGO           =>",
                               recargo.toString(), ", ",
                               "UN_RESOLUCION        =>'", resolucion, "', ",
                               "UN_USUARIO           =>'", usuario, "', ",
                               "UN_RECSOPORTE        =>'", recsoporte, "', ",
                               "UN_APLICADSCESP      =>",
                               aplicadscesp ? "-1" : "0", ", ",
                               "UN_PREELIMINAR       =>",
                               preeliminar ? "-1" : "0", ", ",
                               "UN_INDABONOINICIAL   =>",
                               indabonoinicial ? "-1" : "0", ", ",
                               "UN_VLRABONOINICIAL   =>'", vlrabonoinicial,
                               "', ", "UN_NITCOMPANIA       =>'", nitcompania,
                               "', ", "UN_ACUERDOPASTO      =>",
                               acuerdopasto ? "-1" : "0", ""

        };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.FC_CREARACUERDO",
                        SysmanFunciones.concatenar(parametro),
                        Types.INTEGER);
    }

    @Override
    public void calcularAcuerdo(
        String compania,
        String acuerdo,
        int periodo,
        String tacuerdo,
        String tfacturadosacu,
        String interescompuesto,
        String llamadopor,
        boolean indabonoinicial,
        String vlrabonoinicial,
        String nitcompania,
        boolean acuerdopasto,
        int anomenor,
        int anomayor)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                               "UN_ACUERDO            =>'", acuerdo, "', ",
                               "UN_PERIODO            =>",
                               Integer.toString(periodo), ", ",
                               "UN_TACUERDO          =>'", tacuerdo, "', ",
                               "UN_TFACTURADOSACU    =>'", tfacturadosacu,
                               "', ", "UN_INTERESCOMPUESTO  =>'",
                               interescompuesto, "', ",
                               "UN_LLAMADOPOR        =>'", llamadopor, "', ",
                               "UN_INDABONOINICIAL   =>",
                               indabonoinicial ? "-1" : "0", ", ",
                               "UN_VLRABONOINICIAL   =>'", vlrabonoinicial,
                               "', ", "UN_NITCOMPANIA       =>'", nitcompania,
                               "', ", "UN_ACUERDOPASTO      =>",
                               acuerdopasto ? "-1" : "0", ", ",
                               "UN_ANOMENOR          =>",
                               Integer.toString(anomenor), ", ",
                               "UN_ANOMAYOR          =>",
                               Integer.toString(anomayor), ""

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_CALCULARACUERDO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void reCalcularCapitalAc(
        String compania,
        String codigoacuerdo,
        int periodo,
        String tacuerdo,
        String tfacturadosacu,
        boolean manejacompuesto)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CODIGOACUERDO     =>'", codigoacuerdo, "', ",
                               "UN_PERIODO           =>",
                               Integer.toString(periodo), ", ",
                               "UN_TACUERDO          =>'", tacuerdo, "', ",
                               "UN_TFACTURADOSACU    =>'", tfacturadosacu,
                               "', ", "UN_MANEJACOMPUESTO   =>",
                               manejacompuesto ? "-1" : "0", ""

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_RECALCULAR_CAPITAL_AC",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public Date optenerFechaFinalAcuerdo(
        String compania,
        Date fechaacuerdo,
        BigDecimal ncuotas,
        BigDecimal periodicidad)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                                   "UN_FECHAACUERDO      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechaacuerdo),
                                   "','DD/MM/YYYY'), ",
                                   "UN_NCUOTAS           =>",
                                   ncuotas.toString(), ", ",
                                   "UN_PERIODICIDAD      =>",
                                   periodicidad.toString(), ""

            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_FIN.FC_FECHAFINAL_ACUERDO",
                            SysmanFunciones.concatenar(parametro),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal obtenerFechaFinalAcuerdo()
                    throws SystemException {
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.FC_TASAINTERESVIGENTE",
                        "",
                        Types.DECIMAL);
    }

    @Override
    public void distribuirAcuerdo(
        String compania,
        String predio,
        String acuerdo,
        int preanoi,
        int preano,
        String tFacturadosacu)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA              =>'", compania, "', ",
                               "UN_PREDIO                =>'", predio, "', ",
                               "UN_ACUERDO               =>'", acuerdo, "', ",
                               "UN_PREANOI               =>",
                               Integer.toString(preanoi), ", ",
                               "UN_PREANO            =>",
                               Integer.toString(preano), ", ",
                               "UN_T_FACTURADOSACU   =>'", tFacturadosacu, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_DISTRIBUIRACUERDO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public int distribuirAcuerdoCapitalInteres(
        String compania,
        String predio,
        String acuerdo,
        int preanoi,
        int preano,
        String tFacturadosacu,
        boolean escapital)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA               =>'", compania, "', ",
                               "UN_PREDIO            =>'", predio, "', ",
                               "UN_ACUERDO           =>'", acuerdo, "', ",
                               "UN_PREANOI           =>",
                               Integer.toString(preanoi), ", ",
                               "UN_PREANO            =>",
                               Integer.toString(preano), ", ",
                               "UN_T_FACTURADOSACU   =>'", tFacturadosacu,
                               "', ", "UN_ESCAPITAL         =>",
                               escapital ? "-1" : "0", ""

        };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.FC_DISTRIBUIR_CAPITALINTERES",
                        SysmanFunciones.concatenar(parametro),
                        Types.INTEGER);
    }

    @Override
    public void cargarInformacionExcedentes(
        String compania,
        String usuario,
        String numeroorden,
        String factura,
        String predio,
        String anocausoexcedente,
        String anoaplicarexcedente,
        String banco,
        String observaciones,
        String c1,
        String c2,
        String c3,
        String c4,
        String c13,
        String c14,
        String c15,
        String c16,
        String c17,
        String c18,
        String c19,
        String c20)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_USUARIO         =>'", usuario, "', ",
                               "UN_NUMEROORDEN       =>'", numeroorden, "', ",
                               "UN_FACTURA           =>'", factura, "', ",
                               "UN_PREDIO            =>'", predio, "', ",
                               "UN_ANOCAUSOEXCEDENTE =>'", anocausoexcedente,
                               "', ", "UN_ANOAPLICAREXCEDENTE =>'",
                               anoaplicarexcedente, "', ",
                               "UN_BANCO             =>'", banco, "', ",
                               "UN_OBSERVACIONES     =>'", observaciones, "', ",
                               "UN_C1                =>'", c1, "', ",
                               "UN_C2                =>'", c2, "', ",
                               "UN_C3                =>'", c3, "', ",
                               "UN_C4                =>'", c4, "', ",
                               "UN_C13               =>'", c13, "', ",
                               "UN_C14               =>'", c14, "', ",
                               "UN_C15               =>'", c15, "', ",
                               "UN_C16               =>'", c16, "', ",
                               "UN_C17               =>'", c17, "', ",
                               "UN_C18               =>'", c18, "', ",
                               "UN_C19               =>'", c19, "', ",
                               "UN_C20               =>'", c20, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_FIN.PR_CARGARINFORMACIONEXCDENTES",
                        SysmanFunciones.concatenar(parametro));
    }
}