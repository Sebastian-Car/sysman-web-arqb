package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilizarNominaLocal
{

    String contabilizarNomina(
        String compania,
        String companiaNomina,
        int ano,
        int mes,
        int periodo,
        int proceso,
        Date fechaInter,
        String tipoComprobante,
        boolean compporEmpleado,
        boolean compporTercero,
        boolean compporCentroCosto,
        String empleado,
        boolean patronoT,
        boolean empleadoT,
        boolean provisionesT,
        String centroCostoUnico,
        String usuario) throws SystemException;

}