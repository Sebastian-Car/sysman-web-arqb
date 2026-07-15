package com.sysman.chipfut.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbChipFutUnoLocal {

    String generarArchivoPlanoIngresosRegaliasFut(
        String compania,
        int ano,
        String codigoentidad,
        int mes,
        int trimestre,
        boolean miles,
        boolean excel,
        int opcion) throws SystemException;

    String generarPlanoReservasPptales(
        String compania,
        String codentidad,
        int trimestre,
        int ano,
        int tipoActoAdmtivo,
        int nroActoAdmtivo,
        Date fechaDocumento,
        boolean pesos,
        boolean separadas,
        boolean excel) throws SystemException;

    String verificarConfiguracion(
        String compania,
        int ano) throws SystemException;

    void subirSeguimientoReciprocas(
        String compania,
        String cambios,
        String usuario,
        String consecutivo) throws SystemException;

    String validarCuentas(
        String compania,
        int ano,
        String cuentaExcel) throws SystemException;

    String enviarFormatoEspecial(
        String compania,
        int ano,
        int trimestre,
        String cuentasExcel)
                    throws SystemException;

    String cuentasExistentes(
        String compania, 
        int ano, 
        String cuentaexcel) throws SystemException;
    
    String generarProcesoSiaSql(
    		String  strsql,
    		int  carac_esp)
                        throws SystemException;

}