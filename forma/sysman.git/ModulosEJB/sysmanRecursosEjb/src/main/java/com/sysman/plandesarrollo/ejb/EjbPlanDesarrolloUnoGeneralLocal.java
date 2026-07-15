package com.sysman.plandesarrollo.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbPlanDesarrolloUnoGeneralLocal
{

    void generarMantenimientoPlan(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException;
}