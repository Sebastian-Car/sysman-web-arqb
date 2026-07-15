/*-
 * CAlmacenContabilidadBajaControlador.java
 *
 * 1.0
 * 
 * 26/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.enums.CAlmacenContabilidadBajaControladorEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadBajaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que cambia la interfaz de almacen a contabilidad por
 * retiro de activos
 *
 * @version 1.0, 26/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidadBajaControlador
extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	/**
	 * Constante a nivel de clase que almacena el modulo de la en el
	 * cual inicio sesion el usuario, el valor de esta constante es
	 * asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista que guarda los tipos de movimientos
	 */
	private RegistroDataModelImpl listaTipo;
	/**
	 * Lista que guarda los tipos de movimientos en la grilla
	 */
	private RegistroDataModelImpl listaTipoE;
	/**
	 * Lista que almacena las cuentas para debitos valor historico
	 */
	private RegistroDataModelImpl listaDebitoHistoricoBaja;
	/**
	 * Lista que almacena las cuentas para debitos valor historico en
	 * la grilla
	 */
	private RegistroDataModelImpl listaDebitoHistoricoBajaE;
	/**
	 * Lista que almacena las cuentas para creditos valor historico
	 */
	private RegistroDataModelImpl listaCreditoHistoricoBaja;
	/**
	 * Lista que almacena las cuentas para creditos valor historico en
	 * la grilla
	 */
	private RegistroDataModelImpl listaCreditoHistoricoBajaE;
	/**
	 * Lista que almacena las cuentas para debito apreciacion
	 * acumulado
	 */
	private RegistroDataModelImpl listaDebitoAcumuladaBaja;
	/**
	 * Lista que almacena las cuentas para debito apreciacion
	 * acumulado en la grilla
	 */
	private RegistroDataModelImpl listaDebitoAcumuladaBajaE;
	/**
	 * Lista que almacena las cuentas para credito apreciacion
	 * acumulado
	 */
	private RegistroDataModelImpl listaCreditoAcumuladaBaja;
	/**
	 * Lista que almacena las cuentas para credito apreciacion
	 * acumulado en la grilla
	 */
	private RegistroDataModelImpl listaCreditoAcumuladaBajaE;
	/**
	 * Lista que almacena las cuentas para debito valor en libros
	 */
	private RegistroDataModelImpl listaDebitoLibrosBaja;
	/**
	 * Lista que almacena las cuentas para debito valor en libros en
	 * la grilla
	 */
	private RegistroDataModelImpl listaDebitoLibrosBajaE;
	/**
	 * Lista que almacena las cuentas para credito valor en libros
	 */
	private RegistroDataModelImpl listaCreditoLibrosBaja;
	/**
	 * Lista que almacena las cuentas para credito valor en libros en
	 * la grilla
	 */
	private RegistroDataModelImpl listaCreditoLibrosBajaE;
	/**
	 * Lista que almacena las cuentas para debito valor historico
	 * activos retirados
	 */
	private RegistroDataModelImpl listaDebitoRetiradosBaja;
	/**
	 * Lista que almacena las cuentas para debito valor historico
	 * activos retirados en la grilla
	 */
	private RegistroDataModelImpl listaDebitoRetiradosBajaE;
	/**
	 * Lista que almacena las cuentas para credito valor historico
	 * activos retirados
	 */
	private RegistroDataModelImpl listaCreditoRetiradosBaja;
	/**
	 * Lista que almacena las cuentas para credito valor historico
	 * activos retirados en la grilla
	 */
	private RegistroDataModelImpl listaCreditoRetiradosBajaE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en
	 * esta se alamcena el identificador del registro que se
	 * selecciono
	 */
	private String auxiliar;
	/**
	 * Atributo que almacena el valor del codio de elemento
	 * proveninete como parametro
	 */
	private String codigoElemento;

	/**
	 * Atributo que almacena el nombre del elemento proveninete como
	 * parametro
	 */
	private String nombreLargo;
	/**
	 * Atributo que almacena el valor del anio proveninete como
	 * parametro
	 */
	private String anio;

	/**
	 * Atributo que almacena el tipo proveninete como parametro
	 */
	private String tipo;

	private Map<String, Object> ridP;

	private Map<String, Object> parametrosEntrada;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CAlmacenContabilidadBajaControlador
	 */
	public CAlmacenContabilidadBajaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		try {
			numFormulario = GeneralCodigoFormaEnum.CALMACEN_CONTABILIDAD_BAJA_CONTROLADOR
					.getCodigo();
			parametrosEntrada = SessionUtil.getFlash();

			if (parametrosEntrada != null) {

				ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");

				codigoElemento = parametrosEntrada.get("codigoElemento")
						.toString();

				anio = parametrosEntrada.get("anio")
						.toString();

				tipo = parametrosEntrada.get("tipo")
						.toString();

				nombreLargo = parametrosEntrada.get("nombre")
						.toString();

			}
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
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
	public void inicializar() {
		tabla = GenericUrlEnum.ALMACEN_CONTABILIDAD.getTable();
		reasignarOrigen();
		buscarLlave();
		registro = new Registro();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTipo();
		cargarListaTipoE();
		cargarListaDebitoHistoricoBaja();
		cargarListaDebitoHistoricoBajaE();
		cargarListaCreditoHistoricoBaja();
		cargarListaCreditoHistoricoBajaE();
		cargarListaDebitoAcumuladaBaja();
		cargarListaDebitoAcumuladaBajaE();
		cargarListaCreditoAcumuladaBaja();
		cargarListaCreditoAcumuladaBajaE();
		cargarListaDebitoLibrosBaja();
		cargarListaDebitoLibrosBajaE();
		cargarListaCreditoLibrosBaja();
		cargarListaCreditoLibrosBajaE();
		cargarListaDebitoRetiradosBaja();
		cargarListaDebitoRetiradosBajaE();
		cargarListaCreditoRetiradosBaja();
		cargarListaCreditoRetiradosBajaE();
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

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
				codigoElemento);

		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaControladorUrlEnum.URL20888
						.getValue());

		urlCreacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaControladorUrlEnum.URL25587
						.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaControladorUrlEnum.URL19121
						.getValue());

		urlEliminacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaControladorUrlEnum.URL28803
						.getValue());

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 */
	public void cargarListaTipo() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaControladorUrlEnum.URL11457
						.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(GeneralParameterEnum.CLASE.getName(),
				"E");

		param.put(GeneralParameterEnum.CONCEPTO.getName(),
				"CM,DS,L");

		param.put(CAlmacenContabilidadBajaControladorEnum.TIPO.getValue(),
				"C");

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 */
	public void cargarListaTipoE() {
		listaTipoE = listaTipo;

	}

	/**
	 * 
	 * Carga la lista listaDebitoHistoricoBaja
	 *
	 */
	public void cargarListaDebitoHistoricoBaja() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaControladorUrlEnum.URL14271
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				anio);

		listaDebitoHistoricoBaja = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaDebitoHistoricoBaja
	 *
	 */
	public void cargarListaDebitoHistoricoBajaE() {
		listaDebitoHistoricoBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoHistoricoBaja
	 *
	 */
	public void cargarListaCreditoHistoricoBaja() {
		listaCreditoHistoricoBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoHistoricoBaja
	 *
	 */
	public void cargarListaCreditoHistoricoBajaE() {
		listaCreditoHistoricoBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaDebitoAcumuladaBaja
	 *
	 */
	public void cargarListaDebitoAcumuladaBaja() {
		listaDebitoAcumuladaBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaDebitoAcumuladaBaja
	 *
	 */
	public void cargarListaDebitoAcumuladaBajaE() {
		listaDebitoAcumuladaBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoAcumuladaBaja
	 *
	 */
	public void cargarListaCreditoAcumuladaBaja() {
		listaCreditoAcumuladaBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoAcumuladaBaja
	 *
	 */
	public void cargarListaCreditoAcumuladaBajaE() {
		listaCreditoAcumuladaBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaDebitoLibrosBaja
	 *
	 */
	public void cargarListaDebitoLibrosBaja() {
		listaDebitoLibrosBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaDebitoLibrosBaja
	 *
	 */
	public void cargarListaDebitoLibrosBajaE() {
		listaDebitoLibrosBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoLibrosBaja
	 *
	 */
	public void cargarListaCreditoLibrosBaja() {
		listaCreditoLibrosBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoLibrosBaja
	 *
	 */
	public void cargarListaCreditoLibrosBajaE() {
		listaCreditoLibrosBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaDebitoRetiradosBaja
	 *
	 */
	public void cargarListaDebitoRetiradosBaja() {
		listaDebitoRetiradosBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaDebitoRetiradosBaja
	 *
	 */
	public void cargarListaDebitoRetiradosBajaE() {
		listaDebitoRetiradosBajaE = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoRetiradosBaja
	 *
	 */
	public void cargarListaCreditoRetiradosBaja() {
		listaCreditoRetiradosBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * 
	 * Carga la lista listaCreditoRetiradosBaja
	 *
	 */
	public void cargarListaCreditoRetiradosBajaE() {
		listaCreditoRetiradosBajaE = listaDebitoHistoricoBaja;
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Tipo en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarTipoC(int rowNum) {
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put(GeneralParameterEnum.NOMBRE.getName(), registro
				.getCampos()
				.get(GeneralParameterEnum.NOMBRE
						.getName()));
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPOMOVIMIENTO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(
						GeneralParameterEnum.NOMBRE.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName());

		registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(
						GeneralParameterEnum.NOMBRE.getName()));

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoHistoricoBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoHistoricoBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DEBITO_HISTORICO_BAJA", 
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoHistoricoBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoHistoricoBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoHistoricoBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoHistoricoBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CREDITO_HISTORICO_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoHistoricoBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoHistoricoBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoAcumuladaBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoAcumuladaBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DEBITO_ACUMULADA_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoAcumuladaBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoAcumuladaBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoAcumuladaBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoAcumuladaBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CREDITO_ACUMULADA_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoAcumuladaBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoAcumuladaBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoLibrosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoLibrosBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DEBITO_LIBROS_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoLibrosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoLibrosBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoLibrosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoLibrosBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CREDITO_LIBROS_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoLibrosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoLibrosBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoRetiradosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoRetiradosBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DEBITO_RETIRADOS_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDebitoRetiradosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDebitoRetiradosBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoRetiradosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoRetiradosBaja(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CREDITO_RETIRADOS_BAJA",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCreditoRetiradosBaja
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCreditoRetiradosBajaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	}

	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro
	 * seleccionado
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
	public boolean insertarAntes() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
				codigoElemento);

		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
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
	 */
	@Override
	public boolean actualizarAntes() {
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y
	 * actualizacion del registro
	 * 
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
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el
	 * formulario
	 * 
	 */
	public void ejecutarrcCerrar() {

		Map<String, Object> param = new TreeMap<>();
		param.put("rid", ridP);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put("codigoElemento", registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(param);
		direccionador.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.CALMACEN_CONTABILIDADS_CONTROLADOR
				.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores al
	 * registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// METODO_NO_IMPLEMENTADO
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en listaTipo
	 */
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}

	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipoE() {
		return listaTipoE;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en listaTipo
	 */
	public void setListaTipoE(RegistroDataModelImpl listaTipoE) {
		this.listaTipoE = listaTipoE;
	}

	/**
	 * Retorna la lista listaDebitoHistoricoBaja
	 * 
	 * @return listaDebitoHistoricoBaja
	 */
	public RegistroDataModelImpl getListaDebitoHistoricoBaja() {
		return listaDebitoHistoricoBaja;
	}

	/**
	 * Asigna la lista listaDebitoHistoricoBaja
	 * 
	 * @param listaDebitoHistoricoBaja
	 * Variable a asignar en listaDebitoHistoricoBaja
	 */
	public void setListaDebitoHistoricoBaja(
			RegistroDataModelImpl listaDebitoHistoricoBaja) {
		this.listaDebitoHistoricoBaja = listaDebitoHistoricoBaja;
	}

	/**
	 * Retorna la lista listaDebitoHistoricoBaja
	 * 
	 * @return listaDebitoHistoricoBaja
	 */
	public RegistroDataModelImpl getListaDebitoHistoricoBajaE() {
		return listaDebitoHistoricoBajaE;
	}

	/**
	 * Asigna la lista listaDebitoHistoricoBaja
	 * 
	 * @param listaDebitoHistoricoBaja
	 * Variable a asignar en listaDebitoHistoricoBaja
	 */
	public void setListaDebitoHistoricoBajaE(
			RegistroDataModelImpl listaDebitoHistoricoBajaE) {
		this.listaDebitoHistoricoBajaE = listaDebitoHistoricoBajaE;
	}

	/**
	 * Retorna la lista listaCreditoHistoricoBaja
	 * 
	 * @return listaCreditoHistoricoBaja
	 */
	public RegistroDataModelImpl getListaCreditoHistoricoBaja() {
		return listaCreditoHistoricoBaja;
	}

	/**
	 * Asigna la lista listaCreditoHistoricoBaja
	 * 
	 * @param listaCreditoHistoricoBaja
	 * Variable a asignar en listaCreditoHistoricoBaja
	 */
	public void setListaCreditoHistoricoBaja(
			RegistroDataModelImpl listaCreditoHistoricoBaja) {
		this.listaCreditoHistoricoBaja = listaCreditoHistoricoBaja;
	}

	/**
	 * Retorna la lista listaCreditoHistoricoBaja
	 * 
	 * @return listaCreditoHistoricoBaja
	 */
	public RegistroDataModelImpl getListaCreditoHistoricoBajaE() {
		return listaCreditoHistoricoBajaE;
	}

	/**
	 * Asigna la lista listaCreditoHistoricoBaja
	 * 
	 * @param listaCreditoHistoricoBaja
	 * Variable a asignar en listaCreditoHistoricoBaja
	 */
	public void setListaCreditoHistoricoBajaE(
			RegistroDataModelImpl listaCreditoHistoricoBajaE) {
		this.listaCreditoHistoricoBajaE = listaCreditoHistoricoBajaE;
	}

	/**
	 * Retorna la lista listaDebitoAcumuladaBaja
	 * 
	 * @return listaDebitoAcumuladaBaja
	 */
	public RegistroDataModelImpl getListaDebitoAcumuladaBaja() {
		return listaDebitoAcumuladaBaja;
	}

	/**
	 * Asigna la lista listaDebitoAcumuladaBaja
	 * 
	 * @param listaDebitoAcumuladaBaja
	 * Variable a asignar en listaDebitoAcumuladaBaja
	 */
	public void setListaDebitoAcumuladaBaja(
			RegistroDataModelImpl listaDebitoAcumuladaBaja) {
		this.listaDebitoAcumuladaBaja = listaDebitoAcumuladaBaja;
	}

	/**
	 * Retorna la lista listaDebitoAcumuladaBaja
	 * 
	 * @return listaDebitoAcumuladaBaja
	 */
	public RegistroDataModelImpl getListaDebitoAcumuladaBajaE() {
		return listaDebitoAcumuladaBajaE;
	}

	/**
	 * Asigna la lista listaDebitoAcumuladaBaja
	 * 
	 * @param listaDebitoAcumuladaBaja
	 * Variable a asignar en listaDebitoAcumuladaBaja
	 */
	public void setListaDebitoAcumuladaBajaE(
			RegistroDataModelImpl listaDebitoAcumuladaBajaE) {
		this.listaDebitoAcumuladaBajaE = listaDebitoAcumuladaBajaE;
	}

	/**
	 * Retorna la lista listaCreditoAcumuladaBaja
	 * 
	 * @return listaCreditoAcumuladaBaja
	 */
	public RegistroDataModelImpl getListaCreditoAcumuladaBaja() {
		return listaCreditoAcumuladaBaja;
	}

	/**
	 * Asigna la lista listaCreditoAcumuladaBaja
	 * 
	 * @param listaCreditoAcumuladaBaja
	 * Variable a asignar en listaCreditoAcumuladaBaja
	 */
	public void setListaCreditoAcumuladaBaja(
			RegistroDataModelImpl listaCreditoAcumuladaBaja) {
		this.listaCreditoAcumuladaBaja = listaCreditoAcumuladaBaja;
	}

	/**
	 * Retorna la lista listaCreditoAcumuladaBaja
	 * 
	 * @return listaCreditoAcumuladaBaja
	 */
	public RegistroDataModelImpl getListaCreditoAcumuladaBajaE() {
		return listaCreditoAcumuladaBajaE;
	}

	/**
	 * Asigna la lista listaCreditoAcumuladaBaja
	 * 
	 * @param listaCreditoAcumuladaBaja
	 * Variable a asignar en listaCreditoAcumuladaBaja
	 */
	public void setListaCreditoAcumuladaBajaE(
			RegistroDataModelImpl listaCreditoAcumuladaBajaE) {
		this.listaCreditoAcumuladaBajaE = listaCreditoAcumuladaBajaE;
	}

	/**
	 * Retorna la lista listaDebitoLibrosBaja
	 * 
	 * @return listaDebitoLibrosBaja
	 */
	public RegistroDataModelImpl getListaDebitoLibrosBaja() {
		return listaDebitoLibrosBaja;
	}

	/**
	 * Asigna la lista listaDebitoLibrosBaja
	 * 
	 * @param listaDebitoLibrosBaja
	 * Variable a asignar en listaDebitoLibrosBaja
	 */
	public void setListaDebitoLibrosBaja(
			RegistroDataModelImpl listaDebitoLibrosBaja) {
		this.listaDebitoLibrosBaja = listaDebitoLibrosBaja;
	}

	/**
	 * Retorna la lista listaDebitoLibrosBaja
	 * 
	 * @return listaDebitoLibrosBaja
	 */
	public RegistroDataModelImpl getListaDebitoLibrosBajaE() {
		return listaDebitoLibrosBajaE;
	}

	/**
	 * Asigna la lista listaDebitoLibrosBaja
	 * 
	 * @param listaDebitoLibrosBaja
	 * Variable a asignar en listaDebitoLibrosBaja
	 */
	public void setListaDebitoLibrosBajaE(
			RegistroDataModelImpl listaDebitoLibrosBajaE) {
		this.listaDebitoLibrosBajaE = listaDebitoLibrosBajaE;
	}

	/**
	 * Retorna la lista listaCreditoLibrosBaja
	 * 
	 * @return listaCreditoLibrosBaja
	 */
	public RegistroDataModelImpl getListaCreditoLibrosBaja() {
		return listaCreditoLibrosBaja;
	}

	/**
	 * Asigna la lista listaCreditoLibrosBaja
	 * 
	 * @param listaCreditoLibrosBaja
	 * Variable a asignar en listaCreditoLibrosBaja
	 */
	public void setListaCreditoLibrosBaja(
			RegistroDataModelImpl listaCreditoLibrosBaja) {
		this.listaCreditoLibrosBaja = listaCreditoLibrosBaja;
	}

	/**
	 * Retorna la lista listaCreditoLibrosBaja
	 * 
	 * @return listaCreditoLibrosBaja
	 */
	public RegistroDataModelImpl getListaCreditoLibrosBajaE() {
		return listaCreditoLibrosBajaE;
	}

	/**
	 * Asigna la lista listaCreditoLibrosBaja
	 * 
	 * @param listaCreditoLibrosBaja
	 * Variable a asignar en listaCreditoLibrosBaja
	 */
	public void setListaCreditoLibrosBajaE(
			RegistroDataModelImpl listaCreditoLibrosBajaE) {
		this.listaCreditoLibrosBajaE = listaCreditoLibrosBajaE;
	}

	/**
	 * Retorna la lista listaDebitoRetiradosBaja
	 * 
	 * @return listaDebitoRetiradosBaja
	 */
	public RegistroDataModelImpl getListaDebitoRetiradosBaja() {
		return listaDebitoRetiradosBaja;
	}

	/**
	 * Asigna la lista listaDebitoRetiradosBaja
	 * 
	 * @param listaDebitoRetiradosBaja
	 * Variable a asignar en listaDebitoRetiradosBaja
	 */
	public void setListaDebitoRetiradosBaja(
			RegistroDataModelImpl listaDebitoRetiradosBaja) {
		this.listaDebitoRetiradosBaja = listaDebitoRetiradosBaja;
	}

	/**
	 * Retorna la lista listaDebitoRetiradosBaja
	 * 
	 * @return listaDebitoRetiradosBaja
	 */
	public RegistroDataModelImpl getListaDebitoRetiradosBajaE() {
		return listaDebitoRetiradosBajaE;
	}

	/**
	 * Asigna la lista listaDebitoRetiradosBaja
	 * 
	 * @param listaDebitoRetiradosBaja
	 * Variable a asignar en listaDebitoRetiradosBaja
	 */
	public void setListaDebitoRetiradosBajaE(
			RegistroDataModelImpl listaDebitoRetiradosBajaE) {
		this.listaDebitoRetiradosBajaE = listaDebitoRetiradosBajaE;
	}

	/**
	 * Retorna la lista listaCreditoRetiradosBaja
	 * 
	 * @return listaCreditoRetiradosBaja
	 */
	public RegistroDataModelImpl getListaCreditoRetiradosBaja() {
		return listaCreditoRetiradosBaja;
	}

	/**
	 * Asigna la lista listaCreditoRetiradosBaja
	 * 
	 * @param listaCreditoRetiradosBaja
	 * Variable a asignar en listaCreditoRetiradosBaja
	 */
	public void setListaCreditoRetiradosBaja(
			RegistroDataModelImpl listaCreditoRetiradosBaja) {
		this.listaCreditoRetiradosBaja = listaCreditoRetiradosBaja;
	}

	/**
	 * Retorna la lista listaCreditoRetiradosBaja
	 * 
	 * @return listaCreditoRetiradosBaja
	 */
	public RegistroDataModelImpl getListaCreditoRetiradosBajaE() {
		return listaCreditoRetiradosBajaE;
	}

	/**
	 * Asigna la lista listaCreditoRetiradosBaja
	 * 
	 * @param listaCreditoRetiradosBaja
	 * Variable a asignar en listaCreditoRetiradosBaja
	 */
	public void setListaCreditoRetiradosBajaE(
			RegistroDataModelImpl listaCreditoRetiradosBajaE) {
		this.listaCreditoRetiradosBajaE = listaCreditoRetiradosBajaE;
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
		this.auxiliar = auxiliar;
	}

	public String getCodigoElemento() {
		return codigoElemento;
	}

	public void setCodigoElemento(String codigoElemento) {
		this.codigoElemento = codigoElemento;
	}

	public String getNombreLargo() {
		return nombreLargo;
	}

	public void setNombreLargo(String nombreLargo) {
		this.nombreLargo = nombreLargo;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Map<String, Object> getRidP() {
		return ridP;
	}

	public void setRidP(Map<String, Object> ridP) {
		this.ridP = ridP;
	}

	public Map<String, Object> getParametrosEntrada() {
		return parametrosEntrada;
	}

	public void setParametrosEntrada(Map<String, Object> parametrosEntrada) {
		this.parametrosEntrada = parametrosEntrada;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
