package com.sysman.chipfut.ejb.impl;

import com.sysman.chipfut.ejb.EjbChipFutUnoGeneralRemote;
import com.sysman.chipfut.ejb.EjbChipFutUnoLocal;
import com.sysman.chipfut.ejb.EjbChipFutUnoRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ChipFutUno
 */
@Stateless
@LocalBean
public class EjbChipFutUno implements EjbChipFutUnoRemote, EjbChipFutUnoLocal
{

    @EJB
    private EjbChipFutUnoGeneralRemote ejbChipFutUnoGeneral;

    /**
     * Default constructor.
     */
    public EjbChipFutUno()
    {
    }

    @Override
    public String generarArchivoPlanoIngresosRegaliasFut(String compania,
        int ano, String codigoentidad, int mes,
        int trimestre, boolean miles, boolean excel, int opcion)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGOENTIDAD     =>'", codigoentidad,
                                "', ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_TRIMESTRE         =>",
                                Integer.toString(trimestre), ", ",
                                "UN_MILES             =>", miles ? "-1" : "0",
                                ", ",
                                "UN_EXCEL             =>", excel ? "-1" : "0",
                                ", ", "UN_OPCION            =>",
                                Integer.toString(opcion), "" };
        try {
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT1.FC_GENERARPLANOSGRINGRESOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoReservasPptales(String compania,
        String codentidad, int trimestre, int ano,
        int tipoActoAdmtivo, int nroActoAdmtivo, Date fechaDocumento,
        boolean pesos, boolean separadas,
        boolean excel) throws SystemException
    {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODENTIDAD        =>'", codentidad,
                                    "', ", "UN_TRIMESTRE         =>",
                                    Integer.toString(trimestre), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPOACTOADMTIVO   =>",
                                    Integer.toString(tipoActoAdmtivo), ", ",
                                    "UN_NROACTOADMTIVO    =>",
                                    Integer.toString(nroActoAdmtivo), ", ",
                                    "UN_FECHADOCUMENTO    =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaDocumento),
                                    "','DD/MM/YYYY'), ",
                                    "UN_PESOS             =>",
                                    pesos ? "-1" : "0", ", ",
                                    "UN_SEPARADAS         =>",
                                    separadas ? "-1" : "0", ",",
                                    "UN_EXCEL             =>",
                                    excel ? "-1" : "0", "" };
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT1.FC_GENERARPLANORESERVASPPTALES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String verificarConfiguracion(String compania, int ano)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), "" };
        try {
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT1.FC_VERIFICARCONFIGURACION",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void subirSeguimientoReciprocas(String compania, String cambios,
        String usuario, String consecutivo)
                    throws SystemException
    {

        ejbChipFutUnoGeneral.subirSeguimientoReciprocas(compania, cambios,
                        usuario, consecutivo);

    }

    @Override
    public String validarCuentas(String compania, int ano, String cuentaExcel)
                    throws SystemException

    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CUENTAEXCEL       =>", cuentaExcel, "" };
        try {
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT1.FC_VALIDAR_CUENTAS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String enviarFormatoEspecial(
        String compania,
        int ano,
        int trimestre,
        String cuentasExcel)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TRIMESTRE         =>",
                                Integer.toString(trimestre), ", ",
                                "UN_CUENTASEXCEL      =>", cuentasExcel, ""
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CHIPFUT1.FC_ENVIARFORMATOESPECIAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String cuentasExistentes(
        String compania,
        int ano,
        String cuentaexcel)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", 
                                "UN_ANO               =>", Integer.toString(ano), ", ",
                                "UN_CUENTAEXCEL       =>", cuentaexcel, ""
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_CHIPFUT1.FC_CUENTAS_EXISTENTES",
                                            SysmanFunciones.concatenar(parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    
    @Override
    public String generarProcesoSiaSql(
        String  strsql,
        int carac_esp)
                    throws SystemException {
        try {
            String[] parametros = { "UN_STRSQL          =>'", strsql, "', ",
            		                "UN_IND				=>", Integer.toString(carac_esp) ,""
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
}