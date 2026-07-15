/*-
 * FrmInventarioFisicoControlador.java
 *
 * 1.0
 * 
 * 11/11/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.text.ParseException;
import java.util.Date;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.FrmInventarioFisicoControladorEnum;
import com.sysman.almacen.enums.FrmInventarioFisicoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/11/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmInventarioFisicoControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;
//<DECLARAR_ATRIBUTOS>
	private String bodega;

	private String conteo;

	private Date fechaCorte;

	private String fuente;

	private String elemento;

	private String auxiliar;

	private boolean bloqueaConteo1;

	private boolean bloqueaConteo2;

	private boolean bloqueaConteo3;

	// </DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>

	private List<Registro> listaLote;

	private RegistroDataModelImpl listaBodega;
	private RegistroDataModelImpl listaBodegaE;
	private RegistroDataModelImpl listaElemento;
	private RegistroDataModelImpl listaElementoE;
	private boolean pideVencimiento;
	private String modulo;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbAlmacenCincoRemote cincoRemote;
	
	/**
	 * Crea una nueva instancia de FrmInventarioFisicoControlador
	 */
	public FrmInventarioFisicoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		bodega = "20";
		conteo = "1";
		bloqueaConteo1 = false;
		bloqueaConteo2 = true;
		bloqueaConteo3 = true;
		pideVencimiento = true;
		try {
			fechaCorte = new Date();
			numFormulario = GeneralCodigoFormaEnum.FRM_INVENTARIO_FISICO_CONSUMO.getCodigo();// 2553;
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
		enumBase = GenericUrlEnum.INVENTARIO_FISICO_CONSUMO;
		reasignarOrigen();
		buscarLlave();
		registro = new Registro();
		cargarListaLote();
		cargarListaBodega();
		cargarListaBodegaE();
		cargarListaElemento();
		cargarListaElementoE();
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
		try {
			parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			parametrosListado.put("BODEGA", bodega);
			parametrosListado.put("FECHA", SysmanFunciones.convertirAFechaCadena(fechaCorte, "dd/MM/yyyy"));
		} catch ( ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//<METODOS_CARGAR_LISTA>
	private void cargarListaLote() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaLote = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmInventarioFisicoControladorUrlEnum.URL119033.getValue())
									.getUrl(),
							param));
			
			String LOTE_DEFECTO = "999999999999999999";
            
            for (Registro r : listaLote) {
                if (r.getCampos().get("LOTE") == null) {
                    r.getCampos().put("LOTE", LOTE_DEFECTO);
                }
            }
            
		} catch (SystemException e) {
			Logger.getLogger(DepreciacionMesDependenciaControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaBodega
	 *
	 */
	public void cargarListaBodega() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASE_BODEGA", "20");

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInventarioFisicoControladorUrlEnum.URL135009.getValue());

		listaBodega = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaBodega
	 *
	 */
	public void cargarListaBodegaE() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASE_BODEGA", "20");

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInventarioFisicoControladorUrlEnum.URL135009.getValue());

		listaBodegaE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElemento() {
		String urlEnumId;

		urlEnumId = FrmInventarioFisicoControladorUrlEnum.URL112199.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmInventarioFisicoControladorEnum.BODEGA.getValue(), bodega);
		listaElemento = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ROWIDCON");

	}

	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElementoE() {
		String urlEnumId;

		urlEnumId = FrmInventarioFisicoControladorUrlEnum.URL112199.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmInventarioFisicoControladorEnum.BODEGA.getValue(), bodega);
		listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ROWIDCON");

	}

	public void cambiarfechaCorte() {
		// <CODIGO_DESARROLLADO>
		reasignarOrigen();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo1
	 * 
	 * 
	 */
	public void cambiarConteo1() {
		calcularDiferencias("CONTEO1");
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo2
	 * 
	 * 
	 */
	public void cambiarConteo2() {
		calcularDiferencias("CONTEO2");
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo3
	 * 
	 * 
	 */
	public void cambiarConteo3() {
		calcularDiferencias("CONTEO3");
	}

	private void calcularDiferencias(String numConteo) {
		int saldo = Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get("SALDO")));
		int numeroConteo = Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get(numConteo)));

		int result = 0;
		result = saldo - numeroConteo;
		if (result < 0) {
			registro.getCampos().put("DEBITO", Math.abs(result));
			registro.getCampos().put("CREDITO", 0);
		} else {
			registro.getCampos().put("CREDITO", Math.abs(result));
			registro.getCampos().put("DEBITO", 0);

		}
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarConteoC(int rowNum) {

	}

	/**
	 * Metodo ejecutado al cambiar el control Elemento en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarElementoC(int rowNum) {

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CODIGOELEMENTO", "hola ");
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo1 en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarConteo1C(int rowNum) {
		calcularDiferenciasC(rowNum, "CONTEO1");
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo2 en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarConteo2C(int rowNum) {
		calcularDiferenciasC(rowNum, "CONTEO2");
	}

	/**
	 * Metodo ejecutado al cambiar el control Conteo3 en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarConteo3C(int rowNum) {
		calcularDiferenciasC(rowNum, "CONTEO3");
	}

	private void calcularDiferenciasC(int rowNum, String numConteo) {

		int saldo = Integer.parseInt(
				SysmanFunciones.toString(listaInicial.getDatasource().get(rowNum % 10).getCampos().get("SALDO")));
		int conteo = Integer.parseInt(
				SysmanFunciones.toString(listaInicial.getDatasource().get(rowNum % 10).getCampos().get(numConteo)));

		int result = saldo - conteo;
		if (result < 0) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("DEBITO", Math.abs(result));
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CREDITO", 0);
		} else {
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CREDITO", Math.abs(result));
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("DEBITO", 0);
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CargarElementos en la vista
	 *
	 *
	 */
	public void oprimirCargarElementos() {
		try {
		Date fechaActual = new Date();
		int ano = SysmanFunciones.ano(fechaActual);
			cincoRemote.cargarElemConsumoFisico(compania, 
					bodega, 
					ano, 
					fechaCorte,
					SessionUtil.getUser().getCodigo());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));	
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	
	public void oprimiraplicarAjuste() {
		// <CODIGO_DESARROLLADO>
		try {
			String[] campos = {"bodega","fechaCorte"};
			    String[] valores = {bodega, SysmanFunciones.convertirAFechaCadena(fechaCorte, "dd/MM/yyyy")};
			    SessionUtil.cargarModalDatosFlashCerrar(
			                    String.valueOf(GeneralCodigoFormaEnum.APLICAR_AJUSTE_CONTROLADOR
			                                    .getCodigo()),
			                    modulo,
			                    campos, valores);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());		
		}
		// </CODIGO_DESARROLLADO>
	}
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al cambiar el control Conteo
	 * 
	 * 
	 */
	public void cambiarConteo() {
		switch (conteo) {
		case "1":
			bloqueaConteo1 = false;
			bloqueaConteo2 = true;
			bloqueaConteo3 = true;
			break;
		case "2":
			bloqueaConteo1 = true;
			bloqueaConteo2 = false;
			bloqueaConteo3 = true;
			break;
		case "3":
			bloqueaConteo1 = true;
			bloqueaConteo2 = true;
			bloqueaConteo3 = false;
			break;
		default:
			break;
		}
		asignarValoresDefecto();
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		asignarValoresDefecto();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listabodega
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBodega(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bodega = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		reasignarOrigen();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaBodega
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBodegaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaElemento
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGOELEMENTO", registroAux.getCampos().get("CODIGOELEMENTO"));
		registro.getCampos().put("NOMBREELEMENTO", registroAux.getCampos().get("NOMBRELARGO"));
		registro.getCampos().put("FUENTE", registroAux.getCampos().get("FUENTEDERECURSO"));
		if (SysmanFunciones.toString(registro.getCampos().get("FUENTE")) == "") {
			registro.getCampos().put("FUENTE", "99999999999999999999");
		}
		registro.getCampos().put("SALDO", registroAux.getCampos().get("EXISTENCIA"));
		registro.getCampos().put("CONTEO1", 0);
		registro.getCampos().put("CONTEO2", 0);
		registro.getCampos().put("CONTEO3", 0);
		registro.getCampos().put("DEBITO", 0);
		registro.getCampos().put("CREDITO", 0);
		pideVencimiento = (SysmanFunciones.toString(registroAux.getCampos().get("PIDE_VENCIMIENTO")).equals("true"))?false:true;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaElemento
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGOELEMENTO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuente
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuente
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado TODO
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		if (!pideVencimiento) {
			if(SysmanFunciones.nvl(registro.getCampos().get("LOTE"),"").equals("")) {
				JsfUtil.agregarMensajeError("El elemento pide vencimiento, el campo lote es obligatorio");
				return false;
			}
		}
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
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", SysmanFunciones.ano(fechaCorte));
		registro.getCampos().put("FECHA_CORTE", fechaCorte);
		registro.getCampos().put("BODEGA", bodega);
		registro.getCampos().put("CONTEO", conteo);
		registro.getCampos().remove("PIDE_VENCIMIENTO");
		if (!pideVencimiento) {
			if(SysmanFunciones.nvl(registro.getCampos().get("LOTE"),"").equals("")) {
				JsfUtil.agregarMensajeError("El elemento pide vencimiento, el campo lote es obligatorio");
				return false;
			}
		}
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
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
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
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
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario
	 * 
	 *
	 * @param registro registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
		pideVencimiento = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice).getCampos().get("PIDE_VENCIMIENTO"),"").equals(true)?false:true;
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		asignarValoresDefecto();
	}

	private void asignarValoresDefecto() {
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", SysmanFunciones.ano(fechaCorte));
		registro.getCampos().put("FECHA_CORTE", fechaCorte);
		registro.getCampos().put("BODEGA", bodega);
		registro.getCampos().put("CONTEO", conteo);
		registro.getCampos().put("CONTEO1", 0);
		registro.getCampos().put("CONTEO2", 0);
		registro.getCampos().put("CONTEO3", 0);
		registro.getCampos().put("DEBITO", 0);
		registro.getCampos().put("CREDITO", 0);

	}

	/**
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable bodega
	 * 
	 * @return bodega
	 */
	public String getBodega() {
		return bodega;
	}

	/**
	 * Asigna la variable bodega
	 * 
	 * @param bodega Variable a asignar en bodega
	 */
	public void setBodega(String bodega) {
		this.bodega = bodega;
	}

	/**
	 * Retorna la variable conteo
	 * 
	 * @return conteo
	 */
	public String getConteo() {
		return conteo;
	}

	/**
	 * Asigna la variable conteo
	 * 
	 * @param conteo Variable a asignar en conteo
	 */
	public void setConteo(String conteo) {
		this.conteo = conteo;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaBodega
	 * 
	 * @return listaBodega
	 */
	public RegistroDataModelImpl getListaBodega() {
		return listaBodega;
	}

	/**
	 * Asigna la lista listaBodega
	 * 
	 * @param listaBodega Variable a asignar en listaBodega
	 */
	public void setListaBodega(RegistroDataModelImpl listaBodega) {
		this.listaBodega = listaBodega;
	}

	/**
	 * @return the fechaCorte
	 */
	public Date getFechaCorte() {
		return fechaCorte;
	}

	/**
	 * @param fechaCorte the fechaCorte to set
	 */
	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

	/**
	 * @return the fuente
	 */
	public String getFuente() {
		return fuente;
	}

	/**
	 * @param fuente the fuente to set
	 */
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}

	/**
	 * @return the elemento
	 */
	public String getElemento() {
		return elemento;
	}

	/**
	 * @param elemento the elemento to set
	 */
	public void setElemento(String elemento) {
		this.elemento = elemento;
	}

	/**
	 * @return the listaBodegaE
	 */
	public RegistroDataModelImpl getListaBodegaE() {
		return listaBodegaE;
	}

	/**
	 * @param listaBodegaE the listaBodegaE to set
	 */
	public void setListaBodegaE(RegistroDataModelImpl listaBodegaE) {
		this.listaBodegaE = listaBodegaE;
	}

	/**
	 * @return the listaElemento
	 */
	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}

	/**
	 * @param listaElemento the listaElemento to set
	 */
	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}

	/**
	 * @return the listaElementoE
	 */
	public RegistroDataModelImpl getListaElementoE() {
		return listaElementoE;
	}

	/**
	 * @param listaElementoE the listaElementoE to set
	 */
	public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
		this.listaElementoE = listaElementoE;
	}

	/**
	 * @return the bloqueaConteo1
	 */
	public boolean getBloqueaConteo1() {
		return bloqueaConteo1;
	}

	/**
	 * @param bloqueaConteo1 the bloqueaConteo1 to set
	 */
	public void setBloqueaConteo1(boolean bloqueaConteo1) {
		this.bloqueaConteo1 = bloqueaConteo1;
	}

	/**
	 * @return the bloqueaConteo2
	 */
	public boolean getBloqueaConteo2() {
		return bloqueaConteo2;
	}

	/**
	 * @param bloqueaConteo2 the bloqueaConteo2 to set
	 */
	public void setBloqueaConteo2(boolean bloqueaConteo2) {
		this.bloqueaConteo2 = bloqueaConteo2;
	}

	/**
	 * @return the bloqueaConteo3
	 */
	public boolean getBloqueaConteo3() {
		return bloqueaConteo3;
	}

	/**
	 * @param bloqueaConteo3 the bloqueaConteo3 to set
	 */
	public void setBloqueaConteo3(boolean bloqueaConteo3) {
		this.bloqueaConteo3 = bloqueaConteo3;
	}

	/**
	 * Retorna la lista listaLote
	 * 
	 * @return listaLote
	 */
	public List<Registro> getListaLote() {
		return listaLote;
	}

	/**
	 * Asigna la lista listaLote
	 * 
	 * @param listaLote Variable a asignar en listaLote
	 */
	public void setListaLote(List<Registro> listaLote) {
		this.listaLote = listaLote;
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

	/**
	 * @return the pideVencimiento
	 */
	public boolean isPideVencimiento() {
		return pideVencimiento;
	}

	/**
	 * @param pideVencimiento the pideVencimiento to set
	 */
	public void setPideVencimiento(boolean pideVencimiento) {
		this.pideVencimiento = pideVencimiento;
	}
	
	

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaBodega
	 * 
	 * @return listaBodega
	 */

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
