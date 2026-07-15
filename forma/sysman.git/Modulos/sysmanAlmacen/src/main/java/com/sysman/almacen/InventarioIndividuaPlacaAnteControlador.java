/*-
 * InventarioIndividuaPlacaAnteControlador.java
 *
 * 1.0
 * 
 * 13/08/2018
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

import com.sysman.almacen.enums.InventarioIndividuaPlacaAnteControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
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
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Controlador que imprime el inventario de placa anterior individual.
 *
 * @version 1.0, 13/08/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  InventarioIndividuaPlacaAnteControlador extends BeanBaseModal{
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

	private String cargo;

	private String ordenar;

	private String formatoinvent;

	private int verReporte;
	
	private String cmbResponsable;
	
	private String observacion;
	
	private String nombreResponsable;
	
	private StreamedContent archivoDescarga;
	
	private RegistroDataModelImpl listaResponsable;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InventarioIndividuaPlacaAnteControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public InventarioIndividuaPlacaAnteControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.INVENTARIO_INDIVIDUA_PLACA_ANTE_CONTROLADOR.getCodigo();
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
		try {
			digitos = ejbSysmanUtil.consultarParametro(compania,"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
			cargo = ejbSysmanUtil.consultarParametro(compania, "CARGO COORDINADOR ALMACEN", modulo, new Date(),false);
			ordenar = ejbSysmanUtil.consultarParametro(compania, "ORDENADOR ALMACEN", modulo, new Date(),false);
			formatoinvent= ejbSysmanUtil.consultarParametro(compania, "FORMATO INVENTARIO INDIVIDUAL", modulo, new Date(),false);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
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
		/*
FR1891-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   'formularioAbrir 10, Me.Name
End Sub
		 */
		cargarListaResponsable();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaResponsable
	 * 
	 */
	public void cargarListaResponsable(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioIndividuaPlacaAnteControladorUrlEnum.URL2472.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"CEDULA");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodos de imprimir.
	 * 
	 */
	public void oprimirbtnPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generaInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	
	public void oprimirbtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generaInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	
	public void generaInforme(ReportesBean.FORMATOS formato) {

		try {
			reporte = "001866INVINDIVIDUALCORT";
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("digito", digitos);
			reemplazar.put("cmbResponsable", cmbResponsable);
	
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros); 
			parametros.put("PR_CARGO", cargo);
			parametros.put("PR_ORDENAR", ordenar);
			parametros.put("PR_MOSTAR", "INV_INDIVIDUAL_CORT".equals(formatoinvent)? true:false);
			parametros.put("PR_OBSERVACIONES",observacion);
			parametros.put("PR_RESPONSABILIDAD_I_INV_INDIVIDUAL",nombreResponsable);
		

			archivoDescarga = JsfUtil.exportarStreamed(reporte,parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException   e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo de seleccion de la lista.
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsable(SelectEvent event) {
				Registro registroAux = (Registro) event.getObject();
				cmbResponsable = registroAux.getCampos().get("CEDULA").toString();
				nombreResponsable = SysmanFunciones
						.nvl(registroAux.getCampos().get("NOMBRE"), "")
						.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cmbResponsable
	 * 
	 * @return  cmbResponsable
	 */
	public String getCmbResponsable() {
		return cmbResponsable;
	}
	/**
	 * Asigna la variable  cmbResponsable
	 * 
	 * @param  cmbResponsable
	 * Variable a asignar en  cmbResponsable
	 */
	public void setCmbResponsable(String cmbResponsable) {
		this.cmbResponsable = cmbResponsable;
	}
//	
	/**
	 * Retorna la variable nombreResponsable
	 * 
	 * @return  nombreResponsable
	 */
	public String getNombreResponsable() {
		return nombreResponsable;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	/**
	 * Asigna la variable  nombreResponsable
	 * 
	 * @param  nombreResponsable
	 * Variable a asignar en  nombreResponsable
	 */
	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
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
	 * Retorna la lista listaResponsable
	 * 
	 * @return listaResponsable
	 */
	public RegistroDataModelImpl getListaResponsable() {
		return listaResponsable;
	}
	/**
	 * Asigna la lista listaResponsable
	 * 
	 * @param listaResponsable
	 * Variable a asignar en  listaResponsable
	 */
	public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
		this.listaResponsable = listaResponsable;
	}
	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getFormatoinvent() {
		return formatoinvent;
	}
	public void setFormatoinvent(String formatoinvent) {
		this.formatoinvent = formatoinvent;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	public String getOrdenar() {
		return ordenar;
	}
	public void setOrdenar(String ordenar) {
		this.ordenar = ordenar;
	}
	public int getVerReporte() {
		return verReporte;
	}
	public void setVerReporte(int verReporte) {
		this.verReporte = verReporte;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
