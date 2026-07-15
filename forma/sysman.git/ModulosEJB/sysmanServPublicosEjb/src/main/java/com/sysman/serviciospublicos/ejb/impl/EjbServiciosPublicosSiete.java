package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosSieteGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosSiete
 * 
 * @author eamaya
 * @version 2.0 , Refactoring concatenacion
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosSiete
                implements EjbServiciosPublicosSieteRemote,
                EjbServiciosPublicosSieteLocal {

    @EJB
    private EjbServiciosPublicosSieteGeneralRemote ejbServPublicosSieteGeneral;

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosSiete() {
    }

    @Override
    public String calcularFacturacion(
        String compania,
        int intciclo,
        String strcodigoinicial,
        String strcodigofinal,
        boolean enserie,
        boolean finall,
        String usuario)
                    throws SystemException {

        return ejbServPublicosSieteGeneral.calcularFacturacion(compania,
                        intciclo, strcodigoinicial, strcodigofinal, enserie,
                        finall, usuario);
    }

    @Override
    public BigDecimal obtenerAjustePeso(
        String valor,
        BigDecimal ajustedecena,
        String redondeoporencima)
                    throws SystemException {

        String[] parametros = { "UN_VALOR             =>'", valor, "', ",
                                "UN_AJUSTEDECENA      =>",
                                ajustedecena.toString(), ", ",
                                "UN_REDONDEOPORENCIMA =>'", redondeoporencima,
                                "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.FC_AJUSTEALPESO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal obtenerCalculoDescuentoConcepto(
        String compania,
        String strusuario,
        int intciclo,
        int intano,
        String strperiodo,
        String totaseo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_STRUSUARIO        =>'", strusuario, "', ",
                                "UN_INTCICLO          =>",
                                Integer.toString(intciclo), ", ",
                                "UN_INTANO            =>",
                                Integer.toString(intano), ", ",
                                "UN_STRPERIODO        =>'", strperiodo, "', ",
                                "UN_TOTASEO           =>'", totaseo, "'"
        };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.FC_CALCULOPRODUCTIVIDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal obtenerModificacionesFacturado(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int intciclo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_INTCICLO          =>",
                                Integer.toString(intciclo)
        };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.FC_MODIFICACIONESFACTURADO(",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void distribuirFinanciableDeuda(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int ciclo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo)
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.PR_DISTRIBUIRCUOTA12",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void distribuirDeudaFinanciable12(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int ciclo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo)
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.PR_DISTRIBUIRDEUDA12",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal calcularFacturacion(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        String notacredito,
        boolean facturado)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_NOTACREDITO       =>'", notacredito, "', ",
                                "UN_FACTURADO         =>",
                                facturado ? "-1" : "0"
        };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.FC_DISTRIBUIRSALDOSCREDITO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void calcularFacturacion(
        String compania,
        int ano,
        String periodo,
        String codigoruta,
        int ciclo,
        String totfact)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_TOTFACT           =>'", totfact, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.PR_VALORDESSALDOCREDITO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarError(
        String compania,
        String codigoruta,
        int ciclo,
        int codigoerrorinterno,
        String mensaje)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGOERRORINTERN =>",
                                Integer.toString(codigoerrorinterno),
                                ", ",
                                "UN_MENSAJE           =>'", mensaje,
                                "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.PR_INSERTAERRORCALCULO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarRangos(
        String compania)
                    throws SystemException {
        ejbServPublicosSieteGeneral.actualizarRangos(compania);
    }

    @Override
    public void actualizarEstadoMedidores(
        String compania,
        int ciclo,
        String codigoRuta,
        long medidor,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                                "UN_MEDIDOR           =>",
                                Long.toString(medidor), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.PR_ACTUALIZAR_MEDIDORES",
                        SysmanFunciones.concatenar(parametros));
    }

}