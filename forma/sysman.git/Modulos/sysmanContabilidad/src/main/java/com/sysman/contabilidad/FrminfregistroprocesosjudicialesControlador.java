/*-
 * FrminfregistroprocesosjudicialesControlador.java
 *
 * 1.0
 * 
 * 28/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/05/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminfregistroprocesosjudicialesControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Atributo que maneja el filtro del check Mostrar solo registros con saldo:
	 */
	 private boolean saldoValorActual;
	 
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Crea una nueva instancia de FrminfregistroprocesosjudicialesControlador
	 */
	public FrminfregistroprocesosjudicialesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INF_REGISTRO_PROCESOS_JUDICIALES.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {

		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		archivoDescarga = null;
		String reporte = "800627REGISTRO_PROCESOS_JUDICIALES";
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("compania", compania);
		
		if (saldoValorActual) {
		    reemplazar.put("condicionValorActual", "AND PJ.VALOR_ACTUAL > 0");
		} else {
		    reemplazar.put("condicionValorActual", "AND PJ.VALOR_ACTUAL <= 0");
		}
		try {
			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.EXCEL, reporte);

		} catch (JRException | IOException | SQLException | DRException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	
	
	/**
	 * @return the saldoValorActual
	 */
	public boolean isSaldoValorActual() {
		return saldoValorActual;
	}

	/**
	 * @param saldoValorActual the saldoValorActual to set
	 */
	public void setSaldoValorActual(boolean saldoValorActual) {
		this.saldoValorActual = saldoValorActual;
	}
}
