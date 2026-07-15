package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbNominaTresRemote
{
    void incluirConcepto(
        String nombre,
        int codigo,
        int tipoc,
        String unidad,
        String usuario)
                    throws SystemException;

    String nombreConcepto(
        String compania,
        int codigo)
                    throws SystemException;

    BigDecimal deducibleDependientes(
        String compania,
        int idempleado)
                    throws SystemException;

    boolean deduciblePendiente(
        String compania,
        String parametro,
        int idempleado)
                    throws SystemException;

    BigDecimal deducibleValor300(
        String compania,
        int idempleado)
                    throws SystemException;

    BigDecimal miSueldo(
        String compania,
        int idempleado,
        int ano)
                    throws SystemException;

    void actualizarNovedad304(
        String compania,
        int anio,
        String usuario)
                    throws SystemException;

    int mesesLaborados(
        String compania,
        int anio,
        int idEmpleado,
        int concepto)
                    throws SystemException;

    void calcularDiferenciaRetroactivo(
        String compania,
        int idempleado,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    void reteFteRetroActivos(
        String compania,
        int idempleado,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    void netosRetroActivo(
        String compania,
        int proceso,
        int mes,
        int anio,
        int periodo,
        String usuario)
                    throws SystemException;

    void calcRetencion(
        String compania,
        Date fechaCombo2,
        Date ingreso,
        BigDecimal vporcentaje,
        String documento,
        String usuario)
                    throws SystemException;

    BigDecimal deduciblePrepagada(
        String compania,
        int iddeempleado)
                    throws SystemException;

    void cargarParCalCret(
        String compania,
        String usuario)
                    throws SystemException;

    String calcularRtfParUno(String compania,
        int ano,
        int proceso,
        String fechaInicial,
        String fechaFinal,
        boolean cn309,
        int ckAnioHasta,
        String usuario) throws SystemException;

    String calcularRtfParDos(
        String compania,
        String documento,
        BigDecimal promedio,
        Date fechaCombo1,
        Date fechaCombo2,
        BigDecimal imeses,
        BigDecimal promedioc22,
        boolean asignarPorReten,
        String usuario)
                    throws SystemException;

    String calcularRtfParTres(
        String compania,
        int ano,
        String fechaInicial,
        String fechaFinal,
        String usuario) throws SystemException;

    String generarPlanoIncapacidadSiif(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int archivo,
        String salida)
                    throws SystemException;

    String generarPlanoRetefuenteSiif(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int archivo,
        String salida)
                    throws SystemException;

    String generarPlanoBeneficiosSiif(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int archivo,
        String salida)
                    throws SystemException;
    
    
    boolean validarPeriodoActivoNominaH(
			String compania, 
			int proceso, 
			int anio, 
			int mes, 
			String fecha)
						throws SystemException;
}