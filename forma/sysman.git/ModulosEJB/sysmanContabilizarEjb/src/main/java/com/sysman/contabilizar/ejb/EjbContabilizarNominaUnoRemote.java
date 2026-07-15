package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContabilizarNominaUnoRemote
{

    String contabilizarRetenciones(
        String companiaDs,
        String companiaNomina,
        int proceso,
        int ano,
        int mes,
        String centroCostoUnico,
        Date fechaInter,
        String usuario) throws SystemException;

    String contabilizarCuentasporpagar(
        String companiaNomina,
        String companiaDs,
        int proceso,
        int ano,
        int mes,
        Date fechaInter,
        String usuario) throws SystemException;

}