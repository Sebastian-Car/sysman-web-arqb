/*-
 * FrmWFTraerDeudadeControlador.java
 *
 * 1.0
 * 
 * 09/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
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
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/12/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmWFTraerDeudadeControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private String proceso;
	private String tipoTramite;
	private String tramite;
	private String detalleTramite;
	private String nodo;
	private int codFormRedireccion;
	private Map<String, Object> ridTramite;
	private int indice;
	private String modulo;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmWFTraerDeudadeControlador
	 */
	public FrmWFTraerDeudadeControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {//2218
			numFormulario=GeneralCodigoFormaEnum.FRM_WF_TRAER_DEUDADE_CONTROLADOR.getCodigo();
			validarPermisos();

			Map<String, Object> paramIn = SessionUtil.getFlash();

			if (paramIn != null) {
				proceso = paramIn
						.get(DTramiteVariablesControladorEnum.PR_PROCESO
								.getValue())
						.toString();

				tipoTramite = paramIn
						.get(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE
								.getValue())
						.toString();

				tramite = paramIn
						.get(DTramiteVariablesControladorEnum.PR_TRAMITE
								.getValue())
						.toString();

				detalleTramite = paramIn
						.get(DTramiteVariablesControladorEnum.PR_D_TRAMITE
								.getValue())
						.toString();

				nodo = paramIn.get(DTramiteVariablesControladorEnum.PR_NODO
						.getValue()).toString();

				codFormRedireccion = (int) paramIn
						.get(DTramiteVariablesControladorEnum.PR_COD_FORM
								.getValue());

				ridTramite = (Map<String, Object>) paramIn
						.get(FrmTramitesControladorEnum.PR_ROWKEY
								.getValue());
			}
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
		enumBase = GenericUrlEnum.TRAMITE_DEUDA;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
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

		parametrosListado.put(
				DTramiteVariablesControladorEnum.PROCESO.getValue(),
				proceso);

		parametrosListado.put(DTramiteVariablesControladorEnum.TIPO_TRAMITE
				.getValue(), tipoTramite);

		parametrosListado.put(
				DTramiteVariablesControladorEnum.TRAMITE.getValue(),
				tramite);

	}
	//<METODOS_CARGAR_LISTA>

	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
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
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.PROCESOJUD.getName());
		registro.getCampos().remove(GeneralParameterEnum.TIPO_TRAMITE.getName());
		registro.getCampos().remove(GeneralParameterEnum.TRAMITEJUD.getName());
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

	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}
	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), ridTramite);

		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer.toString(codFormRedireccion));

		if (GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR
				.getCodigo() == codFormRedireccion) {
			param.put(DTramiteVariablesControladorEnum.PR_PROCESO.getValue(),
					proceso);

			param.put(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE
					.getValue(), tipoTramite);

			param.put(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue(),
					tramite);

			param.put(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue(),
					detalleTramite);

			param.put(DTramiteVariablesControladorEnum.PR_NODO.getValue(),
					nodo);
		}

		dir.setParametros(param);

		SessionUtil.redireccionarForma(dir, modulo);
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
	//<SET_GET_ATRIBUTOS>
	public int getIndice() {
		return indice;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}


	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
