/*-
 * FrmGenerarFlujoEfectivoControlador.java
 *
 * 1.0
 * 
 * 04/05/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmFlujoDeEfectivoControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmGenerarFlujoEfectivoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 04/05/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmGenerarFlujoEfectivoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String anioInicial;
	private String mesInicial;
	private String mesFinal;
	private String anioFinal;
	private StreamedContent archivoDescarga;
	private List<Registro> listaAnioInicial;
	private List<Registro> listaMesInicial;
	private List<Registro> listaMesFinal;
	private List<Registro> listaAnioFinal;
	
	@EJB
	EjbSysmanUtil ejbSysmanUtil;
	
	public FrmGenerarFlujoEfectivoControlador() {
		super();
		compania = SessionUtil.getCompania();
		anioInicial = Integer.toString(SysmanFunciones.ano(new Date()));
		anioFinal  = Integer.toString(SysmanFunciones.ano(new Date()));
		mesInicial = Integer.toString(SysmanFunciones.mes(new Date()));
		mesFinal = Integer.toString(SysmanFunciones.mes(new Date()));
		try {
			//2172
			numFormulario= GeneralCodigoFormaEnum.FRM_GENERAR_FLUJO_EFECTIVO_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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
		cargarListaAnioInicial();
		cargarListaMesInicial();
		cargarListaMesFinal();
		cargarListaAnioFinal();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
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
	 * Carga la lista listaAnioInicial
	 *
	 */
	public void cargarListaAnioInicial(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);

			listaAnioInicial = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmGenerarFlujoEfectivoControladorUrlEnum.URL0001
							.getValue())
					.getUrl(),
					param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			param.put(GeneralParameterEnum.ANO.getName(),
					anioInicial);

			listaMesInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmGenerarFlujoEfectivoControladorUrlEnum.URL0002
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMesFinal
	 *
	 */
	public void cargarListaMesFinal(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			param.put(GeneralParameterEnum.ANO.getName(),
					anioFinal);

			listaMesFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmGenerarFlujoEfectivoControladorUrlEnum.URL0002
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaAnioFinal
	 *
	 */
	public void cargarListaAnioFinal(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);

			listaAnioFinal = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmGenerarFlujoEfectivoControladorUrlEnum.URL0001
							.getValue())
					.getUrl(),
					param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		  archivoDescarga = null;
	        generarExcel();
		//</CODIGO_DESARROLLADO>
 	}
	//</METODOS_BOTONES>
	 private void generarExcel() {
		 try {
		 

	        Map<String, Object> reemplazos = new TreeMap<>();
	        Map<String, Object> parametros = new TreeMap<>();
	        
	        String reporte = "002396FlujoEfectivo";
	        
	        reemplazos.put("compania", compania);
	        reemplazos.put("anioInicial", anioInicial);
	        reemplazos.put("anioFinal", anioFinal);
	        reemplazos.put("mesInicial", mesInicial);
	        reemplazos.put("mesFinal", mesFinal);	    
	        
	        
			String nombreMesInicial = ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mesInicial)).toUpperCase();			
			String nombreMesFinal = ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mesFinal)).toUpperCase();
			
			String titulo = "Por el periodo comprendido entre 01 de " +  nombreMesInicial + " de " + anioInicial + " y el 31 de " +  nombreMesFinal +  " de " + anioFinal ;

			String pAnioInicial = anioInicial;
			String pAnioFinal = anioFinal;
			
	        reemplazosTitulo(titulo);
	        parametros.put("PR_TITULO", titulo);
	        parametros.put("PR_COMPANIA", compania);
	        parametros.put("PR_NOMBRECOMPANIA",
                    SessionUtil.getCompaniaIngreso().getNombre());
	        parametros.put("PR_NITCOMPANIA",
                    SessionUtil.getCompaniaIngreso().getNit()); 
	        
	        parametros.put("PR_ANOFIN", pAnioFinal);
	        parametros.put("PR_ANOINI", pAnioInicial);
	        
	        parametros.put("PR_MESFIN", nombreMesFinal);
	        parametros.put("PR_MESINI", nombreMesInicial);
	       
	        
	        Reporteador.resuelveConsulta(reporte,
	                        Integer.parseInt(SessionUtil.getModulo()),
	                        reemplazos,
	                        parametros);
	        try {
	            archivoDescarga = JsfUtil.exportarStreamed(reporte,
	                            parametros,
	                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);
	        }
	        catch (JRException | IOException | SysmanException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
		 } catch (NumberFormatException | SystemException e1) {
				e1.printStackTrace();
			} 
	    }
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control AnioInicial
	 * 
	 * 
	 */
	public void cambiarAnioInicial() {
		//<CODIGO_DESARROLLADO>
	    mesInicial = null;
	    cargarListaMesInicial();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control AnioFinal
	 * 
	 * 
	 */
	public void cambiarAnioFinal() {
		//<CODIGO_DESARROLLADO>
		mesFinal = null;
		cargarListaMesFinal();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	
	public String reemplazosTitulo(String titulo) {
		
		String mes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)];
		String mes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesFinal)];

		titulo.replace(
				"s$anioInicial$s",
				anioInicial);
		
		titulo.replace(
				"s$mesInicial$s",
				mes1);

		titulo.replace(
				"s$anioFinal$s",
				anioFinal);
		
		titulo.replace(
				"s$mesFinal$s",
				mes2);
		
		return titulo;
	}

	public String getAnioInicial() {
		return anioInicial;
	}

	public void setAnioInicial(String anioInicial) {
		this.anioInicial = anioInicial;
	}

	public String getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	public String getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	public String getAnioFinal() {
		return anioFinal;
	}

	public void setAnioFinal(String anioFinal) {
		this.anioFinal = anioFinal;
	}

	public List<Registro> getListaAnioInicial() {
		return listaAnioInicial;
	}

	public void setListaAnioInicial(List<Registro> listaAnioInicial) {
		this.listaAnioInicial = listaAnioInicial;
	}

	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}

	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}

	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}

	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}
	public List<Registro> getListaAnioFinal() {
		return listaAnioFinal;
	}

	public void setListaAnioFinal(List<Registro> listaAnioFinal) {
		this.listaAnioFinal = listaAnioFinal;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

}
