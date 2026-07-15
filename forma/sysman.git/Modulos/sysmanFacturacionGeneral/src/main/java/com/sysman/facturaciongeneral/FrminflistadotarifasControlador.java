/*-
 * FrminflistadotarifasControlador.java
 *
 * 1.0
 * 
 * 24/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrminflistadotarifasControladorEnum;
import com.sysman.facturaciongeneral.enums.FrminflistadotarifasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase permite la generación de listados de Tarifas por Año, con dos
 * filtros: Todas y Agrupar por concepto relacionado.
 *
 * @version 1.0, 24/11/2017
 * @author dnino
 * @version 2.0, 29/08/2018
 * @author jgomezp se realizo cambio para ingresar a otro formulario.
 */
@ManagedBean
@ViewScoped
public class FrminflistadotarifasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private String titulo;
	private String iniciales;
	private String finales;
	private boolean generaExcel = false;
	private boolean visibleGenera = true;
	private boolean indTodas;
	private UrlBean urlBean;
	private String menu = "69030104";
	private final String compania;
	/**
	 * Constante a nivel de clase que aloja el codigo del modulo desde el cual el
	 * usuario inicio sesion.
	 */
	private final String modulo = SessionUtil.getModulo();

	/**
	 * Constante a nivel de clase que aloja el nombre de la compania desde la cual
	 * se inicio sesion.
	 */
	private final String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Variable que recibe el indicador de Todas las tarifas.
	 */
	
	/**
	 * Variable que recibe el indicador de Agrupar las tarifas.
	 */
	private boolean indAgrupar;
	/**
	 * Variable que recibe el año.
	 */
	
	private int ano;
	/**
	 * Tipo de cobro seleccionado en el modal "Seleccionar tipo de cobro"
	 */
	private String tipoCobro;
	/**
	 * Concepto relacionado al año seleccionado.
	 */
	/**
	 * 
	 */
	private String tarifaInicial;
	private String condicion;
	/**
	 * 
	 */
	private String tarifaFinal;
	private StreamedContent archivoDescarga;
	/**
	 * Variable a nivel de clase que almacena el nombre del tipo de cobro
	 * seleccionado al iniciar sesion en el modulo.
	 */
	private String nombreTipoCobro;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Declaración de la lista de Años.
	 */
	private List<Registro> listaAno;
	/**
	 * Declaración de Lista de conceptos relacionados,
	 */
	private RegistroDataModelImpl listaConcRelacionado;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaTarifaInicial;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaTarifaFinal;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminflistadotarifasControlador
	 */
	public FrminflistadotarifasControlador() {
		super();
		compania = SessionUtil.getCompania();
		if (menu.equals(SessionUtil.getMenuActual())) {
			titulo = "LISTADO DE ESTRATOS";
			iniciales = "Estrato Inicial";
			finales = "Estrato Final";
		} else {

			titulo = "LISTADO DE TARIFAS";
			iniciales = "Tarifa Inicial";
			finales = "Tarifa Final"; 
		}
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INF_LISTADO_TARIFAS_CONTROLADOR.getCodigo();
			// <INI_ADICIONAL>
			// Variables de sesion
			tipoCobro = SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()).toString();

			nombreTipoCobro = SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO.getValue())
					.toString();
			validarPermisos();
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
		abrirFormulario();
		cargarListaAno();
		ano = SysmanFunciones.ano(new Date());
		cargarListaTarifaInicial();
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
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */

	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrminflistadotarifasControladorUrlEnum.URL5822.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaConcRelacionado
	 *
	 */


	/**
	 * 
	 * Carga listaTarifaInicial
	 *
	 */
	public void cargarListaTarifaInicial() {
		if (menu.equals(SessionUtil.getMenuActual())) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrminflistadotarifasControladorUrlEnum.URL3649.getValue());
		} else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrminflistadotarifasControladorUrlEnum.URL5520.getValue());
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(FrminflistadotarifasControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
		listaTarifaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga listalistaTarifaFinal
	 *
	 */
	public void cargarListaTarifaFinal() {
		if (menu.equals(SessionUtil.getMenuActual())) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrminflistadotarifasControladorUrlEnum.URL3650.getValue());
		} else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrminflistadotarifasControladorUrlEnum.URL5521.getValue());
		
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put("CONCEPTORELACIONADO", tarifaInicial);
		param.put(FrminflistadotarifasControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
		listaTarifaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarPDF en la vista
	 * 
	 */
	public void oprimirGenerarPDF() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarExcel en la vista
	 * 
	 *
	 */
	public void oprimirGenerarExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}
	public void oprimirGenerarPDF2() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null; 
        generarReporte(ReportesBean.FORMATOS.PDF);
       //</CODIGO_DESARROLLADO>
   }
   /**
    * 
    * Metodo ejecutado al oprimir el boton GenerarExcel2
    * en la vista
    *
    * TODO DOCUMENTACION ADICIONAL
    *
    */
