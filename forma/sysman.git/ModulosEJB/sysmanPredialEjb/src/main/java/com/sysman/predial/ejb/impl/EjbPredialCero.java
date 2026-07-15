package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroLocal;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialCero
 * 
 * @modified jguerrero
 * @version 2. 07/06/2017
 */
@Stateless
@LocalBean
public class EjbPredialCero
                implements EjbPredialCeroRemote, EjbPredialCeroLocal {
    /**
     * Default constructor.
     */
    public EjbPredialCero() {
    }

    @Override
    public String consultarEncabezadoDeColumna(
        String compania,
        int concepto)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto) };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.FC_ENCABEZADO_COLUMNA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean consultarNombreUsuarioEnParametro(
        String compania,
        int modulo,
        String accion,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA =>'", compania,
                                "', ",
                                "UN_MODULO          =>",
                                Integer.toString(modulo), ", ",
                                "UN_ACCION          =>'", accion, "', ",
                                "UN_USUARIO         =>'", usuario, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.FC_PERMISOACCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public int consultarVigenciaValidaParaPago(
        String compania,
        String predio)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_PREDIO         =>'", predio, "'" };

        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.FC_PAGOANOVALIDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void actualizarUltimaVigenciaCancelada(
        String compania,
        String codigo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_CODIGO          =>'", codigo, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.PR_ACT_ULTVIGCANCELADA_ANT",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String reversarPagoPredio(
        String compania,
        String numFactura,
        String codPredio,
        String pagBan,
        String paquete,
        Date fechapago,
        String codAcuerdo,
        String usuario)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_NUM_FACTURA     =>'", numFactura,
                                    "', ", "UN_COD_PREDIO        =>'",
                                    codPredio, "', ",
                                    "UN_PAG_BAN           =>'", pagBan, "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_FECHAPAGO         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechapago),
                                    "','DD/MM/YYYY'), ",
                                    "UN_COD_ACUERDO       =>'", codAcuerdo,
                                    "', ", "UN_USUARIO  =>'", usuario,
                                    "'" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL.FC_REVERSARPAGO_RECIBO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void insertarCambiosEnAuditoria(
        String compania,
        String codmod,
        String opemod,
        String ccomod,
        String vanmod,
        String vnumod,
        String descripcion,
        Date fecmod,
        Date hormod,
        String numeroOrden)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                    "UN_CODMOD            =>'", codmod, "', ",
                                    "UN_OPEMOD            =>'", opemod, "', ",
                                    "UN_CCOMOD            =>'", ccomod, "', ",
                                    "UN_VANMOD            =>'", vanmod, "', ",
                                    "UN_VNUMOD            =>'", vnumod, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_FECMOD            =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecmod,
                                                    "dd/MM/yyyy HH:mm:ss"),
                                    "','DD/MM/YYYY HH24:MI:SS'), ",
                                    "UN_HORMOD            =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    hormod,
                                                    "dd/MM/yyyy HH:mm:ss"),
                                    "','DD/MM/YYYY HH24:MI:SS'), ",
                                    "UN_NUMERO_ORDEN      =>'", numeroOrden,
                                    "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL.PR_AUDITORIA",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String actualizarAvaluo(
        String compania,
        String codigopred,
        String vigencia,
        int modulo,
        BigDecimal avaluoigag,
        BigDecimal avaluo,
        String trpcodAnt,
        String trpcod,
        int preano,
        int ano,
        String consecutivorad,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_CODIGOPRED        =>'", codigopred, "', ",
                                "UN_VIGENCIA          =>'", vigencia, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_AVALUOIGAG        =>",
                                (avaluoigag).toString(), ", ",
                                "UN_AVALUO            =>", avaluo.toString(),
                                ", ",
                                "UN_TRPCOD_ANT        =>'", trpcodAnt, "', ",
                                "UN_TRPCOD            =>'", trpcod, "', ",
                                "UN_PREANO            =>",
                                Integer.toString(preano), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CONSECUTIVORAD    =>'", consecutivorad,
                                "', ", "UN_USUARIO =>'",
                                usuario, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.FC_AUSUBAUTOAVALUO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public long consultarAbonoEnRecibosDePago(
        String compania,
        String codigopred,
        int vigencia)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_CODIGOPRED        =>'", codigopred, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia)
        };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.FC_BUSUBAUTOAVALUO",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public String consultarNumeroDeCuotasPorUsuario(
        String compania,
        String acuerdo,
        String predio,
        String recibo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_ACUERDO           =>'", acuerdo, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_RECIBO            =>'", recibo, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.FC_CUOTASRECIBOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void generarFacturaVigencia(
        String compania,
        String codpredio,
        String numorden,
        int menorvigencia,
        int mayorvigencia,
        int anocorte,
        String nombrepropfact,
        String nitpropfact,
        String numordenfacturar,
        Date fechalimite,
        boolean imprimirTotalcero,
        int modulo,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODPREDIO         =>'", codpredio,
                                    "', ", "UN_NUMORDEN          =>'", numorden,
                                    "', ", "UN_MENORVIGENCIA     =>",
                                    Integer.toString(menorvigencia), ", ",
                                    "UN_MAYORVIGENCIA     =>",
                                    Integer.toString(mayorvigencia), ", ",
                                    "UN_ANOCORTE          =>",
                                    Integer.toString(anocorte), ", ",
                                    "UN_NOMBREPROPFACT    =>'", nombrepropfact,
                                    "', ", "UN_NITPROPFACT       =>'",
                                    nitpropfact, "', ",
                                    "UN_NUMORDENFACTURAR  =>'",
                                    numordenfacturar, "', ",
                                    "UN_FECHALIMITE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite),
                                    "','DD/MM/YYYY'), ",
                                    "UN_IMPRIMIR_TOTALCER =>",
                                    imprimirTotalcero ? "-1" : "0", ", ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL.PR_IMPRESORAVIGENCIA",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void crearDetalleFactura(
        String compania,
        String codpredio,
        String numorden,
        String docnum,
        String tipofra,
        int menorvigencia,
        int mayorvigencia,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_NUMORDEN          =>'", numorden, "', ",
                                "UN_DOCNUM            =>'", docnum, "', ",
                                "UN_TIPOFRA           =>'", tipofra, "', ",
                                "UN_MENORVIGENCIA     =>",
                                Integer.toString(menorvigencia), ", ",
                                "UN_MAYORVIGENCIA     =>",
                                Integer.toString(mayorvigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL.PR_CREARDETALLE_FACTURA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void realizarPagoPazySalvo(
        String compania,
        String referencia,
        Date fechaPago,
        String bancoPago,
        String paquete,
        String usuario,
        String nroCuponesAcu,
        String vlacumaldo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_REFERENCIA        =>'", referencia,
                                    "', ",
                                    "UN_FECHAPAGO         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaPago),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCOPAGO         =>'", bancoPago,
                                    "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_NROCUPONESACU     =>'", nroCuponesAcu,
                                    "', ", "UN_VLACUMALDO        =>'",
                                    vlacumaldo,
                                    "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL.PR_REALIZAPAGO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

}
