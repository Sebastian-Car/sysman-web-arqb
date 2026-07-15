package com.sysman.workflow.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Remote;

@Remote
public interface EjbWorkflowCeroRemote
{

    void cerrarTramite(
        String compania,
        String proceso,
        String tipoTramite,
        BigInteger tramite,
        String nodoActual,
        String usuario)
                    throws SystemException;

    void prepararVariablesTramite(
        String compania,
        String proceso,
        String tipoTramite,
        BigInteger tramite,
        String nodo,
        BigInteger dTramite,
        String usuario) throws SystemException;

    void tramitar(
        String compania,
        String proceso,
        String tipoTramite,
        BigInteger numero,
        String nodoOrigen,
        String nodoDestino,
        boolean tramiteIni,
        String usuarioDestino,
        String archivocentral,
        String usuario) throws SystemException;

    void validarInfDetallada(
        String compania,
        String proceso,
        String tipotramite,
        BigInteger tramite,
        BigInteger dtramite,
        String nodo) throws SystemException;

    void eliminarTramite(
        String compania,
        String proceso,
        String tipotramite,
        BigInteger tramite) throws SystemException;

    void cambiarEjecutor(
        String compania,
        String proceso,
        String tipoTramite,
        BigInteger tramite,
        BigInteger dTramite,
        String usuarioInterno,
        String usuario) throws SystemException;

    void proyectarTramite(
        String compania,
        String tipoTramite,
        String proceso,
        BigInteger tramite,
        String nodoOrigen,
        int diasReprog,
        String usuario) throws SystemException;
    
    int tramitarAProceso(
            String compania,
            String proceso,
            String tipoTramite,
            BigInteger numero,
            BigInteger consecutivo,
            String nodoOrigen,
            String nodoDestino,
            String usuario,
            boolean proyecciones,
            String nombre,
            String direccion,
            String email,
            String anexo1,
            String anexo2,
            String anexo3,
            String descripcion,
            String tipoTramiteUsuario
            )throws SystemException;
    
    BigInteger workflow_pqrs(
            String compania,
            String proceso,
            String tipoTramite,
            BigInteger numero,
            BigInteger consecutivo,
            String nodo,
            String identificacion,
            String usuario,
            boolean proyecciones,
            String nombre,
            String direccion,
            String email,
            String anexo1,
            String anexo2,
            String anexo3,
            String descripcion,
            String tipoTramiteUsuario
            )throws SystemException;

}