public void oprimirGenerarExcel2() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
       //</CODIGO_DESARROLLADO>
   }

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control ANO
	 *
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		cargarListaTarifaInicial();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarIndTodas() {
		
		if(indTodas) {
			generaExcel = true;
			visibleGenera = false;
		}else {
			generaExcel = false;
			visibleGenera = true;
			
		}
		
		
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}
	

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaConcRelacionado
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */


	public void seleccionarFilaTarifaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tarifaInicial = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		cargarListaTarifaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTarifa Final
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTarifaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tarifaFinal = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	/**
	 * Genera un reporte con un determinado formato.
	 * 
	 * @param formato
	 * Tipo de documento a generar.
	 */
	private void generarReporte(ReportesBean.FORMATOS formato) {

		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		// Código de los reporte
		if(menu.equals(SessionUtil.getMenuActual()) && indTodas) {
			condicion = " ";
			
		}else {
			if(menu != SessionUtil.getMenuActual() && indTodas) {
				condicion = " ";

				}else {
					if(menu.equals(SessionUtil.getMenuActual()) && !indTodas) {
							condicion = "AND NVL(SF_ESTRATO.CODIGO,'') BETWEEN '" + tarifaInicial + "' AND '" + tarifaFinal + "' ";

						}else {
					condicion = "AND NVL(SF_TARIFA.CODIGO,'') BETWEEN '" + tarifaInicial + "' AND '" + tarifaFinal + "' " ;
				
								}
						}
			}
		String reporte = "";
		if (menu.equals(SessionUtil.getMenuActual())) {
			reporte = "001510INFLISTESTRATOS";

		} else {
			reporte = "001524INFLISTTARIFAS";
		}
		// Definición de parámetro Año para Encabezado del reporte

		archivoDescarga = null;

		// <REEMPLAZAR VARIABLES EN CONSULTA>
		reemplazar.put("condicion", condicion);
		reemplazar.put("compania", compania);
		reemplazar.put("ano", ano);
		reemplazar.put("tipoCobro", tipoCobro);


		// </REEMPLAZAR VARIABLES EN CONSULTA>
		try {
			// <ENVIAR PARAMETROS AL REPORTE>

			parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
			parametros.put("PR_NOMTIPOCOBRO", nombreTipoCobro);
			parametros.put("PR_IND_AGRUPAR_TODAS", indAgrupar);
			// </ENVIAR PARAMETROS AL REPORTE>
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			/*-aqui reporte hace referencia al nombre del reporte*/

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	// <SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * @return the indTodas
	 */
	public boolean isIndTodas() {
		return indTodas;
	}

	/**
	 * @param indTodas
	 *            the indTodas to set
	 */
	public void setIndTodas(boolean indTodas) {
		this.indTodas = indTodas;
	}

	/**
	 * @return the indAgrupar
	 */
	public boolean isIndAgrupar() {
		return indAgrupar;
	}

	/**
	 * @param indAgrupar
	 *            the indAgrupar to set
	 */
	public void setIndAgrupar(boolean indAgrupar) {
		this.indAgrupar = indAgrupar;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano
	 *            Variable a asignar en ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @param archivoDescarga
	 *            the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the reemplazos
	 */

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
	 * @return the nombreTipoCobro
	 */
	public String getNombreTipoCobro() {
		return nombreTipoCobro;
	}

	/**
	 * @param nombreTipoCobro
	 *            the nombreTipoCobro to set
	 */
	public void setNombreTipoCobro(String nombreTipoCobro) {
		this.nombreTipoCobro = nombreTipoCobro;
	}

	/**
	 * @return the modulo
	 */
	public String getModulo() {
		return modulo;
	}

	/**
	 * @return the nombreCompania
	 */
	public String getNombreCompania() {
		return nombreCompania;
	}

	/**
	 * @return the tipoCobro
	 */
	public String getTipoCobro() {
		return tipoCobro;
	}

	/**
	 * @param tipoCobro
	 *            the tipoCobro to set
	 */
	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}

	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 *            Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * Retorna la lista listaConcRelacionado
	 * 
	 * @return listaConcRelacionado
	 */
	public RegistroDataModelImpl getListaConcRelacionado() {
		return listaConcRelacionado;
	}

	/**
	 * Asigna la lista listaConcRelacionado
	 * 
	 * @param listaConcRelacionado
	 *            Variable a asignar en listaConcRelacionado
	 */
	public void setListaConcRelacionado(RegistroDataModelImpl listaConcRelacionado) {
		this.listaConcRelacionado = listaConcRelacionado;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public UrlBean getUrlBean() {
		return urlBean;
	}

	public void setUrlBean(UrlBean urlBean) {
		this.urlBean = urlBean;
	}

	public String getTarifaInicial() {
		return tarifaInicial;
	}

	public void setTarifaInicial(String tarifaInicial) {
		this.tarifaInicial = tarifaInicial;
	}

	public String getTarifaFinal() {
		return tarifaFinal;
	}

	public void setTarifaFinal(String tarifaFinal) {
		this.tarifaFinal = tarifaFinal;
	}

	public RegistroDataModelImpl getListaTarifaInicial() {
		return listaTarifaInicial;
	}

	public void setListaTarifaInicial(RegistroDataModelImpl listaTarifaInicial) {
		this.listaTarifaInicial = listaTarifaInicial;
	}

	public RegistroDataModelImpl getListaTarifaFinal() {
		return listaTarifaFinal;
	}

	public void setListaTarifaFinal(RegistroDataModelImpl listaTarifaFinal) {
		this.listaTarifaFinal = listaTarifaFinal;
	}

	public String getiniciales() {
		return iniciales;
	}

	public void setiniciales(String iniciales) {
		this.iniciales = iniciales;
	}

	public String getFinales() {
		return finales;
	}

	public void setFinales(String finales) {
		this.finales = finales;
	}

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public boolean isGeneraExcel() {
		return generaExcel;
	}

	public void setGeneraExcel(boolean generaExcel) {
		this.generaExcel = generaExcel;
	}

	public boolean isVisibleGenera() {
		return visibleGenera;
	}

	public void setVisibleGenera(boolean visibleGenera) {
		this.visibleGenera = visibleGenera;
	}

	

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
