/*-
 * FrminformehislaboralControlador.java
 *
 * 1.0
 * 
 * 23/09/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/09/2025
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminformehislaboralControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * variable encargada de alamacenar la cedula de la persona a la que se le
	 * quiere imprimir la historia laboral
	 */

	private String cedula;
	/**
	 * variable encargada de alamacenar la sucursal de la persona a la que se le
	 * quiere imprimir la historia laboral
	 */
	private String sucursal;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String formato;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminformehislaboralControlador
	 */
	public FrminformehislaboralControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				setCedula((String) parametrosEntrada.get("numeroDcto"));
				setSucursal((String) parametrosEntrada.get("sucursal"));
			} else {
				SessionUtil.redireccionarMenuPermisos();
			}
			numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_HISLABORAL_CONTROLADOR.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
		formato = "1";
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCancelar() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirAceptar() {
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);
	}

	/**
	 * metodo que contiene la logia para generar el reporte en formato pdf
	 *
	 * @param format
	 */
	public void generarInforme(FORMATOS format) {
		try {
			String modulo = SessionUtil.getModulo();
			String reporte = "";
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("cedulaEmpleado", cedula.toString());

			if ("1".equals(formato)) {
				reporte = "002844HistorialLaboralAnexos";
			} else {
				reporte = "002845HistorialLaboralRequisitos";
				reemplazar.put("sucursal", sucursal);
			}
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, format);

		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable formato
	 * 
	 * @return formato
	 */
	public String getFormato() {
		return formato;
	}

	/**
	 * Asigna la variable formato
	 * 
	 * @param formato Variable a asignar en formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
}
