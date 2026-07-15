/*-
 * FrmNodoDisparaControlador.java
 *
 * 1.0
 * 
 * 14/11/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.workflow.enums.FrmDNodosControladorUrlEnum;
import com.sysman.workflow.enums.FrmNodoDisparaControladorUrlEnum;
import com.sysman.workflow.enums.FrmNodosControladorEnum;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 14/11/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmNodoDisparaControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private List<Registro> listaproceso;
	private List<Registro> listanodoOrigen;
	private List<Registro> listanodoDestino;
	private List<Registro> listaprocesoDispara;
	private List<Registro> listanodoDispara;
	private List<Registro> listatipoTramite;
	private Map<String, Object> parametrosEntrada;
	private String proceso;
	private Object nodoOrigen;
	private Object nodoDestino;
	private String procesoDispara;
	private String titulo;

	private Map<String, Object> ridForm;

	/**
	 * Atributo auxliar el cual es asiganado en el momento que se
	 * activa la edicion de un registro. Toma el valor del indice
	 * dentro de la grilla del registro seleccionado para editar
	 */
	private int indice;
	private String nombreproceso;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmNodoDisparaControlador
	 */
	public FrmNodoDisparaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {//2129
			numFormulario = GeneralCodigoFormaEnum.FRM_NODO_DISPARA_CONTROLADOR.getCodigo();

			parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {


				proceso = (String) parametrosEntrada.get("proceso");
				nodoOrigen = (Object) parametrosEntrada.get("nodoOrigen");
				nodoDestino = (Object) parametrosEntrada.get("nodoDestino");
				nombreproceso = (String) parametrosEntrada.get("nombreproceso");
				ridForm = (Map<String, Object>) parametrosEntrada.get("PR_RID");

			}
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		finally {
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
	public void inicializar() {

		enumBase = GenericUrlEnum.D_NODO_DISPARA;
		registro = new Registro();
		reasignarOrigen();
		buscarLlave();

		// <CARGAR_LISTA>
		cargarListaproceso();
		cargarListanodoOrigen();
		cargarListanodoDestino();
		cargarListaprocesoDispara();
		cargarListanodoDispara();

		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();


	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen() {

		buscarUrls();

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put("CODIGO_PROCESO", proceso);
		parametrosListado.put("ORIGEN_NODO", nodoOrigen);
		parametrosListado.put("DESTINO_NODO", nodoDestino);

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaproceso
	 *
	 */
	public void cargarListaproceso() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaproceso = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoDisparaControladorUrlEnum.URL5169
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
	 * Carga la lista listanodoOrigen
	 *
	 */
	public void cargarListanodoOrigen() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("PROCESO", proceso);
		param.put(GeneralParameterEnum.ESTADO.getName(), 4); // Estado Activo

		registro.getCampos();

		try {
			listanodoOrigen = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoDisparaControladorUrlEnum.URL4334
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
	 * Carga la lista listanodoDestino
	 *
	 */
	public void cargarListanodoDestino() {


		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("PROCESO", proceso);
		param.put(GeneralParameterEnum.ESTADO.getName(), 4); // Estado Activo

		try {
			listanodoDestino = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoDisparaControladorUrlEnum.URL4334
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
	 * Carga la lista listaprocesoDispara
	 *
	 */
	public void cargarListaprocesoDispara() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaprocesoDispara = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoDisparaControladorUrlEnum.URL5169
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
	 * Carga la lista listanodoDispara
	 *
	 */
	public void cargarListanodoDispara() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("PROCESO", procesoDispara);
		param.put(GeneralParameterEnum.ESTADO.getName(), 4); // Estado Activo

		try {
			listanodoDispara = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoDisparaControladorUrlEnum.URL4334
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
	 * Carga la lista listatipoTramite
	 *
	 */
	public void cargarListatipoTramite(){
		// listatipoTramite = service.getListado(conectorPool, "SELECT TIPOTRAMITE,"+
		//"     NOMBRE"+
		//" FROM"+
		//"     TIPOTRAMITES"+
		//" WHERE"+
		//"     COMPANIA = :COMPANIA"+
		//"     AND PROCESOS = :PROCESO");

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("PROCESO", procesoDispara);

		try {
			listatipoTramite = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoDisparaControladorUrlEnum.URL5170
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control procesoDispara
	 * 
	 * 
	 */
	public void cambiarprocesoDispara() {
		//<CODIGO_DESARROLLADO>
		procesoDispara = registro.getCampos().get("PROCESO_DISPARA").toString();

		registro.getCampos().put("NODO_DISPARA", "");
		registro.getCampos().put("NODO_D_NOM_DIS", "");
		cargarListanodoDispara();
		cargarListatipoTramite();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control procesoDispara en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarprocesoDisparaC(int rowNum) {

		//<CODIGO_DESARROLLADO>

		procesoDispara = listaInicial.getDatasource().get(rowNum % 10)
				.getCampos().get("PROCESO_DISPARA").toString();

		cargarListanodoDispara();
		cargarListatipoTramite();

		//</CODIGO_DESARROLLADO>
	}
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		titulo = "PROCESO: ".concat(nombreproceso);
		registro.getCampos().put("PROCESO", proceso);
		registro.getCampos().put("NODO_ORIGEN", nodoOrigen);
		registro.getCampos().put("NODO_DESTINO", nodoDestino);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro
	 * seleccionado TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
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
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y
	 * actualizacion del registro
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
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
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
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {

		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove("NODO_D_NOM_DIS");
		registro.getCampos().remove("PROCESO_NOM_DIS");
		registro.getCampos().remove("NODO_O_NOM");
		registro.getCampos().remove("PROCESO_NOM");
		registro.getCampos().remove("NODO_D_NOM");
		registro.getCampos().remove("TRAMITE_NOM");
	}


	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmNodosControladorEnum.PR_RID.getValue(), ridForm);
		param.put("PR_PROCESO",proceso); 

		Direccionador dir = new Direccionador();
		dir.setParametros(param);

		dir.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_D_NODOS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del
	 * formulario.
	 *
	 * @param registro
	 * registro del cual se activo la edicion.
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();

		/*- Se asigna el codigo del proceso a la variable de clase. */
		procesoDispara = registro.getCampos().get("PROCESO_DISPARA").toString();

		cargarListanodoDispara();
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores al
	 * registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub

		registro.getCampos().put("PROCESO", proceso);
		registro.getCampos().put("NODO_ORIGEN", nodoOrigen);
		registro.getCampos().put("NODO_DESTINO", nodoDestino);
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaproceso
	 * 
	 * @return listaproceso
	 */
	public List<Registro> getListaproceso() {
		return listaproceso;
	}

	/**
	 * Asigna la lista listaproceso
	 * 
	 * @param listaproceso
	 * Variable a asignar en listaproceso
	 */
	public void setListaproceso(List<Registro> listaproceso) {
		this.listaproceso = listaproceso;
	}

	/**
	 * Retorna la lista listanodoOrigen
	 * 
	 * @return listanodoOrigen
	 */
	public List<Registro> getListanodoOrigen() {
		return listanodoOrigen;
	}

	/**
	 * Asigna la lista listanodoOrigen
	 * 
	 * @param listanodoOrigen
	 * Variable a asignar en listanodoOrigen
	 */
	public void setListanodoOrigen(List<Registro> listanodoOrigen) {
		this.listanodoOrigen = listanodoOrigen;
	}

	/**
	 * Retorna la lista listanodoDestino
	 * 
	 * @return listanodoDestino
	 */
	public List<Registro> getListanodoDestino() {
		return listanodoDestino;
	}

	/**
	 * Asigna la lista listanodoDestino
	 * 
	 * @param listanodoDestino
	 * Variable a asignar en listanodoDestino
	 */
	public void setListanodoDestino(List<Registro> listanodoDestino) {
		this.listanodoDestino = listanodoDestino;
	}

	/**
	 * Retorna la lista listaprocesoDispara
	 * 
	 * @return listaprocesoDispara
	 */
	public List<Registro> getListaprocesoDispara() {
		return listaprocesoDispara;
	}

	/**
	 * Asigna la lista listaprocesoDispara
	 * 
	 * @param listaprocesoDispara
	 * Variable a asignar en listaprocesoDispara
	 */
	public void setListaprocesoDispara(List<Registro> listaprocesoDispara) {
		this.listaprocesoDispara = listaprocesoDispara;
	}

	/**
	 * Retorna la lista listanodoDispara
	 * 
	 * @return listanodoDispara
	 */
	public List<Registro> getListanodoDispara() {
		return listanodoDispara;
	}

	/**
	 * Asigna la lista listanodoDispara
	 * 
	 * @param listanodoDispara
	 * Variable a asignar en listanodoDispara
	 */
	public void setListanodoDispara(List<Registro> listanodoDispara) {
		this.listanodoDispara = listanodoDispara;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public List<Registro> getListatipoTramite() {
		return listatipoTramite;
	}

	public void setListatipoTramite(List<Registro> listatipoTramite) {
		this.listatipoTramite = listatipoTramite;
	}




	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
