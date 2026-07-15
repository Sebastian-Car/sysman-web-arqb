package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbPresupuestoCeroGeneralLocal {

    void insertarAuxiliarenPresupuesto(
        String compania,
        int ano,
        String usuario)
                    throws SystemException;

}