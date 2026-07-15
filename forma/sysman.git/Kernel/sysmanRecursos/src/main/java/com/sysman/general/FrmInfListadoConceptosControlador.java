/*-
 * FrmInfListadoConceptosControlador.java
 *
 * 1.0
 * 
 * 17/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmInfListadoConceptosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera un reporte en formato pdf o excel por a�o
 *
 * @version 1.0, 17/11/2017
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class FrmInfListadoConceptosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	private String ano;
	private String tipoCobro;
	private boolean visiblePdf = true;
	private boolean indDetalle;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * lista que almacena los anos
	 */
	private List<Registro> listaAno;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmInfListadoConceptosControlador
	 */

	public FrmInfListadoConceptosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		
		if (modulo.equals("1")){
			tipoCobro = "CIA";
		}else {
			tipoCobro = SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()).toString();
		}
		
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INF_LISTADOCONCEPTOS_CONTROLADOR.getCodigo();
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
		// <CARGAR_LISTA>
		cargarListaAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
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
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * Permite abrir formulario
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmInfListadoConceptosControladorUrlEnum.URL3736.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdPrevia en la vista
	 *
	 *
	 */
	public void oprimirPDF() {

		generarReporte(FORMATOS.PDF);
	}

	public void oprimirExcel() {

		if (!indDetalle) {
			generarReporte(FORMATOS.EXCEL);
		} else {
			generarReporteDetallado();
		}
	}

	/**
	 * @author gfigueredo Ticket 7703247 - 7706218 Funci�n que hace vivisible u
	 *         oculta el boton pdf dependeiendo del indicado IndDetalle
	 */
	public void cambiarIndDetalle() {

		if (indDetalle) {
			visiblePdf = false;
		} else {
			visiblePdf = true;
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdOficial en la vista
	 *
	 */

	private void generarReporte(FORMATOS formato) {
		archivoDescarga = null;

		if (!validarCamposVacios()) {
			return;
		}

		try {
			Map<String, Object> reemplazos = new TreeMap<>();
			Map<String, Object> parametros = new TreeMap<>();

			reemplazos.put("compania", compania);
			reemplazos.put("ano", ano);
			reemplazos.put("tipocobro", tipoCobro);

			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_FORMS_FRM_INFLISTADO_CONCEPTOS_ANO", ano);

			Reporteador.resuelveConsulta("001509INFLISTCONCEPTOS", Integer.parseInt(modulo), reemplazos, parametros);

			archivoDescarga = JsfUtil.exportarStreamed("001509INFLISTCONCEPTOS", parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * @author gfigueredo Ticket 7703247 - 7706218 Funci�n encargada de generar el
	 *         archivo detallado de los conceptos
	 */
	public void generarReporteDetallado() {
		try {
			String nombreConsulta = "800509ConceptosWebService";
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("compania", compania);
			reemplazar.put("ano", ano);
			reemplazar.put("usuario", "webservice");

			String sql = Reporteador.resuelveConsulta(nombreConsulta, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
					nombreConsulta);
		} catch (JRException | IOException | SysmanException | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private boolean validarCamposVacios() {
		if (SysmanFunciones.validarVariableVacio(ano)) {
			JsfUtil.agregarMensajeError(idioma.getString("MSM_DEBE_ANO"));
			return false;
		}
		return true;
	}

	public void oprimircmdOficial() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String getTipoCobro() {
		return tipoCobro;
	}

	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}

	public boolean isVisiblePdf() {
		return visiblePdf;
	}

	public void setVisiblePdf(boolean visiblePdf) {
		this.visiblePdf = visiblePdf;
	}

	public boolean isIndDetalle() {
		return indDetalle;
	}

	public void setIndDetalle(boolean indDetalle) {
		this.indDetalle = indDetalle;
	}
	
	
}
