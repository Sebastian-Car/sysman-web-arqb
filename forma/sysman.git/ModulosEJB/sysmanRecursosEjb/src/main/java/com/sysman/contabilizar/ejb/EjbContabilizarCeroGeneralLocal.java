/*-
 * EjbContabilizarCeroGeneralLocal.java
 *
 * 1.0
 * 
 * 5/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

/**
 * 
 * 
 * @version 1.0, 5/02/2018
 * @author jrodrigueza
 */
@Local
public interface EjbContabilizarCeroGeneralLocal {

    String contabilizarPorPlano(
        String compania,
        boolean resumido,
        boolean sinPptal,
        boolean terceroDetalle,
        boolean conciliar,
        String plano,
        String usuario) throws SystemException;

    String contabilizarPorPlanoEsp(String compania, boolean resumido,
        boolean sinpptal, boolean tercerodetalle, boolean conciliar,
        String plano, String usuario) throws SystemException;
    
    String contabilizarHNivelesCC(
			String compania, 
			int ano, 
			int mes, 
			Date fechinterf, 
			String tipo, 
			BigDecimal numero,
			boolean niif, 
			String usuario) throws SystemException;

	String contabilizarPlanoSIOT(
			String compania, 
			boolean conciliar, 
			String plano, 
			String usuario) throws SystemException;

}
