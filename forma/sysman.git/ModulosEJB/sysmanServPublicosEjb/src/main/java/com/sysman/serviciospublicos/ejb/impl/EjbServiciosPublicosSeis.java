package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSeisLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSeisRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosSeis
 * 
 * @modifier amonroy, 10/06/2017 Implementacion de
 * SysmanFunciones.concatenar() para envio de parametros a funciones y
 * procedimientos
 * 
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosSeis implements EjbServiciosPublicosSeisRemote,
                EjbServiciosPublicosSeisLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosSeis() {
    }

    @Override
    public int generarActa(
        String compania,
        String codigoinicial,
        String codigofinal,
        int ciclo,
        String problemainicial,
        String problemafinal,
        int acta)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_CODIGOINICIAL      =>'", codigoinicial,
                                "', ",
                                "UN_CODIGOFINAL        =>'", codigofinal, "', ",
                                "UN_CICLO              =>",
                                Integer.toString(ciclo), ", ",
                                "UN_PROBLEMAINICIAL    =>'", problemainicial,
                                "', ",
                                "UN_PROBLEMAFINAL      =>'",
                                problemafinal, "', ",
                                "UN_ACTA               =>",
                                Integer.toString(acta) };

        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.FC_GEN_ACTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public boolean autorizarDescuentoTotal(
        String compania,
        String nit)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_NIT                =>'", nit, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.FC_AUTORIZACION_DESCTOTFACTURA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);

        return rta != 0;
    }

    @Override
    public boolean autorizarLimpiaDeshabilitados(
        String compania,
        String nit)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_NIT                 =>'", nit, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.FC_AUTORIZALIMPIADESHCIERRE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);

        return rta != 0;
    }

    @Override
    public String prepararSiguientePeriodo(
        String compania,
        int ciclo,
        String usuario,
        String nit,
        int anio,
        String periodo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA            =>'", compania,
                                    "', ",
                                    "UN_CICLO               =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_USUARIO             =>'", usuario,
                                    "', ",
                                    "UN_NIT                 =>'", nit, "',",
                                    "UN_ANIO                =>",
                                    Integer.toString(anio), ",",
                                    "UN_PERIODO             =>'", periodo,
                                    "'" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM6.FC_PREPARARSIGPERIODO",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String llamarPrepararSigPeriodo(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String sobreproduct,
        String sobredescuento,
        String nit,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA             =>'", compania,
                                    "', ",
                                    "UN_CICLO                =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_ANIO                  =>",
                                    Integer.toString(anio), ", ",
                                    "UN_PERIODO              =>'", periodo,
                                    "', ",
                                    "UN_SOBREPRODUCT         =>'", sobreproduct,
                                    "', ", "UN_SOBREDESCUENTO       =>'",
                                    sobredescuento, "', ",
                                    "UN_NIT                  =>'", nit, "', ",
                                    "UN_USUARIO              =>'", usuario,
                                    "'" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM6.FC_LLAMARPREPARARPER",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void guardarHistoria(
        String compania,
        int ciclo,
        int anio,
        String periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA             =>'", compania, "', ",
                                "UN_CICLO                =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO                 =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO              =>'", periodo, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_GUARDAHISTORIA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void cerrarConsumosMicro(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_CICLO            =>",
                                Integer.toString(ciclo),
                                ",UN_USUARIO       =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_CIERRECONSUMOSMICRO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void realizarCierrePersuasivo(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_CICLO            =>",
                                Integer.toString(ciclo), ",",
                                "UN_USUARIO       =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_CIERREPERSUASIVO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void guardarHistoricosFactura(
        String compania,
        int ciclo,
        int anio,
        String periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_CICLO           =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO            =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO         =>'", periodo, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_GUARDARHISTORICOSFACTURA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void auditarDesvio(
        String compania,
        String usuario,
        String proceso,
        int anio,
        String periodo,
        int ciclo,
        String codinterno,
        String descripcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_USUARIO        =>'", usuario, "', ",
                                "UN_PROCESO        =>'", proceso, "', ",
                                "UN_ANIO           =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO        =>'", periodo, "', ",
                                "UN_CICLO          =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODINTERNO     =>'", codinterno, "', ",
                                "UN_DESCRIPCION    =>'", descripcion, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_AUDITORIADESVIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void sumarPesoAseo(
        String compania,
        int ciclo,
        int anio,
        String periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_CICLO          =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO           =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO        =>'", periodo, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_SUMPESOASEO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void revisarFacturadosAbonos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_CICLO         =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO          =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO       =>'", periodo, "',",
                                "UN_USUARIO       =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_REVISAFACTURADOSABONOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void verificarFacturadosNulos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_CICLO         =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO          =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO       =>'", periodo, "',",
                                "UN_USUARIO       =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_FACTURADOSNULOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarEstadisticasTrifarias(
        String compania,
        int ciclo,
        int anio,
        String periodo, String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_CICLO        =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO         =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO      =>'", periodo, "', "
                                    + "UN_USUARIO     =>'",
                                usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTADISTICASTARIFARIAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void cuadrarEstratos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA    =>'", compania, "', ",
                                "UN_CICLO       =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO        =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO     =>'", periodo, "', "
                                    + "UN_USUARIO     =>'",
                                usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_CUADREESTRATOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void discriminarRes688(
        String compania,
        int ciclo,
        String periodo,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA    =>'", compania, "', ",
                                "UN_CICLO       =>",
                                Integer.toString(ciclo), ", ",
                                "UN_PERIODO     =>'", periodo, "', ",
                                "UN_ANIO        =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO     =>'",
                                usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_DISCRIMINARES688",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarEstadisticaFacturacion(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_CICLO      =>",
                                Integer.toString(ciclo), ",",
                                "UN_USUARIO       =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTADISTICAFACTURACION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarEstadisticasRecaudo(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_CICLO      =>",
                                Integer.toString(ciclo), ", ",
                                "UN_USUARIO    =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTRECAUDOPERATRASO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void verificarFacturadosNulos(
        String compania,
        int ciclo,
        String servicio,
        String rango,
        BigDecimal limiteinferior,
        BigDecimal limitesuperior,
        String condicion,
        String condicion1,
        String calsuspendidos,
        boolean conmanuales,
        String sitio,
        BigDecimal pesoaseoestad,
        boolean aseores720,
        boolean parexcluirpnocobro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_CICLO     =>",
                                Integer.toString(ciclo), ", ",
                                "UN_SERVICIO          =>'", servicio, "', ",
                                "UN_RANGO             =>'", rango, "', ",
                                "UN_LIMITEINFERIOR    =>",
                                limiteinferior.toString(), ", ",
                                "UN_LIMITESUPERIOR    =>",
                                limitesuperior.toString(), ", ",
                                "UN_CONDICION         =>'", condicion, "', ",
                                "UN_CONDICION1        =>'", condicion1, "', ",
                                "UN_CALSUSPENDIDOS    =>'", calsuspendidos,
                                "', ", "UN_CONMANUALES       =>",
                                conmanuales ? "-1" : "0", ", ",
                                "UN_SITIO             =>'", sitio, "', ",
                                "UN_PESOASEOESTAD     =>",
                                pesoaseoestad.toString(), ", ",
                                "UN_ASEORES720        =>",
                                aseores720 ? "-1" : "0", ", ",
                                "UN_PAREXCLUIRPNOCOBR =>",
                                parexcluirpnocobro ? "-1" : "0" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarEstadisticasConsumo(
        String compania,
        int ciclo, String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_CICLO     =>",
                                Integer.toString(ciclo), ",",
                                "UN_USUARIO   =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTADISTICACONSUMO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void cerrarDescuentos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String nit,
        String sobreescribir,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_NIT               =>'", nit, "', ",
                                "UN_SOBREESCRIBIR     =>'", sobreescribir,
                                "',", "UN_USUARIO   =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_CERRARDESCUENTOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void cerrarProductividad(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String sobreescribir,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_SOBREESCRIBIR     =>'", sobreescribir,
                                "', ", "UN_USUARIO           =>'", usuario,
                                "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_CERRARPRODUCTIVIDAD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void insertarEstadistica(
        String compania,
        int ciclo,
        String servicio,
        String rango,
        BigDecimal limiteInferior,
        BigDecimal limiteSuperior,
        String condicion,
        String condicion1,
        String calSuspendidos,
        boolean conManuales,
        String sitio,
        BigDecimal pesoAseoEstado,
        boolean aseores720,
        boolean parExcluirPNoCobro,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_SERVICIO          =>'", servicio, "', ",
                                "UN_RANGO             =>'", rango, "', ",
                                "UN_LIMITEINFERIOR    =>",
                                limiteInferior.toString(), ", ",
                                "UN_LIMITESUPERIOR    =>",
                                limiteSuperior.toString(), ", ",
                                "UN_CONDICION         =>'", condicion, "', ",
                                "UN_CONDICION1        =>'", condicion1, "', ",
                                "UN_CALSUSPENDIDOS    =>'", calSuspendidos,
                                "', ", "UN_CONMANUALES       =>",
                                conManuales ? "-1" : "0", ", ",
                                "UN_SITIO             =>'", sitio, "', ",
                                "UN_PESOASEOESTAD     =>",
                                pesoAseoEstado.toString(), ", ",
                                "UN_ASEORES720        =>",
                                aseores720 ? "-1" : "0", ", ",
                                "UN_PAREXCLUIRPNOCOBR =>",
                                parExcluirPNoCobro ? "-1" : "0", ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA",
                        SysmanFunciones.concatenar(parametros));
    }

}