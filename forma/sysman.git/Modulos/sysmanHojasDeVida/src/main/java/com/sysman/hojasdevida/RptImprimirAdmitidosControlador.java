/*-
 * RptImprimirAdmitidosControlador.java
 *
 * 1.0
 * 
 * 21/12/2017
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.RptImprimirAdmitidosControladorEnum;
import com.sysman.hojasdevida.enums.RptImprimirAdmitidosControladorUrlEnum;
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
 * Clase para imprimir los informes de aspirantes admitidos.
 *
 * @version 1.0, 21/12/2017
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class RptImprimirAdmitidosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el código de la compañía en la cual
	 * inició sesión el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesión correspondiente.
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * El número de la convocatoria seleccionada por el usuario.
	 */
	private String convocatoria;

	/**
	 * El número de la prueba seleccionada por el usuario.
	 */
	private String prueba;

	/**
	 * El número consecutivo determinado por la prueba seleccionada.
	 */
	private String consecutivo;

	private String fechaConvocatoria;

	private String descripcionPrueba;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista.
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Constante a nivel de clase que aloja el código del módulo desde el cual el
	 * usuario inició sesión.
	 */
	private final String modulo = SessionUtil.getModulo();

	/**
	 * Constante a nivel de clase que aloja el nombre de la compañía desde la cual
	 * se inició sesión.
	 */
	private final String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
	/**
	 * Listado de la convocatoria.
	 */
	private RegistroDataModelImpl listaCmbConvocatoria;
	/**
	 * Listado de la prueba.
	 */
	private RegistroDataModelImpl listaCmbPrueba;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de RptImprimirAdmitidosControlador.
	 */
	public RptImprimirAdmitidosControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			/**
			 * Número de formulario: 1536.
			 */
			numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_ADMITIDOS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualización del formulario, como lo son las tablas, origenes de datos,
	 * inicialización de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCmbConvocatoria();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este método es invocado el método inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario.
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista listaCmbConvocatoria.
	 */
	public void cargarListaCmbConvocatoria() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(RptImprimirAdmitidosControladorUrlEnum.URL145.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(RptImprimirAdmitidosControladorEnum.COMPANIA.getValue(), compania);

		listaCmbConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				RptImprimirAdmitidosControladorEnum.NRO_CONVOCATORIA.getValue());
	}

	/**
	 * Carga la lista listaCmbPrueba.
	 */
	public void cargarListaCmbPrueba() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(RptImprimirAdmitidosControladorUrlEnum.URL173.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(RptImprimirAdmitidosControladorEnum.COMPANIA.getValue(), compania);
		param.put(RptImprimirAdmitidosControladorEnum.CONVOCATORIA.getValue(), convocatoria);

		listaCmbPrueba = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				RptImprimirAdmitidosControladorEnum.PRUEBA.getValue());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * Método ejecutado al oprimir el boton GenerarPDF en la vista.
	 */
	public void oprimirGenerarPDF() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado al oprimir el boton GenerarEXCEL en la vista.
	 */
	public void oprimirGenerarExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Genera un reporte con un determinado formato.
	 * 
	 * @param formato
	 *            Tipo de documento a generar.
	 */
	private void generarReporte(FORMATOS formato) {
		Map<String, Object> reemplazar = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();

		String reporte;
		reporte = (Integer.parseInt(consecutivo) > 2) ? "001589ListaAdmitidos" : "001591ListaAdmitidosRequisitos";

		archivoDescarga = null;

		// <REEMPLAZAR VARIABLES EN CONSULTA>
		reemplazar.put("convocatoria", convocatoria);
		reemplazar.put("consecutivo", consecutivo);
		reemplazar.put("compania", compania);
		// </REEMPLAZAR VARIABLES EN CONSULTA>
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			parametros.put("PR_FORMS_IMPRIMIRADMITIDOS_CMBPRUEBA_COLUMN(1)", descripcionPrueba.toUpperCase());
			parametros.put("PR_NOMBRE_COMPANIA", nombreCompania);
			// </ENVIAR PARAMETROS AL REPORTE>

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			/*-aqui reporte hace referencia al nombre del reporte*/

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>

	/**
	 * 
	 * Método ejecutado al seleccionar una fila de la lista listaCmbConvocatoria.
	 *
	 * @param event
	 *            objeto que encapsula la acción proveniente de la vista.
	 */
	public void seleccionarFilaCmbConvocatoria(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		convocatoria = registroAux.getCampos()
				.get(RptImprimirAdmitidosControladorEnum.NRO_CONVOCATORIA.getValue()) == null ? ""
						: registroAux.getCampos().get(RptImprimirAdmitidosControladorEnum.NRO_CONVOCATORIA.getValue())
								.toString();

		fechaConvocatoria = registroAux.getCampos()
				.get(RptImprimirAdmitidosControladorEnum.FECHA_CONVOCATORIA.getValue()).toString();

		prueba = null;
		consecutivo = null;
		descripcionPrueba = null;
		cargarListaCmbPrueba();

	}

	/**
	 * 
	 * Método ejecutado al seleccionar una fila de la lista listaCmbPrueba.
	 *
	 * @param event
	 *            objeto que encapsula la acción proveniente de la vista.
	 */
	public void seleccionarFilaCmbPrueba(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		prueba = registroAux.getCampos().get(RptImprimirAdmitidosControladorEnum.PRUEBA.getValue()) == null ? ""
				: registroAux.getCampos().get(RptImprimirAdmitidosControladorEnum.PRUEBA.getValue()).toString();

		consecutivo = registroAux.getCampos().get(RptImprimirAdmitidosControladorEnum.CONSECUTIVO.getValue()) == null
				? ""
				: registroAux.getCampos().get(RptImprimirAdmitidosControladorEnum.CONSECUTIVO.getValue()).toString();

		descripcionPrueba = registroAux.getCampos().get(RptImprimirAdmitidosControladorEnum.DESCRIPCION.getValue())
				.toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable convocatoria.
	 * 
	 * @return convocatoria
	 */
	public String getConvocatoria() {
		return convocatoria;
	}

	/**
	 * Asigna la variable convocatoria.
	 * 
	 * @param convocatoria
	 *            Variable a asignar en convocatoria
	 */
	public void setConvocatoria(String convocatoria) {
		this.convocatoria = convocatoria;
	}

	/**
	 * Retorna la variable prueba.
	 * 
	 * @return prueba
	 */
	public String getPrueba() {
		return prueba;
	}

	/**
	 * Asigna la variable prueba.
	 * 
	 * @param prueba
	 *            Variable a asignar en prueba
	 */
	public void setPrueba(String prueba) {
		this.prueba = prueba;
	}

	public String getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	public String getFechaConvocatoria() {
		return fechaConvocatoria;
	}

	public void setFechaConvocatoria(String fechaConvocatoria) {
		this.fechaConvocatoria = fechaConvocatoria;
	}

	public String getDescripcionPrueba() {
		return descripcionPrueba;
	}

	public void setDescripcionPrueba(String descripcionPrueba) {
		this.descripcionPrueba = descripcionPrueba;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista.
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
	 * Retorna la lista listaCmbConvocatoria.
	 * 
	 * @return listaCmbConvocatoria
	 */
	public RegistroDataModelImpl getListaCmbConvocatoria() {
		return listaCmbConvocatoria;
	}

	/**
	 * Asigna la lista listaCmbConvocatoria.
	 * 
	 * @param listaCmbConvocatoria
	 *            Variable a asignar en listaCmbConvocatoria
	 */
	public void setListaCmbConvocatoria(RegistroDataModelImpl listaCmbConvocatoria) {
		this.listaCmbConvocatoria = listaCmbConvocatoria;
	}

	/**
	 * Retorna la lista listaCmbPrueba.
	 * 
	 * @return listaCmbPrueba
	 */
	public RegistroDataModelImpl getListaCmbPrueba() {
		return listaCmbPrueba;
	}

	/**
	 * Asigna la lista listaCmbPrueba.
	 * 
	 * @param listaCmbPrueba
	 *            Variable a asignar en listaCmbPrueba
	 */
	public void setListaCmbPrueba(RegistroDataModelImpl listaCmbPrueba) {
		this.listaCmbPrueba = listaCmbPrueba;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
