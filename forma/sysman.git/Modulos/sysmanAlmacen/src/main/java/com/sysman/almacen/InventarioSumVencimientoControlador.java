/*-
 * InventarioSumVencimientoControlador.java
 *
 * 1.0
 * 
 * 22/11/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.InventarioSumVencimientoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 22/11/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  InventarioSumVencimientoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>

	private String conSaldoCero;

	private String ordenadoPor;
	private String elementoIni;
	private String elementoFin;
	private Date fecha;
	private String nombreElementoIni;
	private String nombreElementoFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaElementoIni;
	private RegistroDataModelImpl listaElementoFin;
	private String cNombreLargo;

	private String cInvCodElemento;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InventarioSumVencimientoControlador
	 */
	public InventarioSumVencimientoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			
			//2435
			numFormulario = GeneralCodigoFormaEnum.INVENTARIO_SUM_VENCIMIENTO_CONTROLADOR.getCodigo();
			
			cNombreLargo = "INVENTARIO.NOMBRELARGO";
			cInvCodElemento = "INVENTARIO.CODIGOELEMENTO";
			fecha = new Date();
			conSaldoCero = "2";
			ordenadoPor = "1";
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElementoIni(); cargarListaElementoFin();
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
	 * Carga la lista listaElementoIni
	 *
	 */
	public void cargarListaElementoIni(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioSumVencimientoControladorUrlEnum.URL3619.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElementoIni= new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());

	}
	/**
	 * 
	 * Carga la lista listaElementoFin
	 *
	 */
	public void cargarListaElementoFin(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioSumVencimientoControladorUrlEnum.URL4263.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("CODIGO", String.valueOf(elementoIni));

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElementoFin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());

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
		archivoDescarga=null;    
		generaReporte(FORMATOS.EXCEL);
		
		//</CODIGO_DESARROLLADO>
	}
	
	
	 private void generaReporte(FORMATOS formato) {
	        try {
	        	
	            String reporte = "800597InventarioSuministrosVencimiento";

	            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
	            HashMap<String, Object> reemplazar = new HashMap<>();
	            reemplazar.put("fecha", SysmanFunciones.formatearFecha(fecha));
	            reemplazar.put("elementoIni", elementoIni);
	            reemplazar.put("elementoFin", elementoFin);
	            reemplazar.put("orden", "2".equals(ordenadoPor) ? cNombreLargo : cInvCodElemento);
	            reemplazar.put("conSaldo", "2".equals(conSaldoCero) ? "": "AND (NVL(V_MOVIMIENTO_VENCIMIENTO.SALDO,0) * INVENTARIO.VLRUNITARIOPROM ) > 0");

	            String sql= Reporteador.resuelveConsulta(reporte,
	                            Integer.parseInt(SessionUtil.getModulo()),
	                            reemplazar);
	            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, reporte);
         
	        }
	        catch (JRException | IOException | NumberFormatException  | DRException | SQLException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException  e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	    }
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoIni
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoIni = SysmanFunciones.toString(registroAux.getCampos().get("CODIGOELEMENTO"));
		nombreElementoIni = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRELARGO"));
		elementoFin = null;
		nombreElementoFin = null;
		cargarListaElementoFin();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoFin
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFin= SysmanFunciones.toString(registroAux.getCampos().get("CODIGOELEMENTO"));
		nombreElementoFin = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRELARGO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable conSaldoCero
	 * 
	 * @return  conSaldoCero
	 */
	public String getConSaldoCero() {
		return conSaldoCero;
	}
	/**
	 * Asigna la variable  conSaldoCero
	 * 
	 * @param  conSaldoCero
	 * Variable a asignar en  conSaldoCero
	 */
	public void setConSaldoCero(String conSaldoCero) {
		this.conSaldoCero = conSaldoCero;
	}

	/**
	 * @return the ordenadoPor
	 */
	public String getOrdenadoPor() {
		return ordenadoPor;
	}
	/**
	 * @param ordenadoPor the ordenadoPor to set
	 */
	public void setOrdenadoPor(String ordenadoPor) {
		this.ordenadoPor = ordenadoPor;
	}
	/**
	 * Retorna la variable elementoIni
	 * 
	 * @return  elementoIni
	 */
	public String getElementoIni() {
		return elementoIni;
	}
	/**
	 * Asigna la variable  elementoIni
	 * 
	 * @param  elementoIni
	 * Variable a asignar en  elementoIni
	 */
	public void setElementoIni(String elementoIni) {
		this.elementoIni = elementoIni;
	}
	/**
	 * Retorna la variable elementoFin
	 * 
	 * @return  elementoFin
	 */
	public String getElementoFin() {
		return elementoFin;
	}
	/**
	 * Asigna la variable  elementoFin
	 * 
	 * @param  elementoFin
	 * Variable a asignar en  elementoFin
	 */
	public void setElementoFin(String elementoFin) {
		this.elementoFin = elementoFin;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	/**
	 * Retorna la variable nombreElementoIni
	 * 
	 * @return  nombreElementoIni
	 */
	public String getNombreElementoIni() {
		return nombreElementoIni;
	}
	/**
	 * Asigna la variable  nombreElementoIni
	 * 
	 * @param  nombreElementoIni
	 * Variable a asignar en  nombreElementoIni
	 */
	public void setNombreElementoIni(String nombreElementoIni) {
		this.nombreElementoIni = nombreElementoIni;
	}
	/**
	 * Retorna la variable nombreElementoFin
	 * 
	 * @return  nombreElementoFin
	 */
	public String getNombreElementoFin() {
		return nombreElementoFin;
	}
	/**
	 * Asigna la variable  nombreElementoFin
	 * 
	 * @param  nombreElementoFin
	 * Variable a asignar en  nombreElementoFin
	 */
	public void setNombreElementoFin(String nombreElementoFin) {
		this.nombreElementoFin = nombreElementoFin;
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
	 * Retorna la lista listaElementoIni
	 * 
	 * @return listaElementoIni
	 */
	public RegistroDataModelImpl getListaElementoIni() {
		return listaElementoIni;
	}
	/**
	 * Asigna la lista listaElementoIni
	 * 
	 * @param listaElementoIni
	 * Variable a asignar en  listaElementoIni
	 */
	public void setListaElementoIni(RegistroDataModelImpl listaElementoIni) {
		this.listaElementoIni = listaElementoIni;
	}
	/**
	 * Retorna la lista listaElementoFin
	 * 
	 * @return listaElementoFin
	 */
	public RegistroDataModelImpl getListaElementoFin() {
		return listaElementoFin;
	}
	/**
	 * Asigna la lista listaElementoFin
	 * 
	 * @param listaElementoFin
	 * Variable a asignar en  listaElementoFin
	 */
	public void setListaElementoFin(RegistroDataModelImpl listaElementoFin) {
		this.listaElementoFin = listaElementoFin;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
