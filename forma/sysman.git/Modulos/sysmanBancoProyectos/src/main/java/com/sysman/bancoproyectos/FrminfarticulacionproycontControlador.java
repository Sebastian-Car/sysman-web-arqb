/*-
 * FrminfarticulacionproycontControlador.java
 *
 * 1.0
 * 
 * 20/01/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.bancoproyectos.enums.FrminfarticulacionproycontControladorUrlEnum;
import com.sysman.bancoproyectos.enums.ProgramacionProyInversionControladorEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 20/01/2025
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminfarticulacionproycontControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;	
	/**
     * Constante a nivel de clase que almacena el codigo del modulo al
     * cual ingresa el usuario
     */
    private final String modulo;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean fntFinanciacion;

	private boolean visibleFinanciacion;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean proyectos;

	private boolean visibleProyectos;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fteFinanciacionIni;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fteFinanciacionFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String vigencia;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String proyectoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String proyectoFin;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaVigencia;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFteFinanciacionIni;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFteFinanciacionFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProyectoinicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProyectofinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminfarticulacionproycontControlador
	 */
	public FrminfarticulacionproycontControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INF_ARTICULACION_PROY_CONT_CONTROLADOR.getCodigo();
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

		fntFinanciacion = true;
		proyectos = true;
		vigencia = (String.valueOf(SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR)));
		fechaInicial = fechaFin = new Date();

		cargarListaVigencia();
		cargarListaFteFinanciacionIni();
		cargarListaFteFinanciacionFinal();
		cargarListaProyectoinicial();
		cargarListaProyectofinal();
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
	/**
	 * 
	 * Carga la lista listaVigencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaVigencia() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			listaVigencia = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrminfarticulacionproycontControladorUrlEnum.URL4043.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaFteFinanciacionIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaFteFinanciacionIni() {
		String urlEnumId = FrminfarticulacionproycontControladorUrlEnum.URL34001.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listaFteFinanciacionIni = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaFteFinanciacionFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaFteFinanciacionFinal() {
		String urlEnumId = FrminfarticulacionproycontControladorUrlEnum.URL34003.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		param.put(ProgramacionProyInversionControladorEnum.FUENTEINICIAL.getValue(), fteFinanciacionIni);

		listaFteFinanciacionFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaProyectoinicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProyectoinicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrminfarticulacionproycontControladorUrlEnum.URL32003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaProyectofinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProyectofinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrminfarticulacionproycontControladorUrlEnum.URL32013.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), proyectoInicial);

		listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		String reporte = "002701InformeArticulacionProyectosContratos";
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        
        reemplazar.put("compania", compania);
        reemplazar.put("vigencia", vigencia);
        reemplazar.put("fechaInicial",
                SysmanFunciones.formatearFecha(fechaInicial));
		reemplazar.put("fechaFinal",
		                SysmanFunciones.formatearFecha(fechaFin));
		reemplazar.put("fuenteInicial", fntFinanciacion ? "1" : fteFinanciacionIni);
		reemplazar.put("fuenteFinal", fntFinanciacion ? SysmanConstantes.CONS_FUENTE : fteFinanciacionFinal);
		reemplazar.put("proyectoInicial", proyectos ? "1" : proyectoInicial);
		reemplazar.put("proyectoFinal", proyectos ? SysmanConstantes.CONS_AUXILIAR : proyectoFin);
		
		parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
		parametros.put("PR_VIGENCIA", vigencia);            
        try {

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					FORMATOS.EXCEL);
		} catch (

			FileNotFoundException e) {
				JsfUtil.agregarMensajeError(
						idioma.getString("MSM_INFORME_VAR_NO_EXISTE").replace("s$reporte$s", reporte) + e.getMessage());
				logger.error(e.getMessage(), e);
		}
		catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Vigencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarVigencia() {
		cargarListaVigencia();
	}

	/**
	 * Metodo ejecutado al cambiar el control Fechainicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarFechainicial() {
		if (vigencia != null
				&& SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR) != Integer.parseInt(vigencia)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2293"));
			fechaInicial = null;
			return;

		}
		if (fechaFin != null && fechaInicial.after(fechaFin)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
			fechaInicial = null;

		}
	}

	/**
	 * Metodo ejecutado al cambiar el control Fechafinal
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarFechafinal() {
		if (vigencia != null && SysmanFunciones.getParteFecha(fechaFin, Calendar.YEAR) != Integer.parseInt(vigencia)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2293"));
			fechaFin = null;
			return;
		}
		if (fechaInicial != null && fechaInicial.after(fechaFin)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2295"));
			fechaFin = null;
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control fntFinanciacion
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarfntFinanciacion() {
		fteFinanciacionIni = null;
		fteFinanciacionFinal = null;
		archivoDescarga = null;
	}

	/**
	 * Metodo ejecutado al cambiar el control Proyectos
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarProyectos() {
		proyectoInicial = null;
		proyectoFin = null;
		archivoDescarga = null;
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFteFinanciacionIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFteFinanciacionIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fteFinanciacionIni = registroAux.getCampos().get("CODIGO").toString();
		cargarListaFteFinanciacionFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFteFinanciacionFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFteFinanciacionFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fteFinanciacionFinal = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProyectoinicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectoinicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoInicial = registroAux.getCampos().get("CODIGO").toString();
		cargarListaProyectofinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProyectofinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectofinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoFin = registroAux.getCampos().get("CODIGO").toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable fntFinanciacion
	 * 
	 * @return fntFinanciacion
	 */
	public boolean getFntFinanciacion() {
		return fntFinanciacion;
	}

	/**
	 * Asigna la variable fntFinanciacion
	 * 
	 * @param fntFinanciacion Variable a asignar en fntFinanciacion
	 */
	public void setFntFinanciacion(boolean fntFinanciacion) {
		this.fntFinanciacion = fntFinanciacion;
	}

	/**
	 * Retorna la variable proyectos
	 * 
	 * @return proyectos
	 */
	public boolean getProyectos() {
		return proyectos;
	}

	/**
	 * Asigna la variable proyectos
	 * 
	 * @param proyectos Variable a asignar en proyectos
	 */
	public void setProyectos(boolean proyectos) {
		this.proyectos = proyectos;
	}

	/**
	 * Retorna la variable fteFinanciacionIni
	 * 
	 * @return fteFinanciacionIni
	 */
	public String getFteFinanciacionIni() {
		return fteFinanciacionIni;
	}

	/**
	 * Asigna la variable fteFinanciacionIni
	 * 
	 * @param fteFinanciacionIni Variable a asignar en fteFinanciacionIni
	 */
	public void setFteFinanciacionIni(String fteFinanciacionIni) {
		this.fteFinanciacionIni = fteFinanciacionIni;
	}

	/**
	 * Retorna la variable fteFinanciacionFinal
	 * 
	 * @return fteFinanciacionFinal
	 */
	public String getFteFinanciacionFinal() {
		return fteFinanciacionFinal;
	}

	/**
	 * Asigna la variable fteFinanciacionFinal
	 * 
	 * @param fteFinanciacionFinal Variable a asignar en fteFinanciacionFinal
	 */
	public void setFteFinanciacionFinal(String fteFinanciacionFinal) {
		this.fteFinanciacionFinal = fteFinanciacionFinal;
	}

	/**
	 * Retorna la variable vigencia
	 * 
	 * @return vigencia
	 */
	public String getVigencia() {
		return vigencia;
	}

	/**
	 * Asigna la variable vigencia
	 * 
	 * @param vigencia Variable a asignar en vigencia
	 */
	public void setVigencia(String vigencia) {
		this.vigencia = vigencia;
	}

	/**
	 * Retorna la variable proyectoInicial
	 * 
	 * @return proyectoInicial
	 */
	public String getProyectoInicial() {
		return proyectoInicial;
	}

	/**
	 * Asigna la variable proyectoInicial
	 * 
	 * @param proyectoInicial Variable a asignar en proyectoInicial
	 */
	public void setProyectoInicial(String proyectoInicial) {
		this.proyectoInicial = proyectoInicial;
	}

	/**
	 * Retorna la variable proyectoFin
	 * 
	 * @return proyectoFin
	 */
	public String getProyectoFin() {
		return proyectoFin;
	}

	/**
	 * Asigna la variable proyectoFin
	 * 
	 * @param proyectoFin Variable a asignar en proyectoFin
	 */
	public void setProyectoFin(String proyectoFin) {
		this.proyectoFin = proyectoFin;
	}

	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * Asigna la variable fechaInicial
	 * 
	 * @param fechaInicial Variable a asignar en fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * Retorna la variable fechaFin
	 * 
	 * @return fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * Asigna la variable fechaFin
	 * 
	 * @param fechaFin Variable a asignar en fechaFin
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
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
	/**
	 * Retorna la lista listaVigencia
	 * 
	 * @return listaVigencia
	 */
	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}

	/**
	 * Asigna la lista listaVigencia
	 * 
	 * @param listaVigencia Variable a asignar en listaVigencia
	 */
	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaFteFinanciacionIni
	 * 
	 * @return listaFteFinanciacionIni
	 */
	public RegistroDataModelImpl getListaFteFinanciacionIni() {
		return listaFteFinanciacionIni;
	}

	/**
	 * Asigna la lista listaFteFinanciacionIni
	 * 
	 * @param listaFteFinanciacionIni Variable a asignar en listaFteFinanciacionIni
	 */
	public void setListaFteFinanciacionIni(RegistroDataModelImpl listaFteFinanciacionIni) {
		this.listaFteFinanciacionIni = listaFteFinanciacionIni;
	}

	/**
	 * Retorna la lista listaFteFinanciacionFinal
	 * 
	 * @return listaFteFinanciacionFinal
	 */
	public RegistroDataModelImpl getListaFteFinanciacionFinal() {
		return listaFteFinanciacionFinal;
	}

	/**
	 * Asigna la lista listaFteFinanciacionFinal
	 * 
	 * @param listaFteFinanciacionFinal Variable a asignar en
	 *                                  listaFteFinanciacionFinal
	 */
	public void setListaFteFinanciacionFinal(RegistroDataModelImpl listaFteFinanciacionFinal) {
		this.listaFteFinanciacionFinal = listaFteFinanciacionFinal;
	}

	/**
	 * Retorna la lista listaProyectoinicial
	 * 
	 * @return listaProyectoinicial
	 */
	public RegistroDataModelImpl getListaProyectoinicial() {
		return listaProyectoinicial;
	}

	/**
	 * Asigna la lista listaProyectoinicial
	 * 
	 * @param listaProyectoinicial Variable a asignar en listaProyectoinicial
	 */
	public void setListaProyectoinicial(RegistroDataModelImpl listaProyectoinicial) {
		this.listaProyectoinicial = listaProyectoinicial;
	}

	/**
	 * Retorna la lista listaProyectofinal
	 * 
	 * @return listaProyectofinal
	 */
	public RegistroDataModelImpl getListaProyectofinal() {
		return listaProyectofinal;
	}

	/**
	 * Asigna la lista listaProyectofinal
	 * 
	 * @param listaProyectofinal Variable a asignar en listaProyectofinal
	 */
	public void setListaProyectofinal(RegistroDataModelImpl listaProyectofinal) {
		this.listaProyectofinal = listaProyectofinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isVisibleProyectos() {
		return visibleProyectos;
	}

	public void setVisibleProyectos(boolean visibleProyectos) {
		this.visibleProyectos = visibleProyectos;
	}

	public boolean isVisibleFinanciacion() {
		return visibleFinanciacion;
	}

	public void setVisibleFinanciacion(boolean visibleFinanciacion) {
		this.visibleFinanciacion = visibleFinanciacion;
	}
}
