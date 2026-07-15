package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.InventariosControladorEnum;
import com.sysman.almacen.enums.InventariosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.ArrayList;
import java.util.Calendar;
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
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author ngomez
 * @version 1, 22/10/2015
 *
 * @version 2, 04/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 *
 ** @version 3, 08/05/2017
 * @author sdaza se ajusta validacion en la linea 1608, se dbe validar
 * es diferente de cero.
 * 
 * @author spina - refactorizo conexiones
 * @version 4, 12/06/2017
 */

@ManagedBean
@ViewScoped
public class InventariosControlador extends BeanBaseDatosAcmeImpl {

	private final String compania;
	private final String modulo = SessionUtil.getModulo();
	/**
	 * Constante que identifica el nombre del campo CODIGOELEMENTO
	 */
	private final String campoCodElemento;
	/**
	 * Constante que identifica el nombre del campo CUENTA
	 */
	private final String campoCuenta;
	/**
	 * Constante que identifica el nombre del campo CUENTAACTIVO
	 */
	private final String campoCuentaAct;
	/**
	 * Constante que identifica el nombre del campo CUENTAACTIVOI
	 */
	private final String campoCuentaActI;
	/**
	 * Constante que identifica el nombre del campo CUENTAACTIVOR
	 */
	private final String campoCuentaActR;
	/**
	 * Constante que identifica el nombre del campo CUENTAACTIVOS
	 */
	private final String campoCuentaActS;
	/**
	 * Constante que identifica el nombre del campo IDENTIFICADOR
	 */
	private final String campoIdentificador;
	/**
	 * Constante que identifica el nombre del campo MEDIDA
	 */
	private final String campoMedida;
	/**
	 * Constante que identifica el nombre del campo NOMBRE
	 */
	private final String campoNombre;
	/**
	 * Constante que identifica el nombre del campo NOMBRECORTO
	 */
	private final String campoNomCorto;
	/**
	 * Constante que identifica el nombre del campo NOMBRELARGO
	 */
	private final String campoNomLargo;
	/**
	 * Constante que identifica el nombre del campo PREDECESOR
	 */
	private final String campoPredecesor;
	/**
	 * Constante que identifica el nombre del campo REFERENCIA
	 */
	private final String campoReferencia;
	/**
	 * Constante que identifica el nombre del campo TIENEMOVIMIENTO
	 */
	private final String campotieneMov;
	/**
	 * Constante que identifica el nombre del campo UNIDAD
	 */
	private final String campoUnidad;
	/**
	 * Constante que identifica el nombre de la tabla INVENTARIO
	 */
	private final String tInventario;

	private int anioActual = SysmanFunciones.getParteFecha(new Date(),
			Calendar.YEAR);
	private List<Registro> listaListaAnio;
	private List<Registro> listaLocalizacion;
	private RegistroDataModelImpl listaUnidad;
	private RegistroDataModelImpl listaTipoActivo;
	private RegistroDataModelImpl listaCuentaActivo;
	private RegistroDataModelImpl listaCuentaActivoI;
	private RegistroDataModelImpl listaCuentaActivoS;
	private RegistroDataModelImpl listaCuentaActivoR;
	private RegistroDataModelImpl listaAlmacen;
	private boolean validarParametro;
	private boolean cubsVisible;
	private boolean identificadorVisible;
	private boolean depreciacionVisible;
	private boolean cuentaActivoSVisible;
	private boolean cuentaActivoRVisible;
	private boolean interfazContabilidadVisible;
	private boolean tipoActivoVisible;
	private boolean marcaEditable;
	private boolean medidaEditable;
	private boolean referenciaEditable;
	private boolean informacionAdicionalVisible;
	private boolean inActivoVisible;
	private boolean anioVisible;
	private String estructuraEstandar;
	private String permiteInterfaseSinNivel;
	private String digitosAgrupacion;
	private String anio;
	private String auxPredecesor;
	private String auxTipo;
	private String activoNombre;
	private String activoINombre;
	private String activoSNombre;
	private String activoRNombre;
	private boolean auxTieneMov;
	private String auxCodigo;
	private int direcciona;
	private boolean auxGuarda = true;
	private boolean actualizaciones = false;
	private boolean visibleVencimiento = false;
	private String codGrupo;
	private boolean porcValorResidual = false;
	private boolean verPorcValorResidual = false;
	private int digPorcValorResidual;
	private boolean bloqIdentificador = false;
	private boolean bloqTipo = false;
	private String codTipo;
	private String codIdentificador;
	private boolean identificadornueve = false ;
	
	private List<SelectItem> listaTipos;
	/**
	 * Variable que almacena el valor del parametro = DIGITOS AGRUPACION INVENTARIO
	 */
	private int digitosAgrupacionInventario;
	/**
	 * Variable para habilitar el check Incluir en Informe de Amortizacion
	 */
	private boolean habilitarAgrupacionInventario = false;
	/**
	 * Variable que almacena el valor del parametro = MANEJA INFORME DE AMORTIZACION AJUSTADA
	 */
	private boolean manejaInformeAmortizacion = false;
	/**
	 * Variable que almacena el valor del parametro = ALMACEN INTERCOMPANIAS
	 */
	private boolean manejaInterCompania;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtilRemote;

	@EJB
	private EjbAlmacenCeroRemote ejbAlmacenCeroRemote;
	
