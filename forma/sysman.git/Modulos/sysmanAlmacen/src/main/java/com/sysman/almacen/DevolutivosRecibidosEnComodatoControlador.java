/*-
 * DevolutivosRecibidosEnComodatoControlador.java
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

import com.sysman.almacen.enums.DevolutivosRecibidosEnComodatoControladorURLEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Controlador que imprime recibos de comodatos devolutivos.
 *
 * @version 1.0, 09/08/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  DevolutivosRecibidosEnComodatoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	private final String modulo;

	private String reporte;
	
	private String digitos;

	private String cmbElementoDesde;
	
	private String cmbElementoHasta;

	private String lblElementoDesde;
	
	private String lblElementoHasta;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	
	private RegistroDataModelImpl listacmbElementoDesde;
	
	private RegistroDataModelImpl listacmbElementoHasta;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DevolutivosRecibidosEnComodatoControlador
	 */

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public DevolutivosRecibidosEnComodatoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo=SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_RECIBIDOS_EN_COMODATO_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		try {
			digitos = ejbSysmanUtil.consultarParametro(compania,"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		cargarListacmbElementoDesde(); 
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
		/*
FR1887-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   'formularioAbrir 10, Me.Name
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga las listas de los combos.
	 */
	public void cargarListacmbElementoDesde(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosRecibidosEnComodatoControladorURLEnum.URL2474.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"CODIGOELEMENTO");

	}
	
	public void cargarListacmbElementoHasta(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosRecibidosEnComodatoControladorURLEnum.URL2475.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINICIAL", cmbElementoDesde);
		listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOELEMENTO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo de imprimir.
	 * 
	 */
	public void oprimirbtnPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generaInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	public void oprimirbtnEXCEL() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generaInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	
	public void generaInforme(ReportesBean.FORMATOS formato) {

		try{			
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			
			String formatoUspec = ejbSysmanUtil.consultarParametro(compania,
					"FORMATOS UNICOS USPEC", modulo, new Date(), false); 
			
			
			if (formatoUspec.equals("SI")) {				
				reporte="002479DevolutivosRecibidosEnComodatoUSPEC"; 				
				
				String nombreCoordinadorAlmacen = ejbSysmanUtil.consultarParametro(compania,
				        "COORDINADOR ALMACEN", modulo,
                        new Date(),
                        true);
				String cargoCoordinadorAlmacen = ejbSysmanUtil.consultarParametro(compania,
				        "CARGO COORDINADOR ALMACEN", modulo,
                        new Date(),
                        true);
				String nombreAlmacenista = ejbSysmanUtil.consultarParametro(compania,
				        "ALMACENISTA", modulo,
                        new Date(),
                        true);
				String cargoAlmacenista = ejbSysmanUtil.consultarParametro(compania,
				        "CARGO ALMACENISTA", modulo,
                        new Date(),
                        true);

				reemplazar.put("cmbElementoDesde",cmbElementoDesde);
				reemplazar.put("cmbElementoHasta",cmbElementoHasta);
				reemplazar.put("compania",compania);
				reemplazar.put("digito", digitos);
				
				parametros.put("PR_NOMBRE_COORDINADOR_ALMACEN",
						nombreCoordinadorAlmacen);
	            parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
	            		cargoCoordinadorAlmacen);
	            parametros.put("PR_NOMBRE_ALMACENISTA",
	            		nombreAlmacenista);
	            parametros.put("PR_CARGO_ALMACENISTA",
	            		cargoAlmacenista);
								
			}
			else {
				reporte="001860DevolutivosRecibidosEnComodato"; 

				reemplazar.put("cmbElementoDesde",cmbElementoDesde);
				reemplazar.put("cmbElementoHasta",cmbElementoHasta);
				reemplazar.put("compania",compania);
				reemplazar.put("digito", digitos);
			}
			
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros);    			
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
						
		}
		catch (JRException | IOException | SysmanException | SystemException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 * Metodos de seleccion con cargue de los combos.
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementoDesde= SysmanFunciones.nvl(registroAux.getCampos().get("CODIGOELEMENTO"),"").toString();
		lblElementoDesde = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();
		cmbElementoHasta = null;
		lblElementoHasta= null;
		cargarListacmbElementoHasta();
	}
	
	public void seleccionarFilacmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementoHasta= SysmanFunciones.nvl(registroAux.getCampos().get("CODIGOELEMENTO"),"").toString();
		lblElementoHasta = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cmbElementoDesde
	 * 
	 * @return  cmbElementoDesde
	 */
	public String getCmbElementoDesde() {
		return cmbElementoDesde;
	}
	/**
	 * Asigna la variable  cmbElementoDesde
	 * 
	 * @param  cmbElementoDesde
	 * Variable a asignar en  cmbElementoDesde
	 */
	public void setCmbElementoDesde(String cmbElementoDesde) {
		this.cmbElementoDesde = cmbElementoDesde;
	}
	/**
	 * Retorna la variable cmbElementoHasta
	 * 
	 * @return  cmbElementoHasta
	 */
	public String getCmbElementoHasta() {
		return cmbElementoHasta;
	}
	/**
	 * Asigna la variable  cmbElementoHasta
	 * 
	 * @param  cmbElementoHasta
	 * Variable a asignar en  cmbElementoHasta
	 */
	public void setCmbElementoHasta(String cmbElementoHasta) {
		this.cmbElementoHasta = cmbElementoHasta;
	}
	/**
	 * Retorna la variable lblElementoDesde
	 * 
	 * @return  lblElementoDesde
	 */
	public String getLblElementoDesde() {
		return lblElementoDesde;
	}
	/**
	 * Asigna la variable  lblElementoDesde
	 * 
	 * @param  lblElementoDesde
	 * Variable a asignar en  lblElementoDesde
	 */
	public void setLblElementoDesde(String lblElementoDesde) {
		this.lblElementoDesde = lblElementoDesde;
	}
	/**
	 * Retorna la variable lblElementoHasta
	 * 
	 * @return  lblElementoHasta
	 */
	public String getLblElementoHasta() {
		return lblElementoHasta;
	}
	/**
	 * Asigna la variable  lblElementoHasta
	 * 
	 * @param  lblElementoHasta
	 * Variable a asignar en  lblElementoHasta
	 */
	public void setLblElementoHasta(String lblElementoHasta) {
		this.lblElementoHasta = lblElementoHasta;
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacmbElementoDesde
	 * 
	 * @return listacmbElementoDesde
	 */
	public RegistroDataModelImpl getListacmbElementoDesde() {
		return listacmbElementoDesde;
	}
	/**
	 * Asigna la lista listacmbElementoDesde
	 * 
	 * @param listacmbElementoDesde
	 * Variable a asignar en  listacmbElementoDesde
	 */
	public void setListacmbElementoDesde(RegistroDataModelImpl listacmbElementoDesde) {
		this.listacmbElementoDesde = listacmbElementoDesde;
	}
	/**
	 * Retorna la lista listacmbElementoHasta
	 * 
	 * @return listacmbElementoHasta
	 */
	public RegistroDataModelImpl getListacmbElementoHasta() {
		return listacmbElementoHasta;
	}
	/**
	 * Asigna la lista listacmbElementoHasta
	 * 
	 * @param listacmbElementoHasta
	 * Variable a asignar en  listacmbElementoHasta
	 */
	public void setListacmbElementoHasta(RegistroDataModelImpl listacmbElementoHasta) {
		this.listacmbElementoHasta = listacmbElementoHasta;
	}
	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
