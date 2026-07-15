/*-
 * EjbPlaneacionUnoRemote.java
 *
 * 1.0
 * 
 * 7/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.planeacion.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ejb.Remote;

@Remote
public interface EjbPlaneacionCeroRemote {

    boolean actualizarPlanCompras(
        String compania,
        int ano,
        String usuario)
                    throws SystemException;

    void registrarActualizacionPlanAdquisiciones(String compania,
        boolean realizada, BigInteger actualizacion, int ano, String usuario)
                    throws SystemException;

    BigDecimal calcularValorEjecutado(String compania, int anio, String codigo)
                    throws SystemException;

    BigDecimal traerUltimoValorElemento(String compania, int anio,
        String elemento) throws SystemException;

    BigDecimal calcularValorProgramado(String compania, int anio, String rubro, 
    		String fuenteRecurso, 
    		String referencia, 
    		String centroCosto, 
    		String auxiliar)
                    throws SystemException;

    boolean tieneDetallesPlanCompras(String compania, int anio, String rubro,
        String dependencia) throws SystemException;
}
