/*-
 * FrmTipoVigenciaControlador.java
 *
 * 1.0
 * 
 * 29/06/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.componentes.Direccionador;
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
import com.sysman.presupuesto.enums.FrmTipoVigenciaControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/06/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmTipoVigenciaControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Variable que controla si el formulario permite crear, editar y eliminar
	 * registros.
	 */
	private boolean permiteCrud;
	
	private RegistroDataModelImpl listaEquivalenteCuipo;
	private RegistroDataModelImpl listaEquivalenteCuipoE;
	private RegistroDataModelImpl listaEquivalenteAppui;
	private RegistroDataModelImpl listaEquivalenteAppuiE;
    
    
    private String auxiliar;

	/**
	 * Crea una nueva instancia de FrmTipoVigenciaControlador
	 */
	public FrmTipoVigenciaControlador() 
	{
		super();
		compania = SessionUtil.getCompania();
		
		setPermiteCrud(true);
		
		try {
			//2356
			numFormulario = GeneralCodigoFormaEnum.FRM_TIPO_VIGENCIA
                    .getCodigo();
			validarPermisos();

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
	public void inicializar() 
	{
        enumBase = GenericUrlEnum.TIPOVIGENCIA;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaEquivalenteCuipo();
        cargarListaEquivalenteCuipoE();
        cargarListaEquivalenteAppuiE();
        cargarListaEquivalenteAppui();
        abrirFormulario();
    }	

	private void cargarListaEquivalenteAppui() {
		 UrlBean urlBean = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID(
                 		FrmTipoVigenciaControladorUrlEnum.URL195800G
                                                 .getValue());

		    listaEquivalenteAppui = new RegistroDataModelImpl(urlBean.getUrl(),
			urlBean.getUrlConteo().getUrl(), null,
			true, GeneralParameterEnum.CODIGO.getName());		
	}

	private void cargarListaEquivalenteAppuiE() {
		 UrlBean urlBean = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID(
                 		FrmTipoVigenciaControladorUrlEnum.URL195800G
                                                 .getValue());

		    listaEquivalenteAppuiE = new RegistroDataModelImpl(urlBean.getUrl(),
			urlBean.getUrlConteo().getUrl(), null,
			true, GeneralParameterEnum.CODIGO.getName());		
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
    public void reasignarOrigen() 
	{
        buscarUrls();
    }

//<METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista listaEquivalenteCuipo
	 */
	public void cargarListaEquivalenteCuipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmTipoVigenciaControladorUrlEnum.URL1891001
                                                        .getValue());

        listaEquivalenteCuipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null,
				true, GeneralParameterEnum.CODIGO.getName());
    }	
	/**
	 * Carga la lista listaEquivalenteCuipoE
	 */
	public void cargarListaEquivalenteCuipoE() 
	{
		listaEquivalenteCuipoE= listaEquivalenteCuipo;
    }
	
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEquivalenteCuipo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEquivalenteCuipo(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
    	
        registro.getCampos().put("EQUIVALENTE_CUIPO", 
        		registroAux.getCampos().get(
        				GeneralParameterEnum.CODIGO.getName()));
    }
    
    public void seleccionarFilaEquivalenteAppui(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
    	
        registro.getCampos().put("EQUIVALENTE_APPUI", 
        		registroAux.getCampos().get(
        				GeneralParameterEnum.CODIGO.getName()));    	
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSector
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEquivalenteCuipoE(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
    	
    	setAuxiliar(SysmanFunciones.nvl(registroAux.getCampos()
    			.get(GeneralParameterEnum.CODIGO.getName()), "")
    			.toString());
    }
    
    public void seleccionarFilaEquivalenteAppuiE(SelectEvent event)
    {
    	Registro registroAux = (Registro) event.getObject();
    	
    	setAuxiliar(SysmanFunciones.nvl(registroAux.getCampos()
    			.get(GeneralParameterEnum.CODIGO.getName()), "")
    			.toString());
    }

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR130-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 52,
		 * Me.Name DoCmd.Maximize End Sub
		 */
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
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
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
		registro.getCampos().remove("NOMBRECUIPO");
		registro.getCampos().remove("NOMBRE_APPUI");
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
     * Validaciones realizadas antes de cerrar el formulario.
     */
    public void ejecutarrcCerrar() {

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

	/**
	 * @return the listaEquivalenteCuipo
	 */
	public RegistroDataModelImpl getListaEquivalenteCuipo() {
		return listaEquivalenteCuipo;
	}

	/**
	 * @param listaEquivalenteCuipo the listaEquivalenteCuipo to set
	 */
	public void setListaEquivalenteCuipo(RegistroDataModelImpl listaEquivalenteCuipo) {
		this.listaEquivalenteCuipo = listaEquivalenteCuipo;
	}

	/**
	 * @return the listaEquivalenteCuipoE
	 */
	public RegistroDataModelImpl getListaEquivalenteCuipoE() {
		return listaEquivalenteCuipoE;
	}

	/**
	 * @param listaEquivalenteCuipoE the listaEquivalenteCuipoE to set
	 */
	public void setListaEquivalenteCuipoE(RegistroDataModelImpl listaEquivalenteCuipoE) {
		this.listaEquivalenteCuipoE = listaEquivalenteCuipoE;
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

	/**
	 * @return the listaEquivalenteAppui
	 */
	public RegistroDataModelImpl getListaEquivalenteAppui() {
		return listaEquivalenteAppui;
	}

	/**
	 * @param listaEquivalenteAppui the listaEquivalenteAppui to set
	 */
	public void setListaEquivalenteAppui(RegistroDataModelImpl listaEquivalenteAppui) {
		this.listaEquivalenteAppui = listaEquivalenteAppui;
	}

	/**
	 * @return the listaEquivalenteAppuiE
	 */
	public RegistroDataModelImpl getListaEquivalenteAppuiE() {
		return listaEquivalenteAppuiE;
	}

	/**
	 * @param listaEquivalenteAppuiE the listaEquivalenteAppuiE to set
	 */
	public void setListaEquivalenteAppuiE(RegistroDataModelImpl listaEquivalenteAppuiE) {
		this.listaEquivalenteAppuiE = listaEquivalenteAppuiE;
	}
	
	
}
