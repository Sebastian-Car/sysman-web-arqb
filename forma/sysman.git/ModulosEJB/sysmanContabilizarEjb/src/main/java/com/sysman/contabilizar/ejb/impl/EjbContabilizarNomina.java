package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaGeneralRemote;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaRemote;
import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilizarNomina
 */
@Stateless
@LocalBean

public class EjbContabilizarNomina implements EjbContabilizarNominaRemote, EjbContabilizarNominaLocal
{

    @EJB
    private EjbContabilizarNominaGeneralRemote ejbContabilizarNominaGeneral;

    /**
     * Default constructor.
     */
    public EjbContabilizarNomina()
    {
    }

    @Override
    public String contabilizarNomina(
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
        String usuario)
                    throws SystemException
    {
        return ejbContabilizarNominaGeneral.contabilizarNomina(compania, companiaNomina, ano, mes, periodo, proceso, fechaInter,
                        tipoComprobante, compporEmpleado, compporTercero, compporCentroCosto, empleado, patronoT, empleadoT, provisionesT,
                        centroCostoUnico, usuario);

    }

}