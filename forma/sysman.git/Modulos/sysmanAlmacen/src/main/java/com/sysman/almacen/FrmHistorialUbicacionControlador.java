/*-
 * FrmHistorialUbicacionControlador.java
 *
 * 1.0
 * 
 * 14/06/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.FrmHistorialUbicacionControladorEnum;
import com.sysman.almacen.enums.FrmHistorialUbicacionControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import org.primefaces.model.StreamedContent;

/**
 * Clase que permite ver y crear ubicaciones fisicas para los devolutivos
 *
 * @version 1.0, 14/06/2026
 * @author jlopez
 */
@ManagedBean
@ViewScoped
public class FrmHistorialUbicacionControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	private boolean bloqEditar = false;
	
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaElemento;
	private RegistroDataModelImpl listaElementoE;
	private RegistroDataModelImpl listaPlaca;
	private RegistroDataModelImpl listaPlacaE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	private String elemento;

	@EJB
	private EjbAlmacenCincoRemote cincoRemote;

	@EJB
	private EjbSysmanUtilRemote sysmanUtil;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmHistorialUbicacionControlador
	 */
	public FrmHistorialUbicacionControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			// 2592
			numFormulario = GeneralCodigoFormaEnum.FRM_HISTORIAL_UBICACION_CONTROLADOR.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
			enumBase = GenericUrlEnum.HISTORIAL_UBICACION;
			reasignarOrigen();
			buscarLlave();
			registro = new Registro();
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
			cargarListaElemento();
			cargarListaElementoE();
//</CARGAR_LISTA_COMBO_GRANDE>
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
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElemento() {
				UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmHistorialUbicacionControladorUrlEnum.URL259200
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, FrmHistorialUbicacionControladorEnum.CODIGOELEMENTO.getValue());
	
	}

	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 *
	 */
	public void cargarListaElementoE() {
		listaElementoE  = listaElemento;
	}

	/**
	 * 
	 * Carga la lista listaPlaca
	 *
	 * 
	 */
	public void cargarListaPlaca() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmHistorialUbicacionControladorUrlEnum.URL259201
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));

		listaPlaca = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());
	}

	/**
	 * 
	 * Carga la lista listaPlaca
	 *
	 * 
	 */
	public void cargarListaPlacaE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmHistorialUbicacionControladorUrlEnum.URL259201
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

		listaPlacaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());
	}

	
	/**
	 * Metodo ejecutado al cambiar el control Fecha
	 * 
	 * 
	 */
	public void cambiarFecha() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Fecha en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFechaC(int rowNum) {
		
		// <CODIGO_DESARROLLADO>// </CODIGO_DESARROLLADO>
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
		registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(), registroAux.getCampos().get(FrmHistorialUbicacionControladorEnum.CODIGOELEMENTO.getValue()));
		registro.getCampos().put(FrmHistorialUbicacionControladorEnum.NOMBREELEMENTO.getValue(), registroAux.getCampos().get(FrmHistorialUbicacionControladorEnum.NOMBRELARGO.getValue()));
		
		cargarListaPlaca();
		registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), null);
		registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), null);
	
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
	 * Metodo ejecutado al seleccionar una fila de la lista listaPlaca
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlaca(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPlaca
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlacaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("SERIE"));
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
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>

		realizarAccion("I");
		
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		//</CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro 
	 */
	@Override
	public boolean insertarDespues() {
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
		/*No se utiliza*/
	}

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario
	 * @param registro registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		/*No se utiliza*/
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		/*No se utiliza*/
	}

	/**
	 * Método que permite realizar las operaciones (CRUD)
	 * relacionadas con la ubicaicon física
	 * @param accion Parámetro que indica la acción a realizar:
	 * 	"I" = Insertar nuevo registro
	 * 	"E" = Eliminar un registro 
	 */
	public void realizarAccion(String accion) {

		try {
		
			int idHistorial = -1;
			String serie = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.SERIE.getName()));
			String codElemento = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
			Date fecha = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
			String ubicacion = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.UBICACION.getName()));
			String observaciones = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()));
			
			if (accion.equals("E")) {
				idHistorial = (int) Double.parseDouble(SysmanFunciones.toString(registro.getCampos().get(FrmHistorialUbicacionControladorEnum.ID_HISTORIAL.getValue())));
			}
			cincoRemote.ubicacionFisica(accion,idHistorial, compania, codElemento, serie, fecha, ubicacion, observaciones, SessionUtil.getUser().getCodigo());
			
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}

	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento Variable a asignar en listaElemento
	 */
	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}

	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElementoE() {
		return listaElementoE;
	}

	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento Variable a asignar en listaElemento
	 */
	public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
		this.listaElementoE = listaElementoE;
	}

	/**
	 * Retorna la lista listaPlaca
	 * 
	 * @return listaPlaca
	 */
	public RegistroDataModelImpl getListaPlaca() {
		return listaPlaca;
	}

	/**
	 * Asigna la lista listaPlaca
	 * 
	 * @param listaPlaca Variable a asignar en listaPlaca
	 */
	public void setListaPlaca(RegistroDataModelImpl listaPlaca) {
		this.listaPlaca = listaPlaca;
	}

	/**
	 * Retorna la lista listaPlaca
	 * 
	 * @return listaPlaca
	 */
	public RegistroDataModelImpl getListaPlacaE() {
		return listaPlacaE;
	}

	/**
	 * Asigna la lista listaPlaca
	 * 
	 * @param listaPlaca Variable a asignar en listaPlaca
	 */
	public void setListaPlacaE(RegistroDataModelImpl listaPlacaE) {
		this.listaPlacaE = listaPlacaE;
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
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
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
	 * @return the bloqEditar
	 */
	public boolean isBloqEditar() {
		return bloqEditar;
	}
	/**
	 * @param bloqEditar the bloqEditar to set
	 */
	public void setBloqEditar(boolean bloqEditar) {
		this.bloqEditar = bloqEditar;
	}

	@Override
	public boolean actualizarAntes() {
		return false;
	}

	@Override
	public boolean actualizarDespues() {
		return false;
	}

	@Override
	public boolean eliminarAntes() {
		
		//</CODIGO_DESARROLLADO>
		realizarAccion("E");
		return false;
	}

	@Override
	public boolean eliminarDespues() {
		
		return false;
	}
	
	/**
	 * Método que permite obtener la fecha actual
	 * @return Retorna Date con la fecha actual
	 */
	public Date getFechaActual() {
	    return new Date();
	}
}
