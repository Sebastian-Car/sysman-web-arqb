package com.sysman.chipfut.ejb.impl;

import com.sysman.chipfut.ejb.EjbChipFutCeroLocal;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ChipFutCero
 */
@Stateless
@LocalBean
public class EjbChipFutCero
                implements EjbChipFutCeroRemote, EjbChipFutCeroLocal {
    /**
     * Default constructor.
     */
    public EjbChipFutCero() {
    }

    @Override
    public void traerFuentesPresupuesto(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CHIPFUT.PR_TRAER_FUENTES_PRESUPUESTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void traerCrearCodigosFut(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CHIPFUT.PR_CREAR_CODIGOS_FUT",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String generarPlanoSaldoMovimiento(
        String compania,
        int ano,
        int trimestre,
        String codigoEntidad,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos,
        boolean	covid)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TRIMESTRE         =>",
                                Integer.toString(trimestre), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoEntidad,
                                "', ",
                                "UN_DIGITOS           =>",
                                Integer.toString(digitos), ", ",
                                "UN_EXCEL             =>", (excel ? "-1" : "0"),
                                ", ",
                                "UN_MILES             =>", (miles ? "-1" : "0"),
                                ",",
                                "UN_CENTAVOS          =>",
                                (centavos ? "-1" : "0"),
                                ",",
                                "UN_COVID          =>",
                                (covid ? "-1" : "0"), ""

        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERARARCHIVOSALDOMOV",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoReciproco(
        String compania,
        int ano,
        int trimestre,
        String codigoEntidad,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos,
        boolean cgn,
        boolean codEquiv)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TRIMESTRE         =>",
                                Integer.toString(trimestre), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoEntidad,
                                "', ",
                                "UN_DIGITOS           =>",
                                Integer.toString(digitos), ", ",
                                "UN_EXCEL             =>", excel ? "-1" : "0",
                                ", ",
                                "UN_MILES             =>", miles ? "-1" : "0",
                                ", ",
                                "UN_CENTAVOS          =>",
                                centavos ? "-1" : "0", ", ",
                                "UN_CGN               =>", cgn ? "-1" : "0",
                                ", ",
                                "UN_COD_EQUIV         =>",
                                codEquiv ? "-1" : "0", ""

        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERARARCHIVORECIPROCAS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoBalance(
        String compania,
        int ano,
        String codigoEntidad,
        String codigoInicial,
        String codigoFinal,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoEntidad,
                                "', ", "UN_CODIGOINICIAL     =>'",
                                codigoInicial, "', ",
                                "UN_CODIGOFINAL       =>'", codigoFinal, "', ",
                                "UN_DIGITOS           =>",
                                Integer.toString(digitos), ", ",
                                "UN_EXCEL             =>", excel ? "-1" : "0",
                                ", ",
                                "UN_MILES             =>", miles ? "-1" : "0",
                                ", ",
                                "UN_CENTAVOS          =>",
                                centavos ? "-1" : "0", ""
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERARARCHIVOBALANCE",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoGastosInv(
        String compania,
        int ano,
        boolean miles,
        int mesInicial,
        int mesFinal,
        int trimestre,
        String codigoEntidad,
        boolean excel,
        boolean sicep,
        String tipo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MILES             =>", miles ? "-1" : "0",
                                ", ",
                                "UN_MESINICIAL        =>",
                                Integer.toString(mesInicial), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesFinal), ", ",
                                "UN_TRIMESTRE         =>",
                                Integer.toString(trimestre), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoEntidad,
                                "', ",
                                "UN_EXCEL             =>", excel ? "-1" : "0",
                                ", ",
                                "UN_SICEP             =>", sicep ? "-1" : "0",
                                ", ",
                                "UN_TIPO              =>'", tipo, "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERARINCONSGASTOSINV",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void trasladarConfSiguienteAnio(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CHIPFUT.PR_TRASLADARCONFFUT",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String generarArchivoPlanoIngresosFut(
        String compania,
        int ano,
        String codigoEntidad,
        int mes,
        int trimestre,
        boolean transBancos,
        boolean miles,
        boolean excel)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoEntidad,
                                "', ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_TRIMESTRE         =>",
                                Integer.toString(trimestre), ", ",
                                "UN_TRANSBANCOS       =>",
                                transBancos ? "-1" : "0", ", ",
                                "UN_MILES             =>", miles ? "-1" : "0",
                                ", ",
                                "UN_EXCEL             =>", excel ? "-1" : "0",
                                ""
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERARINGRESOSFUT",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int prepararAnio(
        String compania,
        int anio,
        int anioDestino)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_ANIODESTINO       =>",
                                Integer.toString(anioDestino), ""
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CHIPFUT.FC_PREPARAANIOCBANCOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String generarArchivoPlano2009BDME(
        String compania,
        int anio,
        String codigoentidad,
        int mes,
        String fecha)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoentidad,
                                "', ", "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_FECHA             =>'", fecha, "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERAR2009BDMESEMESTRAL",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public String generarProcesoSiaSql(
        String  strsql)
                    throws SystemException {
        try {
            String[] parametros = { "UN_STRSQL          =>'", strsql, "' "
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT1.FC_GENERARPROCESOSIA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch ( IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    @Override
    public String generarPlanoSaldoMovimientoMensual(
        String compania,
        int ano,
        int mes,
        String codigoEntidad,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos,
        boolean	covid)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES         =>",
                                Integer.toString(mes), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoEntidad,
                                "', ",
                                "UN_DIGITOS           =>",
                                Integer.toString(digitos), ", ",
                                "UN_EXCEL             =>", (excel ? "-1" : "0"),
                                ", ",
                                "UN_MILES             =>", (miles ? "-1" : "0"),
                                ",",
                                "UN_CENTAVOS          =>",
                                (centavos ? "-1" : "0"),
                                ",",
                                "UN_COVID          =>",
                                (covid ? "-1" : "0"), ""

        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT.FC_GENERASALDOMOVMES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}