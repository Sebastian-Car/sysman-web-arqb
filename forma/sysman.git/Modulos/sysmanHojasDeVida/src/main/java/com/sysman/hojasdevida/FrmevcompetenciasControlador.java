/*-
 * FrmevcompetenciasControlador.java
 *
 * 1.0
 *
 * 20/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmevcompetenciasControladorEnum;
import com.sysman.hojasdevida.enums.FrmevcompetenciasControladorUrlEnum;
import com.sysman.hojasdevida.enums.FrmevmanualsControladorEnum;
import com.sysman.hojasdevida.enums.FrmevrequisitosControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Clase para ingresar, modificar, listar y/o eliminar datos de la tabla
 * EV_COMPETENCIAS.
 *
 * @version 1.0, 20/01/2018
 * @author fperez
 *
 * @version 2 05/03/2017
 * @author jeguerrero Optimizacion del codigo del formulario.
 *
 */
@ManagedBean
@ViewScoped
public class FrmevcompetenciasControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el código de la compañía en la
	 * cual inició sesión el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesión correspondiente.
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista de tipo competencia.
	 */
	private RegistroDataModelImpl listacmbTipoCompetencia;
	/**
	 * Lista de número de manual.
	 */
	private RegistroDataModelImpl listacmbNumeroManual;

	// Llamado EJB para invocar funciones.
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Guarda la llave del registro del manual para redireccionar al formulario
	 * frmevmanualsControlador
	 */
	private Map<String, Object> ridManual;

	/**
	 * permite bloquear el combo numero manual al entrar desde el formulario
	 * frmevmanualsControlador
	 */
	private boolean bloqNumero;

	/**
	 * nombre del manual cargado desde el formulario principal
	 */
	private String nombreManual;

	/**
	 * nombre del manual cargado desde el formulario principal
	 */
	private String numeroManual;

	/**
	 * sigla del manual cargado desde el formulario principal
	 */
	private String sigla;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmevcompetenciasControlador
	 */
	@SuppressWarnings("unchecked")
	public FrmevcompetenciasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			/** Formulario número 1628 */
			numFormulario = GeneralCodigoFormaEnum.FRM_EV_COMPETENCIAS_CONTROLADOR.getCodigo();
			validarPermisos();
			registro = new Registro();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			bloqNumero = false;
			if (parametrosEntrada != null && parametrosEntrada.get("rid") != null) {
				ridManual = (Map<String, Object>) parametrosEntrada.get("rid");

				numeroManual = ridManual.get("KEY_NUMERO_MANUAL").toString();

				nombreManual = SysmanFunciones
						.nvl(parametrosEntrada.get(FrmevrequisitosControladorEnum.NOMBRE_MANUAL.getValue()), "")
						.toString();
				sigla = SysmanFunciones.nvl(parametrosEntrada.get(GeneralParameterEnum.SIGLA.getName()), "").toString();
				parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
						ridManual.get(FrmevmanualsControladorEnum.KEY_NUMERO_MANUAL.getValue()));
				parametrosListado.put(FrmevmanualsControladorEnum.VERSION.getValue(),
						ridManual.get(FrmevmanualsControladorEnum.KEY_VERSION.getValue()));
				bloqNumero = true;
			} else {
				numeroManual = "0";
			}
			// <INI_ADICIONAL>s
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * En este método se hace la invocación de lo métodos de carga de todas las
	 * listas, menos las que son de subformularios.
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListacmbTipoCompetencia();
		cargarListacmbNumeroManual();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este método se hace la invocación de lo métodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este método se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.EV_COMPETENCIAS;
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Se realiza la asignación de la variable origenDatos por la consulta
	 * correspondiente del formulario.
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(FrmevcompetenciasControladorEnum.NUM_MANUAL.getValue(), numeroManual);
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista listacmbTipoCompetencia
	 */
	public void cargarListacmbTipoCompetencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmevcompetenciasControladorUrlEnum.URL179.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacmbTipoCompetencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 *
	 * Carga la lista listacmbNumeroManual
	 *
	 */
	public void cargarListacmbNumeroManual() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmevcompetenciasControladorUrlEnum.URL220.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listacmbNumeroManual = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, CacheUtil.getLlaveServicio(urlConexionCache,
							FrmevcompetenciasControladorEnum.EV_MANUAL.getValue()));
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Método ejecutado al seleccionar una fila de la lista listacmbNumeroManual
	 *
	 * @param event
	 *            objeto que encapsula la acción proveniente de la vista
	 */
	public void seleccionarFilacmbNumeroManual(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(FrmevcompetenciasControladorEnum.NUMERO_MANUAL.getValue(),
				retornarString(registroAux, FrmevcompetenciasControladorEnum.NUMERO_MANUAL.getValue()));

		registro.getCampos().put(FrmevcompetenciasControladorEnum.NOMBRE_MANUAL.getValue(),
				retornarString(registroAux, FrmevcompetenciasControladorEnum.NOMBRE_MANUAL.getValue()));

		registro.getCampos().put(FrmevcompetenciasControladorEnum.VERSION.getValue(),
				retornarString(registroAux, FrmevcompetenciasControladorEnum.VERSION.getValue()));

		registro.getCampos().put(GeneralParameterEnum.SIGLA.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SIGLA.getName()));

	}

	/**
	 * Método ejecutado al seleccionar una fila de la lista listacmbNumeroManual
	 *
	 * @param event
	 *            objeto que encapsula la acción proveniente de la vista
	 */
	public void seleccionarFilacmbTipoCompetencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(FrmevcompetenciasControladorEnum.TIPO_COMPETENCIA.getValue(),
				retornarString(registroAux, GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(FrmevcompetenciasControladorEnum.NOMBRE_COMPETENCIA.getValue(),
				retornarString(registroAux, GeneralParameterEnum.NOMBRE.getName()));
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	public Long generarConsecutivo() {
		long consecutivo = 1;

		try {
			String numeroManual = retornarString(registro, FrmevcompetenciasControladorEnum.NUMERO_MANUAL.getValue());
			String version = retornarString(registro, FrmevcompetenciasControladorEnum.VERSION.getValue());
			String tipoCompetencia = retornarString(registro, "TIPO_COMPETENCIA");

			String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania, "''  AND NUMERO_MANUAL = ''",
					numeroManual, "'' AND VERSION =  ''", version, "'' AND TIPO_COMPETENCIA = ''", tipoCompetencia,
					"'' ");

			consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(GenericUrlEnum.EV_COMPETENCIAS.getTable(), criterio,
					GeneralParameterEnum.CONSECUTIVO.getName());

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return consecutivo;

	}

	public void ejecutarrcCerrar() {
		String menuActual = SessionUtil.getMenuActual();
		if ("21070101".equals(menuActual)) {
			Direccionador direccionador = new Direccionador();
			HashMap<String, Object> param = new HashMap<>();
			direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.FRM_EVMANUAL_CONTROLADOR.getCodigo()));
			param.put("rid", ridManual);
			direccionador.setParametros(param);
			SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
		} else {

			SessionUtil.redireccionarMenu();
		}
	}

	// </METODOS_ADICIONALES>
	/**
	 * Este método es invocado el método inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado en el momento después de cargar el registro
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		if (ACCION_INSERTAR.equals(accion) && ridManual != null) {
			registro.getCampos().put(FrmevmanualsControladorEnum.NUMERO_MANUAL.getValue(),
					ridManual.get(FrmevmanualsControladorEnum.KEY_NUMERO_MANUAL.getValue()));
			registro.getCampos().put(FrmevmanualsControladorEnum.VERSION.getValue(),
					ridManual.get(FrmevmanualsControladorEnum.KEY_VERSION.getValue()));

			registro.getCampos().put(GeneralParameterEnum.SIGLA.getName(), sigla);
			registro.getCampos().put(FrmevrequisitosControladorEnum.NOMBRE_MANUAL.getValue(), nombreManual);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado antes de realizar la inserción del registro.
	 *
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), generarConsecutivo());

		registro.getCampos().remove(GeneralParameterEnum.SIGLA.getName());

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción del registro.
	 *
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la inserción y actualizacion del
	 * registro
	 *
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove(FrmevcompetenciasControladorEnum.NOMBRE_COMPETENCIA.getValue());
		registro.getCampos().remove(FrmevcompetenciasControladorEnum.NOMBRE_MANUAL.getValue());
		registro.getCampos().remove(GeneralParameterEnum.SIGLA.getName());

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción y actualización del
	 * registro.
	 *
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		if (css != null) {
			cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la eliminación del registro
	 *
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado despues de realizar la eliminación del registro
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
	/**
	 * Retorna la lista listacmbTipoCompetencia
	 *
	 * @return listacmbTipoCompetencia
	 */
	public RegistroDataModelImpl getListacmbTipoCompetencia() {
		return listacmbTipoCompetencia;
	}

	/**
	 * Asigna la lista listacmbTipoCompetencia
	 *
	 * @param listacmbTipoCompetencia
	 *            Variable a asignar en listacmbTipoCompetencia
	 */
	public void setListacmbTipoCompetencia(RegistroDataModelImpl listacmbTipoCompetencia) {
		this.listacmbTipoCompetencia = listacmbTipoCompetencia;
	}

	/**
	 * @return the listacmbNumeroManual
	 */
	public RegistroDataModelImpl getListacmbNumeroManual() {
		return listacmbNumeroManual;
	}

	/**
	 * @param listacmbNumeroManual
	 *            the listacmbNumeroManual to set
	 */
	public void setListacmbNumeroManual(RegistroDataModelImpl listacmbNumeroManual) {
		this.listacmbNumeroManual = listacmbNumeroManual;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>

	private String retornarString(Registro reg, String campo) {
		return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "" : reg.getCampos().get(campo).toString();
	}

	public Map<String, Object> getRidManual() {
		return ridManual;
	}

	public void setRidManual(Map<String, Object> ridManual) {
		this.ridManual = ridManual;
	}

	public boolean isBloqNumero() {
		return bloqNumero;
	}

	public void setBloqNumero(boolean bloqNumero) {
		this.bloqNumero = bloqNumero;
	}

	public String getNombreManual() {
		return nombreManual;
	}

	public void setNombreManual(String nombreManual) {
		this.nombreManual = nombreManual;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

}
