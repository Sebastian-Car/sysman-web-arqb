package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPresupuestoUnoLocal {

    boolean verificarIndicadoresDeMovimientoPresupuestales(
        String compania,
        int anio,
        String codigo)
                        throws SystemException;

    void insertarSaldosAuxiliaresPresupuestales(
        String compania,
        int anio,
        String codigo,
        String centro,
        String tercero,
        String sucursal,
        String auxiliar,
        String referencia,
        String fuenterecurso,
        String naturaleza,
        boolean indDepurados)
                        throws SystemException;

    void prepararActualizacionPresupuesto(
        String tipoCpte,
        String compania,
        Date fecha,
        String codigo,
        String centro,
        String tercero,
        String sucursal,
        String auxiliar,
        String referencia,
        String fuente,
        String naturaleza,
        BigDecimal debitoAnt,
        BigDecimal creditoAnt,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal diferencia,
        BigDecimal diferenciaant,
        String tipo,
        String tipoingreso)
                        throws SystemException;

    void actualizarAuxiliaresContables(
        String clase,
        String compania,
        int anio,
        String codigo,
        String tercero,
        String sucursal,
        String auxiliar,
        String centro,
        String referencia,
        String fuenterecurso,
        int mes,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal debitoAnt,
        BigDecimal creditoAnt,
        String naturaleza,
        BigDecimal diferencia,
        BigDecimal diferenciaant,
        String tipo,
        String tipoingreso,
        boolean indDepurados)
                        throws SystemException;

    void crearComprobanteDeModificacionPresupuestal(
        String clase,
        String compania,
        int modulo,
        int anio,
        int mes,
        String codigo,
        BigDecimal debitoant,
        BigDecimal creditoant,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal diferencia,
        BigDecimal diferenciaant,
        String tipo,
        String tipoingreso,
        int aniocpte,
        String tipocpte,
        BigInteger nrocpte,
        BigDecimal debitocpte,
        BigDecimal creditocpte,
        String ctacpte,
        BigDecimal csccpte)
                        throws SystemException;

    String seleccionarDocumentoAfectar(
        String compania,
        int anoh,
        String tipoh,
        String tipoc,
        BigDecimal numeroh,
        BigDecimal numeroc,
        String claseh,
        String clasec,
        Date fechaCpte,
        Date fechaAux,
        String usuario)
                        throws SystemException;

    void actualizarValorDocumento(
        String compania,
        int ano,
        String tipomovimiento,
        BigInteger movimiento)
                        throws SystemException;

    void insertarSaldosPlanPresupuestal(
        String compania,
        int anio,
        String codigo)
                        throws SystemException;
}