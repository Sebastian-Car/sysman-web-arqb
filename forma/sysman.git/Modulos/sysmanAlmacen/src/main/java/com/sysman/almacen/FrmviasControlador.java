package com.sysman.almacen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.sysman.almacen.enums.FrmviasControladorEnum;
import com.sysman.almacen.enums.FrmviasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 18/02/2016
 * 
 * @version 2, 28/04/2017, pespitia :<br>
 *          Manejo de EJBs.<br>
 *          Refactoring.
 */
@ManagedBean
@ViewScoped
public class FrmviasControlador extends BeanBaseDatosAcmeImpl {

	private final String compania;

	/**
	 * Constante a nivel de clase que aloja el codigo del modulo del que se llama el
	 * formulario.
	 */
	private final String modulo;

	/**
	 * Constante a nivel de clase que aloja el codigo del usuario que abre el
	 * formulario.
	 */
	private final String usuario;

	/**
	 * Constante a nivel de clase que aloja el nombre del enumerado
	 * <code>GeneralParameterEnum.SERIE</code>
	 */
	private final String cSerie;

	/**
	 * Constante a nivel de clase que aloja el nombre del enumerado
	 * <code>GeneralParameterEnum.DATE_MODIFIED</code>
	 */
	private final String cDateModified;

	/**
	 * Constante a nivel de clase que aloja el nombre del enumerado
	 * <code>GeneralParameterEnum.DATE_CREATED</code>
	 */
	private final String cDateCreated;

	/**
	 * Constante a nivel de clase que aloja el nombre del enumerado
	 * <code>GeneralParameterEnum.CREATED_BY</code>
	 */
	private final String cCreatedBy;

	/**
	 * Constante a nivel de clase que aloja el nombre del enumerado
	 * <code>GeneralParameterEnum.COMPANIA</code>
	 */
	private final String cCompania;

	/**
	 * Constante a nivel de clase que aloja el valor del enumerado
	 * <code>FrmviasControladorEnum.ID_VIA</code>
	 */
	private final String cIdVia;

	/**
	 * Nombre del campo que tiene el area del terreno.
	 */
	private static final String AREA_TERRENO = "AREA_TERRENO";

	/**
	 * Prefijo que se le asigna a las imagenes.
	 */
	private static final String FOTO_VIA = "fotoVia";
	/**
	 * Nombre de la propiedad que contiene el texto de registro insertado.
	 */
	private static final String MSM_REGISTRO_INGRESADO = "MSM_REGISTRO_INGRESADO";
	/**
	 * Nombre de la propiedad que contiene el texto de registro eliminado.
	 */
	private static final String MSM_REGISTRO_ELIMINADO = "MSM_REGISTRO_ELIMINADO";
	/**
	 * Nombre de la propiedad que contiene el texto de registro modificado.
	 */
	private static final String MSM_REGISTRO_MODIFICADO = "MSM_REGISTRO_MODIFICADO";
	/**
	 * Nombre del campo valor total.
	 */
	private static final String VALOR_TOTAL = "VALOR_TOTAL";
	/**
	 * Nombre del campo valor obra.
	 */
	private static final String VAL_OBRA = "VAL_OBRA";
	/**
	 * Nombre del campo valor terreno.
	 */
	private static final String VAL_TERRENO = "VAL_TERRENO";
	/**
	 * Nombre del campo valor unitario.
	 */
	private static final String VAL_UNITARIO = "VAL_UNITARIO";
	/**
	 * Nombre del campo ruta foto.
	 */
	private static final String RUTA_FOTO = "RUTA_FOTO";
	/**
	 * Nombre del campo codigo foto.
	 */
	private static final String COD_FOTO = "COD_FOTO";
	/**
	 * Nombre del campo responsable.
	 */
	private static final String RESPONSABLE = "RESPONSABLE";
	/**
	 * Nombre del campo sucursal.
	 */
	private static final String SUCURSAL = "SUCURSAL";

	/**
	 * Nombre del campo nombre.
	 */
	private static final String NOMBRE = "NOMBRE";

