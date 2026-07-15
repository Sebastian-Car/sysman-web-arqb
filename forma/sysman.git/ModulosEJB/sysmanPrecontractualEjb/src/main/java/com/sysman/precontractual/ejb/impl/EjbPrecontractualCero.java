package com.sysman.precontractual.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.ejb.EjbPrecontractualCeroLocal;
import com.sysman.precontractual.ejb.EjbPrecontractualCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PrecontractualCero
 */
@Stateless
@LocalBean
public class EjbPrecontractualCero implements EjbPrecontractualCeroRemote,
                EjbPrecontractualCeroLocal {
    /**
     * Default constructor.
     */
    public EjbPrecontractualCero() {
    }

    @Override
    public void actualizarVariablesPrponentes(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_USUARIO   =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL.PR_VARIABLESPROPONENTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarVariables(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_USUARIO  =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL.PR_ACTUALIZARVARIABLES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String actualizarTransaccion(
        String compania,
        String tipocontrato,
        long consecutivo,
        String estado,
        String observacion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_TIPOCONTRATO    =>'", tipocontrato, "', ",
                                "UN_CONSECUTIVO       =>",
                                Long.toString(consecutivo), ", ",
                                "UN_ESTADO          =>'", estado, "', ",
                                "UN_OBSERVACION     =>'", observacion, "', ",
                                "UN_USUARIO         =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL.FC_ACTUALIZARTRANSACCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarEtapas(
        String compania,
        String tipocontrato,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_TIPOCONTRATO   =>'", tipocontrato, "', ",
                                "UN_USUARIO        =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL.PR_ACTUALIZARETAPAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public Date adicionarDias(
        String compania,
        Date fechainicial,
        int numdias,
        boolean tipo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                    "UN_FECHAINICIAL     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NUMDIAS           =>",
                                    Integer.toString(numdias), ", ",
                                    "UN_TIPO              =>",
                                    tipo ? "-1" : "0"
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRECONTRACTUAL.FC_FECHACIERRE",
                            SysmanFunciones.concatenar(parametros),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean evaluarFestivo(
        String compania,
        Date fecha)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY')"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRECONTRACTUAL.FC_ESFESTIVO",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarVariables(
        String compania,
        String tipocontrato,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCONTRATO      =>'", tipocontrato, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL.PR_ACTUALIZARVARIABLESPREC",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String convertirVariables(
        String variable)
                    throws SystemException {
        String[] parametros = { "UN_VARIABLE          =>'", variable, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL.FC_CONVERTIR_VARIABLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
}