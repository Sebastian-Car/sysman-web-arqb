/*-
 * ComprobanteCntRetencionSdControlador.java
 *
 * 1.0
 * 
 * 14/03/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ComprobanteCntRetencionSdControladorEnum;
import com.sysman.general.enums.ComprobanteCntRetencionSdControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @version 1.0, 14/03/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  ComprobanteCntRetencionSdControlador  extends BeanBaseContinuoAcmeImpl{
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
	private String auxiliar;
	private int ano;
	private String tipo;
	private String nombreComprobante;
	private String numeroComprobante;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaTipo;
	private List<Registro> listaTipoRetencion;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCodigoRetencion;
	private RegistroDataModelImpl listaCodigoRetencionE;
	
	 @EJB
	 private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Campo TIPORETENCION.
	 */
	private static final String CAMPO_TIPORETENCION = "TIPORETENCION";
	/**
	 * Campo VALORBASE.
	 */
	private static final String CAMPO_VALORBASE = "VALORBASE";
	/**
	 * Campo CODIGORETENCION.
	 */
	private static final String CAMPO_CODIGORETENCION = "CODIGORETENCION";


	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ComprobanteCntRetencionSdControlador
	 */
	public ComprobanteCntRetencionSdControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2250
			numFormulario = GeneralCodigoFormaEnum.COMPROBANTECNTRETENCIONSD_CONTROLADOR
					.getCodigo();

			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {
				ano = Integer.parseInt(parametros.get("ano").toString());
				tipo = extraerString(parametros.get("tipoComp"));
				numeroComprobante = extraerString(parametros.get("numeroComp"));
			}
			validarPermisos();
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
		enumBase = GenericUrlEnum.TEMP_COMPROBANTE_CNTRETENCION;
		nombreComprobante = tipo + " No. " + numeroComprobante;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		cargarListaTipo();
		cargarListaTipoRetencion();
		cargarListaCodigoRetencion(); 

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
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
		parametrosListado.put("TIPO", tipo);
		parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
				numeroComprobante);
	}

	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 */
	public void cargarListaTipo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaTipo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ComprobanteCntRetencionSdControladorUrlEnum.URL6758
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
	 * Carga la lista listaTipoRetencion
	 *
	 */
	public void cargarListaTipoRetencion(){
		try {
			listaTipoRetencion = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ComprobanteCntRetencionSdControladorUrlEnum.URL7072
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaCodigoRetencion
	 *
	 */
	public void cargarListaCodigoRetencion(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ComprobanteCntRetencionSdControladorUrlEnum.URL7597
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(ComprobanteCntRetencionSdControladorEnum.PARAM0.getValue(),
				compania);
		param.put(ComprobanteCntRetencionSdControladorEnum.PARAM1.getValue(),
				ano);
		param.put(ComprobanteCntRetencionSdControladorEnum.PARAM2.getValue(),
				registro.getCampos().get(CAMPO_TIPORETENCION));

		listaCodigoRetencion = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaCodigoRetencion
	 *
	 */
	public void  cargarListaCodigoRetencionE(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ComprobanteCntRetencionSdControladorUrlEnum.URL7597
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(ComprobanteCntRetencionSdControladorEnum.PARAM0.getValue(),
				compania);
		param.put(ComprobanteCntRetencionSdControladorEnum.PARAM1.getValue(),
				ano);
		param.put(ComprobanteCntRetencionSdControladorEnum.PARAM2.getValue(),
				listaInicial.getDatasource().get(indice % 10)
				.getCampos().get(CAMPO_TIPORETENCION));

		listaCodigoRetencionE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control TipoRetencion
	 * 
	 * 
	 */
	public void cambiarTipoRetencion() {
		//<CODIGO_DESARROLLADO>
		String tipoRet = SysmanFunciones
				.nvl(registro.getCampos().get(CAMPO_TIPORETENCION), "")
				.toString();
		if ("".equals(tipoRet)) {
			registro.getCampos().put(CAMPO_VALORBASE, null);
			registro.getCampos().put(CAMPO_CODIGORETENCION, null);
			registro.getCampos().put(GeneralParameterEnum.VALOR.getName(),
					null);
			return;
		}
		
		asignarValorBase(tipoRet);
		registro.getCampos().put(CAMPO_CODIGORETENCION, null);
		registro.getCampos().put(GeneralParameterEnum.VALOR.getName(), 0);
		cargarListaCodigoRetencion();
		//</CODIGO_DESARROLLADO>
	}
	
	
	 /**
     * Segun el tipo de retencion y el parametro <i>MONTO SOMETIDO
     * RETENCION IVA = VALOR IVA</i> asigna el valor base.
     * 
     * @param tipoRetencion
     * Tipo de Retencion.
     */
    private void asignarValorBase(Object tipoRetencion) {
        double nuevoValorBase;
        String parametroMontoIgualIVA = getParametro(
                        "MONTO SOMETIDO RETENCION IVA = VALOR IVA", "NO");
        if ("IVA".equals(tipoRetencion)
            && "SI".equals(parametroMontoIgualIVA)) {
            nuevoValorBase = 0;
        }
        else {
            nuevoValorBase = 0;
        }
        registro.getCampos().put(CAMPO_VALORBASE, nuevoValorBase);
    }
	/**
	 * Metodo ejecutado al cambiar el control Valor
	 * 
	 * 
	 */
	public void cambiarValor() {
		//<CODIGO_DESARROLLADO>

		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ValorBase
	 * 
	 * 
	 */
	public void cambiarValorBase() {
		//<CODIGO_DESARROLLADO>

		//</CODIGO_DESARROLLADO>
	}


	/**
	 * Metodo ejecutado al cambiar el control TipoRetencion en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarTipoRetencionC(int rowNum) {
		String tipoRet = SysmanFunciones
				.nvl(listaInicial.getDatasource().get(rowNum % 10)
						.getCampos().get(CAMPO_TIPORETENCION),
						"")
				.toString();
		if ("".equals(tipoRet)) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(CAMPO_VALORBASE, null);
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(CAMPO_CODIGORETENCION, null);
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(GeneralParameterEnum.VALOR.getName(), null);
			return;
		}

		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put(CAMPO_CODIGORETENCION, null);
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
				GeneralParameterEnum.VALOR.getName(),
				0);
		cargarListaCodigoRetencionE();
	}
	/**
	 * Metodo ejecutado al cambiar el control CodigoRetencion en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarCodigoRetencionC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}
	
	 public void eliminarRegistro()
	    {
	        try
	        {
	            Map<String, Object> params = new TreeMap<>();

	            UrlBean urlDelete = UrlServiceUtil.getInstance()
	                            .getUrlServiceByUrlByEnumID(ComprobanteCntRetencionSdControladorUrlEnum.URL7593.getValue());
	            requestManager.delete(urlDelete.getUrl(), params);

	            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
	        }
	        catch (SystemException e)
	        {
	            JsfUtil.agregarMensajeError(e.getMessage());
	            logger.error(e.getMessage(), e);

	        }
	    }
	/**
	 * Metodo ejecutado al cambiar el control Valor en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarValorC(int rowNum) {
		//<CODIGO_DESARROLLADO>


		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ValorBase en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarValorBaseC(int rowNum) {
		//<CODIGO_DESARROLLADO>

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoRetencion
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoRetencion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(CAMPO_CODIGORETENCION,
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoRetencion
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoRetencionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()));
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
		eliminarRegistro();
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
		registro.getCampos().remove("NOMBRERETENCIONES");
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", ano);
		registro.getCampos().put("TIPO", tipo);
		registro.getCampos().put("NUMERO", numeroComprobante);
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
		if (faltanCamposObligatorios()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB7"));
			return false;
		}
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
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove("TIPO");
		registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		registro.getCampos().remove(GeneralParameterEnum.PORCIVA.getName());
		registro.getCampos().remove("NOMBRERETENCIONES");
	}

	/**
	 * Validacion de campos obligatorios segun el tipo.
	 *
	 * @return Verdadero si hay camos nulos o vacios.
	 */
	private boolean faltanCamposObligatorios() {
		Map<String, Object> campos = registro.getCampos();
		if (SysmanFunciones.validarCampoVacio(campos, CAMPO_TIPORETENCION)
				|| SysmanFunciones.validarCampoVacio(campos, CAMPO_CODIGORETENCION)
				|| SysmanFunciones.validarCampoVacio(campos,
						GeneralParameterEnum.VALOR.getName())) {
			return true;
		}
		if (SysmanFunciones.validarCampoVacio(campos, CAMPO_VALORBASE)) {
			return true;
		}
		return false;
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
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
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
		cargarListaCodigoRetencionE();
	}
	  public void cerrarFormulario() {
	        RequestContext.getCurrentInstance().closeDialog(null);
	    }
	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
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

	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente
	 * de nulo.
	 * 
	 * @param object
	 * Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable nombreComprobante
	 * 
	 * @return  nombreComprobante
	 */
	public String getNombreComprobante() {
		return nombreComprobante;
	}
	/**
	 * Asigna la variable  nombreComprobante
	 * 
	 * @param  nombreComprobante
	 * Variable a asignar en  nombreComprobante
	 */
	public void setNombreComprobante(String nombreComprobante) {
		this.nombreComprobante = nombreComprobante;
	}
	/**
	 * Retorna la variable numeroComprobante
	 * 
	 * @return  numeroComprobante
	 */
	public String getNumeroComprobante() {
		return numeroComprobante;
	}
	/**
	 * Asigna la variable  numeroComprobante
	 * 
	 * @param  numeroComprobante
	 * Variable a asignar en  numeroComprobante
	 */
	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public List<Registro> getListaTipo() {
		return listaTipo;
	}
	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en  listaTipo
	 */
	public void setListaTipo(List<Registro> listaTipo) {
		this.listaTipo = listaTipo;
	}
	/**
	 * Retorna la lista listaTipoRetencion
	 * 
	 * @return listaTipoRetencion
	 */
	public List<Registro> getListaTipoRetencion() {
		return listaTipoRetencion;
	}
	/**
	 * Asigna la lista listaTipoRetencion
	 * 
	 * @param listaTipoRetencion
	 * Variable a asignar en  listaTipoRetencion
	 */
	public void setListaTipoRetencion(List<Registro> listaTipoRetencion) {
		this.listaTipoRetencion = listaTipoRetencion;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCodigoRetencion
	 * 
	 * @return listaCodigoRetencion
	 */
	public RegistroDataModelImpl getListaCodigoRetencion() {
		return listaCodigoRetencion;
	}
	/**
	 * Asigna la lista listaCodigoRetencion
	 * 
	 * @param listaCodigoRetencion
	 * Variable a asignar en  listaCodigoRetencion
	 */
	public void setListaCodigoRetencion(RegistroDataModelImpl listaCodigoRetencion) {
		this.listaCodigoRetencion = listaCodigoRetencion;
	}
	/**
	 * Retorna la lista listaCodigoRetencion
	 * 
	 * @return listaCodigoRetencion
	 */
	public RegistroDataModelImpl getListaCodigoRetencionE() {
		return listaCodigoRetencionE;
	}
	/**
	 * Asigna la lista listaCodigoRetencion
	 * 
	 * @param listaCodigoRetencion
	 * Variable a asignar en  listaCodigoRetencion
	 */
	public void setListaCodigoRetencionE(RegistroDataModelImpl listaCodigoRetencionE) {
		this.listaCodigoRetencionE = listaCodigoRetencionE;
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
	 * @return the ano
	 */
	public int getAno() {
		return ano;
	}
	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * @param ano the ano to set
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * @return the indice
	 */
	public int getIndice() {
		return indice;
	}
	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}



	//</SET_GET_LISTAS_COMBO_GRANDE>
}
