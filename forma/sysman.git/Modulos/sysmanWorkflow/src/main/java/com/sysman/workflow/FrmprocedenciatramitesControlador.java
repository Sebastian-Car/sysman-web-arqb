/*
 * 
 * FrmprocedenciatramitesControlador.java 1.0 17/04/2018 Copyright (c) 2016 Stefanini Sysman.
 * 
 * Paipa, Boyaca.
 * 
 * All rights reserved.
 * 
 */

package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ArmarDireccionesControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;
import com.sysman.workflow.enums.FrmprocedenciatramitesControladorEnum;
import com.sysman.workflow.enums.FrmprocedenciatramitesControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * 
 * Migración del formulario en access FRM_SERIEDOCUMENTAL a web con el controlador FrmprocedenciatramitesControlador
 * 
 * @version 1.0, 17/04/2018
 * 
 * @author lbotia
 * 
 */

@ManagedBean

@ViewScoped

public class FrmprocedenciatramitesControlador extends BeanBaseDatosAcmeImpl {

	/**
	 * 
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 * 
	 */

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private final String compania;

	/**
	 * 
	 * Constante a nivel de clase que aloja el codigo del modulo desde el cual el usuario inicio sesion.
	 * 
	 */
	private static final String CAMPO_CODIGO = "CODIGO";

	private String modulo = SessionUtil.getModulo();

	/**
	 * Lista que contiene los detalles del combo NIT (CB5883).
	 * 
	 */

	private RegistroDataModelImpl listaNIT;

	/**
	 * 
	 * Lista que contiene los detalles del combo REPRESENTANTELEGAL (CB5884).
	 * 
	 */

	private RegistroDataModelImpl listaREPRESENTANTELEGAL;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>

	/**
	 * Variable que almacena la llave del tramite desde el que se accede a este formulario.
	 */
	private Map<String, Object> ridTramite;

	private String obligatorio;

	private Registro rsNit;

	private String obligatorioNit;

	/**
	 * 
	 * Crea una nueva instancia de FrmprocedenciatramitesControlador
	 * 
	 */
	@SuppressWarnings("unchecked")
	public FrmprocedenciatramitesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_PROCEDENCIA_TRAMITE_CONTROLADOR
					.getCodigo();
			validarPermisos();

			// <INI_ADICIONAL>
			Map<String, Object> paramIn = SessionUtil.getFlash();

			if (paramIn != null) {

				ridTramite = (Map<String, Object>) paramIn
						.get(FrmTramitesControladorEnum.PR_ROWKEY
								.getValue());

				rid = (Map<String, Object>) paramIn.get("rid");
			}

			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario. Retorna al formulario de Tramites (1763).
	 */
	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>

		if ("350107".equals(SessionUtil.getMenuActual())) {

			SessionUtil.redireccionarMenu();
		}
		else {

			Map<String, Object> param = new TreeMap<>();
			param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(),
					ridTramite);

			Direccionador dir = new Direccionador();

			dir.setNumForm(Integer.toString(
					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
					.getCodigo()));

			dir.setParametros(param);

			SessionUtil.redireccionarForma(dir, modulo);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
	 * 
	 */

	@Override

	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaNIT();
		cargarListaREPRESENTANTELEGAL();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * 
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
	 * 
	 */

	@Override

	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * 
	 * En este metodo se iguala a null todas las listas de los subformularios
	 * 
	 */

	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * 
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
	 * son tablas, origenes de datos, inicializacion de listas y demas necesarios
	 * 
	 */

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.PROCEDENCIA_TRAMITE;
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * 
	 * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
	 * 
	 * 
	 * 
	 */

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista listaNIT asociada al combo NIT
	 * 
	 */

