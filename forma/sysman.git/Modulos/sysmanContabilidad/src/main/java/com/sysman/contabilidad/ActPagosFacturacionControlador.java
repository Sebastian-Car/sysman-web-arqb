/*-
 * ActPagosFacturacionControlador.java
 *
 * 1.0
 * 
 * 22/01/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ActPagosFacturacionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/01/2024
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  ActPagosFacturacionControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	private String tipoCobroInicial;

	private String facturaInicial;
	
	private String facturaFinal;
	
	private String tipoCobroFinal;
	
	private Date fechaInicial;
	
	private Date fechaFinal;

	private int anoInicial;
	
	private int anoFinal;
	
	//<DECLARAR_LISTAS>
	
	private List<Registro> listafacturaInicial;
	
	private List<Registro> listafacturaFinal;
	
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	
	private RegistroDataModelImpl listatipoCobroInicial;
	
	private RegistroDataModelImpl listatipoCobroFinal;
	
	   /**
     * variable EJB
     */
    @EJB
    EjbContabilidadTresRemote ejbContabilidadTres;
    
	/**
	 * Crea una nueva instancia de ActPagosFacturacionControlador
	 */
	public ActPagosFacturacionControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.ACT_PAGOS_FACTURACION
                    .getCodigo();
			validarPermisos();
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

		
		cargarListafacturaInicial();
		cargarListafacturaFinal();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListatipoCobroInicial();
		cargarListatipoCobroFinal();
		
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
	 * Carga la lista listafacturaInicial
	 */
	public void cargarListafacturaInicial(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),String.valueOf(compania));
		param.put("TIPO_INI",tipoCobroInicial);
		param.put("TIPO_FIN",tipoCobroFinal);
		param.put("ANO_INI",anoInicial);
		param.put("ANO_FIN",anoFinal);

		try {
			listafacturaInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ActPagosFacturacionControladorUrlEnum.URL661075
									.getValue())
							.getUrl(), param));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listafacturaFinal
	 *
	 */
	public void cargarListafacturaFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),String.valueOf(compania));
		param.put("TIPO_INI",tipoCobroInicial);
		param.put("TIPO_FIN",tipoCobroFinal);
		param.put("ANO_INI",anoInicial);
		param.put("ANO_FIN",anoFinal);

		try {
			listafacturaFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ActPagosFacturacionControladorUrlEnum.URL661075
									.getValue())
							.getUrl(), param));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listatipoCobroInicial
	 *
	 */
	public void cargarListatipoCobroInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ActPagosFacturacionControladorUrlEnum.URL665031
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listatipoCobroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listatipoCobroFinal
	 *
	 */
	public void cargarListatipoCobroFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ActPagosFacturacionControladorUrlEnum.URL665031
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listatipoCobroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Actualizar
	 * en la vista
	 *
	 */
	public void oprimirActualizar() {
		try {
			String general = "1";
			ejbContabilidadTres.actPagosFacturacion(compania, fechaInicial, fechaFinal, tipoCobroInicial,
					tipoCobroFinal, facturaInicial, facturaFinal, general);
			JsfUtil.agregarMensajeInformativo(
                    idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cambiarfechaInicial() {

		anoInicial = obtenerAnio(fechaInicial);
	}
	
	/**
	 * Metodo ejecutado al cambiar el control fechaFinal
	 * 
	 */
	public void cambiarfechaFinal() {

		anoFinal = obtenerAnio(fechaFinal);

	}

	private int obtenerAnio(Date fecha) {
		int respuesta = 0;
		respuesta = SysmanFunciones.ano(fecha);		
		return respuesta;
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipoCobroInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipoCobroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoCobroInicial= registroAux.getCampos().get("CODIGO").toString();

		cargarListafacturaInicial();
		cargarListafacturaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipoCobroFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipoCobroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoCobroFinal= registroAux.getCampos().get("CODIGO").toString();

		cargarListafacturaInicial();
		cargarListafacturaFinal();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoCobroInicial
	 * 
	 * @return  tipoCobroInicial
	 */
	public String getTipoCobroInicial() {
		return tipoCobroInicial;
	}
	/**
	 * Asigna la variable  tipoCobroInicial
	 * 
	 * @param  tipoCobroInicial
	 * Variable a asignar en  tipoCobroInicial
	 */
	public void setTipoCobroInicial(String tipoCobroInicial) {
		this.tipoCobroInicial = tipoCobroInicial;
	}
	/**
	 * Retorna la variable facturaInicial
	 * 
	 * @return  facturaInicial
	 */
	public String getFacturaInicial() {
		return facturaInicial;
	}
	/**
	 * Asigna la variable  facturaInicial
	 * 
	 * @param  facturaInicial
	 * Variable a asignar en  facturaInicial
	 */
	public void setFacturaInicial(String facturaInicial) {
		this.facturaInicial = facturaInicial;
	}
	/**
	 * Retorna la variable facturaFinal
	 * 
	 * @return  facturaFinal
	 */
	public String getFacturaFinal() {
		return facturaFinal;
	}
	/**
	 * Asigna la variable  facturaFinal
	 * 
	 * @param  facturaFinal
	 * Variable a asignar en  facturaFinal
	 */
	public void setFacturaFinal(String facturaFinal) {
		this.facturaFinal = facturaFinal;
	}
	/**
	 * Retorna la variable tipoCobroFinal
	 * 
	 * @return  tipoCobroFinal
	 */
	public String getTipoCobroFinal() {
		return tipoCobroFinal;
	}
	/**
	 * Asigna la variable  tipoCobroFinal
	 * 
	 * @param  tipoCobroFinal
	 * Variable a asignar en  tipoCobroFinal
	 */
	public void setTipoCobroFinal(String tipoCobroFinal) {
		this.tipoCobroFinal = tipoCobroFinal;
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
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listafacturaInicial
	 * 
	 * @return listafacturaInicial
	 */
	public List<Registro> getListafacturaInicial() {
		return listafacturaInicial;
	}
	/**
	 * Asigna la lista listafacturaInicial
	 * 
	 * @param listafacturaInicial
	 * Variable a asignar en  listafacturaInicial
	 */
	public void setListafacturaInicial(List<Registro> listafacturaInicial) {
		this.listafacturaInicial = listafacturaInicial;
	}
	/**
	 * Retorna la lista listafacturaFinal
	 * 
	 * @return listafacturaFinal
	 */
	public List<Registro> getListafacturaFinal() {
		return listafacturaFinal;
	}
	/**
	 * Asigna la lista listafacturaFinal
	 * 
	 * @param listafacturaFinal
	 * Variable a asignar en  listafacturaFinal
	 */
	public void setListafacturaFinal(List<Registro> listafacturaFinal) {
		this.listafacturaFinal = listafacturaFinal;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listatipoCobroInicial
	 * 
	 * @return listatipoCobroInicial
	 */
	public RegistroDataModelImpl getListatipoCobroInicial() {
		return listatipoCobroInicial;
	}
	/**
	 * Asigna la lista listatipoCobroInicial
	 * 
	 * @param listatipoCobroInicial
	 * Variable a asignar en  listatipoCobroInicial
	 */
	public void setListatipoCobroInicial(RegistroDataModelImpl listatipoCobroInicial) {
		this.listatipoCobroInicial = listatipoCobroInicial;
	}
	/**
	 * Retorna la lista listatipoCobroFinal
	 * 
	 * @return listatipoCobroFinal
	 */
	public RegistroDataModelImpl getListatipoCobroFinal() {
		return listatipoCobroFinal;
	}
	/**
	 * Asigna la lista listatipoCobroFinal
	 * 
	 * @param listatipoCobroFinal
	 * Variable a asignar en  listatipoCobroFinal
	 */
	public void setListatipoCobroFinal(RegistroDataModelImpl listatipoCobroFinal) {
		this.listatipoCobroFinal = listatipoCobroFinal;
	}
	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * @return the anoInicial
	 */
	public int getAnoInicial() {
		return anoInicial;
	}
	/**
	 * @param anoInicial the anoInicial to set
	 */
	public void setAnoInicial(int anoInicial) {
		this.anoInicial = anoInicial;
	}
	/**
	 * @return the anoFinal
	 */
	public int getAnoFinal() {
		return anoFinal;
	}
	/**
	 * @param anoFinal the anoFinal to set
	 */
	public void setAnoFinal(int anoFinal) {
		this.anoFinal = anoFinal;
	}

}