	/**
	 * Nombre del campo serie placa.
	 */
	private static final String SERIE_PLACA = "SERIE_PLACA";
	/**
	 * Nombre del campo cuenta.
	 */
	private static final String CUENTA = "CUENTA";
	/**
	 * Nombre del campo descripcion.
	 */
	private static final String DESCRIPCION = "DESCRIPCION";
	private Registro registroSubSubItems;
	private Registro registroSubSubValuoVia;
	private Registro registroSubSubAdiciones;
	private List<Registro> listaClaseVia;
	private List<Registro> listaEstado;
	private List<Registro> listaSector;
	private List<Registro> listaSubitems;
	private List<Registro> listaSubvaluovia;
	private List<Registro> listaSubadiciones;
	private List<Registro> listaAnio;
	private RegistroDataModelImpl listaResponsable;
	private RegistroDataModelImpl listaCSeriePlaca;
	private RegistroDataModelImpl listaNombre;
	private int anioHistorico;
	private String clasificacion;
	private String directorio;
	private boolean cargaAvaluo;
	private boolean dialogoVisible;
	private String dialogoMensaje;
	private Registro registroCambio;
	private String nombreResponsable;
	private String stringFoto;
	private String rutaFoto;
	private String nombreFoto;
	private InputStream inputStreamFoto;
	private StreamedContent archivoDescarga;

	// <EJBs>
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	// <EJBs>

