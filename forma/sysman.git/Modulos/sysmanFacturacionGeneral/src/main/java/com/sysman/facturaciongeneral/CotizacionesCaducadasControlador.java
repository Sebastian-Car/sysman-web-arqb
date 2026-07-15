/*-
 * CotizacionesCaducadasControlador.java
 *
 * 1.0
 * 
 * 08/11/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;
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

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.CotizacionesCaducadasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 08/11/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  CotizacionesCaducadasControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private Date fechaCorte;
	private Date fecha;
	private boolean todos;
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
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CotizacionesCaducadasControlador
	 */
	public CotizacionesCaducadasControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2492;
			validarPermisos();
			fechaCorte = new Date();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		tabla="SF_COTIZACION";
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		try {
			parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			parametrosListado.put("FECHACORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte));

			urlListado = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(CotizacionesCaducadasControladorUrlEnum.URL678010.getValue());

			urlActualizacion = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(CotizacionesCaducadasControladorUrlEnum.URL678012.getValue());

		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}	
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CambiarCancelada
	 * en la vista
	 *
	 *
	 */
	public void oprimirCambiarCancelada() {
		//<CODIGO_DESARROLLADO>
		cambiarEstado();
		reasignarOrigen();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CotizacionVencer
	 * en la vista
	 *
	 *
	 */
	public void oprimirCotizacionVencer() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generarReporte();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control fechas
	 * 
	 * 
	 */
	public void cambiarfechas() {
		//<CODIGO_DESARROLLADO>
		reasignarOrigen();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	public void generarReporte(){
		try {

			Map<String, Object> reemplazos = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			reemplazos.put("fechaCorte",SysmanFunciones.convertirAFechaCadena(fechaCorte));
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NIT", SessionUtil.getCompaniaIngreso().getNit());

			Reporteador.resuelveConsulta("002650ListadoCotizacionesCaducar",Integer.parseInt(modulo), reemplazos, parametros);

			archivoDescarga = JsfUtil.exportarStreamed("002650ListadoCotizacionesCaducar", parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}		
	}
	
	public void cambiarEstado() {
		try {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("COMPANIA", compania);
		param.put("FECHACORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte));

		Parameter parameter = new Parameter();
		parameter.setFields(param);

		String urlEnumId = CotizacionesCaducadasControladorUrlEnum.URL678013.getValue();
		UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
		 
		int reg = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		
		JsfUtil.agregarMensajeInformativo("El estado de " + reg + " cotizaciones ha sido cambiado a cancelado.");
		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control cambiarTodo
	 * 
	 * 
	 */
	public void cambiarcambiarTodo() {
		//<CODIGO_DESARROLLADO>
		if(todos) {
			try {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("COMPANIA", compania);
				param.put("FECHACORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte));
				if(fecha != null) {
					param.put("FECHA", SysmanFunciones.convertirAFechaCadena(fecha));
					param.put("VALOR", 1);
				}else {
					param.put("FECHA", SysmanFunciones.convertirAFechaCadena(new Date()));
					param.put("VALOR", 0);
				}

				Parameter parameter = new Parameter();
				parameter.setFields(param);

				String urlEnumId = CotizacionesCaducadasControladorUrlEnum.URL678014.getValue();
				UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

				int reg = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
				
				JsfUtil.agregarMensajeInformativo("Se han seleccionado:" + reg + " cotizaciones.");
			} catch (SystemException | ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			cambiarfechas();

		}
		//</CODIGO_DESARROLLADO>
	}
		
		/**
		 * Metodo ejecutado al cambiar el control cambiarTodo
		 * 
		 * 
		 */
		public void cambiarFecha() {
			todos = false;
			cambiarcambiarTodo();
		}
		
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
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
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().remove("COMPANIA");
		registro.getCampos().remove("TIPO");
		registro.getCampos().remove("NUMERO");
		registro.getCampos().remove("NOMBRETERCERO");
		registro.getCampos().remove("TERCERO");
		registro.getCampos().remove("FECHA");
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
	}
	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	
	/**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
    	RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable fechaCorte
	 * 
	 * @return  fechaCorte
	 */
	public Date getFechaCorte() {
		return fechaCorte;
	}
	/**
	 * Asigna la variable  fechaCorte
	 * 
	 * @param  fechaCorte
	 * Variable a asignar en  fechaCorte
	 */
	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
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
	 * @return the todos
	 */
	public boolean isTodos() {
		return todos;
	}
	/**
	 * @param todos the todos to set
	 */
	public void setTodos(boolean todos) {
		this.todos = todos;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
