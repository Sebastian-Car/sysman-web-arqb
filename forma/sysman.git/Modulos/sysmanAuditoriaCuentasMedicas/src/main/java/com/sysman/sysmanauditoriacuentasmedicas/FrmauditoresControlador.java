/*-
 * FrmauditoresControlador.java
 *
 * 1.0
 * 
 * 07/06/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/06/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmauditoresControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	/**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCedula;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCedulaE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;

	/**
	 * Variable que controla si el formulario permite crear, editar y eliminar
	 * registros.
	 */
	private boolean permiteCrud;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmauditoresControlador
	 */
	public FrmauditoresControlador() 
	{
		super();
		compania = SessionUtil.getCompania();
		
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        
		permiteCrud = true;
		
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_AUDITORES_CONTROLADOR
                    .getCodigo();
			validarPermisos();

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
	public void inicializar() 
	{
		enumBase = GenericUrlEnum.CM_AUDITORES;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaCedula();
        cargarListaCedulaE();
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
	 * Carga la lista listaCedula
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCedula() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmImportarRipsControladorUrlEnum.URL4391
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);

		listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, "NIT");
	}

	/**
	 * 
	 * Carga la lista listaCedula
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCedulaE() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmImportarRipsControladorUrlEnum.URL4391
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);

		listaCedulaE = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, "NIT");
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCedula
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCedula(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NIT.getName(),
                        SysmanFunciones.nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()), "")
                                        .toString());
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        SysmanFunciones.nvl(
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()),
                                        "").toString());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                SysmanFunciones.nvl(
                                registroAux.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName()),
                                "").toString());
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCedula
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCedulaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NIT.getName(),
                        SysmanFunciones.nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()), "")
                                        .toString());
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        SysmanFunciones.nvl(
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()),
                                        "").toString());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                SysmanFunciones.nvl(
                                registroAux.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName()),
                                "").toString());
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
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
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
	public boolean actualizarAntes() 
	{
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
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
	public void removerCombos() 
	{
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void ejecutarrcCerrar() 
	{
		SessionUtil.redireccionarMenu();
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
	 * Retorna la lista listaCedula
	 * 
	 * @return listaCedula
	 */
	public RegistroDataModelImpl getListaCedula() {
		return listaCedula;
	}

	/**
	 * Asigna la lista listaCedula
	 * 
	 * @param listaCedula Variable a asignar en listaCedula
	 */
	public void setListaCedula(RegistroDataModelImpl listaCedula) {
		this.listaCedula = listaCedula;
	}

	/**
	 * Retorna la lista listaCedula
	 * 
	 * @return listaCedula
	 */
	public RegistroDataModelImpl getListaCedulaE() {
		return listaCedulaE;
	}

	/**
	 * Asigna la lista listaCedula
	 * 
	 * @param listaCedula Variable a asignar en listaCedula
	 */
	public void setListaCedulaE(RegistroDataModelImpl listaCedulaE) {
		this.listaCedulaE = listaCedulaE;
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

//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the permiteCrud
	 */
	public boolean isPermiteCrud() {
		return permiteCrud;
	}

	/**
	 * @param permiteCrud the permiteCrud to set
	 */
	public void setPermiteCrud(boolean permiteCrud) {
		this.permiteCrud = permiteCrud;
	}
}
