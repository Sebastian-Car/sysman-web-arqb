package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbActualizaLocal;
import com.sysman.recursos.ejb.EjbActualizaRemote;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbActualiza
 */
@Stateless
@LocalBean
public class EjbActualiza implements EjbActualizaRemote, EjbActualizaLocal {
    /**
     * Default constructor.
     */
    public EjbActualiza() {
    }

    @Override
    public void crearTabla(
        String tabla,
        String campos,
        String llaves,
        String tablespace)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ACTUALIZA.PR_CREAR_TABLA_CA",
                        "UN_TABLA             =>'" + tabla + "', "
                            + "UN_CAMPOS            =>'" + campos + "', "
                            + "UN_LLAVES            =>'" + llaves + "', "
                            + "UN_TABLESPACE        =>'" + tablespace + "'");
    }

    @Override
    public void crearCampo(
        String tabla,
        String campo,
        String tipoTamano,
        String parametrosNull,
        String parametrosDefault)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ACTUALIZA.PR_CREAR_CAMPO_CA",
                        "UN_TABLA             =>'" + tabla + "', "
                            + "UN_CAMPO             =>'" + campo + "', "
                            + "UN_TIPO_TAMANO       =>'" + tipoTamano + "', "
                            + "UN_PARAMETROS_NULL   =>'" + parametrosNull
                            + "', "
                            + "UN_PARAMETROS_DEFAUL =>'" + parametrosDefault
                            + "'");
    }

    @Override
    public void crearValorDefecto(
        String tabla,
        String campo,
        String valDefault)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ACTUALIZA.PR_CAMPO_DEFAULT",
                        "UN_TABLA             =>'" + tabla + "', "
                            + "UN_CAMPO             =>'" + campo + "', "
                            + "UN_VAL_DEFAULT       =>'" + valDefault + "'");
    }

    @Override
    public void cambiarValorporDefecto(
        String tabla,
        String campo,
        String nuevoestado)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ACTUALIZA.PR_CAMBIAR_NULL",
                        "UN_TABLA             =>'" + tabla + "', "
                            + "UN_CAMPO             =>'" + campo + "', "
                            + "UN_NUEVOESTADO       =>'" + nuevoestado + "'");
    }
}