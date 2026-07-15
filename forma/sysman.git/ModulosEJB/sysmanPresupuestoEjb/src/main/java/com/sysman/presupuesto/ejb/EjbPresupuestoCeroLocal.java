package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPresupuestoCeroLocal
{

    boolean proyectarPresupuestoSiguienteVigencia(
        String compania,
        int anofuente,
        int anodestino,
        BigDecimal inc,
        boolean sa,
        boolean regalias,
        boolean soloregalias)
                    throws SystemException;

    boolean verificarPeriodoPresupuestal(
        String compania,
        int ano,
        int mes)
                    throws SystemException;

    void mayorizarRubrosPresupuestales(
        String compania,
        int ano,
        int mesinicial,
        int mesfinal)
                    throws SystemException;

    boolean revisarAfectacionesPresupuestales(
        String compania,
        int ano,
        String usuario)
                    throws SystemException;

    String consultarPredecesorPresupuestal(
        String compania,
        int anio,
        String codigo)
                    throws SystemException;

    String consultarConsecutivosPresupuestalesFaltantes(
        String tipo,
        String compania,
        Date fechainicial,
        Date fechafinal)
                    throws SystemException;

    String contabilizarApropiacionesIniciales(
        String compania,
        int anio)
                    throws SystemException;

    long insertarSaldosPresupuestalesIniciales(
        String compania,
        int anio)
                    throws SystemException;

    long actualizarBancoDeProyectos(
        String compania,
        int ano,
        String tipo,
        BigInteger comprobante)
                    throws SystemException;

    void eliminarComprobantePresupuestal(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        int mes,
        int dia,
        BigDecimal afectaciones,
        String usuario,
        boolean impreso)
                    throws SystemException;

    void validarDisponible(
        String compania,
        int anio,
        String codigo,
        BigDecimal apropiacioninicial)
                    throws SystemException;

    void insertarAuxiliarenPresupuesto(
        String compania, int ano, String usuario)
                    throws SystemException;

    void limpiarSaldoPPtal(
        String compania,
        int anio,
        String codigoinicial,
        String codigofinal)
                    throws SystemException;

    void contabilizarComprobantePptal(
        String compania,
        int anio,
        String tipoCpte,
        BigInteger comprobante,
        String usuario) throws SystemException;

}