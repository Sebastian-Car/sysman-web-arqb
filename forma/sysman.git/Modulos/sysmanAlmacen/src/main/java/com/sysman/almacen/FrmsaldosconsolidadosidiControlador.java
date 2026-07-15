/*-
 * FrmsaldosconsolidadosidiControlador.java
 *
 * 1.0
 * 
 * 18/06/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.ibm.icu.text.SimpleDateFormat;
import com.sysman.almacen.enums.FrmsaldosconsolidadosidiControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/06/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmsaldosconsolidadosidiControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual esta trabajando el usuario, el valor de esta constante
     * es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean ckConsolidado;
	private boolean ckPorCompania;
	private String elementoDesde;
	private String companiaInicial;
	private String elementoHasta;
	private String companiaFinal;
	private String nombreElementoDesde;
	private String nombreCompaniaI;
	private String nombreElementoHasta;
	private String nombreCompaniaF;
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
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaElementoDesde;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacompaniaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaElementoHasta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacompaniaFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmsaldosconsolidadosidiControlador
	 */
	public FrmsaldosconsolidadosidiControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2594;
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElementoDesde(); 
		cargarListacompaniaInicial(); 
		cargarListaElementoHasta(); 
		cargarListacompaniaFinal();
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
		ckConsolidado = true;
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaElementoDesde(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsaldosconsolidadosidiControladorUrlEnum.URL112158
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
	/**
	 * 
	 * Carga la lista listaElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaElementoHasta(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsaldosconsolidadosidiControladorUrlEnum.URL112160
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ELEMENTOINICIAL", elementoDesde);

		listaElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
	/**
	 * 
	 * Carga la lista listacompaniaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacompaniaInicial(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsaldosconsolidadosidiControladorUrlEnum.URL59031
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacompaniaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}
	/**
	 * 
	 * Carga la lista listacompaniaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacompaniaFinal(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsaldosconsolidadosidiControladorUrlEnum.URL59031
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacompaniaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnPdf
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBtnPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	
	private void generarInforme(FORMATOS formato) {

		try
		{
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			String strSQL;
			String informe = "002966InventarioConsolidado";
			reemplazar.put("elementoDesde", elementoDesde);
			reemplazar.put("elementoHasta", elementoHasta);

			if (ckPorCompania)
			{
				String whereCompania = "AND INVENTARIO_BODEGA.COMPANIA BETWEEN '" + companiaInicial + "' AND '" + companiaFinal + "'";
				reemplazar.put("compania", whereCompania);
			}
			else
			{
				reemplazar.put("compania", "");
			}
			strSQL = Reporteador.resuelveConsulta(informe,
					Integer.parseInt(modulo), reemplazar);
			parametros.put("PR_STRSQL", strSQL);
			archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (JRException | IOException | SysmanException ex)
		{
			JsfUtil.agregarMensajeError(ex.getMessage());
			Logger.getLogger(ListaInventarioExisControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control ckPorCompania
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarckPorCompania() {
		//<CODIGO_DESARROLLADO>
		if (ckPorCompania) {
			ckConsolidado = false;
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control cambiarckConsolidado
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarckConsolidado() {
		//<CODIGO_DESARROLLADO>
		if (ckConsolidado) {
			ckPorCompania = false;
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoDesde = retornarString(registroAux, GeneralParameterEnum.CODIGOELEMENTO.getName());
        nombreElementoDesde = retornarString(registroAux, "NOMBRELARGO");
        elementoHasta = nombreElementoHasta = null;
        cargarListaElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacompaniaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacompaniaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		companiaInicial= registroAux.getCampos().get("CODIGO").toString();
		nombreCompaniaI= registroAux.getCampos().get("NOMBRE").toString();
		
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        elementoHasta = retornarString(registroAux, GeneralParameterEnum.CODIGOELEMENTO.getName());
        nombreElementoHasta = retornarString(registroAux, "NOMBRELARGO");
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacompaniaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacompaniaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		companiaFinal= registroAux.getCampos().get("CODIGO").toString();
		nombreCompaniaF= registroAux.getCampos().get("NOMBRE").toString();
	}
	
	private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckConsolidado
	 * 
	 * @return  ckConsolidado
	 */
	public boolean getCkConsolidado() {
		return ckConsolidado;
	}
	/**
	 * Asigna la variable  ckConsolidado
	 * 
	 * @param  ckConsolidado
	 * Variable a asignar en  ckConsolidado
	 */
	public void setCkConsolidado(boolean ckConsolidado) {
		this.ckConsolidado = ckConsolidado;
	}
	/**
	 * Retorna la variable ckPorCompania
	 * 
	 * @return  ckPorCompania
	 */
	public boolean getCkPorCompania() {
		return ckPorCompania;
	}
	/**
	 * Asigna la variable  ckPorCompania
	 * 
	 * @param  ckPorCompania
	 * Variable a asignar en  ckPorCompania
	 */
	public void setCkPorCompania(boolean ckPorCompania) {
		this.ckPorCompania = ckPorCompania;
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
	 * Retorna la variable companiaInicial
	 * 
	 * @return  companiaInicial
	 */
	public String getCompaniaInicial() {
		return companiaInicial;
	}
	/**
	 * Asigna la variable  companiaInicial
	 * 
	 * @param  companiaInicial
	 * Variable a asignar en  companiaInicial
	 */
	public void setCompaniaInicial(String companiaInicial) {
		this.companiaInicial = companiaInicial;
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
	 * Retorna la variable companiaFinal
	 * 
	 * @return  companiaFinal
	 */
	public String getCompaniaFinal() {
		return companiaFinal;
	}
	/**
	 * Asigna la variable  companiaFinal
	 * 
	 * @param  companiaFinal
	 * Variable a asignar en  companiaFinal
	 */
	public void setCompaniaFinal(String companiaFinal) {
		this.companiaFinal = companiaFinal;
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
	 * Retorna la variable nombreCompaniaI
	 * 
	 * @return  nombreCompaniaI
	 */
	public String getNombreCompaniaI() {
		return nombreCompaniaI;
	}
	/**
	 * Asigna la variable  nombreCompaniaI
	 * 
	 * @param  nombreCompaniaI
	 * Variable a asignar en  nombreCompaniaI
	 */
	public void setNombreCompaniaI(String nombreCompaniaI) {
		this.nombreCompaniaI = nombreCompaniaI;
	}
	/**
	 * Retorna la variable nombreElementoHasta
	 * 
	 * @return  nombreElementoHasta
	 */
	public String getNombreElementoHasta() {
		return nombreElementoHasta;
	}
	/**
	 * Asigna la variable  nombreElementoHasta
	 * 
	 * @param  nombreElementoHasta
	 * Variable a asignar en  nombreElementoHasta
	 */
	public void setNombreElementoHasta(String nombreElementoHasta) {
		this.nombreElementoHasta = nombreElementoHasta;
	}
	/**
	 * Retorna la variable nombreCompaniaF
	 * 
	 * @return  nombreCompaniaF
	 */
	public String getNombreCompaniaF() {
		return nombreCompaniaF;
	}
	/**
	 * Asigna la variable  nombreCompaniaF
	 * 
	 * @param  nombreCompaniaF
	 * Variable a asignar en  nombreCompaniaF
	 */
	public void setNombreCompaniaF(String nombreCompaniaF) {
		this.nombreCompaniaF = nombreCompaniaF;
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
	 * Retorna la lista listacompaniaInicial
	 * 
	 * @return listacompaniaInicial
	 */
	public RegistroDataModelImpl getListacompaniaInicial() {
		return listacompaniaInicial;
	}
	/**
	 * Asigna la lista listacompaniaInicial
	 * 
	 * @param listacompaniaInicial
	 * Variable a asignar en  listacompaniaInicial
	 */
	public void setListacompaniaInicial(RegistroDataModelImpl listacompaniaInicial) {
		this.listacompaniaInicial = listacompaniaInicial;
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
	/**
	 * Retorna la lista listacompaniaFinal
	 * 
	 * @return listacompaniaFinal
	 */
	public RegistroDataModelImpl getListacompaniaFinal() {
		return listacompaniaFinal;
	}
	/**
	 * Asigna la lista listacompaniaFinal
	 * 
	 * @param listacompaniaFinal
	 * Variable a asignar en  listacompaniaFinal
	 */
	public void setListacompaniaFinal(RegistroDataModelImpl listacompaniaFinal) {
		this.listacompaniaFinal = listacompaniaFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
