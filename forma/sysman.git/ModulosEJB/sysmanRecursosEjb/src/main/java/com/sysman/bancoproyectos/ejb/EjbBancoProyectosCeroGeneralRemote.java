package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Remote;

@Remote
public interface EjbBancoProyectosCeroGeneralRemote {

    BigDecimal insertarVigencias(
        String compania,
        int anioIni,
        int anioFin,
        String opcion,
        String usuario)
                    throws SystemException;

}