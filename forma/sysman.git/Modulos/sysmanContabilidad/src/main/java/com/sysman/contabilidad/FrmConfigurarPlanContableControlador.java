/*-
 * FrmConfigurarPlanContableControlador.java
 *
 * 1.0
 * 
 * 27/10/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.FrmConfigurarPlanContableControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmGeneraNuevoMarcoNorControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 27/10/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmConfigurarPlanContableControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se
	 * activa la edicion de un registro. Toma el valor del indice
	 * dentro de la grilla del registro seleccionado para editar
	 */
	private int indice;
	//<DECLARAR_ATRIBUTOS>
	private String anio;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnio;
	private List<Registro> listaColumna;
	private RegistroDataModelImpl listaConcepto;
	private RegistroDataModelImpl listaConceptoE;

	private RegistroDataModelImpl listaPatrimonio;
	private RegistroDataModelImpl listaPatrimonioE;
	
	 private RegistroDataModelImpl listaConceptoIntegral;
	 private RegistroDataModelImpl listaConceptoIntegralE;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	private String informe;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	/**
	 * Crea una nueva instancia de FrmConfigurarPlanContableControlador
	 */
	public FrmConfigurarPlanContableControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2547;
			validarPermisos();
			anio = SysmanFunciones.toString(SysmanFunciones.ano(new Date()));
			informe = "1";
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
		enumBase = GenericUrlEnum.PLANCONTABLE;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
		cargarListaAnio();
		cargarListaColumna();
		cargarListaConcepto();
		cargarListaConceptoE();
		cargarListaPatrimonio(); 
		cargarListaPatrimonioE();
		cargarListaConceptoIntegral(); 
		cargarListaConceptoIntegralE();
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
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANIO.getName(), anio);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConfigurarPlanContableControladorUrlEnum.URL16234.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConfigurarPlanContableControladorUrlEnum.URL16236.getValue());
	}
	/**
	 * Retorna la variable indice 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmGeneraNuevoMarcoNorControladorUrlEnum.URL6990.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 */
	public void cargarListaConcepto(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConfigurarPlanContableControladorUrlEnum.URL1985001.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);
		param.put("INFORME", "002865EstadoSituacionFinanciera");

		listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 */
	public void  cargarListaConceptoE(){
		listaConceptoE = listaConcepto;
	}

	/**
	 * 
	 * Carga la lista listaPatrimonio
	 *
	 */
	public void cargarListaPatrimonio(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConfigurarPlanContableControladorUrlEnum.URL1985003.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);

		listaPatrimonio  = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaPatrimonio
	 *
	 */
	public void  cargarListaPatrimonioE(){
		listaPatrimonioE= listaPatrimonio;
	}
	/**
	 * 
	 * Carga la lista listaConceptoIntegral
	 *
	 */
	public void cargarListaConceptoIntegral(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConfigurarPlanContableControladorUrlEnum.URL1985001.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);
		param.put("INFORME", "002866EstadoResultadoIntegral");

		listaConceptoIntegral = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaConceptoIntegral
	 *
	 */
	public void  cargarListaConceptoIntegralE(){
		listaConceptoIntegralE = listaConceptoIntegral;
	}
	/**
	 * 
	 * Carga la lista listaColumna
	 *
	 */
	public void cargarListaColumna(){
		Registro reg;
		listaColumna = new ArrayList<>();
		reg = new Registro();
		reg.getCampos().put(GeneralParameterEnum.CODIGO.getName(), "CAPITAL");
		reg.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), "Capital");
		listaColumna.add(reg);

		reg = new Registro();
		reg.getCampos().put(GeneralParameterEnum.CODIGO.getName(), "RESERVAS");
		reg.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), "Reservas");
		listaColumna.add(reg);

		reg = new Registro();
		reg.getCampos().put(GeneralParameterEnum.CODIGO.getName(), "RESULTADOS");
		reg.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), "Resultados del ejercicio");
		listaColumna.add(reg);

		reg = new Registro();
		reg.getCampos().put(GeneralParameterEnum.CODIGO.getName(), "RESULTADOS_ANT");
		reg.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), "Resultados ejercicios anteriores");
		listaColumna.add(reg);

		reg = new Registro();
		reg.getCampos().put(GeneralParameterEnum.CODIGO.getName(), "OTRAS");
		reg.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), "Otras partidas de patrimonio");
		listaColumna.add(reg);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PasarVigencia
	 * en la vista
	 *
	 *
	 */
	public void oprimirPasarVigencia() {
		//<CODIGO_DESARROLLADO>
		String[] campos = new String[2];
		campos[0] = GeneralParameterEnum.ANIO.getName();

		Object[] valores = new Object[2];
		valores[0] = anio;

		SessionUtil.cargarModalDatosFlashCerrar(
				Integer.toString(2582),
				SessionUtil.getModulo(), campos, valores);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		//<CODIGO_DESARROLLADO>
		reasignarOrigen();	
		cargarListaConcepto();
		cargarListaConceptoE();
		cargarListaPatrimonio(); 
		cargarListaPatrimonioE();
		cargarListaConceptoIntegral(); 
		cargarListaConceptoIntegralE();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Informe
	 * 
	 * 
	 */
	public void cambiarInforme() {
		//<CODIGO_DESARROLLADO>
		if(informe.equals("3")) {
			parametrosListado.put("PATRIMONIO", "T");
		}else {
			parametrosListado.put("PATRIMONIO", null);
		}
		listaInicial.load();
		cargarListaConcepto();
		cargarListaConceptoIntegral(); 
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Concepto en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarConceptoC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("COLUMNA_REPORTE_VACIO",
				null);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Patrimonio en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarPatrimonioC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("COLUMNA_REPORTE_VACIO",
				null);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Columna en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarColumnaC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		String aux = SysmanFunciones.toString(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get("COLUMNA_REPORTE_PATR"));
		if (aux == null || aux.isEmpty()) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("COLUMNA_REPORTE_VACIO", "-1");
		}else {
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("COLUMNA_REPORTE_PATR",
					aux);
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("COLUMNA_REPORTE_VACIO",
					null);
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ConceptoIntegral en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarConceptoIntegralC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("COLUMNA_REPORTE_VACIO",
				null);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConcepto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConcepto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTOS_FINANCIEROS", SysmanFunciones.toString(registroAux.getCampos().get("CODIGO")));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConcepto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		if (auxiliar == null || auxiliar.isEmpty()) {
			auxiliar = "-1";
		}
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPatrimonio
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPatrimonio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTOS_PATRIMONIO", SysmanFunciones.toString(registroAux.getCampos().get("CODIGO")));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPatrimonio
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPatrimonioE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		if (auxiliar == null || auxiliar.isEmpty()) {
			auxiliar = "-1";
		}
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConceptoIntegral
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoIntegral(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTOS_INTEGRAL", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConceptoIntegral
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoIntegralE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
		if (auxiliar == null || auxiliar.isEmpty()) {
			auxiliar = "-1";
		}
	}
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
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
		registro.getCampos().remove(GeneralParameterEnum.CLASECUENTA.getName());
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		registro.getCampos().remove(GeneralParameterEnum.MOVIMIENTO.getName());
		registro.getCampos().remove("DESC_COLUMNA_REPORTE_PATR");
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
	 * Metodo ejecutado cuando se activa la edicion de un registro del
	 * formulario
	 * 
	 *
	 * @param registro
	 * registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
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
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}
	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public RegistroDataModelImpl getListaConcepto() {
		return listaConcepto;
	}
	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto
	 * Variable a asignar en  listaConcepto
	 */
	public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
		this.listaConcepto = listaConcepto;
	}
	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public RegistroDataModelImpl getListaConceptoE() {
		return listaConceptoE;
	}
	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto
	 * Variable a asignar en  listaConcepto
	 */
	public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
		this.listaConceptoE = listaConceptoE;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	/**
	 * @return the listaPatrimonio
	 */
	public RegistroDataModelImpl getListaPatrimonio() {
		return listaPatrimonio;
	}
	/**
	 * @param listaPatrimonio the listaPatrimonio to set
	 */
	public void setListaPatrimonio(RegistroDataModelImpl listaPatrimonio) {
		this.listaPatrimonio = listaPatrimonio;
	}
	/**
	 * @return the listaPatrimonioE
	 */
	public RegistroDataModelImpl getListaPatrimonioE() {
		return listaPatrimonioE;
	}
	/**
	 * @param listaPatrimonioE the listaPatrimonioE to set
	 */
	public void setListaPatrimonioE(RegistroDataModelImpl listaPatrimonioE) {
		this.listaPatrimonioE = listaPatrimonioE;
	}
	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the informe
	 */
	public String getInforme() {
		return informe;
	}
	/**
	 * @param informe the informe to set
	 */
	public void setInforme(String informe) {
		this.informe = informe;
	}
	/**
	 * @return the listaColumna
	 */
	public List<Registro> getListaColumna() {
		return listaColumna;
	}
	/**
	 * @param listaColumna the listaColumna to set
	 */
	public void setListaColumna(List<Registro> listaColumna) {
		this.listaColumna = listaColumna;
	}
	/**
	 * @return the listaConceptoIntegral
	 */
	public RegistroDataModelImpl getListaConceptoIntegral() {
		return listaConceptoIntegral;
	}
	/**
	 * @param listaConceptoIntegral the listaConceptoIntegral to set
	 */
	public void setListaConceptoIntegral(RegistroDataModelImpl listaConceptoIntegral) {
		this.listaConceptoIntegral = listaConceptoIntegral;
	}
	/**
	 * @return the listaConceptoIntegralE
	 */
	public RegistroDataModelImpl getListaConceptoIntegralE() {
		return listaConceptoIntegralE;
	}
	/**
	 * @param listaConceptoIntegralE the listaConceptoIntegralE to set
	 */
	public void setListaConceptoIntegralE(RegistroDataModelImpl listaConceptoIntegralE) {
		this.listaConceptoIntegralE = listaConceptoIntegralE;
	}

}
