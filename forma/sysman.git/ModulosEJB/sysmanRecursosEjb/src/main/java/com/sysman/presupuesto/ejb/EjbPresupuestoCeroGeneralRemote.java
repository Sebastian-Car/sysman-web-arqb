package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPresupuestoCeroGeneralRemote {

    void insertarAuxiliarenPresupuesto(
        String compania,
        int ano,
        String usuario)
                    throws SystemException;

}