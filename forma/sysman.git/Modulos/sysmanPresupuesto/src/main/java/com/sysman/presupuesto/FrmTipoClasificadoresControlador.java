/*-
 * FrmTipoClasificadoresControlador.java
 *
 * 1.0
 * 
 * 10/01/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.presupuesto.enums.FrmTipoClasificadoresControladorEnum;
import com.sysman.presupuesto.enums.FrmTipoClasificadoresControladorUrlEnum;
import com.sysman.presupuesto.enums.FrmclasificadoresControladorEnum;
import com.sysman.presupuesto.enums.FrmclasificadoresControladorUrlEnum;
import com.sysman.presupuesto.enums.PlanpresupuestalptosControladorUrlEnum;
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
 * @version 1.0, 10/01/2022
 * @author cperez2
 */
@ManagedBean
@ViewScoped
public class FrmTipoClasificadoresControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anioPreparar;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anio;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaClaseClasificador;

	private boolean preparaAnio;
	/**
	 * lista tipo clasificador sera el hijo
	 */
	private RegistroDataModelImpl listaTipoClasificador;
	/**
	 * lista tipo clasificador sera el hijo
	 * 
	 */
	private RegistroDataModelImpl listaTipoClasificadorE;
	/**
	 * lista general de los hijos de un clasificador padre
	 */
	private List<Registro> listaClasificadorhijosubfrm2;
	/**
	 * variable auxiliar
	 */
	private String auxiliar;
	/**
	 * registro que pertencen al subformulario
	 */
	private Registro registroSub;
	/**
	 * 
	 */
	private String clasClasificador;
	private String IdClasClasificador;
	private boolean visibleCPCdane;
	

	@EJB
	private EjbSysmanUtilRemote sysmanUtil;
	private RegistroDataModelImpl listaClasificadorhijosubfrm;
	
	
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
	 * Crea una nueva instancia de FrmTipoClasificadoresControlador
	 */
	public FrmTipoClasificadoresControlador() {
		super();
		compania = SessionUtil.getCompania();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		registroSub = new Registro(new HashMap<String, Object>());
		try {
			numFormulario = GeneralCodigoFormaEnum.TIPOCLASIFICADOR_CONTROLADOR.getCodigo();
			validarPermisos();
			visibleCPCdane = false;
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaAno();
		cargarListaClaseClasificador();
		

		// cargarListaClasificadorhijosubfrm(registro.getCampos().get("CLASECLASIFICADOR").toString());
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		anio = registro.getCampos().get("ANO").toString();
		IdClasClasificador = registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString();
		clasClasificador = registro.getCampos().get("CLASECLASIFICADOR").toString();
		if (clasClasificador.equals("006") || clasClasificador.equals("010") ) {
			visibleCPCdane = true;
		}
		cargarListaAno();
		cargarListaClaseClasificador();
		cargarListaTipoClasificador();
		cargarListaTipoClasificadorE();

		cargarListaClasificadorhijosubfrm();
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
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
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

		enumBase = GenericUrlEnum.TIPOCLASIFICADOR;
		buscarLlave();
		asignarOrigenDatos();
		// reasignarOrigenGrilla();
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
		// origenDatos=" SELECT
		// COMPANIA,ANO,CLASECLASIFICADOR,CODIGO,NOMBRE,DESCRIPCION, EQUIVALENTE,
		// SECCIONESADI"+
		// " FROM TIPOCLASIFICADOR";
	}

	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * 
	 * @Override public void reasignarOrigenGrilla() { origenGrilla=" SELECT
	 *           COMPANIA,ANO,CLASECLASIFICADOR,CODIGO,NOMBRE,DESCRIPCION,
	 *           EQUIVALENTE, SECCIONESADI"+ " FROM TIPOCLASIFICADOR"+ " WHERE "+ "
	 *           COMPANIA = '"+compania+"'"; if (listaInicial != null) {
	 *           listaInicial.setOrigen(origenGrilla); } if (listaInicialF != null)
	 *           { listaInicialF.setOrigen(origenGrilla); } }
	 */
	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmTipoClasificadoresControladorUrlEnum.URL3567.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(FrmTipoClasificadoresControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaClaseClasificador
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaClaseClasificador() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmTipoClasificadoresControladorUrlEnum.URL3568.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);

		listaClaseClasificador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
		 
	}

	public void cargarListaClasificadorhijosubfrm() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmTipoClasificadoresControladorUrlEnum.URL1889003.getValue());
			UrlBean urlBean2 = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TIPOCLASIFICADORESHIJO.getGridKey());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue(), IdClasClasificador);

			listaClasificadorhijosubfrm2 = RegistroConverter.toListRegistro(				
						requestManager.getList(urlBean.getUrl(), param), CacheUtil.getLlaveServicio(urlConexionCache,
							FrmTipoClasificadoresControladorEnum.TIPOCLASIFICADORESHIJO.getValue()));
			
				
			listaClasificadorhijosubfrm = new RegistroDataModelImpl(
					urlBean2.getUrl(), urlBean2.getUrlConteo().getUrl(), param, CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.TIPOCLASIFICADORESHIJO.getTable()));
		} catch (SystemException | SysmanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void cargarListaTipoClasificador() {
		// OJO ESTE CLASIFICADOR DEBE CONSULTARLO SI ES CLASE PADRE
		String clasificadoresHijo = "";
		if (clasClasificador.equals("001")) {
			clasificadoresHijo = "002";
		} else if (clasClasificador.equals("002")) {
			clasificadoresHijo = "004";
		} else if (clasClasificador.equals("006")) {
			clasificadoresHijo = "007012";
		} else if (clasClasificador.equals("008")) {
			clasificadoresHijo = "012";
		} else if (clasClasificador.equals("010")) {
			clasificadoresHijo = "007";
		} else {
			clasificadoresHijo = "";
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmTipoClasificadoresControladorUrlEnum.URL1884018.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(FrmTipoClasificadoresControladorEnum.CODCLASECLASI.getValue(), clasificadoresHijo);

		listaTipoClasificador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.ID.getName());

	}

	/**
	 * 
	 */
	public void cargarListaTipoClasificadorE() {
		// OJO ESTE CLASIFICADOR DEBE CONSULTARLO SI ES CLASE PADRE
		String clasificadoresHijo = "";
		if (clasClasificador.equals("001")) {
			clasificadoresHijo = "002";
		} else if (clasClasificador.equals("002")) {
			clasificadoresHijo = "004";
		} else if (clasClasificador.equals("006")) {
			clasificadoresHijo = "007,012";
		} else if (clasClasificador.equals("008")) {
			clasificadoresHijo = "012";
		} else if (clasClasificador.equals("010")) {
			clasificadoresHijo = "007";
		} else {
			clasificadoresHijo = "";
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmTipoClasificadoresControladorUrlEnum.URL1884018.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(FrmTipoClasificadoresControladorEnum.CODCLASECLASI.getValue(), clasificadoresHijo);

		listaTipoClasificadorE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.ID.getName());

	}

	// </METODOS_CARGAR_LISTA>
	public void seleccionarFilaClaseClasificador(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CLASECLASIFICADOR", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 */
	public void seleccionarFilaTipoClasificador(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		String idPadre = registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString();
		// TEMNER EN CUENTA QUE ASI SE LLAME PADRE EL PARAMETRO ESTE ES PERTENECIENTE AL
		// HIJO
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.CLASFIFICADORHIJO.getValue(), registroAux.getCampos().get("CLASECLASIFICADORPADRE"));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue(), registroAux.getCampos().get(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue()));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.CODCLASIFICADOR.getValue(), registroAux.getCampos().get("CLASECLASIFICADORHIJO"));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue(), registroAux.getCampos().get(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue()));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue(), registroAux.getCampos().get(GeneralParameterEnum.ID.getName()));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue(), idPadre);
	}

	/**
	 * 
	 */
	public void seleccionarFilaTipoClasificadorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		String idPadre = registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString();

		auxiliar = registroAux.getCampos().get("CLASECLASIFICADORPADRE").toString();
		// TEMNER EN CUENTA QUE ASI SE LLAME PADRE EL PARAMETRO ESTE ES PERTENECIENTE AL
		// HIJO
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.CLASFIFICADORHIJO.getValue(), registroAux.getCampos().get("CLASECLASIFICADORPADRE"));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue(), registroAux.getCampos().get(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue()));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.CODCLASIFICADOR.getValue(), registroAux.getCampos().get("CLASECLASIFICADORHIJO"));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue(), registroAux.getCampos().get(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue()));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue(), registroAux.getCampos().get(GeneralParameterEnum.ID.getName()));
		registroSub.getCampos().put(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue(), idPadre);
	}

	public void cambiarTipoClasificadorC(int rowNum) {
		listaClasificadorhijosubfrm2.get(rowNum).getCampos().put(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue(),
				registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue()));
		listaClasificadorhijosubfrm2.get(rowNum).getCampos().put(FrmTipoClasificadoresControladorEnum.CODCLASIFICADOR.getValue(),
				registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.CODCLASIFICADOR.getValue()));
		listaClasificadorhijosubfrm2.get(rowNum).getCampos().put(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue(),
				registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue()));
	}

	public void editarRegSubClasificadorhijosubfrm(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {

			// se remueven los campos que no sirven para la insercion de la nueva relacion
			reg.getCampos().remove(FrmTipoClasificadoresControladorEnum.NOMBREHIJO.getValue());
			reg.getCampos().remove(FrmTipoClasificadoresControladorEnum.CODCLASIFICADOR.getValue());
			reg.getCampos().remove(FrmTipoClasificadoresControladorEnum.CLASFIFICADORHIJO.getValue());
			reg.getCampos().remove(FrmTipoClasificadoresControladorEnum.NOMBREPADRE.getValue());

			// asignamos los valores de la llave compuesta
			reg.getLlave().put("KEY_ANO", anio);
			reg.getLlave().put("KEY_COMPANIA", compania);
			reg.getLlave().put("KEY_IDPADRE", IdClasClasificador);

			// como tiene una llave compuesta lo eliminamos y creamos la nueva relacion con
			// el padre
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TIPOCLASIFICADORESHIJO.getDeleteKey());

			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			// creamos la nueva relacion con el padre
			// seteamos a el registro los campos faltantes para la creacion correcta.
			reg.getCampos().put(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue(),
					registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue()));
			reg.getCampos().put(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue(),
					registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue()));
			reg.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			reg.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
			reg.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TIPOCLASIFICADORESHIJO.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), reg.getCampos());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			JsfUtil.agregarMensajeError(idioma.getString("ERROR AL MODIFICAR: ") + ex.getMessage());
			Logger.getLogger(TercerosControlador.class.getName()).log(Level.SEVERE, null, ex);

		} finally {
			cargarListaClasificadorhijosubfrm();
		}
	}

	public void cancelarEdicionClasificadorhijosubfrm() {
		cargarListaClasificadorhijosubfrm();
	}

	public void eliminarRegSubClasificadorhijosubfrm(Registro reg) {
		try {
			reg.getLlave().put("KEY_ANO", anio);
			reg.getLlave().put("KEY_COMPANIA", compania);
			reg.getLlave().put("KEY_IDPADRE", IdClasClasificador);
			reg.getLlave().put("KEY_IDHIJO", reg.getCampos().get(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue()));

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TIPOCLASIFICADORESHIJO.getDeleteKey());

			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			
			cargarListaClasificadorhijosubfrm();
		} catch (SystemException ex) {
			JsfUtil.agregarMensajeError(idioma.getString("ERROR AL ELIMINAR: ") + ex.getMessage());
			Logger.getLogger(TercerosControlador.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			cargarListaClasificadorhijosubfrm();
		}
	}

	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo DialogoPrepararAnio
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void aceptarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;

		if (SysmanFunciones.validarVariableVacio(anioPreparar)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB448"));
			return;
		}

		insertarClasificadores();
		insertarClasificadoresHijo();

		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB455"));
		// </CODIGO_DESARROLLADO>
	}

	public boolean insertarClasificadores() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(FrmTipoClasificadoresControladorEnum.ANOPREPARAR.getValue(), anioPreparar);
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), anio);
		parametros.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmTipoClasificadoresControladorUrlEnum.URL8298.getValue());
		try {
			int rta = requestManager.saveCount(urlCreate.getUrl(), urlCreate.getMetodo(), parametros);
			if (rta <= 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB454"));
				return false;
			}
		} catch (SystemException e) {
			Logger.getLogger(FrmTipoClasificadoresControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB456"));
		}
		return true;
	}
	
	public boolean insertarClasificadoresHijo() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(FrmTipoClasificadoresControladorEnum.ANOPREPARAR.getValue(), anioPreparar);
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), anio);
		parametros.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmTipoClasificadoresControladorUrlEnum.URL1889013.getValue());
		try {
			int rta = requestManager.saveCount(urlCreate.getUrl(), urlCreate.getMetodo(), parametros);
			if (rta <= 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB454"));
				return false;
			}
		} catch (SystemException e) {
			Logger.getLogger(FrmTipoClasificadoresControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB456"));
		}
		return true;
	}

	/**
	 * realiza la insercion de los datos
	 */
	public boolean agregarRegistroSubClasificadorhijosubfrm() {

		try {
			Map<String, Object> parametros = new HashMap<>();

			parametros.put(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue(),
					registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.IDPADRE.getValue()));
			parametros.put(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue(),
					registroSub.getCampos().get(FrmTipoClasificadoresControladorEnum.IDHIJO.getValue()));

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			parametros.put(GeneralParameterEnum.ANO.getName(), anio);
			parametros.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.TIPOCLASIFICADORESHIJO.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), parametros);

			cargarListaClasificadorhijosubfrm();
		} catch (SystemException e) {
			Logger.getLogger(FrmTipoClasificadoresControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(FrmTipoClasificadoresControladorEnum.ELREGISTROYAEXISTE.getValue());
		}
		return true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo DialogoPrepararAnio
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void cancelarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Preparar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPreparar() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = true;
		if (!anio.isEmpty()) {
			anioPreparar = String.valueOf(Integer.parseInt(anio) + 1);

		}
		// </CODIGO_DESARROLLADO>
	}

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
		/*
		 * FR546-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 1,
		 * Me.Name If Nz(par("MANEJA RETENCIONES POR CENTRO DE COSTO"), "NO") = "SI"
		 * Then Me.Centro_costo.visible = True End If End Sub
		 *//*
			 * FR546-AL_ABRIR Private Sub Form_Load() DoCmd.Restore End Sub
			 */
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
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
		registro.getCampos().remove("NOMBREAPLICACION");
		registro.getCampos().remove("ID");
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

		// registro.getCampos().remove("ID");
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
		registro.getCampos().remove("NOMBREAPLICACION");
		// registro.getCampos().remove("ID");
		/*
		 * FR546-ANTES_ACTUALIZAR Private Sub Form_BeforeUpdate(Cancel As Integer) If
		 * par("MANEJA RETENCIONES POR CENTRO DE COSTO", Getcompany()) = "SI" Then If
		 * IsNull(Me!Centro_costo) Then MsgBox
		 * "Debe seleccionar un centro de costo, es requerido para el calculo de la retencion"
		 * , vbInformation + vbDefaultButton1, "Sysman Software" End If End If End Sub
		 */
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

	// <SET_GET_ATRIBUTOS>
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

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
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

	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaClaseClasificador
	 * 
	 * @return listaClaseClasificador
	 */
	public RegistroDataModelImpl getListaClaseClasificador() {
		return listaClaseClasificador;
	}

	/**
	 * Asigna la lista listaClaseClasificador
	 * 
	 * @param listaClaseClasificador Variable a asignar en listaClaseClasificador
	 */
	public void setListaClaseClasificador(RegistroDataModelImpl listaClaseClasificador) {
		this.listaClaseClasificador = listaClaseClasificador;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isPreparaAnio() {
		return preparaAnio;
	}

	public void setPreparaAnio(boolean preparaAnio) {
		this.preparaAnio = preparaAnio;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public Registro getRegistroSub() {
		return registroSub;
	}

	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	public List<Registro> getListaClasificadorhijosubfrm2() {
		return listaClasificadorhijosubfrm2;
	}

	public void setListaClasificadorhijosubfrm2(List<Registro> listaClasificadorhijosubfrm2) {
		this.listaClasificadorhijosubfrm2 = listaClasificadorhijosubfrm2;
	}
	
	

	/**
	 * @return the listaClasificadorhijosubfrm
	 */
	public RegistroDataModelImpl getListaClasificadorhijosubfrm() {
		return listaClasificadorhijosubfrm;
	}

	/**
	 * @param listaClasificadorhijosubfrm the listaClasificadorhijosubfrm to set
	 */
	public void setListaClasificadorhijosubfrm(RegistroDataModelImpl listaClasificadorhijosubfrm) {
		this.listaClasificadorhijosubfrm = listaClasificadorhijosubfrm;
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
	public RegistroDataModelImpl getListaTipoClasificador() {
		return listaTipoClasificador;
	}

	public void setListaTipoClasificador(RegistroDataModelImpl listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}

	public RegistroDataModelImpl getListaTipoClasificadorE() {
		return listaTipoClasificadorE;
	}

	public void setListaTipoClasificadorE(RegistroDataModelImpl listaTipoClasificadorE) {
		this.listaTipoClasificadorE = listaTipoClasificadorE;
	}
	public boolean isVisibleCPCdane() {
		return visibleCPCdane;
	}

	public void setVisibleCPCdane(boolean visibleCPCdane) {
		this.visibleCPCdane = visibleCPCdane;
	}

}
