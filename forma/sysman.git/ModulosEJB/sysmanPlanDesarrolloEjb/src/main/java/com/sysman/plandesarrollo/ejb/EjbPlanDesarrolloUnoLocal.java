package com.sysman.plandesarrollo.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbPlanDesarrolloUnoLocal
{

    String actualizarPlanIndicativo(
        String compania,
        int vigencia,
        String tipot,
        long numerot,
        String nombredependencia,
        int accion,
        String usuario)
                    throws SystemException;

    void calcularAvanceIndicadores(
        String compania,
        int vigencia,
        String tipot,
        BigInteger numerot,
        String usuario) throws SystemException;

    void generarMantenimientoPlan(
        String compania,
        int vigencia,
        String usuario) throws SystemException;
}