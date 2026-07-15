/*-
 * InformeDevoControInvenControlador.java
 *
 * 1.0
 * 
 * 22/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.InformacionGeneralDevolutivosControladorEnum;
import com.sysman.almacen.enums.InformeDevoControInvenControladorEnum;
import com.sysman.almacen.enums.InformeDevoControInvenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
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
/**
 *  Esta clase es el controlador para el formulario INFORME DEVOLUTIVOS CONTROL
 * INVENTARIO en Access "INFORMACION_GENERAL_DEVOLUTIVO", el cual es
 * llamado desde Almacen\Informes\Generales\Informe Devolutivo Control Inventario
 *
 * @version 1.0, 22/02/2021
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  InformeDevoControInvenControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
	 * Constante a nivel de clase que almacena el codigo del modulo en
	 * el cual esta trabajando el usuario, el valor de esta constante
	 * es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String modulo;
	/**
	 * Constante definida por el numero de veces que se realiza el
	 * llamado al campo CODIGOELEMENTO en el formulario, almacena el
	 * texto CODIGOELEMENTO
	 */
	private final String cCodigoElemento;

	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo que almacena el codigo del elemento inicial
	 * seleccionado en el formulario
	 */
	private String elementoDesde;
	/**
	 * Atributo que almacena el codigo del elemento final seleccionado
	 * en el formulario
	 */
	private String elementoHasta;
	/**
	 * Atributo que almacena el nombre del elemento inicial
	 * seleccionado en el formulario
	 */
	private String nombreElementoDesde;
	/**
	 * Atributo que almacena el codigo del elemento final seleccionado
	 * en el formulario
	 */
	private String nombreElementoHasta;

	/**
	 * Atributo que almacena la fecha que ha sido seleccionada en el
	 * formulario
	 */
	private Date fecha;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Listado de registros para el combo de Elemento Inicial
	 */
	private RegistroDataModelImpl listaElementoDesde;
	/**
	 * Listado de registros para el combo de Elemento Final
	 */
	private RegistroDataModelImpl listaElementoHasta;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InformeDevoControInvenControlador
	 */
	public InformeDevoControInvenControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		cCodigoElemento = InformacionGeneralDevolutivosControladorEnum.CODIGOELEMENTO
				.getValue();
		try {
			numFormulario = GeneralCodigoFormaEnum.INFORME_DEVO_CONTRO_INVEN_CONTROLADOR
					.getCodigo();// 2247
			validarPermisos();
			// <INI_ADICIONAL>
			fecha = new Date();
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
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
		cargarListaElementoDesde();
		cargarListaElementoHasta();
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
	 * Carga la lista listaElementoDesde
	 *
	 */
	public void cargarListaElementoDesde(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InformeDevoControInvenControladorUrlEnum.URL9785
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}
	/**
	 * 
	 * Carga la lista listaElementoHasta
	 *
	 */
	public void cargarListaElementoHasta(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InformeDevoControInvenControladorUrlEnum.URL9786
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(InformeDevoControInvenControladorEnum.ELEMENTOINICIAL
				.getValue(), elementoDesde);

		listaElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnPdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirBtnPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirBtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme();
		//</CODIGO_DESARROLLADO>
	}

	 private void generarInforme() {

	        try {
	        	
	            String datosExcel = "800422InformeDevolutivoConiInventario";

	            String ultimoDiaFecha = SysmanFunciones.convertirAFechaCadena(
	                            SysmanFunciones.ultimoDiaDate(fecha));
	            String fechaCadena = SysmanFunciones.convertirAFechaCadena(fecha);

	            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
	            Map<String, Object> reemplazar = new HashMap<>();
	            reemplazar.put("ultimoDiaFecha", ultimoDiaFecha);
	            reemplazar.put("compania", compania);
	            reemplazar.put("elementoInicial", elementoDesde);
	            reemplazar.put("elementoFinal", elementoHasta);
	            reemplazar.put("fechaCorte", fechaCadena);
	           
	      
	             datosExcel = Reporteador.resuelveConsulta("800422InformeDevolutivoConiInventario", 
	            			Integer.parseInt(modulo),
	            			reemplazar);

	            	
	            		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, 
	            				ConectorPool.ESQUEMA_SYSMAN,
	            				FORMATOS.EXCEL97,"800422InformeDevolutivoConiInventario");
	            	
	            
	        }
	        catch (JRException | IOException | SysmanException | ParseException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoDesde
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoDesde = retornarString(registroAux, cCodigoElemento);
		nombreElementoDesde = retornarString(registroAux, "NOMBRELARGO");
		elementoHasta = nombreElementoHasta = null;
		cargarListaElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoHasta
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoHasta = retornarString(registroAux, cCodigoElemento);
		nombreElementoHasta = retornarString(registroAux, "NOMBRELARGO");
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	private String retornarString(Registro reg, String campo) {
		return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
				: reg.getCampos().get(campo).toString();
	}
	/**
	 * Retorna la variable elementoDesde
	 * 
	 * @return  elementoDesde
	 */
	public String getElementoDesde() {
		return elementoDesde;
	}
	/**
	 * Asigna la variable  elementoDesde
	 * 
	 * @param  elementoDesde
	 * Variable a asignar en  elementoDesde
	 */
	public void setElementoDesde(String elementoDesde) {
		this.elementoDesde = elementoDesde;
	}
	/**
	 * Retorna la variable elementoHasta
	 * 
	 * @return  elementoHasta
	 */
	public String getElementoHasta() {
		return elementoHasta;
	}
	/**
	 * Asigna la variable  elementoHasta
	 * 
	 * @param  elementoHasta
	 * Variable a asignar en  elementoHasta
	 */
	public void setElementoHasta(String elementoHasta) {
		this.elementoHasta = elementoHasta;
	}
	/**
	 * Retorna la variable fecha
	 * 
	 * @return  fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * Asigna la variable  fecha
	 * 
	 * @param  fecha
	 * Variable a asignar en  fecha
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	/**
	 * Retorna la variable nombreElementoDesde
	 * 
	 * @return  nombreElementoDesde
	 */
	public String getNombreElementoDesde() {
		return nombreElementoDesde;
	}
	/**
	 * Asigna la variable  nombreElementoDesde
	 * 
	 * @param  nombreElementoDesde
	 * Variable a asignar en  nombreElementoDesde
	 */
	public void setNombreElementoDesde(String nombreElementoDesde) {
		this.nombreElementoDesde = nombreElementoDesde;
	}
	/**
	 * Retorna la variable nombreHasta
	 * 
	 * @return  nombreHasta
	 */
	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @return the nombreElementoHasta
	 */
	public String getNombreElementoHasta() {
		return nombreElementoHasta;
	}
	/**
	 * @param nombreElementoHasta the nombreElementoHasta to set
	 */
	public void setNombreElementoHasta(String nombreElementoHasta) {
		this.nombreElementoHasta = nombreElementoHasta;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaElementoDesde
	 * 
	 * @return listaElementoDesde
	 */
	public RegistroDataModelImpl getListaElementoDesde() {
		return listaElementoDesde;
	}
	/**
	 * Asigna la lista listaElementoDesde
	 * 
	 * @param listaElementoDesde
	 * Variable a asignar en  listaElementoDesde
	 */
	public void setListaElementoDesde(RegistroDataModelImpl listaElementoDesde) {
		this.listaElementoDesde = listaElementoDesde;
	}
	/**
	 * Retorna la lista listaElementoHasta
	 * 
	 * @return listaElementoHasta
	 */
	public RegistroDataModelImpl getListaElementoHasta() {
		return listaElementoHasta;
	}
	/**
	 * Asigna la lista listaElementoHasta
	 * 
	 * @param listaElementoHasta
	 * Variable a asignar en  listaElementoHasta
	 */
	public void setListaElementoHasta(RegistroDataModelImpl listaElementoHasta) {
		this.listaElementoHasta = listaElementoHasta;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
