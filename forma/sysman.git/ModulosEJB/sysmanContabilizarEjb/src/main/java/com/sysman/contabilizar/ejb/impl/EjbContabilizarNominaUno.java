package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaUnoGeneralRemote;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaUnoLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaUnoRemote;
import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilizarUno
 */
@Stateless
@LocalBean

public class EjbContabilizarNominaUno implements EjbContabilizarNominaUnoRemote, EjbContabilizarNominaUnoLocal
{
    @EJB
    private EjbContabilizarNominaUnoGeneralRemote ejbContabilizarNominaUno;

    /**
     * Default constructor.
     */
    public EjbContabilizarNominaUno()
    {
    }

    @Override
    public String contabilizarRetenciones(
        String companiaDs,
        String companiaNomina,
        int proceso,
        int ano,
        int mes,
        String centroCostoUnico,
        Date fechaInter,
        String usuario)
                    throws SystemException
    {

        return ejbContabilizarNominaUno.contabilizarRetenciones(companiaDs, companiaNomina, proceso, ano, mes, centroCostoUnico, fechaInter,
                        usuario);
    }

    @Override
    public String contabilizarCuentasporpagar(
        String companiaNomina,
        String companiaDs,
        int proceso,
        int ano,
        int mes,
        Date fechaInter,
        String usuario)
                    throws SystemException
    {
        return ejbContabilizarNominaUno.contabilizarCuentasporpagar(companiaNomina, companiaDs, proceso, ano, mes, fechaInter, usuario);
    }
}