package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosCuatroRemote {
    boolean autorizarFacturaPagada(
        String compania,
        String nit)
                        throws SystemException;

    boolean estarBloqueado(
        String compania,
        int ciclo)
                        throws SystemException;

    boolean autorizarConvenios(
        String compania,
        String nit)
                        throws SystemException;

    boolean permitirAccion(
        String compania,
        String accion,
        String usuario,
        String parametro)
                        throws SystemException;

    String obtenerComentariosPeriodo(
        String compania,
        String comentarios,
        int ano,
        String periodo)
                        throws SystemException;

    String armarConsultaPlanoExp(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal)
                        throws SystemException;

    String enviarPlanosAsobancaria(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String usuario,
        boolean checkath)
                        throws SystemException;

    String enviarFimmCabeza(
        String compania,
        int ciclo,
        int strdiasafacturar,
        int diasdesde,
        int diashasta,
        String codigoinicial,
        String codigofinal,
        Date fechaemision,
        String usuario)
                        throws SystemException;

    String enviarFimmCuerpo(
        String compania,
        int ciclo,
        int diasprimervenc,
        String codigoinicial,
        String codigofinal,
        Date fechaemision,
        Date fechavencimiento,
        boolean indemisionfija,
        boolean indvencimientofijo,
        String usuario)
                        throws SystemException;

    String obtenerNombrePeriodo(
        String compania,
        int ano,
        String periodo)
                        throws SystemException;

    String enviarFimmSoloLecturaCabeza(
        String compania,
        int ciclo,
        int strdiasafacturar,
        int diasdesde,
        int diashasta,
        String codigoinicial,
        String codigofinal)
                        throws SystemException;

    String enviarFimmSoloLecturaCuerpo(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal)
                        throws SystemException;

    void registrarProceso(
        String compania,
        String proceso,
        String descripcion,
        String parametros,
        String resultados,
        String estado,
        String usuario,
        Date fecha)
                        throws SystemException;
}
