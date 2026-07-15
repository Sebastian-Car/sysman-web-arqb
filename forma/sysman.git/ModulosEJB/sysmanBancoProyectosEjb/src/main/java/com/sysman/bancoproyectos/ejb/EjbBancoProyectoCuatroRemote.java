package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Remote;

@Remote
public interface EjbBancoProyectoCuatroRemote
{

    String crearCadenaSumatoria(
        String periocidadrta,
        String periocidad,
        String strcampo)
                    throws SystemException;

    String getCambiarPeriodicidad(
        String compania,
        String proyectoinicial,
        String periocidad,
        String periocidadaux,
        String usuario)
                    throws SystemException;

    String armarSumatoria(
        String periocidadrta,
        String periocidad,
        String strcampo)
                    throws SystemException;

    String mayorizarPonderacion(String compania, int vigencia, boolean temp,
        String indicador, boolean generaReporte, String usuario)
                    throws SystemException;

    String subirMGA(
        String compania,
        int ano,
        BigDecimal id,
        BigDecimal validarCod,
        String nombre,
        int modulo,
        String objetivognrl,
        String usuario)
                    throws SystemException;

    String buscarInsertarActividad(
        String compania,
        String nombre,
        String user,
        String fecha,
        String usuario)
                    throws SystemException;

    void ingresarRubros(String compania, String rubro, int vigencia,
        String usuario) throws SystemException;

    String verificarNuevosRubros(String compania, String rubro, int vigencia,
        String usuario) throws SystemException;

    boolean validarRubroInversion(String compania, int vigencia,
        String codrubro, String accion) throws SystemException;

    void eliminarActividades(
        String compania,
        String proyecto,
        String actividad,
        String usuario) throws SystemException;

}