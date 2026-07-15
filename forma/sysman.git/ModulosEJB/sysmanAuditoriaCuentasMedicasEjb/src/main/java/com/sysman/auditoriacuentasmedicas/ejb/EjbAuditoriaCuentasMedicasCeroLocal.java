/*-
 * EjbAuditoriaCuentasMedicasCeroLocal.java
 *
 * 1.0
 * 
 * 18/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.auditoriacuentasmedicas.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbAuditoriaCuentasMedicasCeroLocal {

    void cargarRips(String compania, int consecutivo, String cadenarip, String tiporip,
        String usuario) throws SystemException;
    
    void eliminarRips(String compania, int consecutivo) throws SystemException;
    
    String causacionCuentasMedicas(String compania, 
        String factura, 
        String codigoPrestadorServicio,
        String tipoComprobante,
        int ano,
        Date fecha,
        String codigoInterfaz,
        String usuario,
        String radicado,
        int consecutivo,
        int agrupado) throws SystemException;
    
    void generarProximoAnio(String codigoTransaccion,int ano,int anoDesde, String usuario) throws SystemException;

	void cargarRipsJson(
		String compania,  
		int    consecutivo,
		String clobUsuarios,
		String clobConsultas,
		String clobProcedimientos,
		String clobMedicamentos,
		String clobUrgencias,
		String clobRecienNacidos,
		String clobOtrosServicios,
		String clobHospitalizacion,
		String clobValFactura,
		String clobValFacturaDetalle,
		String clobArchivos,
		String clobTransaccion,
		String usuario) throws SystemException;
	
	String informeFAC120_plano(
            String compania,
            Date fechaInicial,
            Date fechaFinal,
            String claseCuentaInicial,
            String claseCuentaFinal) throws SystemException;

    String informeFT_033_plano(
			String compania, 
			Date fechaInicial,
			Date fechaFinal,
			String claseCuentaInicial,
			String claseCuentaFinal) throws SystemException;
}
