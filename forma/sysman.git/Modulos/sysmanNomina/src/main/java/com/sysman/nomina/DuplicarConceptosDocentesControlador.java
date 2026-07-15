/*-
 * DuplicarConceptosDocentesControlador.java
 *
 * 1.0
 * 
 * 21/03/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.IOException;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDiezRemote;
import com.sysman.nomina.enums.DuplicarConceptosDocentesControladorUrlEnum;
import com.sysman.nomina.enums.NovedadesControladorEnum;
import com.sysman.nomina.enums.PeriodoTrabajoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 21/03/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  DuplicarConceptosDocentesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	private String nombreConcepto;
	private String nombreEmpleado;
	private String concepto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String empleado;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String escalafon;
	
	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAno;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;
	private List<Registro> listaProceso;
	private List<Registro> listaConcepto;
	private RegistroDataModelImpl listaEmpleado;
	//</DECLARAR_LISTAS>
	
	@EJB
	private EjbNominaDiezRemote nominaDiezRemote;
	//<DECLARAR_LISTAS_COMBO_GRANDE>

	private List<Registro> listaEscalafon;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DuplicarConceptosDocentesControlador
	 */
	public DuplicarConceptosDocentesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2455;
			validarPermisos();

			proceso = validarSessionCadena(
					SessionUtil.getSessionVar("procesoNomina"));
			anio = validarSessionCadena(
					SessionUtil.getSessionVar("anioNomina"));
			mes = validarSessionCadena(
					SessionUtil.getSessionVar("mesNomina"));
			periodo = validarSessionCadena(
					SessionUtil.getSessionVar("periodoNomina"));
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
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();
		cargarListaProceso();
		cargarListaConcepto();
		cargarListaEmpleado();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaEscalafon();
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
FR2455-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
     formularioAbrir 1, Me.Name
     'docmd.Minimize
     Me.EmpleadoI = [Forms]![Novedades]![CODIGO]
     Me.EmpleadoI.Requery
     Me.ConceptoI.Requery
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL4735
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL5723
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaPeriodo
	 *
	 */
	public void cargarListaPeriodo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		try {
			listaPeriodo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL7274
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaProceso = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL4058
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 */
	public void cargarListaConcepto(){
	}
	/**
	 * 
	 * Carga la lista listaEmpleado
	 *
	 */
	public void cargarListaEmpleado(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DuplicarConceptosDocentesControladorUrlEnum.URL6890
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_EMPLEADO");
	}
	/**
	 * 
	 * Carga la lista listaEscalafon
	 *
	 */
	public void cargarListaEscalafon(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaEscalafon = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DuplicarConceptosDocentesControladorUrlEnum.URL001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar
	 * en la vista
	 *
	 *
	 */
	public void oprimirPresentar() {
		//<CODIGO_DESARROLLADO>
		
		try {
			nominaDiezRemote.duplicarNovedadesVac(compania, proceso, concepto, empleado, nombreEmpleado, escalafon, anio, mes, periodo, SessionUtil.getUser().getCodigo());
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		
		HashMap<String, Object> parametros = new HashMap<>();
		HashMap<String, Object> reemplazar = new HashMap<>();
		try {
			 Reporteador.resuelveConsulta(
                     NovedadesControladorEnum.REPORTE000070.getValue(),
                     Integer.parseInt(modulo), reemplazar,
                     parametros); 
			 
			archivoDescarga = JsfUtil.exportarStreamed(
			        NovedadesControladorEnum.REPORTE000070.getValue(),
			        parametros,
			        ConectorPool.ESQUEMA_SYSMAN,
			        ReportesBean.FORMATOS.PDF);
		}
		catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(DuplicarConceptosDocentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException  e) {
            if (!("No existen datos").equals(e.getMessage().toString())) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCancelar() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		cargarListaMes();

		mes = null;
		periodo = null;
		listaPeriodo = null;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * 
	 */
	public void cambiarMes() {
		//<CODIGO_DESARROLLADO>
		cargarListaPeriodo();
		periodo = null;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 * 
	 */
	public void cambiarProceso() {
		//<CODIGO_DESARROLLADO>
		cargarListaAno();
		anio = null;
		mes = null;
		periodo = null;
		listaMes = null;
		listaPeriodo = null;
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
		empleado= registroAux.getCampos().get("ID_DE_EMPLEADO").toString();
		nombreEmpleado= registroAux.getCampos().get("NOMBRECOMPLETO").toString();
	}

	private String validarSessionCadena(Object objeto) {
		return SysmanFunciones.validarVariableVacio(objeto.toString()) ? ""
				: objeto.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable periodo
	 * 
	 * @return  periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * Asigna la variable  periodo
	 * 
	 * @param  periodo
	 * Variable a asignar en  periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * Retorna la variable concepto
	 * 
	 * @return  concepto
	 */
	public String getConcepto() {
		return concepto;
	}
	/**
	 * Asigna la variable  concepto
	 * 
	 * @param  concepto
	 * Variable a asignar en  concepto
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
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
	 * Retorna la variable escalafon
	 * 
	 * @return  escalafon
	 */
	public String getEscalafon() {
		return escalafon;
	}
	/**
	 * Asigna la variable  escalafon
	 * 
	 * @param  escalafon
	 * Variable a asignar en  escalafon
	 */
	public void setEscalafon(String escalafon) {
		this.escalafon = escalafon;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}
	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}
	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}
	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}
	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo
	 * Variable a asignar en  listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}
	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}
	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en  listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}
	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public List<Registro> getListaConcepto() {
		return listaConcepto;
	}
	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto
	 * Variable a asignar en  listaConcepto
	 */
	public void setListaConcepto(List<Registro> listaConcepto) {
		this.listaConcepto = listaConcepto;
	}

	/**
	 * @return the listaEmpleado
	 */
	public RegistroDataModelImpl getListaEmpleado() {
		return listaEmpleado;
	}
	/**
	 * @param listaEmpleado the listaEmpleado to set
	 */
	public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
		this.listaEmpleado = listaEmpleado;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaEscalafon
	 * 
	 * @return listaEscalafon
	 */
	public List<Registro> getListaEscalafon() {
		return listaEscalafon;
	}
	/**
	 * Asigna la lista listaEscalafon
	 * 
	 * @param listaEscalafon
	 * Variable a asignar en  listaEscalafon
	 */
	public void setListaEscalafon(List<Registro> listaEscalafon) {
		this.listaEscalafon = listaEscalafon;
	}
	/**
	 * @return the nombreConcepto
	 */
	public String getNombreConcepto() {
		return nombreConcepto;
	}
	/**
	 * @param nombreConcepto the nombreConcepto to set
	 */
	public void setNombreConcepto(String nombreConcepto) {
		this.nombreConcepto = nombreConcepto;
	}
	/**
	 * @return the nombreEmpleado
	 */
	public String getNombreEmpleado() {
		return nombreEmpleado;
	}
	/**
	 * @param nombreEmpleado the nombreEmpleado to set
	 */
	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
