/*-
 * FrmAvanceFinanVigControlador.java
 *
 * 1.0
 * 
 * 25/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloTresRemote;
import com.sysman.plandesarrollo.enums.FrmAvanceFinanVigControladorUrlEnum;
import com.sysman.plandesarrollo.enums.FrmejecucionplandesarrolloControladorUrlEnum;
import com.sysman.reportes.Reporteador;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 25/09/2019
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class  FrmAvanceFinanVigControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	private final String menuActual;

	//<DECLARAR_ATRIBUTOS>
	private int anio;
	private String tipo;
	private String titulo;
	private boolean validarMenu;
	private String reporte;
	private boolean indCuatrenio;
	private String titulodinamico;
	

	private Map<String, Object> reemplazos = new HashMap<>();
	private Map<String, Object> parametros = new HashMap<>();
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listavigencia;


	@EJB
	EjbPlanDesarrolloTresRemote ejbPlanDesarrolloTresRemote;


	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmAvanceFinanVigControlador
	 */
	public FrmAvanceFinanVigControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		menuActual = SessionUtil.getMenuActual();
		try {
			numFormulario=2108;
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();

		}finally{
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarListavigencia();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
		validaropcionMenu();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listavigencia
	 *
	 */
	public void cargarListavigencia(){


		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
		    
		    if (menuActual.equals("67030902") || menuActual.equals("67031002") || menuActual.equals("67031102")) {
		    
			listavigencia = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmejecucionplandesarrolloControladorUrlEnum.URL002.getValue())
					.getUrl(),
					param));
		    }
		    else {
		        
		        listavigencia = RegistroConverter.toListRegistro(requestManager.getList(
                                        UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        FrmejecucionplandesarrolloControladorUrlEnum.URL001.getValue())
                                        .getUrl(),
                                        param));		        
		        
		    }

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}




	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton genera1
	 * en la vista
	 *
	 *
	 */
	public void oprimirgenera() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;      
		generarInforme(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirexcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>


	public void generarInforme(ReportesBean.FORMATOS formato) {

		try {
			

			reemplazos.put("ano", anio);
			reemplazos.put("ano1", anio + 1 );
			reemplazos.put("ano2", anio + 2 );
			reemplazos.put("ano3", anio + 3 );
			validaropcionMenu();
			ejecutarProcesoAvances();


			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);
			
			 if ("C".equals(tipo)) {
				 titulodinamico = "AVANCE FINANCIERO COMPROMETIDO - VIGENCIA";
			 } 
			 else if ("O".equals(tipo)) {
				 titulodinamico = "AVANCE FINANCIERO OBLIGADO - VIGENCIA";
			 }
			 parametros.put("PR_TITULODINAMICO",titulodinamico);
			parametros.put("PR_ANO_VIGENCIA", anio);
			parametros.put("PR_NOMBRECOMPANIA",
                    SessionUtil.getCompaniaIngreso().getNombre());
		


			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		catch (JRException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void validaropcionMenu() {

		switch (menuActual) {
		case "67030901": // RIC
			titulo = "AVANCE FISICO POR VIGENCIA";
			validarMenu = false;
			reporte = "002063AvanceFisicoVigenciaTun"; 
			indCuatrenio = false;
			break;
		case "67030902": // REC
			titulo = "AVANCE FISICO POR CUATRENIO";
			validarMenu = false;
			reporte = "002064AvanceFisicoCuatrenioTun";
			indCuatrenio = true;
			break;
		case "67031001": // RID
			titulo = "AVANCE FINANCIERO POR VIGENCIA";
			validarMenu = true;
			reporte = "002065AvanceFinancieroVigencia";
			reemplazos.put("tipo",tipo);
			break;
		case "67031002": // RED
			titulo = "AVANCE FINANCIERO POR CUATRENIO";
			validarMenu = true;
			reporte = "002066AvanceFinancieroCuatrenio";
			reemplazos.put("tipo",tipo);
			break;
		case "67031101":
			titulo = "INDICADOR DE RESULTADO POR VIGENCIA";
			validarMenu = false;
			reporte = "002067IndicadorResultadoVigencia";
			break;
		case "67031102":
			titulo = "INDICADOR DE RESULTADO POR CUATRENIO";
			validarMenu = false;
			reporte = "002068IndicadorResultadoCuatrenio";
			
			break;
		default:
		}
	}


	public void ejecutarProcesoAvances(){

		try {

			if ("67030901".equals(menuActual)||"67030902".equals(menuActual)){

				ejbPlanDesarrolloTresRemote.prepararAvanceFisico(compania, anio, indCuatrenio);
			}
			else if ("67031001".equals(menuActual)||"67031002".equals(menuActual)) {

				ejbPlanDesarrolloTresRemote.prepararAvanceFinanciero(compania, anio, indCuatrenio, tipo);

			}
			else {

				ejbPlanDesarrolloTresRemote.prepararAvanceIndResultados(compania, anio, indCuatrenio);

			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}



	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public int getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listavigencia
	 * 
	 * @return listavigencia
	 */
	public List<Registro> getListavigencia() {
		return listavigencia;
	}
	/**
	 * Asigna la lista listavigencia
	 * 
	 * @param listavigencia
	 * Variable a asignar en  listavigencia
	 */
	public void setListavigencia(List<Registro> listavigencia) {
		this.listavigencia = listavigencia;
	}



	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public boolean isValidarMenu() {
		return validarMenu;
	}
	public void setValidarMenu(boolean validarMenu) {
		this.validarMenu = validarMenu;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	public String getTitulodinamico() {
		return titulodinamico;
	}
	public void setTitulodinamico(String titulodinamico) {
		this.titulodinamico = titulodinamico;
	}




	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
