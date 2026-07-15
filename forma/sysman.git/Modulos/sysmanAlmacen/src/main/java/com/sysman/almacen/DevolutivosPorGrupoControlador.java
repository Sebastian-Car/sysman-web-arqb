/*-
 * DevolutivosPorGrupoControlador.java
 *
 * 1.0
 * 
 * 09/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.DevolutivoPorGrupoControladorEnum;
import com.sysman.almacen.enums.DevolutivoPorGrupoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * clase para generar un reporte sobre los devolutivos por grupos
 *
 * @version 1.0, 09/08/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class DevolutivosPorGrupoControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String modulo;

	@EJB
	EjbSysmanUtil ejbSysmanUtil;

	private String digitosAgrupacionInventario;
	private String cmbElementodesde;
	private boolean placaAnul;
	private StreamedContent archivoDescarga;
	private String cmbElementoHasta;
	private String elementoDesde;
	private String elementoHasta;
	private Date fechadeCorte;
	private RegistroDataModelImpl listacmbElementoHasta;
	private RegistroDataModelImpl listacmbElementoDesde;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DevolutivosPorGrupoControlador
	 */
	public DevolutivosPorGrupoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 1886
			numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_POR_GRUPO_CONTROLADOR.getCodigo();
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
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListacmbElementoDesde();
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
		fechadeCorte = new Date();
		try {
			digitosAgrupacionInventario = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(), true), "");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbElementoHasta
	 *
	 */
	public void cargarListacmbElementoHasta() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivoPorGrupoControladorUrlEnum.URL3218.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("CMBELEMENTODESDE", cmbElementodesde);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("AGRUPACION", digitosAgrupacionInventario);

		listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());

	}

	/**
	 * 
	 * Carga la lista listacmbElementoDesde
	 *
	 * carga el elemento inicial a la consulta
	 */
	public void cargarListacmbElementoDesde() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivoPorGrupoControladorUrlEnum.URL3217.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("AGRUPACION", digitosAgrupacionInventario);

		listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control ChkPlacaAnul
	 * 
	 * con este metodo se realiza la confirmacion que el check de placaanulada esté
	 * activo o no
	 * 
	 */
	public void cambiarChkPlacaAnul() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
	 *
	 * Codigo que genera el reporte en Excel
	 *
	 */
	public void oprimirgenera_excel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Comando44 en la vista
	 *
	 * metodo que genera el reporte en pdf
	 *
	 */
	public void oprimirgenera_pdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	private void generarReporte(ReportesBean.FORMATOS formato) {
		// Creacion arreglos
		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		// Codigo del reporte
		String reporte = placaAnul ? DevolutivoPorGrupoControladorEnum.REPORTE001863.getValue()
				: DevolutivoPorGrupoControladorEnum.REPORTE001862.getValue();
		// <REEMPLAZAR VARIABLES EN CONSULTA>
		reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		reemplazar.put(DevolutivoPorGrupoControladorEnum.elementoinicial.getValue(), cmbElementodesde);
		reemplazar.put(DevolutivoPorGrupoControladorEnum.elementofinal.getValue(), cmbElementoHasta);
		reemplazar.put("digitosagrupacioninventario", digitosAgrupacionInventario);
		reemplazar.put("fechadeCorte", SysmanFunciones.formatearFecha(fechadeCorte));
		// </REEMPLAZAR VARIABLES EN CONSULTA
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
			parametros.put("PR_FORMS_DEVOLUTIVOSPORGRUPO_TXTFECHA", fechadeCorte);
			// </ENVIAR PARAMETROS AL REPORTE>
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
			/*-aqui reporte hace referencia al nombre del reporte*/
			archivoDescarga = JsfUtil.exportarStreamed(DevolutivoPorGrupoControladorEnum.REPORTE001862.getValue(),
					parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// Codigo del reporte
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbElementoDesde
	 *
	 * selaciona el dato seleccionado en el combo inicio
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementodesde = registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()).toString();
		elementoDesde = registroAux.getCampos().get("NOMBRELARGO").toString();
		cargarListacmbElementoHasta();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbElementoHasta
	 *
	 * selecciona el dato del combo hasta para limitar el campo del reporte
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementoHasta = registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()).toString();
		setElementoHasta(registroAux.getCampos().get("NOMBRELARGO").toString());
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cmbElementodesde
	 * 
	 * @return cmbElementodesde
	 */
	public String getCmbElementodesde() {
		return cmbElementodesde;
	}

	/**
	 * Asigna la variable cmbElementodesde
	 * 
	 * @param cmbElementodesde
	 *            Variable a asignar en cmbElementodesde
	 */
	public void setCmbElementodesde(String cmbElementodesde) {
		this.cmbElementodesde = cmbElementodesde;
	}

	/**
	 * Retorna la variable cmbElementoHasta
	 * 
	 * @return cmbElementoHasta
	 */
	public String getCmbElementoHasta() {
		return cmbElementoHasta;
	}

	/**
	 * Asigna la variable cmbElementoHasta
	 * 
	 * @param cmbElementoHasta
	 *            Variable a asignar en cmbElementoHasta
	 */
	public void setCmbElementoHasta(String cmbElementoHasta) {
		this.cmbElementoHasta = cmbElementoHasta;
	}

	/**
	 * Retorna la variable elementoDesde
	 * 
	 * @return elementoDesde
	 */
	public String getElementoDesde() {
		return elementoDesde;
	}

	/**
	 * Asigna la variable elementoDesde
	 * 
	 * @param elementoDesde
	 *            Variable a asignar en elementoDesde
	 */
	public void setElementoDesde(String elementoDesde) {
		this.elementoDesde = elementoDesde;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>

	public String getModulo() {
		return modulo;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listacmbElementoDesde
	 * 
	 * @return listacmbElementoDesde
	 */
	public RegistroDataModelImpl getListacmbElementoDesde() {
		return listacmbElementoDesde;
	}

	public RegistroDataModelImpl getListacmbElementoHasta() {
		return listacmbElementoHasta;
	}

	public void setListacmbElementoHasta(RegistroDataModelImpl listacmbElementoHasta) {
		this.listacmbElementoHasta = listacmbElementoHasta;
	}

	public void setListacmbElementoDesde(RegistroDataModelImpl listacmbElementoDesde) {
		this.listacmbElementoDesde = listacmbElementoDesde;
	}

	public String getCompania() {
		return compania;
	}

	public boolean isPlacaAnul() {
		return placaAnul;
	}

	public void setPlacaAnul(boolean placaAnul) {
		this.placaAnul = placaAnul;
	}

	public String getElementoHasta() {
		return elementoHasta;
	}

	public void setElementoHasta(String elementoHasta) {
		this.elementoHasta = elementoHasta;
	}

	public String getDigitosAgrupacionInventario() {
		return digitosAgrupacionInventario;
	}

	public void setDigitosAgrupacionInventario(String digitosAgrupacionInventario) {
		this.digitosAgrupacionInventario = digitosAgrupacionInventario;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public Date getFechadeCorte() {
		return fechadeCorte;
	}

	public void setFechadeCorte(Date fechadeCorte) {
		this.fechadeCorte = fechadeCorte;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
