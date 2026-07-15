/*-
 * FrmDetalleLiquidacionControlador.java
 *
 * 1.0
 * 
 * 19/01/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.IOException;
import java.sql.SQLException;
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.enums.FrmDetalleLiquidacionControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 19/01/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmDetalleLiquidacionControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String opcion;
	private String empleado;
	private String cedula;
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
	 */
	private RegistroDataModelImpl listaEmpleado;
	private String procesoSesion;
	private String mesSesion;
	private String periodoSesion;
	private String anioSesion;
	private String idEmpleado;

	@EJB
	private EjbNominaCeroRemote ejbNominaCero;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmDetalleLiquidacionControlador
	 */
	public FrmDetalleLiquidacionControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			opcion = "1";
			procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
			anioSesion = (String) SessionUtil.getSessionVar("anioNomina");
			mesSesion = (String) SessionUtil.getSessionVar("mesNomina");
			periodoSesion = (String) SessionUtil.getSessionVar("periodoNomina");
			numFormulario=2562;
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
		cargarListaEmpleado();
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
	 * Carga la lista listaEmpleado
	 *
	 */
	public void cargarListaEmpleado(){
		try {
			Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(
					compania,
					Integer.parseInt(procesoSesion),
					Integer.parseInt(anioSesion),
					Integer.parseInt(mesSesion),
					Integer.parseInt(periodoSesion), false, true);
			
			Date fechaIni = ejbNominaCero.getFechaPeriodoIniFin(
					compania,
					Integer.parseInt(procesoSesion),
					Integer.parseInt(anioSesion),
					Integer.parseInt(mesSesion),
					Integer.parseInt(periodoSesion), true, false);

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ESTADO.getName(), 1);
			param.put(GeneralParameterEnum.FECHAFIN.getName(), SysmanFunciones
					.convertirAFechaCadena(fechaFin));
			param.put(GeneralParameterEnum.FECHAINICIO.getName(), SysmanFunciones
					.convertirAFechaCadena(fechaIni));

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmDetalleLiquidacionControladorUrlEnum.URL210169
							.getValue());
			listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, "ID_DE_EMPLEADO");
		} catch (NumberFormatException | SystemException | ParseException e) {
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
		try {
			archivoDescarga=null;       
			Map<String, Object> reemplazos = new HashMap<>();
			reemplazos.put("anio", anioSesion);
			reemplazos.put("mes", mesSesion);
			reemplazos.put("periodo", periodoSesion);
			reemplazos.put("idEmpleado", opcion.equals("2")?idEmpleado:"NULL");

			String sql = Reporteador.resuelveConsulta("800730DetalleLiquidacion", Integer.parseInt(modulo),
					reemplazos);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, "800730DetalleLiquidacion");
		} catch (JRException | IOException | SQLException | DRException
				| com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Opcion
	 * 
	 * 
	 */
	public void cambiarOpcion() {
		//<CODIGO_DESARROLLADO>
		empleado = null;
		cedula = null;
		idEmpleado = null;
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEmpleado
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEmpleado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		empleado = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRECOMPLETO"));
		cedula = SysmanFunciones.toString(registroAux.getCampos().get("NUMERO_DCTO"));
		idEmpleado = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_EMPLEADO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcion
	 * 
	 * @return  opcion
	 */
	public String getOpcion() {
		return opcion;
	}
	/**
	 * Asigna la variable  opcion
	 * 
	 * @param  opcion
	 * Variable a asignar en  opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
	/**
	 * Retorna la variable empleado
	 * 
	 * @return  empleado
	 */
	public String getEmpleado() {
		return empleado;
	}
	/**
	 * Asigna la variable  empleado
	 * 
	 * @param  empleado
	 * Variable a asignar en  empleado
	 */
	public void setEmpleado(String empleado) {
		this.empleado = empleado;
	}
	/**
	 * Retorna la variable cedula
	 * 
	 * @return  cedula
	 */
	public String getcedula() {
		return cedula;
	}
	/**
	 * Asigna la variable  cedula
	 * 
	 * @param  cedula
	 * Variable a asignar en  cedula
	 */
	public void setcedula(String cedula) {
		this.cedula = cedula;
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
	 * Retorna la lista listaEmpleado
	 * 
	 * @return listaEmpleado
	 */
	public RegistroDataModelImpl getListaEmpleado() {
		return listaEmpleado;
	}
	/**
	 * Asigna la lista listaEmpleado
	 * 
	 * @param listaEmpleado
	 * Variable a asignar en  listaEmpleado
	 */
	public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
		this.listaEmpleado = listaEmpleado;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
