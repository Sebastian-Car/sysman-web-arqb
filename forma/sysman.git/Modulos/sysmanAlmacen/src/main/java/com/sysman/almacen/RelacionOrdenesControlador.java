/*-
 * RelacionOrdenesControlador.java
 *
 * 1.0
 * 
 * 18/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.text.ParseException;
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

import com.sysman.almacen.enums.RelacionOrdenesControladorUrlEnum;
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
 * Formulario que imprime reporte de las relaciones ordenadas
 *
 * @version 1.0, 18/10/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  RelacionOrdenesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo ;
	private String reporte ;
	//<DECLARAR_ATRIBUTOS>

	private String tipoContrato;

	private Date fechaInicial;

	private Date fechaFinal;

	private String nombreTipo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	private RegistroDataModelImpl listaTipoContrato;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private String formatoInforme;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de RelacionOrdenesControlador
	 */
	public RelacionOrdenesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.RELACION_ORDENES_CONTROLADOR.getCodigo();
			validarPermisos();

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
		fechaInicial = new Date();
		fechaFinal = new Date();
		cargarListaTipoContrato();
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
		
		formatoInforme = getParametro(
                "FORMATO RELACION DE ORDENES", "001943RelacionOrdenes");
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTipoContrato
	 *
	 */
	public void cargarListaTipoContrato(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(RelacionOrdenesControladorUrlEnum.URL2472.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaTipoContrato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"CODIGO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 *
	 */
	public void oprimirPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generarReporte(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
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
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	private void generarReporte(FORMATOS formato) {
		try{
			reporte= formatoInforme; 
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("tipoContrato",tipoContrato);
			//reemplazar.put("compania",compania);
			reemplazar.put("fechaIni", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazar.put("fechaFin", SysmanFunciones.convertirAFechaCadena(fechaFinal));

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros);    
			parametros.put("PR_FORMS_RELACIONORDENES_DESDE", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("PR_FORMS_RELACIONORDENES_HASTA", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			parametros.put("PR_RELACION_ORDENES_TIPOCONTRATO", nombreTipo);
			
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoContrato
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoContrato(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoContrato= registroAux.getCampos().get("CODIGO").toString();
		nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
		
	}
	
	 /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoContrato
	 * 
	 * @return  tipoContrato
	 */
	public String getTipoContrato() {
		return tipoContrato;
	}
	/**
	 * Asigna la variable  tipoContrato
	 * 
	 * @param  tipoContrato
	 * Variable a asignar en  tipoContrato
	 */
	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * Retorna la variable nombreTipo
	 * 
	 * @return  nombreTipo
	 */
	public String getNombreTipo() {
		return nombreTipo;
	}
	/**
	 * Asigna la variable  nombreTipo
	 * 
	 * @param  nombreTipo
	 * Variable a asignar en  nombreTipo
	 */
	public void setNombreTipo(String nombreTipo) {
		this.nombreTipo = nombreTipo;
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
	 * Retorna la lista listaTipoContrato
	 * 
	 * @return listaTipoContrato
	 */
	public RegistroDataModelImpl getListaTipoContrato() {
		return listaTipoContrato;
	}
	/**
	 * Asigna la lista listaTipoContrato
	 * 
	 * @param listaTipoContrato
	 * Variable a asignar en  listaTipoContrato
	 */
	public void setListaTipoContrato(RegistroDataModelImpl listaTipoContrato) {
		this.listaTipoContrato = listaTipoContrato;
	}
	public String getCompania() {
		return compania;
	}
	public String getModulo() {
		return modulo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