	public void cargarListaNIT() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmprocedenciatramitesControladorUrlEnum.URL3348
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaNIT = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				FrmprocedenciatramitesControladorEnum.NIT.getValue());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * 
	 * Metodo ejecutado al cambiar el control BtEditarDireccion
	 * 
	 */

	public void retornarFormularioBtEditarDireccion() {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> param = SessionUtil.getFlash();
		if (param != null
				&& !SysmanFunciones.validarCampoVacio(param,
						ArmarDireccionesControladorEnum.PR_DIRECCION
						.getValue())) {
			registro.getCampos()
			.put(FrmprocedenciatramitesControladorEnum.DIRECCION
					.getValue(),
					param.get(ArmarDireccionesControladorEnum.PR_DIRECCION
							.getValue()));
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Carga la lista listaREPRESENTANTELEGAL asociada al combo REPRESENTANTELEGAL.
	 * 
	 */

	public void cargarListaREPRESENTANTELEGAL() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmprocedenciatramitesControladorUrlEnum.URL3348
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaREPRESENTANTELEGAL = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				FrmprocedenciatramitesControladorEnum.NIT.getValue());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista listaNIT llena los campos respectivos con el origen de control dependiendo del nit que seleccione traera nombre nit, sucursal, nit.
	 * 
	 * @param event
	 * 
	 * objeto que encapsula la accion proveniente de la vista
	 */

	public void seleccionarFilaNIT(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos()
		.put(FrmprocedenciatramitesControladorEnum.NIT
				.getValue(),
				registroAux.getCampos().get("NIT"));
		registro.getCampos().put(FrmprocedenciatramitesControladorEnum.SUCURSAL
				.getValue(),
				registroAux.getCampos().get("SUCURSAL"));
		registro.getCampos().put(FrmprocedenciatramitesControladorEnum.NOMBRENIT
				.getValue(),
				registroAux.getCampos().get("NOMBRE"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaREPRESENTANTELEGAL llena los campos respectivos con el origen de control dependiendo del representante legal que seleccione traera
	 * nombre nit, sucursal, nit.
	 * 
	 * @param event
	 * 
	 * objeto que encapsula la accion proveniente de la vista
	 * 
	 */

	public void seleccionarFilaREPRESENTANTELEGAL(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos()
		.put(FrmprocedenciatramitesControladorEnum.REPRESENTANTELEGAL
				.getValue(),
				registroAux.getCampos().get("NIT"));
		registro.getCampos()
		.put(FrmprocedenciatramitesControladorEnum.SUCURSAL_REPRESENTANTE
				.getValue(),
				registroAux.getCampos()
				.get("SUCURSAL"));
		registro.getCampos()
		.put(FrmprocedenciatramitesControladorEnum.NOMBREREPRE
				.getValue(),
				registroAux.getCampos().get("NOMBRE"));
	}

	/**
	 * Metodo ejecutado al oprimir el boton BtEditarDireccion en la vista
	 */

	public void oprimirBtEditarDireccion() {
		// <CODIGO_DESARROLLADO>
		SessionUtil.cargarModalDatosFlashCerrar(Integer
				.toString(GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
						.getCodigo()),
				modulo, new String[1], new Object[1]);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirTerceros() {

		if (css == null) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
			return;
		}

		Map<String, Object> param = new HashMap<>();

		param.put("rowidWorkFlow", css);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(param);

		direccionador.setNumForm(
				String.valueOf(GeneralCodigoFormaEnum.TERCEROS_CONTROLADOR
						.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	/**
	 * Metodo ejecutado al cambiar el control DIRECCIONE_MAIL
	 * 
	 * 
	 */
	public void cambiarDIRECCIONE_MAIL() {
		// <CODIGO_DESARROLLADO>

		String Email = registro.getCampos().get("DIRECCIONE_MAIL").toString();
        if (!isValid(Email)) {

            JsfUtil.agregarMensajeAlerta(
                            "El correo no es valido por favor verifique e intente nuevamente.");

        }
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>

	 static boolean isValid(String email) {
         //Validacion anterior:
        //([a-z0-9]+(\\.?[_*+/-a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+"
	    	String regex = "[\\w\\._-]{3,30}\\+?[\\w]{0,10}@[\\w\\.\\-]{3,}\\.\\w{2,5}";
	        return email.matches(regex);
	    }

	public void cambiarTELEFONO() {

	}
	
	public void cambiarCedulaNit(){
		
	}


	public boolean validarNit() {

		boolean valor = true;
		try {


			Map<String, Object> param = new HashMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.NIT.getName(), registro.getCampos().get("DOCUMENTO"));
			param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));


			rsNit = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmprocedenciatramitesControladorUrlEnum.URL3349.getValue())
							.getUrl(),
							param));


			if (rsNit != null) {

				int	conteo = Integer.parseInt(rsNit.getCampos().get("EXISTE").toString());

				if (conteo >= 1) {

					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4395"));
					valor = false;

				}
			}

		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return valor;
	}

	// </METODOS_BOTONES>

	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * 
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
	 * 
	 */

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		if (css == null) {
			registro.getCampos().put(GeneralParameterEnum.NIT.getName(),
					"999999999999999999");
			registro.getCampos().put("REPRESENTANTELEGAL",
					"999999999999999999");
			registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
					"999");
			registro.getCampos().put("SUCURSAL_REPRESENTANTE", "999");
			registro.getCampos().put("NOMBREREPRE", "VARIOS");
			registro.getCampos().put("NOMBRENIT", "VARIOS");
		}
		obligatorio = "#A6A6A6  solid  1px";
	}

	/**
	 * 
	 * Metodo ejecutado antes de realizar la insercion del registro
	 *
	 * @return true
	 * 
	 */

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		try {
			registro.getCampos().put("COMPANIA", compania);
			registro.getCampos().remove("NOMBRENIT");
			registro.getCampos().remove("NOMBREREPRE");

			long consecutivo;

			String condicion = " COMPANIA = ''" + compania + "'' ";
			consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
					GenericUrlEnum.PROCEDENCIA_TRAMITE.getTable(),
					condicion, CAMPO_CODIGO);
			registro.getCampos().put(CAMPO_CODIGO, consecutivo);

			if (SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"OBLIGA NUMERO DE TELEFONO", modulo,
							new Date(), true).toString(), "NO")
					.equals("SI")) {
				if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
						GeneralParameterEnum.TELEFONO.getName())) {
					obligatorio = "#ff0000  solid  1px";
					JsfUtil.agregarMensajeAlerta(
							idioma.getString("TI_MS_ERROR_VALIDACION"));
					return false;
				}
				else {
					obligatorio = "#A6A6A6  solid  1px";
				}
			}

			if(SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"OBLIGA CEDULA O NIT EN PROCEDENCIA", modulo,
							new Date(), true).toString(), "NO")
					.equals("SI")) {

				if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
						"DOCUMENTO")) {
					setObligatorioNit("#ff0000  solid  1px");
					JsfUtil.agregarMensajeAlerta(
							idioma.getString("TI_MS_ERROR_VALIDACION"));
					return false;
				}
				else {
					setObligatorioNit("#A6A6A6  solid  1px");
				}

			}

		}

		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		String Email = registro.getCampos().get("DIRECCIONE_MAIL").toString();
		if (!isValid(Email)) {

			JsfUtil.agregarMensajeAlerta(
					"El correo no es valido por favor verifique e intente nuevamente");
			return false;

		}
		// validarNit();

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * 
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return true
	 * 
	 */

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * 
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 *
	 * @return true
	 * 
	 */

	@Override

	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		try {
			registro.getCampos().remove("NOMBRENIT");
			registro.getCampos().remove("NOMBREREPRE");

			if (SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"OBLIGA NUMERO DE TELEFONO", modulo,
							new Date(), true).toString(), "NO")
					.equals("SI")) {
				if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
						GeneralParameterEnum.TELEFONO.getName())) {
					obligatorio = "#ff0000  solid  1px";
					JsfUtil.agregarMensajeAlerta(
							idioma.getString("TI_MS_ERROR_VALIDACION"));
					return false;
				}else {
					obligatorio = "#A6A6A6  solid  1px";
				}
			}

			if(SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"OBLIGA CEDULA O NIT EN PROCEDENCIA", modulo,
							new Date(), true).toString(), "NO")
					.equals("SI")) {

				if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
						"DOCUMENTO")) {
					setObligatorioNit("#ff0000  solid  1px");
					JsfUtil.agregarMensajeAlerta(
							idioma.getString("TI_MS_ERROR_VALIDACION"));
					return false;
				}
				else {
					setObligatorioNit("#A6A6A6  solid  1px");
				}

			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		String Email = registro.getCampos().get("DIRECCIONE_MAIL").toString();
		if (!isValid(Email)) {

			JsfUtil.agregarMensajeAlerta(
					"El correo no es valido por favor verifique e intente nuevamente");
			return false;

		}
		if(!validarNit()) {
			return false;
		}
		// </CODIGO_DESARROLLADO>
		return true;

	}

	/**
	 * 
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
	 * 
	 * @return true
	 * 
	 */

	@Override

	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;

	}

	/**
	 * 
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * @return true
	 * 
	 */

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;

	}

	/**
	 * 
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * @return true
	 * 
	 */

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * 
	 * Retorna la lista listaNIT
	 * 
	 * @return listaNIT
	 * 
	 */

	public RegistroDataModelImpl getListaNIT() {
		return listaNIT;
	}

	/**
	 * 
	 * Asigna la lista listaNIT
	 * 
	 * @param listaNIT
	 * 
	 * Variable a asignar en listaNIT
	 * 
	 */

	public void setListaNIT(RegistroDataModelImpl listaNIT) {
		this.listaNIT = listaNIT;
	}

	/**
	 * 
	 * Retorna la lista listaREPRESENTANTELEGAL
	 * 
	 * @return listaREPRESENTANTELEGAL
	 * 
	 */

	public RegistroDataModelImpl getListaREPRESENTANTELEGAL() {
		return listaREPRESENTANTELEGAL;
	}

	/**
	 * 
	 * Asigna la lista listaREPRESENTANTELEGAL
	 * 
	 * @param listaREPRESENTANTELEGAL
	 * 
	 * Variable a asignar en listaREPRESENTANTELEGAL
	 * 
	 */

	public void setListaREPRESENTANTELEGAL(
			RegistroDataModelImpl listaREPRESENTANTELEGAL) {
		this.listaREPRESENTANTELEGAL = listaREPRESENTANTELEGAL;
	}

	/**
	 * @return the obligatorio
	 */
	public String getObligatorio() {
		return obligatorio;
	}

	/**
	 * @param obligatorio
	 * the obligatorio to set
	 */
	public void setObligatorio(String obligatorio) {
		this.obligatorio = obligatorio;
	}

	/**
	 * @return the obligatorioNit
	 */
	public String getObligatorioNit() {
		return obligatorioNit;
	}

	/**
	 * @param obligatorioNit the obligatorioNit to set
	 */
	public void setObligatorioNit(String obligatorioNit) {
		this.obligatorioNit = obligatorioNit;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>
}