/*-
 * ImprimirSaludOcupacionalControlador.java
 *
 * 1.0
 * 
 * 06/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.ImprimirSaludOcupacionalControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion del formulario access ImprimirSaludOcupacional a web controlador
 * ImprimirSaludOcupacionalControlador forma imprimirsaludocupacional.xhtml
 * creacion de menu para abrir el formulario modal, creacion de properties para
 * el formulario modal, asi como generacion de los informes Historia_Clinica y
 * Accidentes_Trabajo a partir de un boton.
 *
 *
 * @version 1.0, 06/02/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class ImprimirSaludOcupacionalControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * DOCUMENTACION NECESARIA Variable Utilizada parara obtener la opcion
	 * seleccionada en el check del formulario
	 */
	private String opcionInforme;
	/**
	 * DOCUMENTACION NECESARIA Codigo del empleado obtenido a partir del combo
	 * al momento de seleccionar un registro
	 */
	private String numeroDocumentoEmpleado;

	/**
	 * DOCUMENTACION NECESARIA Nombre del empleado obtenido a partir del combo
	 * al momento de seleccionar un registro
	 */
	private String nombreEmpleado;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * DOCUMENTACION NECESARIA
	 * 
	 * Lista los Empleados en el combo.
	 */
	private RegistroDataModelImpl listaCodigoEmpleado;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ImprimirSaludOcupacionalControlador
	 */
	public ImprimirSaludOcupacionalControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			// 1704
			numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_SALUD_OCUPACIONAL_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCodigoEmpleado();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR1704-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
		 * DoCmd.Restore End Sub
		 */
		opcionInforme = "1";
		// </CODIGO_DESARROLLADO>
	}

	// <MES_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCodigoEmpleado
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoEmpleado() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ImprimirSaludOcupacionalControladorUrlEnum.URL_162.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCodigoEmpleado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params, true,
				GeneralParameterEnum.NUMERO_DCTO.getName());

	}

	// </MES_CARGAR_LISTA>
	// <MES_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPresentar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);

		// </CODIGO_DESARROLLADO>
	}

	public void generarInforme(ReportesBean.FORMATOS formato) {

		Map<String, Object> remplazar = new HashMap<>();
		remplazar.put("noDocumentoEmpleado", numeroDocumentoEmpleado);

		Map<String, Object> params = new HashMap<>();

		params.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());

		if ("1".equals(opcionInforme)) {
			Reporteador.resuelveConsulta("001693HistoriaClinica", Integer.parseInt(SessionUtil.getModulo()), remplazar,
					params);
			try {
				archivoDescarga = JsfUtil.exportarStreamed("001693HistoriaClinica", params, ConectorPool.ESQUEMA_SYSMAN,
						formato);
			} catch (JRException | IOException | SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} else if ("2".equals(opcionInforme)) {

			// Seguridad Industrial

			Reporteador.resuelveConsulta("001695AccidentesTrabajo", Integer.parseInt(SessionUtil.getModulo()),
					remplazar, params);

			try {
				archivoDescarga = JsfUtil.exportarStreamed("001695AccidentesTrabajo", params,
						ConectorPool.ESQUEMA_SYSMAN, formato);
			} catch (JRException | IOException | SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	// </MES_BOTONES>
	// <MES_CAMBIAR>
	// </MES_CAMBIAR>
	// <MES_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoEmpleado
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoEmpleado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numeroDocumentoEmpleado = registroAux.getCampos()
				.get("NUMERO_DCTO"/* GeneralParameterEnum.CODIGO.getName() */).toString();

		nombreEmpleado = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();

	}

	// </MES_COMBOS_GRANDES>
	// <MES_ARBOL>
	// </MES_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcionInforme
	 * 
	 * @return opcionInforme
	 */
	public String getOpcionInforme() {
		return opcionInforme;
	}

	/**
	 * Asigna la variable opcionInforme
	 * 
	 * @param opcionInforme
	 *            Variable a asignar en opcionInforme
	 */
	public void setOpcionInforme(String opcionInforme) {
		this.opcionInforme = opcionInforme;
	}

	public String getNumeroDocumentoEmpleado() {
		return numeroDocumentoEmpleado;
	}

	public void setNumeroDocumentoEmpleado(String numeroDocumentoEmpleado) {
		this.numeroDocumentoEmpleado = numeroDocumentoEmpleado;
	}

	/**
	 * Retorna la variable nombreEmpleado
	 * 
	 * @return nombreEmpleado
	 */
	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	/**
	 * Asigna la variable nombreEmpleado
	 * 
	 * @param nombreEmpleado
	 *            Variable a asignar en nombreEmpleado
	 */
	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaCodigoEmpleado
	 * 
	 * @return listaCodigoEmpleado
	 */
	public RegistroDataModelImpl getListaCodigoEmpleado() {
		return listaCodigoEmpleado;
	}

	/**
	 * Asigna la lista listaCodigoEmpleado
	 * 
	 * @param listaCodigoEmpleado
	 *            Variable a asignar en listaCodigoEmpleado
	 */
	public void setListaCodigoEmpleado(RegistroDataModelImpl listaCodigoEmpleado) {
		this.listaCodigoEmpleado = listaCodigoEmpleado;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
