package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaBancosLocal;
import com.sysman.nomina.ejb.EjbNominaBancosRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaBancos
 */
@Stateless
@LocalBean
public class EjbNominaBancos
                implements EjbNominaBancosRemote, EjbNominaBancosLocal {
    /**
     * Default constructor.
     */
    public EjbNominaBancos() {
    }

    @Override
    public String getDiscoDavivienda(
        String compania,
        int consecutivo,
        String codigoBanco,
        String codigoOficina,
        int proceso,
        int anio,
        int mes,
        int periodo,
        Date fecha)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CONSECUTIVO       =>",
                                    Integer.toString(consecutivo), ", ",
                                    "UN_CODIGOBANCO       =>'", codigoBanco,
                                    "', ",
                                    "UN_CODOFICINA        =>'", codigoOficina,
                                    "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo), ", ",
                                    "UN_FECHA           =>",
                                    SysmanFunciones.formatearFecha(fecha), ""
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_DISCODAVIVIENDA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getDiscoDaviviendaAuditoria(
        String compania,
        String codbanco,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODBANCO          =>'", codbanco, "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo)
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_DISCODAVIVIENDA_AUDITORIA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getDiscoAuditoriaNuevo(
        String compania,
        String codbanco,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODBANCO          =>'", codbanco, "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo)
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_DISCOAUDITORIA_NUEVO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getMediosMagneticosDian(
        String compania,
        int idproceso,
        int ano1,
        int mes1,
        int periodo1,
        int ano2,
        int mes2,
        int periodo2,
        BigDecimal tope)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_IDPROCESO         =>",
                                    Integer.toString(idproceso), ", ",
                                    "UN_ANO1              =>",
                                    Integer.toString(ano1), ", ",
                                    "UN_MES1              =>",
                                    Integer.toString(mes1), ", ",
                                    "UN_PERIODO1          =>",
                                    Integer.toString(periodo1), ", ",
                                    "UN_ANO2              =>",
                                    Integer.toString(ano2), ", ",
                                    "UN_MES2              =>",
                                    Integer.toString(mes2), ", ",
                                    "UN_PERIODO2          =>",
                                    Integer.toString(periodo2), ", ",
                                    "UN_TOPE              =>", tope.toString()
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_MEDIOSMAGNETICOS_DIAN",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarDiscoAvVillas(
        String compania,
        int proceso,
        int ano1,
        int mes1,
        String periodo1,
        String banco,
        Date fecha)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANO1              =>",
                                    Integer.toString(ano1), ", ",
                                    "UN_MES1              =>",
                                    Integer.toString(mes1), ", ",
                                    "UN_PERIODO1          =>'", periodo1, "', ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY')"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_DISCOAVVILLAS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoSudameris(
        String compania,
        int ano,
        int mes,
        String periodo,
        String banco,
        String descripciondeta,
        String descripcionpago,
        String descripcionampl)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_DESCRIPCIONDETA   =>'", descripciondeta,
                                    "', ", "UN_DESCRIPCIONPAGO   =>'",
                                    descripcionpago, "', ",
                                    "UN_DESCRIPCIONAMPL   =>'", descripcionampl,
                                    "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_PLANOSUDAMERIS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoBBogota(
        String compania,
        int ano,
        int mes,
        String periodo,
        Date fecha,
        String banco,
        String codigociudad,
        String conceptopago,
        String centrocosto)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_CODIGOCIUDAD      =>'", codigociudad,
                                    "', ", "UN_CONCEPTOPAGO      =>'",
                                    conceptopago, "', ",
                                    "UN_CENTROCOSTO       =>'", centrocosto, "'"
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_PLANOBANCOBOGOTA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoPagoTerSudameris(
        String compania,
        int ano,
        int mes,
        String periodo,
        int proceso,
        String descripciondeta,
        String descripcionpago,
        String descripcionampl,
        int tipoPlano,
        String centroDeCosto)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_DESCRIPCIONDETA   =>'", descripciondeta,
                                    "', ", "UN_DESCRIPCIONPAGO   =>'",
                                    descripcionpago, "', ",
                                    "UN_DESCRIPCIONAMPL   =>'", descripcionampl,
                                    "', ",
                                    "UN_TIPOPLANO => ",
                                    Integer.toString(tipoPlano), ", ",
                                    "UN_CENTROCOSTO => '", centroDeCosto, "'"
            };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_BAN.FC_PLANOPAGOTERCSUDAMERIS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}