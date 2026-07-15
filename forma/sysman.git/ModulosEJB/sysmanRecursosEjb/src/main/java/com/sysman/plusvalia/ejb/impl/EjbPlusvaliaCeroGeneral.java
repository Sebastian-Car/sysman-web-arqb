package com.sysman.plusvalia.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroGeneralLocal;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PlusvaliaCeroGeneral
 */

@Stateless
@LocalBean
public class EjbPlusvaliaCeroGeneral implements EjbPlusvaliaCeroGeneralRemote,
                EjbPlusvaliaCeroGeneralLocal {

    @Override
    public BigDecimal prorrateoGeneral(
        long acuerdo,
        int cuotaFinal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_ACUERDO           =>",
                                Long.toString(acuerdo), ", ",
                                "UN_CUOTA_FINAL       =>",
                                Integer.toString(cuotaFinal), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ACUERDO_GEN.FC_FACTURARCUOTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void insertConfigurarPago(
        String compania,
        Date fecha,
        String banco,
        String paquete,
        int ncupones,
        String valorRep,
        String referencia,
        int aplicacion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'),",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_NCUPONES          =>",
                                    Integer.toString(ncupones), ", ",
                                    "UN_VALOR_REP         =>'", valorRep, "', ",
                                    "UN_REFERENCIA        =>'", referencia,
                                    "', ", "UN_APLICACION        =>",
                                    Integer.toString(aplicacion), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PLUSVALIA_CAL.PR_CONFIGURAR_PAGO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }

    }

}
