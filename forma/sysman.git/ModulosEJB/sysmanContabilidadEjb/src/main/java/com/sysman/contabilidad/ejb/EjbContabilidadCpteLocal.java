/*-
 * EjbContabilidadCpteLocal.java
 *
 * 1.0
 * 
 * 4/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilidadCpteLocal {

    boolean validarEquivPptales(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException;

    boolean prepararNiif(
        String compania,
        int anio,
        int modulo,
        String tipomovimiento,
        BigInteger numero,
        String usuario)
                    throws SystemException;

    boolean contabilizarNiif(
        String compania,
        int modulo,
        Date fechacreacion,
        Date fecha,
        Date fechavncdoc,
        Date fechapagadodogn,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        String descripcion,
        String texto,
        String tercero,
        String sucursal,
        BigDecimal vlrdocumento,
        String codusuario,
        BigDecimal vlrbase,
        BigDecimal vlrbaseiva,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal vlragirar,
        BigDecimal debitosafectados,
        BigDecimal creditosafectados,
        double porciva,
        String centroCosto,
        String auxiliar,
        String fuenteRecurso,
        String referencia)
                    throws SystemException;

    boolean validarPacEgreso(
        String compania,
        int modulo,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        String clase,
        BigDecimal vlrdocumento,
        BigDecimal vlragirar,
        boolean indimpresion)
                    throws SystemException;

    boolean actualizarNroDocDetalleCnt(
        String compania,
        int modulo,
        String nrodocumento,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        BigDecimal debitosafectados,
        BigDecimal creditosafectados,
        String usuario)
                    throws SystemException;

    boolean borrarDetComproNiif(
        String compania,
        int modulo,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException;

    String verificarCompACopiar(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException;

    boolean verificarTieneDetalle(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero)
                    throws SystemException;

    void actualizarValorDocNoCero(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        BigDecimal vlrbase,
        BigDecimal valorbase,
        BigDecimal vlrbaseiva,
        BigDecimal valoriva,
        String usuario)
                    throws SystemException;

    void actualizarTerceroDet(
        String compania,
        int anio,
        String tipomovimiento,
        BigInteger numero,
        String terceroini,
        String tercerofin,
        String sucursalini,
        String sucursalfin,
        String usuario)
                    throws SystemException;

    void insertarComprobanteCNTRet(
        String compania,
        int anio,
        int modulo,
        String tipomovimiento,
        BigInteger numero,
        BigDecimal vlrbaseiva,
        BigDecimal vlrbase,
        String tipocobro,
        String conceptosf,
        String usuario)
                    throws SystemException;

    BigInteger enumerarComprobanteCnt(String compania, int anio, String tipo,
        BigInteger numero, String centroCosto)
                    throws SystemException;
}