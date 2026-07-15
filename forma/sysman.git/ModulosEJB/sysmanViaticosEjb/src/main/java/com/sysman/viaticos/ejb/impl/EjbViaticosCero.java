package com.sysman.viaticos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.ejb.EjbViaticosCeroLocal;
import com.sysman.viaticos.ejb.EjbViaticosCeroRemote;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ViaticosCero
 */
@Stateless
@LocalBean
public class EjbViaticosCero
                implements EjbViaticosCeroRemote, EjbViaticosCeroLocal {
    /**
     * Default constructor.
     */
    public EjbViaticosCero() {
    }

    @Override
    public void crearDetalleLegalizaViaticos(
        String compania,
        int ano,
        String tercero,
        String codSolicitud,
        String numero,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_CODSOLICITUD      =>'", codSolicitud, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_VIATICOS.PR_CREARDETALLELEGVIATICOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal retornarTotalViaticos(
        String compania,
        int ano,
        String codPersonal,
        BigInteger codigoSolicitud,
        String funcionario,
        String concepto,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODPERSONAL       =>'", codPersonal, "', ",
                                "UN_CODIGOSOLICITUD   =>",
                                codigoSolicitud.toString(), ", ",
                                "UN_FUNCIONARIO       =>'", funcionario, "', ",
                                "UN_CONCEPTO          =>", concepto.toString(),
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_VIATICOS.FC_CALCULARTOTALVIATICOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void actualizarDetalleLegalizaViaticos(
        String compania,
        int ano,
        String numero,
        String numeroafectado,
        String concepto,
        String usuario,
        BigDecimal valor,
        BigDecimal valorAnterior)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_NUMEROAFECTADO  =>'", numeroafectado,
                                "', ", "UN_CONCEPTO          =>'", concepto,
                                "', ", "UN_USUARIO           =>'", usuario,
                                "', ", "UN_VALOR             =>",
                                valor.toString(), ", ",
                                "UN_VALOR_ANTERIOR  =>",
                                valorAnterior.toString()
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_VIATICOS.PR_ACTUALIZARDETALLE",
                        SysmanFunciones.concatenar(parametros));
    }
}