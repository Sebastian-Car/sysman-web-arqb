/*-
 * InventarioFisicoPorDependenciaPlacaAnControlador.java
 *
 * 1.0
 * 
 * 21/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.InventarioFisicoPorDependenciaPlacaAnControladorEnum;
import com.sysman.almacen.enums.InventarioFisicoPorDependenciaPlacaAnControladorUrlEum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 21/08/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class InventarioFisicoPorDependenciaPlacaAnControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>

	private String firmasReponsable;

	@EJB
	EjbSysmanUtil ejbSysmanUtil;
	private String digitosAgrupacionInventario;
	private String dependenciaInicial;
	private String cpdependenciafinal;
	private String cpdependenciaInicial;
	private String dependenciaFinal;
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listacmbDependenciaInicial;
	private RegistroDataModelImpl listacmbDependenciaFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InventarioFisicoPorDependenciaPlacaAnControlador
	 */
	public InventarioFisicoPorDependenciaPlacaAnControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 1896
			numFormulario = GeneralCodigoFormaEnum.INVENTARIO_FISICO_POR_DEPENDENCIA_PLACA_AN_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
		firmasReponsable = "1";
		cargarListacmbDependenciaInicial();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>

	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>

		/*
		 * FR1896-AL_ABRIR Private Sub Form_Open(Cancel As Integer) 'formularioAbrir 10,
		 * Me.Name End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbDependenciaInicial
	 *
	 */
	public void cargarListacmbDependenciaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioFisicoPorDependenciaPlacaAnControladorUrlEum.URL3217.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacmbDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbDependenciaFinal
	 * 
	 */
	public void cargarListacmbDependenciaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioFisicoPorDependenciaPlacaAnControladorUrlEum.URL3718.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("CMBCODIGOINICIAL", dependenciaInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacmbDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GeneraExcel en la vista
	 *
	 * 
	 */
	public void oprimirGeneraExcel() {
		// <CODIGO_DESARROLLADO>

		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GeneraPdf en la vista
	 *
	 *
	 */
	public void oprimirGeneraPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	private void generarReporte(ReportesBean.FORMATOS formato) {
		// Creacion arreglos

		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String reporte;
		// Codigo del reporte
		if ("1".equals(firmasReponsable)) {
			reporte = InventarioFisicoPorDependenciaPlacaAnControladorEnum.REPORTE001873.getValue();

		} else {
			reporte = InventarioFisicoPorDependenciaPlacaAnControladorEnum.REPORTE001875.getValue();
		}

		// <REEMPLAZAR VARIABLES EN CONSULTA>
		reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		reemplazar.put(InventarioFisicoPorDependenciaPlacaAnControladorEnum.codigoinicial.getValue(),
				dependenciaInicial);
		reemplazar.put(InventarioFisicoPorDependenciaPlacaAnControladorEnum.codigofinal.getValue(), dependenciaFinal);

		// </REEMPLAZAR VARIABLES EN CONSULTA
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			// </ENVIAR PARAMETROS AL REPORTE>
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
			/*-aqui reporte hace referencia al nombre del reporte*/
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}

		catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacmbDependenciaInicial
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbDependenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaInicial = registroAux.getCampos().get("CODIGO").toString();
		cpdependenciaInicial = registroAux.getCampos().get("NOMBRE").toString();
		cargarListacmbDependenciaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbDependenciaFinal
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbDependenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaFinal = registroAux.getCampos().get("CODIGO").toString();
		cpdependenciafinal = registroAux.getCampos().get("NOMBRE").toString();

	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable dependenciaInicial
	 * 
	 * @return dependenciaInicial
	 */
	public String getDependenciaInicial() {
		return dependenciaInicial;
	}

	/**
	 * Asigna la variable dependenciaInicial
	 * 
	 * @param dependenciaInicial
	 *            Variable a asignar en dependenciaInicial
	 */
	public void setDependenciaInicial(String dependenciaInicial) {
		this.dependenciaInicial = dependenciaInicial;
	}

	/**
	 * Retorna la variable dependenciaFinal
	 * 
	 * @return dependenciaFinal
	 */
	public String getDependenciaFinal() {
		return dependenciaFinal;
	}

	/**
	 * Asigna la variable dependenciaFinal
	 * 
	 * @param dependenciaFinal
	 *            Variable a asignar en dependenciaFinal
	 */
	public void setDependenciaFinal(String dependenciaFinal) {
		this.dependenciaFinal = dependenciaFinal;
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
	 * Retorna la lista listacmbDependenciaInicial
	 * 
	 * @return listacmbDependenciaInicial
	 */

	public RegistroDataModelImpl getListacmbDependenciaInicial() {
		return listacmbDependenciaInicial;
	}

	public void setListacmbDependenciaInicial(RegistroDataModelImpl listacmbDependenciaInicial) {
		this.listacmbDependenciaInicial = listacmbDependenciaInicial;
	}

	public RegistroDataModelImpl getListacmbDependenciaFinal() {
		return listacmbDependenciaFinal;
	}

	public void setListacmbDependenciaFinal(RegistroDataModelImpl listacmbDependenciaFinal) {
		this.listacmbDependenciaFinal = listacmbDependenciaFinal;
	}

	public String getFirmasReponsable() {
		return firmasReponsable;
	}

	public void setFirmasReponsable(String firmasReponsable) {
		this.firmasReponsable = firmasReponsable;
	}

	public String getCpdependenciaInicial() {
		return cpdependenciaInicial;
	}

	public void setCpdependenciaInicial(String cpdependenciaInicial) {
		this.cpdependenciaInicial = cpdependenciaInicial;
	}

	public String getCpdependenciafinal() {
		return cpdependenciafinal;
	}

	public void setCpdependenciafinal(String cpdependenciafinal) {
		this.cpdependenciafinal = cpdependenciafinal;
	}

	/**
	 * Asigna la lista listacmbDependenciaFinal
	 * 
	 * @param listacmbDependenciaFinal
	 *            Variable a asignar en listacmbDependenciaFinal
	 */

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
