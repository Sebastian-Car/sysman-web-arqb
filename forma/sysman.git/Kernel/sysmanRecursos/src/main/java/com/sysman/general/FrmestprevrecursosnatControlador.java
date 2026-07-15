/*-
 * FrmestprevrecursosnatControlador.java
 *
 * 1.0
 * 
 * 28/01/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmrecursosnaturalesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/01/2026
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmestprevrecursosnatControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCodigo;
	private RegistroDataModelImpl listaCodigoE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	
	private String nombreAux;
	
	private String codEstudio;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmestprevrecursosnatControlador
	 */
	public FrmestprevrecursosnatControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_ESTPREVRECURSOSNAT_CONTROLADOR
            		.getCodigo();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codEstudio = parametrosEntrada.get("codEstudio").toString();
            }
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.ES_RECURSOSNATURALES;
		reasignarOrigen();
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaCodigo();
        cargarListaCodigoE();
        abrirFormulario();
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		parametrosListado.put(GeneralParameterEnum.COD_ESTUDIO.getName(),codEstudio);
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCodigo
	 *
	 */
	public void cargarListaCodigo() {
		Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(FrmrecursosnaturalesControladorUrlEnum.URL1993001.getValue());
        
        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigo
	 *
	 */
	public void cargarListaCodigoE() {
		listaCodigoE = listaCodigo;
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Codigo en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarCodigoC(int rowNum) {
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                auxiliar);
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                nombreAux);
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigo
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("COD_RECURSO", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRE", registroAux.getCampos().get("NOMBRE"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		nombreAux = (String) registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName());
	}

//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado TODO
	 * DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(),
				codEstudio);
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {		
		registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
	}

	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
	}

//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	

	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	public RegistroDataModelImpl getListaCodigo() {
		return listaCodigo;
	}

	public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
		this.listaCodigo = listaCodigo;
	}

	public RegistroDataModelImpl getListaCodigoE() {
		return listaCodigoE;
	}

	public void setListaCodigoE(RegistroDataModelImpl listaCodigoE) {
		this.listaCodigoE = listaCodigoE;
	}

	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	public String getCodEstudio() {
		return codEstudio;
	}

	public void setCodEstudio(String codEstudio) {
		this.codEstudio = codEstudio;
	}
}
