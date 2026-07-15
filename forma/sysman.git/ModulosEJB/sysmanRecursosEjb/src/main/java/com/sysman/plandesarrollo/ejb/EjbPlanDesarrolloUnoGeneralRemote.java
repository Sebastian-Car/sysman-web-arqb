package com.sysman.plandesarrollo.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPlanDesarrolloUnoGeneralRemote
{

    void generarMantenimientoPlan(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException;
}