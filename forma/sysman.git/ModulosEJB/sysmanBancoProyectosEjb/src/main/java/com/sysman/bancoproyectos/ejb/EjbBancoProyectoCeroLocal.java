package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbBancoProyectoCeroLocal {

    BigDecimal insertarVigencias(
        String compania,
        int anioIni,
        int anioFin,
        String opcion,
        String usuario)
                    throws SystemException;

    String actualizarProgramado(
        String compania,
        int vigencia,
        String proyectoini,
        String proyectofin,
        int opcion,
        String usuario)
                    throws SystemException;

    void actualizarDescripcionPlanIndi(
        String compania,
        int vigenciaini,
        String usuario)
                    throws SystemException;

    String anularSolicitudBancoProyecto(
        String compania,
        String tipot,
        String claset,
        long codigo,
        String dependencia,
        int modulo,
        String usuario)
                    throws SystemException;

    String anularSolicitudesBancoProyectoRes(
        Date fecha,
        String compania,
        String tipot,
        int modulo,
        String usuario)
                    throws SystemException;

    boolean crearActividades(String compania, String componente,
        String codigoProyecto, String tipoComponente, String usuario)
                    throws SystemException;

    void insertarComponentes(String compania, String codigo,
        BigDecimal valorTotal, int vigencia, String usuario)
                    throws SystemException;

    void eliminarComponentes(String compania, String codigo,
        BigDecimal valorTotal, int vigencia, String usuario)
                    throws SystemException;

    void armonizarPd(
        String compania,
        int vigencia) throws SystemException;
}