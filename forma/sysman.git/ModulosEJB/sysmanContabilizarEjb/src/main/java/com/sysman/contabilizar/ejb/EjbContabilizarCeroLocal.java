/*-
 * EjbContabilizarCeroLocal.java
 *
 * 1.0
 * 
 * 4/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 4/01/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbContabilizarCeroLocal {

    void cargarInterfazAlmacenCC(
        String compania,
        String codigoelemento,
        int ano)
                    throws SystemException;

    String contabilizarPorPlano(
        String compania,
        boolean resumido,
        boolean sinPptal,
        boolean terceroDetalle,
        boolean conciliar,
        String plano,
        String usuario) throws SystemException;

    void cargarInterfazAlmacenCCNIIF(
        String compania,
        String codigoelemento,
        int ano) throws SystemException;

    void prepararContabilizacionSiguienteAnio(
        String compania,
        int anioInicial,
        int anoFinal,
        String usuario) throws SystemException;

    String contabilizarPorPlanoEsp(String compania, boolean resumido,
        boolean sinPptal, boolean terceroDetalle, boolean conciliar,
        String plano, String usuario) throws SystemException;



	String cargarInterfazporXls(String compania, 
			String tipoCpte, 
			String numero, 
			String cadena, 
			int colfecha,
			int colidcontable, 
			int colvalor, 
			int colValorCredito,
			int coldetalle, 
			int coltexto, 
			int colbase, 
			int colcontrato,
			int coldocumento, 
			int coltercero, 
			int colsucursal, 
			int colcentrocosto, 
			int fuenteRecursos,
			int referencia,
			int colauxiliar, 
			int coltipocontrato,
			boolean retenciones, 
			boolean tercero, 
			boolean agrupado, 
			boolean aplicaaux, 
			int modulo, 
			int filaini,
			String auxContra, 
			String centroContra,
			String fuenteRContra,
			String referenciaContra,
			String descripcion, 
			String usuario) throws SystemException;

	String cargarProcesoJudicialporXls(
			String compania, 
			String anio, 
			String tipoCpte, 
			String numero, 
			String cadena,
			int colfecha, 
			int colidcontable, 
			int coldetalle, 
			int coltexto, 
			int colbase, 
			int colcontrato,
			int coldocumento, 
			int coltercero, 
			int colsucursal, 
			int colcentrocosto, 
			int colfuenter, 
			int colreferencia,
			int colauxiliar, 
			int coltipocontrato, 
			int coltipopagoSia, 
			int colcodigoSia, 
			int colvalorDebito,
			int colvalorCredito, 
			int colnroProceso, 
			boolean tercero, 
			boolean agrupado, 
			boolean aplicaaux, 
			int modulo,
			int filaini, 
			String descripcion, 
			String usuario) throws SystemException;

}
