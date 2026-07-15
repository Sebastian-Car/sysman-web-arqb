package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Local;

@Local
public interface EjbBancoProyectosCeroGeneralLocal {

    BigDecimal insertarVigencias(
        String compania,
        int anioIni,
        int anioFin,
        String opcion,
        String usuario)
                    throws SystemException;

}