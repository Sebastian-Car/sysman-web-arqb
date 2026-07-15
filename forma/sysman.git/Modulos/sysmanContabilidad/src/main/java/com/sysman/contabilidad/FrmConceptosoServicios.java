/*-
 * FrmConceptosoServicios.java
 *
 * 1.0
 * 
 * 05/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.FrmConceptosoServiciosEnum;
import com.sysman.contabilidad.enums.FrmConceptosoServiciosUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TercerosControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 05/05/2024
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmConceptosoServicios extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private String anioPreparar;
	private String ano;
	private String modulo;
	private String usuario;
	private boolean preparaAnio;
	private String codigoRetencion;
	private String tipoRetencion;
	private String auxiliar;

	private boolean referenciadoVisible;
	private boolean mostrarCuentaDebito;

	private List<Registro> listaSubretencionescs;
	private List<Registro> listaClaseComprobante;
	private List<Registro> listaAno;
	private RegistroDataModelImpl listaCuentaDebito;
	private RegistroDataModelImpl listaTipoRetencion;
	private RegistroDataModelImpl listaTipoRetencionE;
	private RegistroDataModelImpl listaCodRetencion;
	private RegistroDataModelImpl listaCodRetencionE;

	private Registro registroSub;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmConceptosoServicios
	 */
	public FrmConceptosoServicios() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		ano = String.valueOf(SysmanFunciones.ano(new Date()));
		try {
			numFormulario = GeneralCodigoFormaEnum.FRCONCEPTOSOSERVICIOS.getCodigo();
			validarPermisos();
			registroSub = new Registro(new HashMap<String, Object>());
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

		cargarListaAno();
		cargarListaClaseComprobante();
		cargarListaTipoRetencion();
		cargarListaTipoRetencionE();
		cargarListaCuentaDebito();
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {

		cargarListaSubretencionescs();
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaSubretencionescs = null;
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
		enumBase = GenericUrlEnum.TA_CONCEPTOS;
		buscarLlave();
		asignarOrigenDatos();
		try {
			referenciadoVisible = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA REFERENCIADO EN CAUSACION AUTOMATICA", modulo, new Date(), true), "NO").equals("SI") ? false
							: true;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
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
		
		try {
			mostrarCuentaDebito = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA REFERENCIADO EN CAUSACION AUTOMATICA", modulo, new Date(), true), "NO").equals("SI") ? false
							: true;
						
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}

	/**
	 * 
	 * Carga la lista listaSubretencionescs
	 *
	 */
	public void cargarListaSubretencionescs() {

		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TA_DETALLE_CONCEPTOS.getGridKey());

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

			listaSubretencionescs = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), param),
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.TA_DETALLE_CONCEPTOS.getTable()));
			cargarListaCodRetencionE();
		} catch (SystemException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaClaseComprobante
	 *
	 */
	public void cargarListaClaseComprobante() {
		try {
			listaClaseComprobante = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmConceptosoServiciosUrlEnum.URL003.getValue())
													.getUrl(),
											null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmConceptosoServiciosUrlEnum.URL001.getValue())
													.getUrl(),
											param));
		} catch (SystemException e) {
			Logger.getLogger(RetencionesControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCuentaDebito
	 *
	 */
	public void cargarListaCuentaDebito() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL002.getValue());
		listaCuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTipoRetencion() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL004.getValue());
		listaTipoRetencion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTipoRetencionE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL004.getValue());
		listaTipoRetencionE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCodRetencion() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoRetencion);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL005.getValue());
		listaCodRetencion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	public void cargarListaCodRetencionE() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoRetencion);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL005.getValue());
		listaCodRetencionE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		cargarListaCuentaDebito();
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirPreparar() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = true;
		if (!ano.isEmpty()) {
			anioPreparar = String.valueOf(Integer.parseInt(ano) + 1);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo DialogoPrepararAnio
	 * en la vista
	 *
	 *
	 */
	public void aceptarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;

		if (SysmanFunciones.validarVariableVacio(ano) || SysmanFunciones.validarVariableVacio(anioPreparar)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB448"));
			return;
		}

		insertarPreparaAnio();

		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB455"));
		// </CODIGO_DESARROLLADO>

	}

	public boolean insertarPreparaAnio() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(FrmConceptosoServiciosEnum.ANOPREPARAR.getValue(), anioPreparar);
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), ano);
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL006.getValue());

		try {
			int rta = requestManager.saveCount(urlCreate.getUrl(), urlCreate.getMetodo(), parametros);

			if (rta <= 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB454"));
				return false;
			}

			Map<String, Object> param = new HashMap<>();
			param.put(FrmConceptosoServiciosEnum.ANOPREPARAR.getValue(), anioPreparar);
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

			UrlBean urlCreatesub = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmConceptosoServiciosUrlEnum.URL007.getValue());

			int rtasub = requestManager.saveCount(urlCreatesub.getUrl(), urlCreatesub.getMetodo(), param);

			if (rtasub <= 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB454"));
				return false;
			}

		} catch (SystemException e) {
			Logger.getLogger(FrmConceptosoServicios.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB456"));
		}
		return true;
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebito
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaDebito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTADEBITO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoRetencion
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoRetencion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoRetencion = registroAux.getCampos().get("CODIGO").toString();
		registroSub.getCampos().put("TIPORETENCION", tipoRetencion);
		cargarListaCodRetencion();
		cargarListaCodRetencionE();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoRetencion
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoRetencionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
		registroSub.getCampos().put("TIPORETENCION", auxiliar);
		cargarListaCodRetencion();
		cargarListaCodRetencionE();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodRetencion
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodRetencion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoRetencion = registroAux.getCampos().get("CODIGO").toString();
		registroSub.getCampos().put("CODIGORETENCION", codigoRetencion);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodRetencion
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodRetencionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
		registroSub.getCampos().put("CODIGORETENCION", auxiliar);
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	

	/**
	 * Metodo de insercion del formulario Subretencionescs
	 * 
	 */
	public void agregarRegistroSubSubretencionescs() {
		try {

			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
			registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TA_DETALLE_CONCEPTOS.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
			cargarListaSubretencionescs();
			
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	/**
	 * Metodo de edicion del formulario Subretencionescs
	 * 
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSubretencionescs(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
            reg.getCampos().remove("NOMBRE_RETENCION");
            reg.getCampos().remove("COMPANIA");
            reg.getCampos().remove("ANO");
            reg.getCampos().remove("CODIGO");
            reg.getCampos().put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
            reg.getCampos().put("DATE_MODIFIED", new Date());
            
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TA_DETALLE_CONCEPTOS.getUpdateKey());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSubretencionescs();
		}
	}

	/**
	 * Metodo de eliminacion del formulario Subretencionescs
	 * 
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSubretencionescs(Registro reg) {
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TA_DETALLE_CONCEPTOS.getDeleteKey());

			requestManager.delete(urlUpdate.getUrl(), reg.getLlave());
			cargarListaSubretencionescs();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Subretencionescs
	 *
	 */
	public void cancelarEdicionSubretencionescs() {
		cargarListaSubretencionescs();
	}

//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
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
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		if (css != null) {
			ano = registro.getCampos().get("ANO").toString();
		}

		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 * @return
	 */
	@Override
	public boolean insertarAntes() {
	//	asignarValoresRegistro("CONSECUTIVO", generarConsecutivo());
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", ano);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return
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
	 * @return
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("ANO", ano);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 * @return
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
	 * @return
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
	 * 
	 * @return
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anioPreparar
	 * 
	 * @return anioPreparar
	 */
	public String getAnioPreparar() {
		return anioPreparar;
	}

	/**
	 * Asigna la variable anioPreparar
	 * 
	 * @param anioPreparar Variable a asignar en anioPreparar
	 */
	public void setAnioPreparar(String anioPreparar) {
		this.anioPreparar = anioPreparar;
	}

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setano(String ano) {
		this.ano = ano;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaClaseComprobante
	 * 
	 * @return listaClaseComprobante
	 */
	public List<Registro> getListaClaseComprobante() {
		return listaClaseComprobante;
	}

	/**
	 * Asigna la lista listaClaseComprobante
	 * 
	 * @param listaClaseComprobante Variable a asignar en listaClaseComprobante
	 */
	public void setListaClaseComprobante(List<Registro> listaClaseComprobante) {
		this.listaClaseComprobante = listaClaseComprobante;
	}

	/**
	 * Retorna la lista listaano
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuentaDebito
	 * 
	 * @return listaCuentaDebito
	 */
	public RegistroDataModelImpl getListaCuentaDebito() {
		return listaCuentaDebito;
	}

	/**
	 * Asigna la lista listaCuentaDebito
	 * 
	 * @param listaCuentaDebito Variable a asignar en listaCuentaDebito
	 */
	public void setListaCuentaDebito(RegistroDataModelImpl listaCuentaDebito) {
		this.listaCuentaDebito = listaCuentaDebito;
	}

//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaSubretencionescs
	 * 
	 * @return listaSubretencionescs
	 */
	public List<Registro> getListaSubretencionescs() {
		return listaSubretencionescs;
	}

	/**
	 * Asigna la lista listaSubretencionescs
	 * 
	 * @param listaSubretencionescs Variable a asignar en listaSubretencionescs
	 */
	public void setListaSubretencionescs(List<Registro> listaSubretencionescs) {
		this.listaSubretencionescs = listaSubretencionescs;
	}

//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
	/**
	 * Retorna el objeto registroSub
	 * 
	 * @return registroSub
	 */
	public Registro getRegistroSub() {
		return registroSub;
	}

	/**
	 * Asigna el objeto registroSub
	 * 
	 * @param registroSub Variable a asignar en registroSub
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}
//</SET_GET_ADICIONALES>

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public boolean isPreparaAnio() {
		return preparaAnio;
	}

	public void setPreparaAnio(boolean preparaAnio) {
		this.preparaAnio = preparaAnio;
	}

	public String getCodigoRetencion() {
		return codigoRetencion;
	}

	public void setCodigoRetencion(String codigoRetencion) {
		this.codigoRetencion = codigoRetencion;
	}

	public String getTipoRetencion() {
		return tipoRetencion;
	}

	public void setTipoRetencion(String tipoRetencion) {
		this.tipoRetencion = tipoRetencion;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public RegistroDataModelImpl getListaTipoRetencion() {
		return listaTipoRetencion;
	}

	public void setListaTipoRetencion(RegistroDataModelImpl listaTipoRetencion) {
		this.listaTipoRetencion = listaTipoRetencion;
	}

	public RegistroDataModelImpl getListaTipoRetencionE() {
		return listaTipoRetencionE;
	}

	public void setListaTipoRetencionE(RegistroDataModelImpl listaTipoRetencionE) {
		this.listaTipoRetencionE = listaTipoRetencionE;
	}

	public RegistroDataModelImpl getListaCodRetencion() {
		return listaCodRetencion;
	}

	public void setListaCodRetencion(RegistroDataModelImpl listaCodRetencion) {
		this.listaCodRetencion = listaCodRetencion;
	}

	public RegistroDataModelImpl getListaCodRetencionE() {
		return listaCodRetencionE;
	}

	public void setListaCodRetencionE(RegistroDataModelImpl listaCodRetencionE) {
		this.listaCodRetencionE = listaCodRetencionE;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}
	

	public boolean isReferenciadoVisible() {
		return referenciadoVisible;
	}

	public void setReferenciadoVisible(boolean referenciadoVisible) {
		this.referenciadoVisible = referenciadoVisible;
	}
	
	public boolean isMostrarCuentaDebito() {
		return mostrarCuentaDebito;
	}

	public void setMostrarCuentaDebito(boolean mostrarCuentaDebito) {
		this.mostrarCuentaDebito = mostrarCuentaDebito;
	}

}
