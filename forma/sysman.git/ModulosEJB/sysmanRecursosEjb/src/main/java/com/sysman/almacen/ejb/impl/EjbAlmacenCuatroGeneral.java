package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbAlmacenCuatroGeneralLocal;
import com.sysman.almacen.ejb.EjbAlmacenCuatroGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class AlmacenCuatroGeneral
 */
@Stateless
@LocalBean
public class EjbAlmacenCuatroGeneral implements EjbAlmacenCuatroGeneralRemote,
                EjbAlmacenCuatroGeneralLocal {
    /**
     * Default constructor.
     */
    public EjbAlmacenCuatroGeneral() {
    }

    @Override
    public int validarRequisicion(
        String compania,
        String dependencia,
        String tercero,
        int ano,
        String sucursal,
        String accion,
        String nuevoValorEstimado,
        String numero)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_DEPENDENCIA       =>'", dependencia, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_ACCION            =>'", accion, "', ",
                                "UN_NUEVO_VALOR_ESTIMADO =>'",
                                nuevoValorEstimado, "', ",
                                "UN_NUMERO            =>'", numero, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_VALIDAR_REQUISICION",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void subirCambioPlaca(
        String compania,
        String cambios,
        String usuario,
        long cambio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CAMBIOS           =>", cambios, ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CAMBIO            =>", Long.toString(cambio)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.PR_LOADFILE_CAMBIOPLACA",
                        SysmanFunciones.concatenar(parametros));
    }
}