package com.sysman.recursos.ejb;

import javax.ejb.Local;

import com.sysman.exception.SystemException;

@Local
public interface EjbActualizaLocal {

    void crearTabla(
        String tabla,
        String campos,
        String llaves,
        String tablespace)
                        throws SystemException;

    void crearCampo(
        String tabla,
        String campo,
        String tipoTamano,
        String parametrosNull,
        String parametrosDefault)
                        throws SystemException;

    void crearValorDefecto(
        String tabla,
        String campo,
        String valDefault)
                        throws SystemException;

    void cambiarValorporDefecto(
        String tabla,
        String campo,
        String nuevoestado)
                        throws SystemException;
}