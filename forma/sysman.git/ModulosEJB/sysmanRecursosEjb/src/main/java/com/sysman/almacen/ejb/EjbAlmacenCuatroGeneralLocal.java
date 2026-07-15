package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbAlmacenCuatroGeneralLocal
{

    int validarRequisicion(
        String compania,
        String dependencia,
        String tercero,
        int ano,
        String sucursal,
        String accion,
        String nuevoValorEstimado,
        String numero)
                    throws SystemException;

    void subirCambioPlaca(
        String compania,
        String cambios,
        String usuario,
        long cambio) throws SystemException;
}