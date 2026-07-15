package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbBancoProyectoTresRemote {

    String calculcarSegumientoPlanIndicativo(String compania,
        int vigenciaGubernamental, int informe, String vigencia,
        int cantidadNiveles) throws SystemException;

    String crearAuxiliarDesdeProyecto(
        String compania,
        String usuario)
                    throws SystemException;

    String eliminarProyecto(
        String compania,
        String proyecto,
        String usuario)
                    throws SystemException;

    boolean generarPredecesorIndicativo(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException;

    String nombrePeriodicidadBancoProy(
        int periocidad,
        int periodo)
                    throws SystemException;

    String prepararDatos(String compania, String vigenciaInicial,
        String proyectoInicial, String proyectoFinal) throws SystemException;

    String prepararDatosSCV17(String compania, int vigencia, Date fechaInicio,
        Date fechaFin, String fuenteInicio, String fuenteFin,
        boolean todasFuentes)
                    throws SystemException;

    String prepararDatosSCV18(String compania, int vigencia, Date fechaIni,
        Date fechaFin, String fuenteIni, String fuenteFin, int fuente)
                    throws SystemException;
}