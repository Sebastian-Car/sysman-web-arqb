/*-
 * DiscoCesantiasFnaCbyControlador.java
 *
 * 1.0
 * 
 * 23/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.DiscoCesantiasFnaCbyControladorUrlEnum;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/04/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  DiscoCesantiasFnaCbyControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	private boolean ckMesDif;
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
    private String fondo;
    private String nombreFondo;
    private boolean visibleMes13;

	private List<Registro> listaAno1;
	private List<Registro> listaMes1;
	private List<Registro> listaPeriodo1;
	private List<Registro> listaProceso;
		
	private StreamedContent archivoDescarga;

	private RegistroDataModelImpl listaBanco1;
	
	@EJB
	private EjbNominaSeisRemote ejbNominaSeis;
	/**
	 * Crea una nueva instancia de DiscoCesantiasFnaCbyControlador
	 */
	public DiscoCesantiasFnaCbyControlador() 
	{
		super();
		
		compania = SessionUtil.getCompania();
		try 
		{
			numFormulario = GeneralCodigoFormaEnum.CESANTIAS_FNA.getCodigo();
			validarPermisos();
		} 
		catch (Exception ex) 
		{
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
	public void inicializar()
	{
		proceso = (String) SessionUtil.getSessionVar("procesoNomina");
		anio = (String) SessionUtil.getSessionVar("anioNomina");
		mes = (String) SessionUtil.getSessionVar("mesNomina");
		periodo = (String) SessionUtil.getSessionVar("periodoNomina");
		fondo = "CES15";
		nombreFondo = "FONDO NACIONAL DE AHORRO";
		
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
		cargarListaProceso();
		cargarListaBanco1();

		abrirFormulario();
		
		if(periodo.equals("8"))
		{
			visibleMes13 = true;
			ckMesDif = true;
		}
		else
		{
			visibleMes13 = false;
			ckMesDif = false;
		}
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {}
	/**
	 * 
	 * Carga la lista listaAno1
	 *
	 */
	public void cargarListaAno1()
	{
		try 
		{
			Map<String,Object> param = new TreeMap<>();
			
			param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

			listaAno1 = RegistroConverter.toListRegistro(
							requestManager.getList(UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
															DiscoCesantiasFnaCbyControladorUrlEnum.URL3933
																					.getValue())
											.getUrl(), param));
		}
		catch (SystemException e) 
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMes1
	 *
	 */
	public void cargarListaMes1()
	{
		try 
		{
			Map<String,Object> param = new TreeMap<>();
			
			param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			param.put(GeneralParameterEnum.ANO.getName(),anio);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),proceso);
			
			listaMes1 = RegistroConverter.toListRegistro(
							requestManager.getList(UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
															DiscoCesantiasFnaCbyControladorUrlEnum.URL5057
																					.getValue())
											.getUrl(), param));
		}
		catch (SystemException e) 
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaPeriodo1
	 *
	 */
	public void cargarListaPeriodo1()
	{
		try 
		{
			Map<String,Object> param = new TreeMap<>();
			
			param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			param.put(GeneralParameterEnum.ANO.getName(),anio);
			param.put(GeneralParameterEnum.MES.getName(),mes);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),proceso);

			listaPeriodo1 = RegistroConverter.toListRegistro(
								requestManager.getList(UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
																DiscoCesantiasFnaCbyControladorUrlEnum.URL5973
																						.getValue())
												.getUrl(), param));
		}
		catch (SystemException e) 
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso()
	{
		try 
		{
			Map<String,Object> param = new TreeMap<>();
			
			param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

			listaProceso = RegistroConverter.toListRegistro(
								requestManager.getList(UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
																DiscoCesantiasFnaCbyControladorUrlEnum.URL8762
																						.getValue())
												.getUrl(), param));
		}
		catch (SystemException e) 
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaBanco1
	 *
	 */
	public void cargarListaBanco1()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                		  DiscoCesantiasFnaCbyControladorUrlEnum.URL8763.getValue());
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaBanco1 = new RegistroDataModelImpl(urlBean.getUrl(),
					  urlBean.getUrlConteo().getUrl(), param,
					  true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarDisco
	 * en la vista
	 *
	 */
	public void oprimirGenerarDisco()
	{
		archivoDescarga = null;   
		
		String nombreplano = "FNA_" + anio + SysmanFunciones.padl(mes, 2, "0") + "_" + SessionUtil.getCompaniaIngreso().getNit() +".txt";
		
		try 
		{
			String datos = ejbNominaSeis.generarDiscofna(compania, 
														 Integer.parseInt(proceso), 
														 Integer.parseInt(periodo), 
														 Integer.parseInt(mes), 
														 Integer.parseInt(anio), 
														 fondo,
														 ckMesDif);
			
			if (datos.isEmpty())
			{
				JsfUtil.agregarMensajeAlerta("No existe informacón con los datos seleccionados.");
			}
			else
			{
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, nombreplano);
			}
		} 
		catch (NumberFormatException | SystemException | JRException | IOException e) 
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
    /**
     * Metodo ejecutado al cambiar el control Ano1
     * 
     */
	public void cambiarAno1() 
	{
		mes = null;
		periodo = null;
		
		cargarListaMes1();
		cargarListaPeriodo1();
    }
    /**
     * Metodo ejecutado al cambiar el control Mes1
     * 
     */
	public void cambiarMes1() 
	{
		periodo = null;
		
		cargarListaPeriodo1();
    }
	/**
     * Metodo ejecutado al cambiar el control Periodo1
     * 
     */
	public void cambiarPeriodo1() 
	{
		if(periodo.equals("8"))
		{
			visibleMes13 = true;
			ckMesDif = true;
		}
		else
		{
			visibleMes13 = false;
			ckMesDif = false;
		}
    }
    /**
     * Metodo ejecutado al cambiar el control Proceso
     * 
     */
	public void cambiarProceso() 
	{
		anio = null;
		mes = null;
		periodo = null;
		
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
    }
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBanco1
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaBanco1(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		fondo = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), " ").toString();
		nombreFondo = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();
	}
	/**
	 * Retorna la variable ckmesDif
	 * 
	 * @return  ckmesDif
	 */
	public boolean isCkMesDif() {
		return ckMesDif;
	}
	/**
	 * Asigna la variable  ckmesDif
	 * 
	 * @param  ckmesDif
	 * Variable a asignar en  ckmesDif
	 */
	public void setCkMesDif(boolean ckMesDif) {
		this.ckMesDif = ckMesDif;
	}
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
     * Retorna la variable fondo
     * 
     * @return  fondo
     */
	public String getFondo() {
		return fondo;
	}
	/**
     * Asigna la variable  fondo
     * 
     * @param  fondo
     * Variable a asignar en  fondo
     */
	public void setFondo(String fondo) {
		this.fondo = fondo;
	}
	/**
     * Retorna la variable nombreFondo
     * 
     * @return  nombreFondo
     */
	public String getNombreFondo() {
		return nombreFondo;
	}
	/**
     * Asigna la variable  nombreFondo
     * 
     * @param  nombreFondo
     * Variable a asignar en  nombreFondo
     */
	public void setNombreFondo(String nombreFondo) {
		this.nombreFondo = nombreFondo;
	}
	/**
     * Retorna la variable visibleMes13
     * 
     * @return  visibleMes13
     */
	public boolean isVisibleMes13() {
        return visibleMes13;
    }
	/**
     * Asigna la variable  visibleMes13
     * 
     * @param  visibleMes13
     * Variable a asignar en  visibleMes13
     */
	public void setVisibleMes13(boolean visibleMes13) {
        this.visibleMes13 = visibleMes13;
    }
	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
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
	/**
	 * Retorna la lista listaAno1
	 * 
	 * @return listaAno1
	 */
	public List<Registro> getListaAno1() {
		return listaAno1;
	}
	/**
	 * Asigna la lista listaAno1
	 * 
	 * @param listaAno1
	 * Variable a asignar en  listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}
	/**
	 * Retorna la lista listaMes1
	 * 
	 * @return listaMes1
	 */
	public List<Registro> getListaMes1() {
		return listaMes1;
	}
	/**
	 * Asigna la lista listaMes1
	 * 
	 * @param listaMes1
	 * Variable a asignar en  listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}
	/**
	 * Retorna la lista listaPeriodo1
	 * 
	 * @return listaPeriodo1
	 */
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}
	/**
	 * Asigna la lista listaPeriodo1
	 * 
	 * @param listaPeriodo1
	 * Variable a asignar en  listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
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
	 * Retorna la lista listaBanco1
	 * 
	 * @return listaBanco1
	 */
	public RegistroDataModelImpl getListaBanco1() {
		return listaBanco1;
	}
	/**
	 * Asigna la lista listaBanco1
	 * 
	 * @param listaBanco1
	 * Variable a asignar en  listaBanco1
	 */
	public void setListaBanco1(RegistroDataModelImpl listaBanco1) {
		this.listaBanco1 = listaBanco1;
	}
}
