package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCierreLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoCierreRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PresupuestoCierre
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPresupuestoCierre implements EjbPresupuestoCierreRemote,
                EjbPresupuestoCierreLocal {
    /**
     * Default constructor.
     */
    public EjbPresupuestoCierre() {
    }

    @Override
    public String cierrePresupuestalCb(
        String compania,
        int anoCierre,
        String usuario,
        boolean cierreNormal,
        boolean cierrePasivo,
        boolean cierreVigFuturas,
        boolean cierreVigFutPasivo,
        boolean cierreRegalias,
        Date fechaCierre,
        boolean cierreCofinanciados)
                    throws SystemException {
        try {
        String[] parametros = {
                                "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOACIERRE        =>",
                                Integer.toString(anoCierre), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CIERRENORMAL      =>",
                                cierreNormal ? "-1" : "0", ", ",
                                "UN_CIERREPASIVO      =>",
                                cierrePasivo ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTURAS  =>",
                                cierreVigFuturas ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTUAPASIVO =>",
                                cierreVigFutPasivo ? "-1" : "0", ", ",
                                "UN_CIERREREGALIAS    =>",
                                cierreRegalias ? "-1" : "0", ", ",
                                "UN_FECHACIERRE       =>TO_DATE('",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaCierre),
                                "','DD/MM/YYYY'), ",
                                "UN_CIERRECOFINANCIADOS    =>",
                                cierreCofinanciados ? "-1" : "0", ""};
        
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_CIE.FC_CIERREPRESUPUESTOCB",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException | ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String validarCierrePlan(
        String compania,
        int anoCierre,
        boolean cierreNormal,
        boolean cierrePasivo,
        boolean cierreVigFuturas,
        boolean cierreVigFutPasivo,
        boolean cierreRegalias,
        boolean cierreCofinanciados)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOACIERRE        =>",
                                Integer.toString(anoCierre), ", ",
                                "UN_CIERRENORMAL      =>",
                                cierreNormal ? "-1" : "0", ", ",
                                "UN_CIERREPASIVO      =>",
                                cierrePasivo ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTURAS  =>",
                                cierreVigFuturas ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTUAPASIVO =>",
                                cierreVigFutPasivo ? "-1" : "0", ", ",
                                "UN_CIERREREGALIAS    =>",
                                cierreRegalias ? "-1" : "0", ", ",
                                "UN_CIERRECOFINANCIADOS    =>",
                                cierreCofinanciados ? "-1" : "0", ""
        };
        try {
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_CIE.FC_VALIDARCIERREPLAN",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String eliminarCierrePlan(
        String compania,
        int anoCierre,
        String usuario,
        boolean cierreNormal,
        boolean cierrePasivo,
        boolean cierreVigFuturas,
        boolean cierreVigFutPasivo,
        boolean cierreRegalias,
        boolean cierreCofinanciados)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOACIERRE        =>",
                                Integer.toString(anoCierre), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CIERRENORMAL      =>",
                                cierreNormal ? "-1" : "0", ", ",
                                "UN_CIERREPASIVO      =>",
                                cierrePasivo ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTURAS  =>",
                                cierreVigFuturas ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTUAPASIVO =>",
                                cierreVigFutPasivo ? "-1" : "0", ", ",
                                "UN_CIERREREGALIAS    =>",
                                cierreRegalias ? "-1" : "0", ", ",
                                "UN_CIERRECOFINANCIADOS    =>",
                                cierreCofinanciados ? "-1" : "0", ""
        };
        try {
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_CIE.FC_ELIMINARCIERREPRESUPUESTO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void crearTipoDefectoCierre(String compania, String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "' ",
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO_CIE.PR_CREAR_TIPOSDEFECTO",
                        SysmanFunciones.concatenar(parametros));

    }
    
    @Override
    public String validarReversarCierre(
        String compania,
        int anoCierre,
        boolean cierreNormal,
        boolean cierrePasivo,
        boolean cierreVigFuturas,
        boolean cierreVigFutPasivo,
        boolean cierreRegalias,
        boolean cierreCofinanciados)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOACIERRE        =>",
                                Integer.toString(anoCierre), ", ",
                                "UN_CIERRENORMAL      =>",
                                cierreNormal ? "-1" : "0", ", ",
                                "UN_CIERREPASIVO      =>",
                                cierrePasivo ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTURAS  =>",
                                cierreVigFuturas ? "-1" : "0", ", ",
                                "UN_CIERREVIGFUTUAPASIVO =>",
                                cierreVigFutPasivo ? "-1" : "0", ", ",
                                "UN_CIERREREGALIAS    =>",
                                cierreRegalias ? "-1" : "0", ", ",
                                "UN_CIERRECOFINANCIADOS    =>",
                                cierreCofinanciados ? "-1" : "0", ""
        };
        try {
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_CIE.FC_VALIDARREVERSARCIE",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
}