	public FrmviasControlador() {
		super();
		numFormulario = GeneralCodigoFormaEnum.FRMVIAS_CONTROLADOR.getCodigo();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		cSerie = GeneralParameterEnum.SERIE.getName();
		cDateModified = GeneralParameterEnum.DATE_MODIFIED.getName();
		cDateCreated = GeneralParameterEnum.DATE_CREATED.getName();
		cCreatedBy = GeneralParameterEnum.CREATED_BY.getName();
		cCompania = GeneralParameterEnum.COMPANIA.getName();
		cIdVia = FrmviasControladorEnum.ID_VIA.getValue();
		try {
			registro = new Registro(new HashMap<String, Object>());
			registroSubSubItems = new Registro(new HashMap<String, Object>());
			registroSubSubValuoVia = new Registro(new HashMap<String, Object>());
			registroSubSubAdiciones = new Registro(new HashMap<String, Object>());
			cargaAvaluo = false;
			validarPermisos();
		} catch (SysmanException ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.VIAS;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(cCompania, compania);
	}

	@Override
	public void iniciarListas() {
		cargarListaCSeriePlaca();
		cargarListaNombre();
		cargarListaResponsable();
		cargarListaClaseVia();
		cargarListaEstado();
		cargarListaSector();
		cargarListaAnio();
	}

	@Override
	public void iniciarListasSub() {
		cargarListaSubitems();
		cargarListaSubvaluovia();
		cargarListaSubadiciones();
	}

	@Override
	public void iniciarListasSubNulo() {
		listaSubitems = null;
		listaSubvaluovia = null;
		listaSubadiciones = null;
	}

	@Override
	public void cargarRegistro() {
		precargarRegistro();
		if (ACCION_INSERTAR.equals(accion)) {
			ejecutarAccionesInsertar();
		} else if (ACCION_MODIFICAR.equals(accion)) {
			ejecutarAccionesActualizar();
		}
	}

	/**
	 * Accciones que se ejecutan al cargar el registro en modo actualizacion.
	 */
	private void ejecutarAccionesActualizar() {
		inicializarStream();
		String idVia = registro.getCampos().get(cIdVia).toString();
		HashMap<String, Object> param = new HashMap<>();
		param.put(cCompania, compania);
		param.put(cIdVia, idVia);
		Registro regAux = recuperarRegistroDSS(param, FrmviasControladorUrlEnum.URL0001.getValue());
		int anoActual = SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR);
		if (regAux != null) {
			anioHistorico = Integer
					.parseInt(SysmanFunciones.nvl(regAux.getCampos().get("MAX_ANO"), anoActual).toString());
		} else {
			anioHistorico = anoActual;
		}
		cargarListaSubvaluovia();
		StringBuilder striTem = new StringBuilder("");
		List<Registro> registros = recuperarListDSS(param, FrmviasControladorUrlEnum.URL0002.getValue());
		String codigoItem = "";
		for (Registro item : registros) {
			codigoItem = (String) item.getCampos().get("CODIGO_ITEM");
			striTem.append(SysmanFunciones.nvlStr(codigoItem, ""));
		}
		registros = recuperarListDSS(param, FrmviasControladorUrlEnum.URL0003.getValue());
		for (int i = 0; i < registros.size(); i++) {
			if (!striTem.toString().contains(codigoItem)) {
				codigoItem = SysmanFunciones.nvlStr(codigoItem, "999");
				try {
					HashMap<String, Object> parSet = new HashMap<>();
					parSet.put(cCompania, compania);
					parSet.put(cIdVia, idVia);
					parSet.put(FrmviasControladorEnum.CODIGO_ITEM.getValue(), codigoItem);
					parSet.put(cDateCreated, new Date());
					parSet.put(cCreatedBy, usuario);

					UrlBean urlCreate = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmviasControladorUrlEnum.URL31286.getValue());

					requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), parSet);
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeAlerta(e.getMessage());
				}

			}
		}

		cargarListaSubitems();

		param.put(FrmviasControladorEnum.TIPO.getValue(), "N");
		param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(), registro.getCampos().get("COD_INVENTARIO").toString());

		registros = recuperarListDSS(param, FrmviasControladorUrlEnum.URL0004.getValue());

		if (!registros.isEmpty()) {
			clasificacion = SysmanFunciones.nvlStr((String) registros.get(0).getCampos().get("NOMBRELARGO"), "");
		}

		recuperarListDSS(param, FrmviasControladorUrlEnum.URL0005.getValue());

		if (!registros.isEmpty()) {
			nombreResponsable = SysmanFunciones.nvlStr((String) registros.get(0).getCampos().get(NOMBRE), "");
		}

		String seriePlaca = registro.getCampos().get(SERIE_PLACA).toString();

		param.put(cSerie, seriePlaca);

		regAux = recuperarRegistroDSS(param, FrmviasControladorUrlEnum.URL0006.getValue());

		accion = "0".equals(regAux.getCampos().get(CUENTA).toString()) ? ACCION_MODIFICAR : ACCION_VER;

		vidaUtilRestante(seriePlaca);
	}

	/**
	 * Util para ejecutar el servicio DSS y recuperar la lista de registros.
	 * 
	 * @param param
	 *            Parametros de la consulta del servicio.
	 * @param enumUrl
	 *            Codigo del servicio y el recurso a utilizar.
	 * @return
	 */
	private List<Registro> recuperarListDSS(Map<String, Object> param, String enumUrl) {
		List<Registro> list = null;
		try {
			list = RegistroConverter.toListRegistro(requestManager
					.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(enumUrl).getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return list;
	}

	/**
	 * Util cuando el servicio retorna un unico registro.
	 * 
	 * @param param
	 *            Parametros de la consulta del recurso.
	 * @param enumUrl
	 *            Codigo del servicio y el recurso.
	 * @return El registro.
	 */
	private Registro recuperarRegistroDSS(Map<String, Object> param, String enumUrl) {
		Registro regAux = null;
		try {
			regAux = RegistroConverter.toRegistro(requestManager
					.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(enumUrl).getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return regAux;
	}

	/**
	 * Accciones que se ejecutan al cargar el registro en modo insercion.
	 */
	private void ejecutarAccionesInsertar() {
		directorio = null;
		clasificacion = null;
		nombreResponsable = null;
		inputStreamFoto = null;
		stringFoto = null;
		rutaFoto = null;
		nombreFoto = null;
		cargaAvaluo = false;
		anioHistorico = SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR);
	}

	public void cargarListaSubitems() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(cCompania, compania);
			param.put(cIdVia, registro.getCampos().get(cIdVia).toString());

			listaSubitems = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(GenericUrlEnum.VIAS_ITEMS.getGridKey()).getUrl(),
							param), CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.VIAS_ITEMS.getTable()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeAlerta(e.getMessage());
		}
	}

	public void cargarListaSubvaluovia() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(cCompania, compania);
			param.put(cIdVia, registro.getCampos().get(cIdVia).toString());

			listaSubvaluovia = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															GenericUrlEnum.HIST_AVALUO_VIA.getGridKey())
													.getUrl(),
											param),
							CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.HIST_AVALUO_VIA.getTable()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeAlerta(e.getMessage());
		}
	}

	public void cargarListaSubadiciones() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(cCompania, compania);
			param.put(cIdVia, registro.getCampos().get(cIdVia).toString());

			listaSubadiciones = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(GenericUrlEnum.ADICION_VIAS.getGridKey()).getUrl(),
							param),
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.ADICION_VIAS.getTable()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeAlerta(e.getMessage());
		}
	}

	public void cargarListaClaseVia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		try {
			listaClaseVia = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmviasControladorUrlEnum.URL18628.getValue())
													.getUrl(),
											param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaEstado() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		try {
			listaEstado = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmviasControladorUrlEnum.URL18996.getValue())
													.getUrl(),
											param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaSector() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		try {
			listaSector = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmviasControladorUrlEnum.URL19388.getValue())
													.getUrl(),
											param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaResponsable() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmviasControladorUrlEnum.URL19805.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"RESPONSABLE");
	}

	public void cargarListaCSeriePlaca() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmviasControladorUrlEnum.URL20665.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmviasControladorEnum.TIPOELEMENTO.getValue(), "N");

		listaCSeriePlaca = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cSerie);

	}

	public void cargarListaNombre() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmviasControladorUrlEnum.URL22409.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmviasControladorEnum.TIPOELEMENTO.getValue(), "N");

		listaNombre = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"SERIE");
	}

	public void cargarListaAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		try {
			listaAnio = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmviasControladorUrlEnum.URL21167.getValue())
													.getUrl(),
											param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void agregarRegistroSubSubitems() {
		// <CODIGO_DESARROLLADO>
		// <CODIGO_DESARROLLADO>
	}

	public void editarRegSubSubitems(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(cCompania);
			reg.getCampos().remove(cIdVia);
			reg.getCampos().remove("CODIGO_ITEM");
			reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
			reg.getCampos().put(cDateModified, new Date());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.VIAS_ITEMS.getUpdateKey());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSubitems();
		}
	}

	public void eliminarRegSubSubitems(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.VIAS_ITEMS.getDeleteKey());

			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(MSM_REGISTRO_ELIMINADO));
			cargarListaSubitems();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	public void cancelarEdicionSubitems() {
		cargarListaSubitems();
		cargarListaSubvaluovia();
		cargarListaSubadiciones();
	}

	public void agregarRegistroSubSubvaluovia() {
		try {
			registroSubSubValuoVia.getCampos().put(cCompania, compania);
			registroSubSubValuoVia.getCampos().put(cCreatedBy, usuario);
			registroSubSubValuoVia.getCampos().put(cDateCreated, new Date());
			registroSubSubValuoVia.getCampos().put(cIdVia, registro.getCampos().get(cIdVia));

			registroSubSubValuoVia.getCampos().remove(VALOR_TOTAL);

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.HIST_AVALUO_VIA.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSubValuoVia.getCampos());

			cargarListaSubvaluovia();
			JsfUtil.agregarMensajeInformativo(idioma.getString(MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSubSubValuoVia = new Registro(new HashMap<String, Object>());
		}
	}

	public void editarRegSubSubvaluovia(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(VALOR_TOTAL);
			reg.getCampos().remove(cCompania);
			reg.getCampos().remove(cIdVia);
			reg.getCampos().put(cDateModified, new Date());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.HIST_AVALUO_VIA.getUpdateKey());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSubvaluovia();
		}
	}

	public void eliminarRegSubSubvaluovia(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.HIST_AVALUO_VIA.getDeleteKey());

			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(MSM_REGISTRO_ELIMINADO));
			cargarListaSubvaluovia();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	public void cancelarEdicionSubvaluovia() {
		cargarListaSubvaluovia();
		cargarListaSubadiciones();
	}

	public void agregarRegistroSubSubadiciones() {
		// <CODIGO_DESARROLLADO>
		// <CODIGO_DESARROLLADO>
	}

	public void editarRegSubSubadiciones(RowEditEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cancelarEdicionSubadiciones() {
		cargarListaSubadiciones();
	}

	public void oprimirCambiar(ActionEvent ac) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaCSeriePlaca(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		if (css != null) {
			dialogoMensaje = idioma.getString("TB_TB3114")
					.replace("#PLACAINI#", registro.getCampos().get(SERIE_PLACA).toString())
					.replace("#PLACAFIN#", SysmanFunciones.nvl(registroAux.getCampos().get(cSerie), "").toString())
					.replace("#INMUEBLE#", registro.getCampos().get(NOMBRE).toString());
			dialogoVisible = true;
			registroCambio = registroAux;
		} else {
			cambiarPlaca(registroAux);
		}

	}

	public void seleccionarFilaNombre(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put(NOMBRE, SysmanFunciones.nvl(registroAux.getCampos().get(DESCRIPCION), "").toString());
	}

	@Override
	public void abrirFormulario() {
		// Metodo heredado de BeanBaseDatosAcme
	}

	@Override
	public boolean insertarAntes() {
		registro.getCampos().put(cCompania, compania);
		registro.getCampos().put(cIdVia, generarIdVia());
		return true;
	}

	@Override
	public boolean insertarDespues() {
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		if (ACCION_MODIFICAR.equals(accion)) {
			registro.getCampos().remove(cCompania);
			registro.getCampos().remove(cIdVia);
		}
		return true;
	}

	@Override
	public boolean actualizarDespues() {
	    if (inputStreamFoto != null && nombreFoto != null && rutaFoto != null) {
                JsfUtil.upload(inputStreamFoto, nombreFoto, rutaFoto);
                inicializarStream();
            }
            return true;
	}

	@Override
	public boolean eliminarAntes() {
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		return true;
	}

	public boolean validarArchivoImagen(UploadedFile archivoImagen) {
	    if (validarArchivo(archivoImagen)) {
                if (SysmanFunciones.validarVariableVacio(directorio)) {
                        JsfUtil.agregarMensajeError(idioma.getString("TB_TB1955"));
                        return false;
                }
                String extImagen = archivoImagen.getFileName();
                if (!esFormatoValido(extImagen)) {
                        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1958"));
                        return false;
                }
                return crearArchivoImagen(archivoImagen, extImagen);
        }
        return false;
	}

	private boolean crearArchivoImagen(UploadedFile archivoImagen, String extImagen) {
	    try {
                    File ficheroImagen = new File(directorio);
    
                    String subExtImagen = extImagen.substring(extImagen.lastIndexOf('.'), extImagen.length());
                    if (ficheroImagen.exists() || (ficheroImagen.isDirectory() && ficheroImagen.mkdir())) {
                            return guardarImagen(archivoImagen, subExtImagen);
                    } else {
                            JsfUtil.agregarMensajeAlertaVentana(idioma.getString("TB_TB1963"));
                            return false;
                    }
            } catch (NullPointerException ex) {
                    logger.error(ex.getMessage(), ex);
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB104"));
                    return false;
            }
	}

	/**
	 * Valida que el archivo no sea nulo o el nombre este vacio.
	 * 
	 * @param archivoImagen
	 * @return Verdadero si es correcto el archivo.
	 */
	private boolean validarArchivo(UploadedFile archivoImagen) {
		return archivoImagen != null && !"".equals(archivoImagen.getFileName());
	}

	/**
	 * Guardado de la imagen.
	 * 
	 * @param archivoImagen
	 *            archivo de la imagen.
	 * @param subExtImagen
	 *            extensi�n de la imagen.
	 * @return Verdadero si la imagen se creo correctamente, falso de otra manera.
	 */
	private boolean guardarImagen(UploadedFile archivoImagen, String subExtImagen) {
	    String nombreArchivo = FOTO_VIA + registro.getCampos().get(cIdVia) + subExtImagen;
            nombreFoto = nombreArchivo;
            registro.getCampos().put(RUTA_FOTO, directorio);
            registro.getCampos().put(COD_FOTO, nombreArchivo);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1961") + directorio + nombreArchivo);
            return true;

	}

	/**
	 * Valida si el formato de la imagen es compatible.
	 * 
	 * @param extImagen
	 *            extension de la imagen
	 * @return Verdadero si el formato es valido.
	 */
	private boolean esFormatoValido(String extImagen) {
		return extImagen.endsWith(".jpg") || extImagen.endsWith(".png") || extImagen.endsWith(".bmp");
	}

	public void cambiarPlaca(Registro registroAux) {
		BigInteger serie = new BigInteger(SysmanFunciones.nvl(registroAux.getCampos().get(cSerie), "0").toString());

		HashMap<String, Object> param = new HashMap<>();
		param.put(cCompania, compania);
		param.put(FrmviasControladorEnum.SERIE_PLACA.getValue(), serie);

		Registro regAux = recuperarRegistroDSS(param, FrmviasControladorUrlEnum.URL0007.getValue());

		if (!"0".equals(regAux.getCampos().get(CUENTA).toString())) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1967"));
			return;
		}

		regAux = recuperarRegistroDSS(param, FrmviasControladorUrlEnum.URL0008.getValue());

		if (!"0".equals(regAux.getCampos().get(CUENTA).toString())) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1968"));
			return;
		}

		registro.getCampos().put(SERIE_PLACA, serie);

		registro.getCampos().put("COD_INVENTARIO",
				SysmanFunciones.nvl(registroAux.getCampos().get("ELEMENTO"), "").toString());

		registro.getCampos().put(RESPONSABLE,
				SysmanFunciones.nvl(registroAux.getCampos().get(RESPONSABLE), "").toString());

		registro.getCampos().put(SUCURSAL,
				SysmanFunciones.nvl(registroAux.getCampos().get("SUCURSAL_RESPONSABLE"), "").toString());

		clasificacion = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRELARGO"), "").toString();

		nombreResponsable = SysmanFunciones.nvl(registroAux.getCampos().get(NOMBRE), "").toString();
	}

	public void cambiarAreaTerreno() {
		if (registroSubSubValuoVia.getCampos().get(AREA_TERRENO) != null
				&& registroSubSubValuoVia.getCampos().get(VAL_UNITARIO) != null) {
			registroSubSubValuoVia.getCampos().put(VAL_TERRENO,
					Double.parseDouble(registroSubSubValuoVia.getCampos().get(AREA_TERRENO).toString())
							* Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_UNITARIO).toString()));
			if (registroSubSubValuoVia.getCampos().get(VAL_OBRA) != null) {
				registroSubSubValuoVia.getCampos().put(VALOR_TOTAL,
						(Double.parseDouble(registroSubSubValuoVia.getCampos().get(AREA_TERRENO).toString())
								* Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_UNITARIO).toString()))
								+ Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_OBRA).toString()));
			}
		}
	}

	public void cambiarAreaTerrenoC(int rowNum) {
		if (listaSubvaluovia.get(rowNum).getCampos().get(AREA_TERRENO) != null
				&& listaSubvaluovia.get(rowNum).getCampos().get(VAL_UNITARIO) != null) {
			listaSubvaluovia.get(rowNum).getCampos().put(VAL_TERRENO,
					Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(AREA_TERRENO).toString()) * Double
							.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_UNITARIO).toString()));
			if (listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA) != null) {
				listaSubvaluovia.get(rowNum).getCampos().put(VALOR_TOTAL, (Double
						.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(AREA_TERRENO).toString())
						* Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_UNITARIO).toString()))
						+ Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA).toString()));
			}
		}
	}

	public void cambiarValUnitario() {
		if (registroSubSubValuoVia.getCampos().get(AREA_TERRENO) != null
				&& registroSubSubValuoVia.getCampos().get(VAL_UNITARIO) != null) {
			registroSubSubValuoVia.getCampos().put(VAL_TERRENO,
					Double.parseDouble(registroSubSubValuoVia.getCampos().get(AREA_TERRENO).toString())
							* Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_UNITARIO).toString()));
			if (registroSubSubValuoVia.getCampos().get(VAL_OBRA) != null) {
				registroSubSubValuoVia.getCampos().put(VALOR_TOTAL,
						(Double.parseDouble(registroSubSubValuoVia.getCampos().get(AREA_TERRENO).toString())
								* Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_UNITARIO).toString()))
								+ Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_OBRA).toString()));
			}
		}
	}

	public void cambiarValUnitarioC(int rowNum) {
		if (listaSubvaluovia.get(rowNum).getCampos().get(AREA_TERRENO) != null
				&& listaSubvaluovia.get(rowNum).getCampos().get(VAL_UNITARIO) != null) {
			listaSubvaluovia.get(rowNum).getCampos().put(VAL_TERRENO,
					Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(AREA_TERRENO).toString()) * Double
							.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_UNITARIO).toString()));
			if (listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA) != null) {
				listaSubvaluovia.get(rowNum).getCampos().put(VALOR_TOTAL, (Double
						.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(AREA_TERRENO).toString())
						* Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_UNITARIO).toString()))
						+ Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA).toString()));
			}
		}
	}

	public void cambiarValTerreno() {
		if (registroSubSubValuoVia.getCampos().get(VAL_TERRENO) != null
				&& registroSubSubValuoVia.getCampos().get(VAL_OBRA) != null) {
			registroSubSubValuoVia.getCampos().put(VALOR_TOTAL,
					Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_TERRENO).toString())
							+ Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_OBRA).toString()));
		}
	}

	public void cambiarValTerrenoC(int rowNum) {
		if (listaSubvaluovia.get(rowNum).getCampos().get(VAL_TERRENO) != null
				&& listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA) != null) {
			listaSubvaluovia.get(rowNum).getCampos().put(VALOR_TOTAL,
					Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_TERRENO).toString())
							+ Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA).toString()));
		}
	}

	public void cambiarValObra() {
		if (registroSubSubValuoVia.getCampos().get(VAL_TERRENO) != null
				&& registroSubSubValuoVia.getCampos().get(VAL_OBRA) != null) {
			registroSubSubValuoVia.getCampos().put(VALOR_TOTAL,
					Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_TERRENO).toString())
							+ Double.parseDouble(registroSubSubValuoVia.getCampos().get(VAL_OBRA).toString()));
		}
	}

	public void cambiarValObraC(int rowNum) {
		if (listaSubvaluovia.get(rowNum).getCampos().get(VAL_TERRENO) != null
				&& listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA) != null) {
			listaSubvaluovia.get(rowNum).getCampos().put(VALOR_TOTAL,
					Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_TERRENO).toString())
							+ Double.parseDouble(listaSubvaluovia.get(rowNum).getCampos().get(VAL_OBRA).toString()));
		}
	}

	public void aceptarCambioPlaca() {
		// <CODIGO_DESARROLLADO>
		cambiarPlaca(registroCambio);
		dialogoVisible = false;
		// </CODIGO_DESARROLLADO>
	}

	public void cancelarCambioPlaca() {
		// <CODIGO_DESARROLLADO>
		dialogoVisible = false;
		// </CODIGO_DESARROLLADO>
	}

	public String generarIdVia() {
		try {
			long codigo = ejbSysmanUtil.generarSiguienteConsecutivo(GenericUrlEnum.VIAS.getTable(),
					"COMPANIA =''" + compania + "''", cIdVia);

			return SysmanFunciones.padl(Long.toString(codigo), 10, "0");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1970"));
			return "";
		}
	}

	public void seleccionarFilaResponsable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put(RESPONSABLE,
				SysmanFunciones.nvl(registroAux.getCampos().get(RESPONSABLE), "").toString());
		registro.getCampos().put(SUCURSAL, SysmanFunciones.nvl(registroAux.getCampos().get(SUCURSAL), "").toString());
	}

	public void vidaUtilRestante(String placa) {
		int vidaUtil = 0;

		HashMap<String, Object> param = new HashMap<>();
		param.put(cCompania, compania);
		param.put(cSerie, placa);

		List<Registro> aux = recuperarListDSS(param, FrmviasControladorUrlEnum.URL0009.getValue());

		if (aux.isEmpty()) {
			return;
		}

		for (Registro aux1 : aux) {
			if (!"00".equals(aux1.getCampos().get("CODIGO"))) {
				if (aux1.getCampos().get("MESESVIDAUTILPLACA") != null) {
					vidaUtil = Integer.parseInt(aux1.getCampos().get("MESESVIDAUTILPLACA").toString());
				} else {
					vidaUtil = Integer.parseInt(aux1.getCampos().get("MESESVIDAUTIL").toString());
				}
			} else {
				cargaAvaluo = true;
				return;
			}
		}

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), "000000000000");

		Registro regAux = recuperarRegistroDSS(param, FrmviasControladorUrlEnum.URL0010.getValue());

		try {
			HashMap<String, Object> parSet = new HashMap<>();
			parSet.put(cCompania, compania);
			parSet.put(cIdVia, registro.getCampos().get(cIdVia).toString());
			parSet.put(GeneralParameterEnum.ANO.getName(), anioHistorico);
			parSet.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			parSet.put(cDateModified, new Date());
			parSet.put(FrmviasControladorEnum.VIDA_UTIL.getValue(),
					vidaUtil - Integer.parseInt(regAux.getCampos().get(CUENTA).toString()));

			Parameter parametro = new Parameter();
			parametro.setFields(parSet);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmviasControladorUrlEnum.URL25884.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parametro);

			cargarListaSubvaluovia();
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void cargarArchivoLectorFoto(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		try {
		    byte[] bytes = IOUtils.toByteArray(event.getFile().getInputstream());
                    stringFoto = JsfUtil.encodeImage(bytes);
                    inputStreamFoto = event.getFile().getInputstream();

                    rutaFoto = ejbSysmanUtil.consultarParametro(compania, "RUTA IMAGENES BIENES INMUEBLES", modulo, new Date(),
                                    false);
                    directorio = "";
                    nombreFoto = event.getFile().getFileName();
                    directorio = rutaFoto;
                    registro.getCampos().put(RUTA_FOTO, rutaFoto);
                    validarArchivoImagen(event.getFile());
		} catch (IOException | SystemException e) {
			logger.error(e.getMessage(), e);
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirDescarga() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (ACCION_INSERTAR.equalsIgnoreCase(accion)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1972"));
			return;
		}
		try {
			if (registro.getCampos().get(RUTA_FOTO) != null && registro.getCampos().get(COD_FOTO) != null) {
				archivoDescarga = JsfUtil.getArchivoDescarga(inputStreamFoto,
						registro.getCampos().get(COD_FOTO).toString());
				inicializarStream();
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1973"));
			}

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1977"));
		} catch (JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeAlertaVentana(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void inicializarStream() {
	    String ruta = SysmanFunciones.nvlStr((String) registro.getCampos().get(RUTA_FOTO), "");
            String codigoFoto = SysmanFunciones.nvlStr((String) registro.getCampos().get(COD_FOTO), "");
            ruta = ruta + codigoFoto;
            if (!SysmanFunciones.validarVariableVacio(ruta)) {
                    try {
                            stringFoto = JsfUtil.encodeImage(ruta);
                            inputStreamFoto = new FileInputStream(new File(ruta));
                    } catch (FileNotFoundException ex) {
                            logger.error(ex.getMessage(), ex);
                            JsfUtil.agregarMensajeAlerta(
                                            SysmanFunciones.concatenar(idioma.getString("TB_TB1978"), "<br>", ex.getMessage()));
                    }
            }
	}

	public List<Registro> getListaClaseVia() {
		return listaClaseVia;
	}

	public void setListaClaseVia(List<Registro> listaClaseVia) {
		this.listaClaseVia = listaClaseVia;
	}

	public List<Registro> getListaEstado() {
		return listaEstado;
	}

	public void setListaEstado(List<Registro> listaEstado) {
		this.listaEstado = listaEstado;
	}

	public List<Registro> getListaSector() {
		return listaSector;
	}

	public void setListaSector(List<Registro> listaSector) {
		this.listaSector = listaSector;
	}

	public RegistroDataModelImpl getListaResponsable() {
		return listaResponsable;
	}

	public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
		this.listaResponsable = listaResponsable;
	}

	public List<Registro> getListaSubitems() {
		return listaSubitems;
	}

	public void setListaSubitems(List<Registro> listaSubitems) {
		this.listaSubitems = listaSubitems;
	}

	public List<Registro> getListaSubvaluovia() {
		return listaSubvaluovia;
	}

	public void setListaSubvaluovia(List<Registro> listaSubvaluovia) {
		this.listaSubvaluovia = listaSubvaluovia;
	}

	public List<Registro> getListaSubadiciones() {
		return listaSubadiciones;
	}

	public void setListaSubadiciones(List<Registro> listaSubadiciones) {
		this.listaSubadiciones = listaSubadiciones;
	}

	public RegistroDataModelImpl getListaCSeriePlaca() {
		return listaCSeriePlaca;
	}

	public void setListaCSeriePlaca(RegistroDataModelImpl listaCSeriePlaca) {
		this.listaCSeriePlaca = listaCSeriePlaca;
	}

	public RegistroDataModelImpl getListaNombre() {
		return listaNombre;
	}

	public void setListaNombre(RegistroDataModelImpl listaNOMBRE) {
		this.listaNombre = listaNOMBRE;
	}

	public Registro getRegistroSubSubItems() {
		return registroSubSubItems;
	}

	public void setRegistroSubSubItems(Registro registroSubSubItems) {
		this.registroSubSubItems = registroSubSubItems;
	}

	public Registro getRegistroSubSubValuoVia() {
		return registroSubSubValuoVia;
	}

	public void setRegistroSubSubValuoVia(Registro registroSubSubValuoVia) {
		this.registroSubSubValuoVia = registroSubSubValuoVia;
	}

	public Registro getRegistroSubSubAdiciones() {
		return registroSubSubAdiciones;
	}

	public void setRegistroSubSubAdiciones(Registro registroSubSubAdiciones) {
		this.registroSubSubAdiciones = registroSubSubAdiciones;
	}

	public int getAnioHistorico() {
		return anioHistorico;
	}

	public void setAnioHistorico(int anioHistorico) {
		this.anioHistorico = anioHistorico;
	}

	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getDirectorio() {
		return directorio;
	}

	public void setDirectorio(String directorio) {
		this.directorio = directorio;
	}

	public boolean isCargaAvaluo() {
		return cargaAvaluo;
	}

	public void setCargaAvaluo(boolean cargaAvaluo) {
		this.cargaAvaluo = cargaAvaluo;
	}

	public boolean isDialogoVisible() {
		return dialogoVisible;
	}

	public void setDialogoVisible(boolean dialogoVisible) {
		this.dialogoVisible = dialogoVisible;
	}

	public String getDialogoMensaje() {
		return dialogoMensaje;
	}

	public void setDialogoMensaje(String dialogoMensaje) {
		this.dialogoMensaje = dialogoMensaje;
	}

	public Registro getRegistroCambio() {
		return registroCambio;
	}

	public void setRegistroCambio(Registro registroCambio) {
		this.registroCambio = registroCambio;
	}

	public String getNombreResponsable() {
		return nombreResponsable;
	}

	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
	}

	public String getStringFoto() {
		return stringFoto;
	}

	public void setStringFoto(String stringFoto) {
		this.stringFoto = stringFoto;
	}

	public InputStream getInputStreamFoto() {
		return inputStreamFoto;
	}

	public void setInputStreamFoto(InputStream inputStreamFoto) {
		this.inputStreamFoto = inputStreamFoto;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
}
