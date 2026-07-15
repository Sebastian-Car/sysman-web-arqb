/*-
 * CuotaspartesdetallesControlador.java
 *
 * 1.0
 * 
 * 05/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.CuotaspartesdetallesControladorEnum;
import com.sysman.nomina.enums.CuotaspartesdetallesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * @author sdaza
 * @version 1, 11/07/2015
 * @modified jguerrero
 * @version 2. 05/09/2017 Se realizo el refactory de las consultas sql en el
 *          controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped
public class CuotaspartesdetallesControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private static final String CTENOMBRE = GeneralParameterEnum.NOMBRE.getName();
	private static final String CTENOMTERCERO = CuotaspartesdetallesControladorEnum.NOMBRE_TERCERO.getValue();
	private static final String CTESUCURSAL = GeneralParameterEnum.SUCURSAL.getName();
	
	private String titulo;
	
	private Map<String, Object> parametrosEntrada;
	// <DECLARAR_ATRIBUTOS>

	private String idEmpleado;
	private String nombreEmpleado;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaNIT;
	protected Map<String, Object> ridCuotas;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de CuotaspartesdetallesControlador
	 */
	@SuppressWarnings("unchecked")
	public CuotaspartesdetallesControlador() {
		super();
		compania = SessionUtil.getCompania();
		 parametrosEntrada = SessionUtil.getFlash();
		try {
			ridCuotas = (Map<String, Object>) parametrosEntrada
					.get(CuotaspartesdetallesControladorEnum.RID_LOWER.getValue());
			idEmpleado = (String) parametrosEntrada
					.get(CuotaspartesdetallesControladorEnum.IDEMPLEADO_LOWER.getValue());
			nombreEmpleado = (String) parametrosEntrada
					.get(CuotaspartesdetallesControladorEnum.NOMBREEMPLEADO_LOWER.getValue());
			
			numFormulario = GeneralCodigoFormaEnum.CUOTASPARTESDETALLES_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaNIT();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
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
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.CUOTASPARTES_DETALLE;
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		 parametrosListado.put(CuotaspartesdetallesControladorEnum.IDEMPLEADO
                 .getValue(), idEmpleado);
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaNIT
	 *
	 */
	public void cargarListaNIT() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(CuotaspartesdetallesControladorUrlEnum.URL2582.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaNIT = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				CuotaspartesdetallesControladorEnum.NIT.getValue());

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control NIT
	 * 
	 * 
	 */
	public void cambiarNIT() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control NIT en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * 
	 * @param rowNum
	 *            indice de la fila seleccionada
	 */
	public void cambiarNITC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS CONTINUOS) se
		// realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");
		// Para el cambio en una fila selecciona (PARA SUBFORMULARIOS) se
		// realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola ");
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNIT
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNIT(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));

		registro.getCampos().put(CuotaspartesdetallesControladorEnum.NIT.getValue(),
				registroAux.getCampos().get(CuotaspartesdetallesControladorEnum.NIT.getValue()));
		registro.getCampos().put(CTESUCURSAL, registroAux.getCampos().get(CTESUCURSAL));
		registro.getCampos().put(CTENOMTERCERO, registroAux.getCampos().get(CTENOMBRE));
		registro.getCampos().put(CuotaspartesdetallesControladorEnum.ID_DE_EMPLEADO.getValue(), idEmpleado);
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		titulo = SysmanFunciones.concatenar(idioma.getString("TB_TB3921").toUpperCase(), " ", nombreEmpleado);
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
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		if (!validarFechas()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
			return false;
		}

		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().remove(CTENOMTERCERO);
		return true;
		// </CODIGO_DESARROLLADO>
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
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (!validarFechas()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
			return false;
		}
		if(accion.equals(ACCION_MODIFICAR)){
			registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
			registro.getCampos().remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		}
		registro.getCampos().remove(CTENOMTERCERO);
		return true;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
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
	 * s
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
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>
		  Map<String, Object> param = new HashMap<>();

	        param.put("ridR", ridCuotas);
	        param.put(CuotaspartesdetallesControladorEnum.IDEMPLEADO_LOWER.getValue(), idEmpleado);
	        param.put(CuotaspartesdetallesControladorEnum.NOMBREEMPLEADO_LOWER.getValue(), nombreEmpleado);
	        Direccionador direccionador = new Direccionador();

	        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PERSONALS_CONTROLADOR.getCodigo()));
	        direccionador.setParametros(param);
	        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
		// </CODIGO_DESARROLLADO>
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable idEmpleado
	 * 
	 * @return idEmpleado
	 */
	public String getIdEmpleado() {
		return idEmpleado;
	}

	/**
	 * Asigna la variable idEmpleado
	 * 
	 * @param idEmpleado
	 *            Variable a asignar en idEmpleado
	 */
	public void setIdEmpleado(String idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	/**
	 * Retorna la variable nombreEmpleado
	 * 
	 * @return nombreEmpleado
	 */
	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	/**
	 * Asigna la variable nombreEmpleado
	 * 
	 * @param nombreEmpleado
	 *            Variable a asignar en nombreEmpleado
	 */
	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaNIT
	 * 
	 * @return listaNIT
	 */
	public RegistroDataModelImpl getListaNIT() {
		return listaNIT;
	}

	/**
	 * Asigna la lista listaNIT
	 * 
	 * @param listaNIT
	 *            Variable a asignar en listaNIT
	 */
	public void setListaNIT(RegistroDataModelImpl listaNIT) {
		this.listaNIT = listaNIT;
	}

	private boolean validarFechas() {
		boolean rta = true;
		if(registro.getCampos().get("FECHAINICIALM")!= null && registro.getCampos().get("FECHAFINALM") != null ){
			Date fechainicial = (Date) registro.getCampos().get("FECHAINICIALM");
			Date fechaFinal = (Date) registro.getCampos().get("FECHAFINALM");
			if (fechainicial.after(fechaFinal)) {
				rta = false;
			}
		}
		return rta;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>
}
