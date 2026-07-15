/*-
 * ElementosDepreciadosCCControlador.java
 *
 * 1.0
 * 
 * 02/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.enums.ElementosDepreciadosCCControladorEnum;
import com.sysman.almacen.enums.ElementosDepreciadosCCControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 02/08/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class  ElementosDepreciadosCCControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	//<DECLARAR_ATRIBUTOS>
	/**
	 * variable que almacena la varible del combo del elemento inicial
	 */
    private String cmbElementoDesde;
    /**
	 * variable que almacena la varible del combo del elemento inicial
	 */
    private StreamedContent archivoDescarga;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cmbElementoHasta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String elementoDesde;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private  String elementoHasta;
	
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	
	
	
	private  String reporte;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private  String modulo;
	
	
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbElementoDesde;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbElementoHasta;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ElementosDepreciadosCCControlador
	 */
	public ElementosDepreciadosCCControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=1879;
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
		
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbElementoDesde(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ElementosDepreciadosCCControladorUrlEnum.URL3223
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ELEMENTO");
		
	}
	/**
	 * 
	 * Carga la lista listacmbElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbElementoHasta(){
 
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ElementosDepreciadosCCControladorUrlEnum.URL3224
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CMBELEMENTODESDE", cmbElementoDesde);
 


    		listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ELEMENTO");
		
		
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton genera_pdf
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException 
	 *
	 */
	public void oprimirgenera_pdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton genera_excel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */ 
	public void oprimirgenera_excel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	private void generarReporte(ReportesBean.FORMATOS formato) {
        // Creacion arreglos
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        // Codigo del reporte
        reporte = "001839ElementosDepreciadosCC"; 
        
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
        reemplazar.put(ElementosDepreciadosCCControladorEnum.elementoInicial.getValue(), cmbElementoDesde); 
        reemplazar.put(ElementosDepreciadosCCControladorEnum.elementoFinal.getValue(), cmbElementoHasta);  
        // </REEMPLAZAR VARIABLES EN CONSULTA>
        try {
            // <ENVIAR PARAMETROS AL REPORTE>
        	  parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                      .getNombre().toUpperCase());
            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
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
	 * listacmbElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL 
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementoDesde= SysmanFunciones.toString(registroAux.getCampos().get("ELEMENTO")) ;
		elementoDesde = SysmanFunciones.toString(registroAux.getCampos().get("DESCRIPCION")) ; 

		cargarListacmbElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacmbElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementoHasta= SysmanFunciones.toString(registroAux.getCampos().get("ELEMENTO"));
		elementoHasta= SysmanFunciones.toString(registroAux.getCampos().get("DESCRIPCION"));
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
	 * Retorna la variable lblElementoDesde
	 * 
	 * @return  lblElementoDesde
	 */
	
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	public RegistroDataModelImpl getListacmbElementoDesde() {
		return listacmbElementoDesde;
	}
	public String getElementoDesde() {
		return elementoDesde;
	}
	public void setElementoDesde(String elementoDesde) {
		this.elementoDesde = elementoDesde;
	} 
	public String getElementoHasta() {
		return elementoHasta;
	}
	public void setElementoHasta(String elementoHasta) {
		this.elementoHasta = elementoHasta;
	}
	public void setListacmbElementoDesde(RegistroDataModelImpl listacmbElementoDesde) {
		this.listacmbElementoDesde = listacmbElementoDesde;
	}
	public RegistroDataModelImpl getListacmbElementoHasta() {
		return listacmbElementoHasta;
	}
	public void setListacmbElementoHasta(RegistroDataModelImpl listacmbElementoHasta) {
		this.listacmbElementoHasta = listacmbElementoHasta;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
