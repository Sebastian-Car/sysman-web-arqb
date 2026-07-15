package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Local;

@Local
public interface EjbBancoProyectoDosLocal {

    String actualizarProyectoMante(
        String compania,
        String proyectoInicial,
        String proyectoFinal,
        String usuario)
                    throws SystemException;

    BigDecimal getCantidadPorActividad(
        String compania,
        int vigencia,
        String proyecto,
        String componente,
        String tipoComponente,
        String actividad)
                    throws SystemException;

    BigDecimal getCantidadDisponible(
        String compania,
        String estado,
        int vigencia,
        String proyecto,
        String componente,
        String tipoComponente,
        String actividad,
        int valNuevo,
        int valViejo)
                    throws SystemException;

    boolean actualizarProgramacion(
        String compania,
        String tipo,
        String clase,
        long novedad,
        String dependencia,
        int vigencia,
        String proyecto,
        int actPlanind,
        String tipoNovedad,
        String usuario)
                    throws SystemException;

    boolean actualizarProgramado(
        String compania,
        String proyectoInicial,
        String proyectoFinal,
        String usuario)
                    throws SystemException;

    int insertaInconsistenciaNovedad(
        String compania,
        String tipo,
        String clase,
        long novedad,
        String dependencia,
        long rscodigo,
        String rsproyecto,
        String rscomponentein,
        String rstipocomponentein,
        String rsactividadin,
        int cantidad,
        String inconsistencia,
        int rsvigencia,
        String estado,
        String usuario)
                    throws SystemException;

    int actualizaIndicadoresNovedad(
        String compania,
        String tipo,
        String clase,
        int novedad,
        String dependencia,
        long rscodigo,
        String rsproyecto,
        String usuario)
                    throws SystemException;

    String actulizaTotalesProyecto(
        String compania,
        String proyectoInicial,
        String proyectoFinal,
        int actTotal,
        String usuario)
                    throws SystemException;

    String validarProcesos(String compania, int proceso, String proyectoInicial,
        String proyectoFinal, boolean actTotal, String usuario, int modulo)
                    throws SystemException;

    void verificarSaldoProyecto(String compania, String proyecto,
        BigDecimal valor, BigDecimal valorAnt, BigDecimal valorproyecto)
                    throws SystemException;

    void validarVOBO(
        String compania,
        String tipot,
        String claset,
        String novedad,
        String dependencia,
        String usuario)
                    throws SystemException;

}