	@EJB
    private EjbAlmacenCincoRemote ejbAlmacenCinco;
    
	
	@SuppressWarnings("unchecked")
	public InventariosControlador() {
		super();
		compania = SessionUtil.getCompania();
		numFormulario = GeneralCodigoFormaEnum.INVENTARIOS_CONTROLADOR
				.getCodigo();
		campoCodElemento = "CODIGOELEMENTO";
		campoCuenta = "CUENTA";
		campoCuentaAct = "CUENTAACTIVO";
		campoCuentaActI = "CUENTAACTIVOI";
		campoCuentaActR = "CUENTAACTIVOR";
		campoCuentaActS = "CUENTAACTIVOS";
		campoIdentificador = "IDENTIFICADOR";
		campoMedida = "MEDIDA";
		campoNombre = "NOMBRE";
		campoNomCorto = "NOMBRECORTO";
		campoNomLargo = "NOMBRELARGO";
		campoPredecesor = "PREDECESOR";
		campoReferencia = "REFERENCIA";
		campotieneMov = "TIENEMOVIMIENTO";
		campoUnidad = "UNIDAD";
		tInventario = "INVENTARIO";

		try {
			validarPermisos();
			registro = new Registro(new HashMap<String, Object>());
			cubsVisible = true;

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

			if (parametrosEntrada != null) {
				rid = (Map<String, Object>) parametrosEntrada.get("rid");
			}
			SessionUtil.cleanFlash();
		}
		catch (SysmanException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.INVENTARIO;
		buscarLlave();
		asignarOrigenDatos();
		
		try {
			verPorcValorResidual = "SI".equals(
					ejbSysmanUtilRemote.consultarParametro(compania,"MANEJA VALOR RESIDUAL POR AGRUPACION EN ALMACEN", modulo, new Date(),true))? true : false;
			
			digPorcValorResidual = Integer.parseInt(ejbSysmanUtilRemote.consultarParametro(compania,"DIGITOS DE AGRUPACION PARA VALOR RESIDUAL EN ALMACEN", modulo, new Date(),true));

			digitosAgrupacionInventario = Integer.parseInt(ejbSysmanUtilRemote.consultarParametro(compania,"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),true));
			
			manejaInterCompania = SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania, "ALMACEN INTERCOMPANIAS", "-1", new Date(), true),"NO").toString().equals("SI")?true:false;

		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
	}

	@Override
	public void iniciarListasSubNulo() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void iniciarListasSub() {
		identificadorVisible = true;
		depreciacionVisible = true;
		cuentaActivoSVisible = true;
		cuentaActivoRVisible = true;

		if (("C").equals(registro.getCampos().get("TIPO"))) {
			identificadorVisible = false;
			depreciacionVisible = false;
			cuentaActivoSVisible = false;
			cuentaActivoRVisible = false;
		}

		inActivoVisible = (boolean) registro.getCampos().get(campotieneMov);

		mostrarTipoActivo();
		validarIdentificadorTipo();

	}

	@Override
	public void iniciarListas() {
		cargarListaUnidad();
		cargarListaTipoActivo();
		cargarListaCuentaActivo();
		cargarListaCuentaActivoI();
		cargarListaCuentaActivoS();
		cargarListaCuentaActivoR();
		cargarListaLocalizacion();
		cargarListaListaAnio();
		cargarListaAlmacen();
	}

	public void cargarListaListaAnio() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			listaListaAnio = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL9245
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaLocalizacion() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			listaLocalizacion = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL9712
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaUnidad() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL10280
						.getValue());
		listaUnidad = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null, true,
				campoUnidad);
	}

	public void cargarListaTipoActivo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL10745
						.getValue());
		listaTipoActivo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null, true,
				"CODIGO");
	}

	public void cargarListaCuentaActivo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL11306
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID");

	}

	public void cargarListaCuentaActivoI() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL11306
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoI = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}

	public void cargarListaCuentaActivoS() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL11306
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoS = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}

	public void cargarListaCuentaActivoR() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL11306
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoR = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}
	/**
	 * 
	 * 
	 * Carga la lista listaAlmacen
	 *
	 */
	public void cargarListaAlmacen(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InventariosControladorUrlEnum.URL9767
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaAlmacen = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");



	}

	public void oprimirInformacionAdicional() {
		// <CODIGO_DESARROLLADO>

		if (((boolean) registro.getCampos().get(campotieneMov))
				&& (("D").equals(registro.getCampos()
						.get(GeneralParameterEnum.TIPO.getName()))
						|| ("E").equals(registro.getCampos()
								.get(GeneralParameterEnum.TIPO.getName()))
						|| ("N").equals(registro.getCampos()
								.get(GeneralParameterEnum.TIPO.getName()))
						|| ("M").equals(registro.getCampos()
								.get(GeneralParameterEnum.TIPO.getName())))) {
			agregarRegistroNuevo(false);

			if (auxGuarda) {
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("rid", css);
				parametros.put("elemento", registro.getCampos()
						.get(campoCodElemento).toString());

				Direccionador direccionador = new Direccionador();
				direccionador.setNumForm(String.valueOf(
						GeneralCodigoFormaEnum.SUBDEVOLUTIVOS_CONTROLADOR
						.getCodigo()));
				if (css != null) {
					direccionador.setParametros(parametros);
				}
				SessionUtil.redireccionarForma(direccionador, modulo);
			}

		}
		else {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4271"));
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCodigoBoton() {
		// <CODIGO_DESARROLLADO>

		if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
				campoCodElemento)) {
			agregarRegistroNuevo(false);
			otroConsecutivo();

			auxCodigo = registro.getCampos().get(campoCodElemento).toString();
			auxPredecesor = SysmanFunciones
					.nvl(registro.getCampos().get(campoPredecesor), "")
					.toString();
			auxTipo = registro.getCampos().get("TIPO").toString();
			auxTieneMov = (boolean) registro.getCampos().get(campotieneMov);
		}

		cargarRegistroNuevo();

		registro.getCampos().put(campoCodElemento, auxCodigo);
		registro.getCampos().put(campoPredecesor, auxPredecesor);
		registro.getCampos().put("TIPO", auxTipo);
		registro.getCampos().put(campotieneMov, auxTieneMov);

		if (registro.getCampos().get("TIPO") != null) {
			mostrarTipoActivo();
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirBotonUnidad() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", css);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.UNIDAD_CONTROLADOR.getCodigo()));

		if (css != null) {
			direccionador.setParametros(parametros);
		}

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	public void oprimirBotonLocalizacion() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", css);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(String.valueOf(
				GeneralCodigoFormaEnum.LOCALIZACIONS_CONTROLADOR
				.getCodigo()));

		if (css != null) {
			direccionador.setParametros(parametros);
		}
		SessionUtil.redireccionarForma(direccionador, modulo);
		// </CODIGO_DESARROLLADO>

	}

	public void oprimirTransaccion() {
		// <CODIGO_DESARROLLADO>
		direcciona = 1;
		anio = String.valueOf(anioActual);
		if (("SI").equals(estructuraEstandar)) {
			if ((registro.getCampos().get(campoCodElemento).toString()
					.length() == Integer
					.parseInt(digitosAgrupacion == null
					? "5"
							: digitosAgrupacion))
					&& (registro.getCampos().get(campoCuentaAct) != null)) {
				if (!verificarCuenta(registro.getCampos().get(campoCuentaAct)
						.toString())) {
					return;
				}
				anioVisible = true;
			}
			else {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString("TB_TB1917").replace(
								"#digitosAgrupacion#",
								digitosAgrupacion));
			}
		}
		else {
			anioVisible = true;
		}
		// </CODIGO_DESARROLLADO>
	}
	
	public void oprimircrearCod() {
		long numero = 0;
		String criterio = "";
		String inicial = "";
		
		try {			
			criterio = "COMPANIA = ''" + compania 				
					+ "'' AND SUBSTR(CODIGOELEMENTO,1,5) = ''" + codGrupo + "''";
			
			inicial = String.valueOf(codGrupo) + "0001";
			
			numero = ejbSysmanUtilRemote.generarConsecutivoConValorInicial(
					"INVENTARIO", criterio,
					GeneralParameterEnum.CODIGOELEMENTO.getName(), inicial);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(), numero);
		
		cambiarTexto291();
    }

	private boolean verificarCuenta(String cuenta) {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
			Registro regAux;
			regAux = RegistroConverter.toRegistro(requestManager.get(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							InventariosControladorUrlEnum.URL7379
							.getValue())
					.getUrl(),
					param));

			if (regAux == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3119")
						.replace("#$cuenta$#", cuenta));
				anioVisible = false;
				return false;
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	public void aceptarCuadroAnio() {
		// <CODIGO_DESARROLLADO>

		if (anio == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1916"));
		}
		else {
			anioVisible = false;
			agregarRegistroNuevo(false);
			switch (direcciona) {
			case 1:
				insertarAlmacenCont();
				abrirSub("1");
				break;
			case 2:
				insertarInventarioCont();
				abrirSub("2");
				break;
			default:
				break;
			}
		}

		// </CODIGO_DESARROLLADO>
	}

	private void abrirSub(String opcion) {
		Map<String, Object> parametros = new HashMap<>();
		Direccionador direccionador = new Direccionador();

		if (("1").equals(opcion)) {
			parametros.put("rid", css);
			parametros.put("tipo", registro.getCampos().get("TIPO").toString());
			parametros.put("cuentaActivo", registro.getCampos()
					.get(campoCuentaAct).toString());

			direccionador.setNumForm(String.valueOf(
					GeneralCodigoFormaEnum.SUBCONTAINVENS_CONTROLADOR
					.getCodigo()));
		}
		else if (("2").equals(opcion)) {
			parametros.put("ridR", css);

			direccionador
			.setNumForm(String.valueOf(
					GeneralCodigoFormaEnum.SUBINVENTCONTABILIDADS_CONTROLADOR
					.getCodigo()));
		}
		parametros.put("codigo",
				registro.getCampos().get(campoCodElemento).toString());
		parametros.put("nombre",
				registro.getCampos().get(campoNomLargo).toString());
		parametros.put("anio", anio);

		if (css != null) {
			direccionador.setParametros(parametros);
		}
		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	private void insertarInventarioCont() {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(campoCodElemento));
			List<Registro> aux;
			aux = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL7270
											.getValue())
									.getUrl(),
									param));

			String aux2 = (aux.get(0).getCampos().get(campoCuenta)).toString();

			if (("0").equals(aux2)) {
				Map<String, Object> campos = new HashMap<>();
				campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				campos.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
						registro.getCampos().get(campoCodElemento));
				campos.put(GeneralParameterEnum.ANO.getName(), anio);
				campos.put(GeneralParameterEnum.CREATED_BY.getName(),
						SessionUtil.getUser().getCodigo());
				campos.put(GeneralParameterEnum.DATE_CREATED.getName(),
						new Date());
				Parameter parameter = new Parameter();
				parameter.setFields(campos);

				UrlBean urlCreate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								InventariosControladorUrlEnum.URL9766
								.getValue());
				requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
						parameter);
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void insertarAlmacenCont() {
		try {
			Map<String, Object> campos = new HashMap<>();
			campos.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(campoCodElemento));
			campos.put(GeneralParameterEnum.ANO.getName(), anio);
			campos.put(InventariosControladorEnum.PARAM1.getValue(),
					registro.getCampos().get(campoCuentaAct));
			campos.put(InventariosControladorEnum.PARAM2.getValue(),
					registro.getCampos().get("TIPO"));
			Parameter parameter = new Parameter();
			parameter.setFields(campos);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							InventariosControladorUrlEnum.URL1368
							.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					parameter);
		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}

	}

	public void oprimirDepreciacion() {
		// <CODIGO_DESARROLLADO>
		direcciona = 2;
		anio = String.valueOf(anioActual);
		if (("SI").equals(estructuraEstandar)) {
			if ((registro.getCampos().get(campoCodElemento).toString()
					.length() == Integer
					.parseInt(digitosAgrupacion == null
					? "3"
							: digitosAgrupacion))
					&& (registro.getCampos().get(campoCuentaAct) != null)) {
				if (!verificarCuenta(registro.getCampos().get(campoCuentaAct)
						.toString())) {
					return;
				}
				anioVisible = true;
			}
			else {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString("TB_TB1917").replace(
								"#digitosAgrupacion#",
								digitosAgrupacion));
			}
		}
		else {
			anioVisible = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarNombreCorto() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(campoNomCorto, registro.getCampos()
				.get(campoNomCorto).toString().replace('\'', 'p'));
		registro.getCampos().put(campoNomCorto, registro.getCampos()
				.get(campoNomCorto).toString().replace('\"', 'p'));
		registro.getCampos().put(campoNomCorto, registro.getCampos()
				.get(campoNomCorto).toString().toUpperCase());
		registro.getCampos().put(campoNomLargo, armarNombreLargo());
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTexto291() {
		// <CODIGO_DESARROLLADO>
		try {
			Object aux = ejbSysmanUtilRemote.consultarParametro(compania,
					"ESTRUCTURA INVENTARIO", modulo, new Date(),
					true);
			if (aux != null) {
				evaluarParametro(aux);
			}
			else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1920"));
			}
			
		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>
	}

	private void evaluarParametro(Object aux) {
		if ((aux.toString()
				.indexOf(String.valueOf(registro.getCampos()
						.get(campoCodElemento).toString()
						.length()))) == -1) {
			if (css == null) {
				registro.getCampos().put(campoCodElemento, "");
				registro.getCampos().put(campoPredecesor, "");
				heredarDatos();
				mostrarTipoActivo();
			}
			else {
				registro.getCampos().put(campoCodElemento,
						registroIni.get(campoCodElemento));
			}
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1919"));
		}
		else {
			if ((registro.getCampos().get(campoCodElemento) != null)
					&& (css == null)) {
				registro.getCampos().put(campoPredecesor, hallaPredecesor(
						campoCodElemento,
						registro.getCampos().get(campoCodElemento)
						.toString(),
						tInventario, 1));
				heredarDatos();
				mostrarTipoActivo();
			}
		}
		
	}

	public void cambiarTipo() {
		// <CODIGO_DESARROLLADO>
		
		int contarElemento = validarElementos();

	    if (contarElemento > 0) {
	        registro.getCampos().put("TIPO", codTipo);
	        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4490"));
	        return;
	    }

		
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(campoCodElemento));
		List<Registro> aux;
		try {
			aux = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL27976
											.getValue())
									.getUrl(),
									param));

			String aux2 = aux.get(0).getCampos().get(campoCuenta).toString();

			if (!("0").equals(aux2)) {
				registro.getCampos().put("TIPO", registroIni.get("TIPO"));
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1931"));
			}
			else {
				mostrarTipoActivo();
				identificadorVisible = ("D")
						.equals(registro.getCampos().get("TIPO"))
						|| ("E").equals(registro.getCampos().get("TIPO"))
						|| ("N").equals(registro.getCampos().get("TIPO"))
						|| ("M").equals(registro.getCampos().get("TIPO"));
			}
			//Se setea el combo de identificador dependiendo del combo tipo
			String tipo = (String) registro.getCampos().get("TIPO");
			if(tipo.equals("D") ||  tipo.equals("E"))
				registro.getCampos().put("IDENTIFICADOR", "P");
			else if(tipo.equals("M") || tipo.equals("N"))
				registro.getCampos().put("IDENTIFICADOR", "C");
			
			//activar porcentaje de valor residual
			if (verPorcValorResidual) {
				if ( tipo.equals("D") ||  tipo.equals("N")) {
					if ( String.valueOf(registro.getCampos().get(campoCodElemento)).length() == digPorcValorResidual ) {
						porcValorResidual = true;
					}
					else {
						porcValorResidual = false;
						registro.getCampos().put("PORC_VALOR_RESIDUAL", null);
					}
				} else {
					porcValorResidual = false;
					registro.getCampos().put("PORC_VALOR_RESIDUAL", null);
				}
			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarIdentificador() {
		// <CODIGO_DESARROLLADO>

		try {
			
			int contarElemento = validarElementos();

		    if (contarElemento > 0) {
		        registro.getCampos().put("IDENTIFICADOR", codIdentificador);
		        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4491"));
		        return;
		    }
			
			String inicial = ejbSysmanUtilRemote.consultarParametro(compania,
					"CONSECUTIVO INVENTARIO INICIAL", modulo,
					new Date(), false);
			if (inicial == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1922"));
			}
			String permiteCambiar = ejbSysmanUtilRemote.consultarParametro(
					compania,
					"PERMITE CAMBIAR IDENTIFICADOR DE DEVOLUTIVOS",
					modulo, new Date(), true);
			if (("SI").equals(permiteCambiar == null ? "NO" : permiteCambiar)) {
				List<Registro> aux;
				Map<String, Object> param = new HashMap<>();
				param.put(GeneralParameterEnum.ELEMENTO.getName(),
						registro.getCampos().get(campoCodElemento));
				param.put(GeneralParameterEnum.SERIE.getName(), inicial);

				if (registro.getCampos().get(campoIdentificador) == "C") {
					aux = RegistroConverter
							.toListRegistro(requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL33831
											.getValue())
									.getUrl(),
									param));
				}
				else {
					aux = RegistroConverter
							.toListRegistro(requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL36478
											.getValue())
									.getUrl(),
									param));
				}

				String aux2 = aux.get(0).getCampos().get(campoCuenta)
						.toString();
				if (!("0").equals(aux2)) {
					registro.getCampos().put(campoIdentificador,
							registroIni.get(campoIdentificador));
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1923"));
				}
				else {
					actualizaciones = true;
				}

			}
			else {
				Map<String, Object> param = new HashMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.CODIGO.getName(),
						registro.getCampos().get(campoCodElemento));

				List<Registro> aux = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												InventariosControladorUrlEnum.URL27976
												.getValue())
										.getUrl(),
										param));

				String aux2 = aux.get(0).getCampos().get(campoCuenta)
						.toString();

				if (!("0").equals(aux2)) {
					registro.getCampos().put(campoIdentificador,
							registroIni.get(campoIdentificador));
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1923"));
				}
			}
			// </CODIGO_DESARROLLADO>
		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
	}

	public void otroConsecutivo() {

		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(campoCodElemento));
		param.put(InventariosControladorEnum.PARAM0.getValue(),
				registro.getCampos().get(campoPredecesor));

		List<Registro> aux;
		try {
			aux = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL53870
											.getValue())
									.getUrl(),
									param));
			String aux2 = (aux.get(0).getCampos().get("ULTIMO")).toString();
			int aux3 = Integer.parseInt(aux2);
			aux3 = aux3 + 1;
			heredarDatos();
			registro.getCampos().put(campoCodElemento, aux3);
			registro.getCampos().put(campoPredecesor, hallaPredecesor(
					campoCodElemento,
					registro.getCampos().get(campoCodElemento)
					.toString(),
					tInventario, 1));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public String hallaPredecesor(String campo, String valor, String tabla,
			int opcion) {
		String predecesor = "";
		try {
			predecesor = ejbAlmacenCeroRemote.hallarPredecesor(compania, campo,
					valor, tabla, opcion);
		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(
					idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
							+ idioma.getString("TB_TB1924")
							+ ex.getMessage());
		}
		return predecesor;
	}

	public void heredarDatos() {

		if (!(registro.getCampos().get(campoPredecesor) == null ? ""
				: registro.getCampos().get(campoPredecesor))
				.toString().isEmpty()
				&& !registro.getCampos().get(campoPredecesor).toString()
				.isEmpty()) {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(campoPredecesor));
			param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

			List<Registro> aux;
			try {
				aux = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												InventariosControladorUrlEnum.URL9708
												.getValue())
										.getUrl(),
										param));
				registro.getCampos().put("TIPO",
						aux.get(0).getCampos().get("TIPO"));
				registro.getCampos().put(campotieneMov,
						aux.get(0).getCampos().get(campotieneMov));

				identificadorVisible = ("D")
						.equals(registro.getCampos().get("TIPO"))
						|| ("M").equals(registro.getCampos().get("TIPO"))
						|| ("E").equals(registro.getCampos().get("TIPO"));
				
				registro.getCampos().put(campoIdentificador,
						aux.get(0).getCampos().get(campoIdentificador));
				
			}
			catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		else {
			registro.getCampos().put("TIPO", "");
			registro.getCampos().put(campotieneMov, false);
			identificadorVisible = true;
		}

		registro.getCampos().put(campotieneMov, bolTieneMov(
				registro.getCampos().get(campoCodElemento).toString()));
		
		validarIdentificadorTipo();
		cargarTiposIniciales();

	}

	public boolean bolTieneMov(String codigo) {
		boolean rta = false;
		int pin;

		try {
			Object aux = ejbSysmanUtilRemote.consultarParametro(compania,
					"ESTRUCTURA INVENTARIO", modulo, new Date(),
					false);
			identificadornueve = ejbSysmanUtilRemote.consultarParametro(
			        compania,
			        "HABILITACION DEL CAMPO IDENTIFICADOR EN INVENTARIO ALMACEN PARA NUEVE DIGITOS",
			        modulo,
			        new Date(),
			        false
			).toString().equalsIgnoreCase("SI");
			
			if (aux == null) {
				rta = false;
			}
			else {
				pin = aux.toString().indexOf(
						String.format("%02d", codigo.length()))
						+ 1;
				if(pin == 9 &&  identificadornueve) {
					rta = false;
				}else {
					rta = (pin + 1) == aux.toString().length();
				}
				
			}
		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}

		return rta;
	}

	public void cambioActualizaciones() {
		if (registroIni != null
		        && (registro.getCampos().get(campoIdentificador) != registroIni
				.get(campoIdentificador))
				&& actualizaciones) {
			try {
				Map<String, Object> campos = new HashMap<>();
				campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				campos.put(GeneralParameterEnum.PLACA.getName(),
						registro.getCampos().get(campoIdentificador));
				campos.put(GeneralParameterEnum.ELEMENTO.getName(),
						registro.getCampos().get(campoCodElemento));
				campos.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
						new Date());
				campos.put(GeneralParameterEnum.MODIFIED_BY.getName(),
						SessionUtil.getUser().getCodigo());
				Parameter parameter = new Parameter();
				parameter.setFields(campos);

				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								InventariosControladorUrlEnum.URL9910
								.getValue());
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
						parameter);

				UrlBean urlUpdate2 = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								InventariosControladorUrlEnum.URL1099
								.getValue());
				requestManager.update(urlUpdate2.getUrl(),
						urlUpdate2.getMetodo(), parameter);

			}
			catch (SystemException ex) {
				Logger.getLogger(InventariosControlador.class.getName())
				.log(Level.SEVERE, null, ex);
			}
			actualizaciones = false;
		}
		if (registroIni != null && registro.getCampos().get(campoCuentaAct) != registroIni
				.get(campoCuentaAct)) {
			try {

				Map<String, Object> campos = new HashMap<>();
				campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				campos.put(GeneralParameterEnum.ANO.getName(), anioActual);
				campos.put(GeneralParameterEnum.ELEMENTO.getName(),
						registro.getCampos().get(campoCodElemento));
				campos.put(InventariosControladorEnum.PARAM1.getValue(),
						registro.getCampos().get(campoCuentaAct));
				Parameter parameter = new Parameter();
				parameter.setFields(campos);

				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								InventariosControladorUrlEnum.URL2496
								.getValue());
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
						parameter);
			}
			catch (SystemException ex) {
				Logger.getLogger(InventariosControlador.class.getName())
				.log(Level.SEVERE, null, ex);
			}
		}
	}

	public void cambiarReferencia() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(campoReferencia,
				registro.getCampos().get(campoReferencia).toString()
				.replace('\'', 'p'));
		registro.getCampos().put(campoReferencia,
				registro.getCampos().get(campoReferencia).toString()
				.replace('\"', 'p'));
		registro.getCampos().put(campoReferencia, registro.getCampos()
				.get(campoReferencia).toString().toUpperCase());
		registro.getCampos().put(campoNomLargo, armarNombreLargo());
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMedida() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(campoMedida, registro.getCampos()
				.get(campoMedida).toString().replace('\'', 'p'));
		registro.getCampos().put(campoMedida, registro.getCampos()
				.get(campoMedida).toString().replace('\"', 'p'));
		registro.getCampos().put(campoMedida, registro.getCampos()
				.get(campoMedida).toString().toUpperCase());
		registro.getCampos().put(campoNomLargo, armarNombreLargo());
		// </CODIGO_DESARROLLADO>
	}

	private String armarNombreLargo() {
		return SysmanFunciones.nvl(registro.getCampos().get(campoNomCorto), "")
				.toString()
				+ " "
				+ SysmanFunciones.nvl(registro.getCampos().get("MARCA"), "")
				.toString()
				+ " "
				+ SysmanFunciones.nvl(registro.getCampos().get(campoMedida), "")
				.toString()
				+ " "
				+ SysmanFunciones.nvl(registro.getCampos().get(campoReferencia), "")
				.toString();
	}

	public void cambiarMarca() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(campoNomLargo, armarNombreLargo());
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaUnidad(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(campoUnidad,
				registroAux.getCampos().get(campoUnidad));
	}

	public void seleccionarFilaTipoActivo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPOACTIVO",
				registroAux.getCampos().get("CODIGO"));
	}

	public void seleccionarFilaCuentaActivo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(campoCuentaAct,
				registroAux.getCampos().get("ID"));
		activoNombre = registroAux.getCampos().get(campoNombre).toString();
	}

	public void seleccionarFilaCuentaActivoI(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(campoCuentaActI,
				registroAux.getCampos().get("ID"));
		activoINombre = registroAux.getCampos().get(campoNombre).toString();
	}

	public void seleccionarFilaCuentaActivoS(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(campoCuentaActS,
				registroAux.getCampos().get("ID"));
		activoSNombre = registroAux.getCampos().get(campoNombre).toString();
	}

	public void seleccionarFilaCuentaActivoR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(campoCuentaActR,
				registroAux.getCampos().get("ID"));
		activoRNombre = registroAux.getCampos().get(campoNombre).toString();
	}

	public void seleccionarFilaAlmacen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_ALMACEN", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRE_ALMACEN", registroAux.getCampos().get("NOMBRE"));

	}

	public void mostrarTipoActivo() {

		if (("C").equals(registro.getCampos().get("TIPO"))
				|| ("S").equals(registro.getCampos().get("TIPO"))) {
			tipoActivoVisible = false;
			marcaEditable = true;
			medidaEditable = true;
			referenciaEditable = true;
			informacionAdicionalVisible = false;
		}
		else {
			tipoActivoVisible = true;
			marcaEditable = false;
			medidaEditable = false;
			referenciaEditable = false;
			identificadorVisible = true;
			informacionAdicionalVisible = true;
		}

	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		Object aux;
		try {
			aux = ejbSysmanUtilRemote.consultarParametro(compania,
					"MANEJA CUBS", modulo, new Date(), true);
			if (aux != null) {
				cubsVisible = ("NO").equals(aux) ? false : cubsVisible;
			}
			else {
				cubsVisible = false;
			}

			validarParametro = SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
					"ENTIDAD MANEJA CODIGO UNSPC", modulo, new Date(), true), "NO").equals("SI") ? true: false;
			
			visibleVencimiento = SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
					"MANEJA INVENTARIO CON VENCIMIENTOS", modulo, new Date(), true), "NO").equals("SI") ? true: false;
			
			manejaInformeAmortizacion = SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
					"MANEJA INFORME DE AMORTIZACION AJUSTADA", modulo, new Date(), true), "NO").equals("SI") ? true: false;

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		evaluarParametros();
		cargarTiposIniciales();

		activoNombre = "";
		activoINombre = "";
		activoSNombre = "";
		activoRNombre = "";
		codGrupo = "";

		try {
			Map<String, Object> param = new HashMap<>();
			param.put("ID", registro.getCampos().get(campoCuentaAct));
			if ((registro.getCampos().get(campoCuentaAct) != null)
					&& ((listaCuentaActivo.getRegistroUnico(param)) != null)) {
				activoNombre = listaCuentaActivo.getRegistroUnico(param)
						.getCampos().get(campoNombre).toString();
			}
			param.put("ID", registro.getCampos().get(campoCuentaActI));
			if ((registro.getCampos().get(campoCuentaActI) != null)
					&& ((listaCuentaActivoI.getRegistroUnico(param)) != null)) {
				activoINombre = listaCuentaActivoI.getRegistroUnico(param)
						.getCampos().get(campoNombre).toString();
			}
			param.put("ID", registro.getCampos().get(campoCuentaActS));
			if ((registro.getCampos().get(campoCuentaActS) != null)
					&& ((listaCuentaActivoS.getRegistroUnico(param)) != null)) {
				activoSNombre = listaCuentaActivoS.getRegistroUnico(param)
						.getCampos().get(campoNombre).toString();
			}
			param.put("ID", registro.getCampos().get(campoCuentaActR));
			if ((registro.getCampos().get(campoCuentaActR) != null)
					&& ((listaCuentaActivoR.getRegistroUnico(param)) != null)) {
				activoRNombre = listaCuentaActivoR.getRegistroUnico(param)
						.getCampos().get(campoNombre).toString();
			}
			
			//activar porcentaje de valor residual
			String tipo = (String) registro.getCampos().get("TIPO");
			if (verPorcValorResidual) {
				if ( tipo.equals("D") ||  tipo.equals("N")) {
					if ( String.valueOf(registro.getCampos().get(campoCodElemento)).length() == digPorcValorResidual ) {
						porcValorResidual = true;
					}
					else {
						porcValorResidual = false;
						registro.getCampos().put("PORC_VALOR_RESIDUAL", null);
					}
				} else {
					porcValorResidual = false;
					registro.getCampos().put("PORC_VALOR_RESIDUAL", null);
				}
			}
			
			if ( String.valueOf(registro.getCampos().get(campoCodElemento)).length() == digitosAgrupacionInventario  && manejaInformeAmortizacion) {
				 habilitarAgrupacionInventario = true;
					
			} else {
				habilitarAgrupacionInventario = false;
			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	private void evaluarParametros() {
		interfazContabilidadVisible = false; // oculta la pestaña
		// contabilizar a
		// contabilidad false=
		// oculta;
		try {
			estructuraEstandar = ejbSysmanUtilRemote.consultarParametro(
					compania, "MANEJA ESTRUCTURA ESTANDAR", modulo,
					new Date(), true);
			if (estructuraEstandar == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1925"));
				estructuraEstandar = "NO";
			}
			permiteInterfaseSinNivel = ejbSysmanUtilRemote.consultarParametro(
					compania,
					"PERMITE CONFIGURAR INTERFASE ALMACEN SIN NIVEL",
					modulo, new Date(), true);
			if (permiteInterfaseSinNivel == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1926"));
				permiteInterfaseSinNivel = "NO";
			}
			digitosAgrupacion = ejbSysmanUtilRemote.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO",
					modulo, new Date(), false);
			if (digitosAgrupacion == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1927"));
				digitosAgrupacion = "0";
			}

			definirVisibilidadCont();
		}
		catch (SystemException ex) {
			Logger.getLogger(InventariosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}

	}

	private void definirVisibilidadCont() {
		if (("SI").equals(estructuraEstandar)) {
			if (registro.getCampos().get(campoCodElemento) != null) {
				if (!((Integer.parseInt(digitosAgrupacion) == registro
						.getCampos().get(campoCodElemento).toString()
						.length())
						|| ("SI").equals(permiteInterfaseSinNivel))) {
					interfazContabilidadVisible = false;
				}
			}
			else {
				interfazContabilidadVisible = false;
			}

		}

	}
	
	private void validarIdentificadorTipo() {
		
		bloqIdentificador = false;
		bloqTipo = false;
		
		if (bolTieneMov(registro.getCampos().get(campoCodElemento).toString())) {
			bloqIdentificador = true;
			bloqTipo = true;
		}
		
		Object tipoObj = registro.getCampos().get("TIPO");
		Object identObj = registro.getCampos().get("IDENTIFICADOR");

		codTipo = (tipoObj != null ? tipoObj.toString() : "");
		codIdentificador = (identObj != null ? identObj.toString() : "");

		
	}
	
	public void cargarTiposIniciales() {

		listaTipos = new ArrayList<>();

	    String elemento = toStringSafe(registro.getCampos().get(campoCodElemento));

	    String tipoFiltro = "";
	    if (elemento != null) {
	        String valTipo = toStringSafe(registro.getCampos().get("TIPO"));
	        tipoFiltro = (valTipo != null) ? valTipo.trim() : "";
	    }

	    switch (tipoFiltro) {
	        case "C":
	            listaTipos.add(new SelectItem("C", idioma.getString("OD_CB1158_2")));
	            break;

	        case "S":
	            listaTipos.add(new SelectItem("S", idioma.getString("OD_CB1158_3")));
	            break;

	        case "":
	            listaTipos.add(new SelectItem("D", idioma.getString("OD_CB1158_0")));
	            listaTipos.add(new SelectItem("N", idioma.getString("OD_CB1158_1")));
	            listaTipos.add(new SelectItem("C", idioma.getString("OD_CB1158_2")));
	            listaTipos.add(new SelectItem("S", idioma.getString("OD_CB1158_3")));
	            listaTipos.add(new SelectItem("M", idioma.getString("OD_CB1158_4")));
	            listaTipos.add(new SelectItem("E", idioma.getString("OD_CB1158_5")));
	            break;

	        default:
	            listaTipos.add(new SelectItem("D", idioma.getString("OD_CB1158_0")));
	            listaTipos.add(new SelectItem("N", idioma.getString("OD_CB1158_1")));
	            listaTipos.add(new SelectItem("M", idioma.getString("OD_CB1158_4")));
	            listaTipos.add(new SelectItem("E", idioma.getString("OD_CB1158_5")));
	            break;
	    }
	}
	
	private String toStringSafe(Object obj) {
	    return (obj != null ? obj.toString() : null);
	}
	
	private int validarElementos() {

	    try {
	        Map<String, Object> param = new HashMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ELEMENTO.getName(),
	                registro.getCampos().get(campoCodElemento));

	        List<Registro> aux = RegistroConverter.toListRegistro(
	                requestManager.getList(
	                        UrlServiceUtil.getInstance()
	                                .getUrlServiceByUrlByEnumID(
	                                        InventariosControladorUrlEnum.URL112202.getValue())
	                                .getUrl(),param));

	        if (aux == null || aux.isEmpty()) {
	            return 1;
	        }

	        Object valor = aux.get(0).getCampos().get(GeneralParameterEnum.ELEMENTO.getName());

	        if (valor == null) {
	            return 1;
	        }

	        return Integer.parseInt(valor.toString());

	    } catch (SystemException ex) {
	        Logger.getLogger(InventariosControlador.class.getName())
	                .log(Level.SEVERE, null, ex);
	        return 1;
	    }
	}


	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);

		actualizarAntes();
		hallaPredecesor(campoCodElemento,
				registro.getCampos().get(campoCodElemento).toString(),
				tInventario, 1);
		registro.getCampos().remove("NOMBRE_ALMACEN");
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove("NOMBRE_ALMACEN");
		auxGuarda = true;
		if ((("N").equals(registro.getCampos().get("TIPO"))
				|| ("D").equals(registro.getCampos().get("TIPO")))
				&& (SysmanFunciones.nvl(registro.getCampos().get("TIPOACTIVO"), "")
						.toString().isEmpty()
						&& (boolean) SysmanFunciones.nvl(registro.getCampos().get(campotieneMov), false))) {
			auxGuarda = false;
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1918"));
		}
		if ((("D").equals(registro.getCampos().get("TIPO")))
				&& ((registro.getCampos().get(campoIdentificador) == null ? ""
						: registro.getCampos().get(campoIdentificador)).toString()
						.isEmpty())) {
			auxGuarda = false;
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1913"));
		}

		// </CODIGO_DESARROLLADO>
		return auxGuarda;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		cambioActualizaciones();
		if (manejaInterCompania) {
			String elemento = registro.getCampos().get(campoCodElemento).toString();
			try {
				String salida = ejbAlmacenCinco.crearElementoCompania(compania,elemento,SessionUtil.getUser().getCodigo());
				JsfUtil.agregarMensajeInformativo(salida);
			} catch (SystemException e){
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	public void retornarFormularioBotonUnidad(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioBotonLocalizacion(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		cargarListaLocalizacion();
		// </CODIGO_DESARROLLADO>
	}

	public List<Registro> getListaLocalizacion() {
		return listaLocalizacion;
	}

	public void setListaLocalizacion(List<Registro> listaLocalizacion) {
		this.listaLocalizacion = listaLocalizacion;
	}

	public RegistroDataModelImpl getListaUnidad() {
		return listaUnidad;
	}

	public void setListaUnidad(RegistroDataModelImpl listaUnidad) {
		this.listaUnidad = listaUnidad;
	}

	public RegistroDataModelImpl getListaTipoActivo() {
		return listaTipoActivo;
	}

	public void setListaTipoActivo(RegistroDataModelImpl listaTipoActivo) {
		this.listaTipoActivo = listaTipoActivo;
	}

	public RegistroDataModelImpl getListaCuentaActivo() {
		return listaCuentaActivo;
	}

	public void setListaCuentaActivo(RegistroDataModelImpl listaCuentaActivo) {
		this.listaCuentaActivo = listaCuentaActivo;
	}

	public RegistroDataModelImpl getListaCuentaActivoI() {
		return listaCuentaActivoI;
	}

	public void setListaCuentaActivoI(
			RegistroDataModelImpl listaCuentaActivoI) {
		this.listaCuentaActivoI = listaCuentaActivoI;
	}

	public RegistroDataModelImpl getListaCuentaActivoS() {
		return listaCuentaActivoS;
	}

	public void setListaCuentaActivoS(
			RegistroDataModelImpl listaCuentaActivoS) {
		this.listaCuentaActivoS = listaCuentaActivoS;
	}

	public RegistroDataModelImpl getListaCuentaActivoR() {
		return listaCuentaActivoR;
	}

	public void setListaCuentaActivoR(
			RegistroDataModelImpl listaCuentaActivoR) {
		this.listaCuentaActivoR = listaCuentaActivoR;
	}

	public boolean isCubsVisible() {
		return cubsVisible;
	}

	public void setCubsVisible(boolean cubsVisible) {
		this.cubsVisible = cubsVisible;
	}

	public boolean isInterfazContabilidadVisible() {
		return interfazContabilidadVisible;
	}

	public void setInterfazContabilidadVisible(
			boolean interfazContabilidadVisible) {
		this.interfazContabilidadVisible = interfazContabilidadVisible;
	}

	public int getAnioActual() {
		return anioActual;
	}

	public void setAnioActual(int anioActual) {
		this.anioActual = anioActual;
	}

	public String getEstructuraEstandar() {
		return estructuraEstandar;
	}

	public void setEstructuraEstandar(String estructuraEstandar) {
		this.estructuraEstandar = estructuraEstandar;
	}

	public String getPermiteInterfaseSinNivel() {
		return permiteInterfaseSinNivel;
	}

	public void setPermiteInterfaseSinNivel(String permiteInterfaseSinNivel) {
		this.permiteInterfaseSinNivel = permiteInterfaseSinNivel;
	}

	public String getDigitosAgrupacion() {
		return digitosAgrupacion;
	}

	public void setDigitosAgrupacion(String digitosAgrupacion) {
		this.digitosAgrupacion = digitosAgrupacion;
	}

	public boolean isIdentificadorVisible() {
		return identificadorVisible;
	}

	public void setIdentificadorVisible(boolean identificadorVisible) {
		this.identificadorVisible = identificadorVisible;
	}

	public boolean isDepreciacionVisible() {
		return depreciacionVisible;
	}

	public void setDepreciacionVisible(boolean depreciacionVisible) {
		this.depreciacionVisible = depreciacionVisible;
	}

	public boolean isCuentaActivoSVisible() {
		return cuentaActivoSVisible;
	}

	public void setCuentaActivoSVisible(boolean cuentaActivoSVisible) {
		this.cuentaActivoSVisible = cuentaActivoSVisible;
	}

	public boolean isCuentaActivoRVisible() {
		return cuentaActivoRVisible;
	}

	public void setCuentaActivoRVisible(boolean cuentaActivoRVisible) {
		this.cuentaActivoRVisible = cuentaActivoRVisible;
	}

	public boolean isTipoActivoVisible() {
		return tipoActivoVisible;
	}

	public boolean isValidarParametro() {
		return validarParametro;
	}

	public void setValidarParametro(boolean validarParametro) {
		this.validarParametro = validarParametro;
	}    

	public void setTipoActivoVisible(boolean tipoActivoVisible) {
		this.tipoActivoVisible = tipoActivoVisible;
	}

	public boolean isMarcaEditable() {
		return marcaEditable;
	}

	public void setMarcaEditable(boolean marcaEditable) {
		this.marcaEditable = marcaEditable;
	}

	public boolean isMedidaEditable() {
		return medidaEditable;
	}

	public void setMedidaEditable(boolean medidaEditable) {
		this.medidaEditable = medidaEditable;
	}

	public boolean isReferenciaEditable() {
		return referenciaEditable;
	}

	public void setReferenciaEditable(boolean referenciaEditable) {
		this.referenciaEditable = referenciaEditable;
	}

	public boolean isInformacionAdicionalVisible() {
		return informacionAdicionalVisible;
	}

	public void setInformacionAdicionalVisible(
			boolean informacionAdicionalVisible) {
		this.informacionAdicionalVisible = informacionAdicionalVisible;
	}

	public boolean isInActivoVisible() {
		return inActivoVisible;
	}

	public void setInActivoVisible(boolean inActivoVisible) {
		this.inActivoVisible = inActivoVisible;
	}

	public boolean isAuxGuarda() {
		return auxGuarda;
	}

	public void setAuxGuarda(boolean auxGuarda) {
		this.auxGuarda = auxGuarda;
	}

	public boolean isAnioVisible() {
		return anioVisible;
	}

	public void setAnioVisible(boolean anioVisible) {
		this.anioVisible = anioVisible;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public List<Registro> getListaListaAnio() {
		return listaListaAnio;
	}

	public void setListaListaAnio(List<Registro> listaListaAnio) {
		this.listaListaAnio = listaListaAnio;
	}

	public int getDirecciona() {
		return direcciona;
	}

	public void setDirecciona(int direcciona) {
		this.direcciona = direcciona;
	}

	public String getAuxPredecesor() {
		return auxPredecesor;
	}

	public void setAuxPredecesor(String auxPredecesor) {
		this.auxPredecesor = auxPredecesor;
	}

	public String getAuxTipo() {
		return auxTipo;
	}

	public void setAuxTipo(String auxTipo) {
		this.auxTipo = auxTipo;
	}

	public boolean isAuxTieneMov() {
		return auxTieneMov;
	}

	public void setAuxTieneMov(boolean auxTieneMov) {
		this.auxTieneMov = auxTieneMov;
	}

	public String getAuxCodigo() {
		return auxCodigo;
	}

	public void setAuxCodigo(String auxCodigo) {
		this.auxCodigo = auxCodigo;
	}

	public boolean isActualizaciones() {
		return actualizaciones;
	}

	public void setActualizaciones(boolean actualizaciones) {
		this.actualizaciones = actualizaciones;
	}

	public String getActivoNombre() {
		return activoNombre;
	}

	public void setActivoNombre(String activoNombre) {
		this.activoNombre = activoNombre;
	}

	public String getActivoINombre() {
		return activoINombre;
	}

	public void setActivoINombre(String activoINombre) {
		this.activoINombre = activoINombre;
	}

	public String getActivoSNombre() {
		return activoSNombre;
	}

	public void setActivoSNombre(String activoSNombre) {
		this.activoSNombre = activoSNombre;
	}

	public String getActivoRNombre() {
		return activoRNombre;
	}

	public void setActivoRNombre(String activoRNombre) {
		this.activoRNombre = activoRNombre;
	}



	public RegistroDataModelImpl getListaAlmacen() {
		return listaAlmacen;
	}

	public void setListaAlmacen(RegistroDataModelImpl listaAlmacen) {
		this.listaAlmacen = listaAlmacen;
	}

	/**
	 * @return the visibleVencimiento
	 */
	public boolean isVisibleVencimiento() {
		return visibleVencimiento;
	}

	/**
	 * @param visibleVencimiento the visibleVencimiento to set
	 */
	public void setVisibleVencimiento(boolean visibleVencimiento) {
		this.visibleVencimiento = visibleVencimiento;
	}
	
	/**
     * Retorna la variable codGrupo
     * 
     * @return  codGrupo
     */
	public String getCodGrupo() {
        return codGrupo;
    }
	
    /**
     * Asigna la variable  codGrupo
     * 
     * @param  codGrupo
     * Variable a asignar en  codGrupo
     */
    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }

	@Override
	public boolean eliminarAntes() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get("CODIGOELEMENTO"));

		List<Registro> aux;
		try {
			aux = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventariosControladorUrlEnum.URL9208
											.getValue())
									.getUrl(),
									param));
			String aux2 = (aux.get(0).getCampos().get(campoCuenta)).toString();
			if (!("0").equals(aux2)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1912"));
				return false;
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	public boolean isPorcValorResidual() {
		return porcValorResidual;
	}

	public void setPorcValorResidual(boolean porcValorResidual) {
		this.porcValorResidual = porcValorResidual;
	}

	public boolean isVerPorcValorResidual() {
		return verPorcValorResidual;
	}

	public void setVerPorcValorResidual(boolean verPorcValorResidual) {
		this.verPorcValorResidual = verPorcValorResidual;
	}

	public int getDigPorcValorResidual() {
		return digPorcValorResidual;
	}

	public void setDigPorcValorResidual(int digPorcValorResidual) {
		this.digPorcValorResidual = digPorcValorResidual;
	}
	/**
	 * @return the habilitarAgrupacionInventario
	 */
	public boolean isHabilitarAgrupacionInventario() {
		return habilitarAgrupacionInventario;
	}

	/**
	 * @param habilitarAgrupacionInventario the habilitarAgrupacionInventario to set
	 */
	public void setHabilitarAgrupacionInventario(boolean habilitarAgrupacionInventario) {
		this.habilitarAgrupacionInventario = habilitarAgrupacionInventario;
	}
	
	public boolean isBloqIdentificador() {
        return bloqIdentificador;
    }

	public void setBloqIdentificador(boolean bloqIdentificador) {
        this.bloqIdentificador = bloqIdentificador;
    }
	
	public boolean isBloqTipo() {
        return bloqTipo;
    }

	public void setBloqTipo(boolean bloqTipo) {
        this.bloqTipo = bloqTipo;
    }
	
	public List<SelectItem> getListaTipos() {
	    return listaTipos;
	}
	
	public boolean isIdentificadornueve() {
		return identificadornueve;
	}

	public void setIdentificadornueve(boolean identificadornueve) {
		this.identificadornueve = identificadornueve;
	}
		
	
}
