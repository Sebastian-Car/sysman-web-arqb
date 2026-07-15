/*-
 * ConceptosdocsoportedianControlador.java
 *
 * 1.0
 * 
 * 05/10/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.ConceptosdocsoportedianControladorEnum;
import com.sysman.contabilidad.enums.ConceptosdocsoportedianControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.TercerosControlador;
import com.sysman.general.enums.TercerosControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/10/2022
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class ConceptosdocsoportedianControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anio;
	/**
	 * 
	 */
	private boolean preparaAnio;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAnio;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCuenta;
//	private RegistroDataModelImpl listaInicial;
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private String anioPreparar;
	private String anioPrepararFinal;
	private String anioPrepararInicial;
	private String cuentaContable;
//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de ConceptosdocsoportedianControlador
	 */
	public ConceptosdocsoportedianControlador() {
		super();
		compania = SessionUtil.getCompania();
//		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		try {
			// 2368
			numFormulario = GeneralCodigoFormaEnum.CONCEPTOSDOCSOPORTEDIAN.getCodigo();
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
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA>
		cargarListaAnio();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		try {
			cuentaContable = ejbSysmanUtil.consultarParametro(compania,
					"CLASE CONTABLE CONCEPTOS DOCUMENTO SOPORTE", SessionUtil.getModulo(), new Date(), true);
			if(cuentaContable == null || cuentaContable.trim().equals("") ) {
				JsfUtil.agregarMensajeInformativo("Paramerto CLASE CONTABLE CONCEPTOS DOCUMENTO SOPORTE No configurado");
				return;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		cargarListaCodigoCuenta();
		// </CARGAR_LISTA_COMBO_GRANDE>		
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
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
//		tabla = "CONCEPTOS_CUDE";
		enumBase = GenericUrlEnum.CONCEPTOSDOCSOPORTEDIAN;
		buscarLlave();
		asignarOrigenDatos();
//		reasignarOrigenGrilla();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		origenDatos = "SELECT CODIGO_CONCEPTO, NOMBRE_CONCEPTO, CODIGO_EQUIVALENTE_DIAN, CUENTA_CONTABLE FROM CONCEPTOS_CUDE";
	}

	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
//	public void reasignarOrigenGrilla() {
//		origenGrilla = "";
//		if (listaInicial != null) {
//			listaInicial.setOrigen(origenGrilla);
//		}
//		if (listaInicialF != null) {
//			listaInicialF.setOrigen(origenGrilla);
//		}
//	}

//<METODOS_CARGAR_LISTA>
	public void cargarLista() {
		UrlBean urlBean;
		try {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CONCEPTOSDOCSOPORTEDIAN.getGridKey());

			Map<String, Object> param = new TreeMap<>();
			param.put("KEY_COMPANIA", compania);
//				param.put("NATURALEZA", naturaleza);
			param.put(GeneralParameterEnum.ANO.getName(), anio);

			listaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.CONCEPTOSDOCSOPORTEDIAN.getTable()));
			listaInicialF = listaInicial;
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConceptosdocsoportedianControladorUrlEnum.URL21760.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(TercerosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCodigoCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCuenta() {
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();
			
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		param.put(GeneralParameterEnum.CUENTACONTABLE.getName(), cuentaContable);

		urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConceptosdocsoportedianControladorUrlEnum.URL1895020.getValue());

		listaCodigoCuenta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());

	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnio() {
		// <CODIGO_DESARROLLADO>
		cargarListaCodigoCuenta();
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCuenta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_CONTABLE", registroAux.getCampos().get("CODIGO"));
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
	/**
	 * 
	 */
	public void oprimirprepararanio() {
		preparaAnio = true; 
	}
	/**
	 * 
	 */
	public void cancelarDialogoPrepararAnio() {
		
	}
	/**
	 * 
	 */
	public void aceptarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;

		if (SysmanFunciones.validarVariableVacio(anioPrepararInicial) || SysmanFunciones.validarVariableVacio(anioPrepararFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB448"));
			return;
		}

		insertarConceptosCudsPreparaAnio();

		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB455"));
		// </CODIGO_DESARROLLADO>
	}
	public boolean insertarConceptosCudsPreparaAnio() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(ConceptosdocsoportedianControladorEnum.ANOPREPARAR.getValue(), anioPrepararFinal);
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), anioPrepararInicial);
		parametros.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConceptosdocsoportedianControladorUrlEnum.URL1895001.getValue());
		try {
			int rta = requestManager.saveCount(urlCreate.getUrl(), urlCreate.getMetodo(), parametros);
			if (rta <= 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB454"));
				return false;
			}
		} catch (SystemException e) {
			Logger.getLogger(ConceptosdocsoportedianControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB456"));
		}
		return true;
	}
//</METODOS_ADICIONALES>
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
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
			precargarRegistro();
		// </CODIGO_DESARROLLADO>
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
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", anio);
//		registro.getCampos().put("CUENTA_CONTABLE",registro.getCampos().get("CODIGO"));
//		registro.getCampos().remove("CODIGO");
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
		registro.getCampos().put("NOMBRE_CONCEPTO", ""); 
		registro.getCampos().put("CUENTA_CONTABLE",""); 
		registro.getCampos().put("CODIGO_CONCEPTO",""); 
		registro.getCampos().put("CODIGO_EQUIVALENTE_DIAN","");
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
		cargarLista();
		// </CODIGO_DESARROLLADO>
		return true;
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCodigoCuenta
	 * 
	 * @return listaCodigoCuenta
	 */
	public RegistroDataModelImpl getListaCodigoCuenta() {
		return listaCodigoCuenta;
	}

	/**
	 * Asigna la lista listaCodigoCuenta
	 * 
	 * @param listaCodigoCuenta Variable a asignar en listaCodigoCuenta
	 */
	public void setListaCodigoCuenta(RegistroDataModelImpl listaCodigoCuenta) {
		this.listaCodigoCuenta = listaCodigoCuenta;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>

	public boolean isPreparaAnio() {
		return preparaAnio;
	}

	public void setPreparaAnio(boolean preparaAnio) {
		this.preparaAnio = preparaAnio;
	}

	public String getAnioPrepararFinal() {
		return anioPrepararFinal;
	}

	public void setAnioPrepararFinal(String anioPrepararFinal) {
		this.anioPrepararFinal = anioPrepararFinal;
	}

	public String getAnioPrepararInicial() {
		return anioPrepararInicial;
	}

	public void setAnioPrepararInicial(String anioPrepararInicial) {
		this.anioPrepararInicial = anioPrepararInicial;
	}

//	public RegistroDataModelImpl getListaInicial() {
//		return listaInicial;
//	}
//
//	public void setListaInicial(RegistroDataModelImpl listaInicial) {
//		this.listaInicial = listaInicial;
//	}

}
