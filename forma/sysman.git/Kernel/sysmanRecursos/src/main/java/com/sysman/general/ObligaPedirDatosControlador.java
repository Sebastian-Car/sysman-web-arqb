package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ObligaPedirDatosControladorEnum;
import com.sysman.general.enums.ObligaPedirDatosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 3, 26/08/2016 15:01:33 -- Modificado por dmaldonado
 * @version 4, 20/04/2017 mzanguna, Refactorizacion y ajustes sonar.
 * @version 5, 24/04/2017 spina refactorizacion ejb
 * @version 6, 03/05/2017 mzanguna, Ajustes forma y nombres de los
 * combos.
 *
 * @version 7.0, 13/06/2017, pespitia:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar el redireccionar por el redireccionarForma de
 * SessionUtil.
 *
 * @author jrodrigueza
 * @version 8, 08/08/2017 Carga de auxiliares seg&uacute;n la
 * configuraci&oacute;n de la cuenta presupuestal y gesti&oacute;n de
 * los componentes gr&aacute;ficos de acuerdo a los indicadores.
 */
@ManagedBean
@ViewScoped
public class ObligaPedirDatosControlador extends BeanBaseDatosAcmeImpl
{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
	 * Campo CODIGO.
	 */
	private final String campoCodigo;
	/**
	 * Campo NOMBRE.
	 */
	private final String campoNombre;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Indica si se debe mostrar todas las fuentes de recurso.
	 */
	private boolean mostrarTodas;
	/**
	 * Trae el tercero seleccionado en el comprobante presupuestal.
	 */
	private String tercero;
	private String centroCosto;
	private String distribuir;
	private String auxiliar;
	private String fuente1;
	private String referencia;
	private String fuente;
	private String nombreTercero;
	private String nombreAuxiliar;
	private String nombreFuente1;
	private String nombreCentro;
	private String nombreFuente;
	private String nombreReferencia;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTercero;
	private RegistroDataModelImpl listaCentroCosto;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaFuente1;
	private RegistroDataModelImpl listaReferencia;
	private RegistroDataModelImpl listaFuente;
	private RegistroDataModelImpl listaModelo;
	private List<Registro> listaFuenteEquiCuipo;


	private RegistroDataModelImpl listaSector;
	private RegistroDataModelImpl listaPrograma;
	private RegistroDataModelImpl listaSupPrograma;
	private RegistroDataModelImpl listaCodigoProducto;
	private RegistroDataModelImpl listaCodigoBPIN;
	private RegistroDataModelImpl listaCodigoCCPET;
	private RegistroDataModelImpl listaCodigoCPCDANE;
	private RegistroDataModelImpl listaCodigoUnidEje;
	private RegistroDataModelImpl listaCodigoFuente;
	private RegistroDataModelImpl listaCodigoCCPETRega;

	private RegistroDataModelImpl listaAuxiliar1;
	private RegistroDataModelImpl listaCentroCosto1;
	private RegistroDataModelImpl listaReferencia1;

	private  List<Registro> listaTipoClasificador;
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	private List<Registro> listaSubcentrocosto;
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario
	 */
	private Registro registroSub;
	private String codigoCuenta;
	private String ano;

	private boolean visibleFuente;
	private int modelo;
	private boolean visibleFuente1;
	private Object claseComprobante;
	private boolean visibleSub;
	private boolean visibleDistribuir;
	private boolean visibleCentroCosto;
	private boolean bloqCentroCosto;
	private boolean bloqTercero;
	private boolean bloqFuente;
	private boolean bloqFuente1;
	private boolean bloqAuxiliar;
	private boolean bloqSoloR;
	//	private boolean bloqSector;
	//	private boolean bloqPrograma;
	//	private boolean bloqSupPrograma;
	//	private boolean bloqCodigoProducto;
	//	private boolean bloqCodigoBPIN;
	//	private boolean bloqCodigoCCPET;
	//	private boolean bloqCodigoCPCDANE;
	//	private boolean bloqCodigoUnidEje;
		private boolean bloqCodigoFuente;
	//	private boolean bloqCodigoCCPETRega;


	//	private String sector;
	//	private String programa;
	//	private String supPrograma;
	//	private String codigoProducto;
	//	private String codigoBPIN;
	//	private String codigoCCPET;
	//	private String codigoCPCDANE;
		private String codigoFuente;
	//	private String codigoUnidEje;
	//	private String codigoCCPETRega;
	private boolean bloqReferencia;
	private String sucursal;
	private boolean manejaTercero;
	private boolean manejaAuxiliar;
	private boolean manejaFuente;
	private boolean manejaCentroCosto;
	private boolean manejaReferencia;

	private boolean mostrarAuxiliar;
	private boolean mostrarReferencia;
	private boolean mostrarCentroCosto;
	private boolean bloqSoloAuxiliar;
	private boolean visibleCc1;
	private boolean visibleReferencia1;
	private boolean visibleAuxiliar1;
	private boolean  visibleAuxiliar;
	private boolean visibleReferencia;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private String centroCosto1;
	private Object auxiliar1;
	private String referencia1;
	private boolean bloqAuxiliar1;
	private boolean bloqReferencia1;
	private boolean bloqCc1;
	private String nombreReferencia1;
	private String nombreAuxiliar1;
	private String nombreCc1;
	private boolean bloqSoloCc;
	private boolean bloqSoloReferencia;

	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de PcontratosControlador
	 * @throws SystemException 
	 */
	public ObligaPedirDatosControlador() throws SystemException
	{
		super();
		compania = SessionUtil.getCompania();
		campoCodigo = GeneralParameterEnum.CODIGO.getName();
		campoNombre = GeneralParameterEnum.NOMBRE.getName();

		try
		{
			// 897
			numFormulario = GeneralCodigoFormaEnum.OBLIGA_PEDIR_DATOS_CONTROLADOR
					.getCodigo();

			validarPermisos();
			// <INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			// </INI_ADICIONAL>
		}
		catch (Exception ex)
		{
			Logger.getLogger(ObligaPedirDatosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas()
	{
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTercero();
		cargarListaCentroCosto();
		cargarListaAuxiliar();
		cargarListaFuente1();
		cargarListaReferencia();
		cargarListaFuente();
		cargarListaAuxiliar1(); 
		cargarListaCentroCosto1(); 
		cargarListaReferencia1();
		//		try {
		//			cargarConfiguracionCuipo();
		//		} catch (SystemException e) {
		//			e.printStackTrace();
		//		}
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub()
	{
		// <CARGAR_LISTAS_SUBFORM>
		cargarListaSubcentrocosto();
		// </CARGAR_LISTAS_SUBFORM>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo()
	{
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaSubcentrocosto = null;
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar()
	{
		tabla = "";
		cargarFlash();
		asignarOrigenDatos();
		abrirFormulario();
	}

	public void cargarFlash()
	{
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null)
		{
			ano = parametrosEntrada.get("ano").toString();
			claseComprobante = parametrosEntrada.get("claseComprobante");
			codigoCuenta = parametrosEntrada.get("codigoCuenta").toString();
			manejaCentroCosto = extraerBoolean(
					parametrosEntrada.get("manejaCentroCosto"));
			manejaAuxiliar = extraerBoolean(
					parametrosEntrada.get("manejaAuxiliar"));
			manejaTercero = extraerBoolean(
					parametrosEntrada.get("manejaTercero"));
			manejaFuente = extraerBoolean(
					parametrosEntrada.get("manejaFuente"));
			manejaReferencia = extraerBoolean(
					parametrosEntrada.get("manejaReferencia"));
			tercero = parametrosEntrada.get("terceroComprobante").toString();
			sucursal = parametrosEntrada.get("sucursalComprobante").toString();
			nombreTercero = parametrosEntrada.get("nomTerComprobante")
					.toString();
		}
		else
		{
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 *
	 */
	@Override
	public void asignarOrigenDatos()
	{
		// Metodo para asignar el origen de datos
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista listaAuxiliar
	 */
	public void cargarListaAuxiliar()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL23054
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ObligaPedirDatosControladorEnum.CODCUENTA.getValue(),
				codigoCuenta);
		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);
	}

	/**
	 * Carga la lista listaTercero
	 */
	public void cargarListaTercero()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL8438
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, "NIT");

	}


	
	//</METODOS_CARGAR_LISTA>

	/**
	 * Carga la lista listaCentroCosto
	 */
	public void cargarListaCentroCosto()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL20076
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ObligaPedirDatosControladorEnum.ANO.getValue(), ano);
		param.put(ObligaPedirDatosControladorEnum.CODEXCLUIDO.getValue(), 0);
		param.put(ObligaPedirDatosControladorEnum.CODCUENTA.getValue(),
				codigoCuenta);

		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);
	}

	/**
	 * Carga la lista listaSubcentrocosto
	 */
	public void cargarListaSubcentrocosto()
	{

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ObligaPedirDatosControladorEnum.ANO.getValue(), ano);

		try
		{
			listaSubcentrocosto = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ObligaPedirDatosControladorUrlEnum.URL10000
											.getValue())
									.getUrl(),
									param),
							CacheUtil.getLlaveServicio(
									urlConexionCache,
									GeneralParameterEnum.CENTRO_COSTO
									.getName()));
		}
		catch (SystemException | SysmanException e)
		{
			Logger.getLogger(ObligaPedirDatosControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Carga la lista listaReferencia
	 */
	public void cargarListaReferencia()
	{

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL13043
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ObligaPedirDatosControladorEnum.CODCUENTA.getValue(),
				codigoCuenta);

		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);
	}

	/**
	 * Carga la lista listaFuente1
	 */
	public void cargarListaFuente1()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL11483
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ObligaPedirDatosControladorEnum.ANO.getValue(), ano);
		param.put(ObligaPedirDatosControladorEnum.CODEXCLUIDO.getValue(), 0);

		listaFuente1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);

	}

	/**
	 * Carga la lista listaFuente
	 */
	public void cargarListaFuente()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL12406
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ObligaPedirDatosControladorEnum.CODCUENTA.getValue(),
				codigoCuenta);
		param.put(ObligaPedirDatosControladorEnum.ANO.getValue(), ano);

		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);

	}

	/**
	 * 
	 * Carga la lista listaAuxiliar1
	 *
	 */
	public void cargarListaAuxiliar1(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL7108
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaAuxiliar1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);
	}
	/**
	 * 
	 * Carga la lista listaCentroCosto1
	 *
	 */
	public void cargarListaCentroCosto1(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL8980
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ObligaPedirDatosControladorEnum.ANO.getValue(), ano);
		param.put(ObligaPedirDatosControladorEnum.CODEXCLUIDO.getValue(), 0);

		listaCentroCosto1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);
	}
	/**
	 * 
	 * Carga la lista listaReferencia1
	 *
	 */
	public void cargarListaReferencia1(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ObligaPedirDatosControladorUrlEnum.URL10915
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaReferencia1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				campoCodigo);
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control SoloR
	 */
	public void cambiarSoloR()
	{
		if (mostrarTodas)
		{
			visibleFuente1 = true;
			bloqFuente1 = false;
			visibleFuente = false;
		}
		else
		{
			visibleFuente1 = false;
			visibleFuente = true;
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control SoloAuxiliar
	 * 
	 * 
	 */
	public void cambiarSoloAuxiliar() {
		//<CODIGO_DESARROLLADO>
		if (mostrarAuxiliar)
		{
			visibleAuxiliar1 = true;
			setBloqAuxiliar1(false);
			visibleAuxiliar = false;
		}
		else
		{
			visibleAuxiliar1 = false;
			visibleAuxiliar = true;
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control SoloReferencia
	 * 
	 * 
	 */
	public void cambiarSoloReferencia() {
		//<CODIGO_DESARROLLADO>
		if (mostrarReferencia)
		{
			visibleReferencia1 = true;
			setBloqReferencia1(false);
			visibleReferencia = false;
		}
		else
		{
			visibleReferencia1 = false;
			visibleReferencia = true;
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control SoloCc
	 * 
	 * 
	 */
	public void cambiarSoloCc() {
		//<CODIGO_DESARROLLADO>
		if (mostrarCentroCosto)
		{
			visibleCc1 = true;
			setBloqCc1(false);
			visibleCentroCosto = false;
		}
		else
		{
			visibleCc1 = false;
			visibleCentroCosto = true;
		}
		//</CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaTercero(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		tercero = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
				.toString();
		nombreTercero = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
		sucursal = SysmanFunciones
				.nvl(registroAux.getCampos().get("SUCURSAL"), "")
				.toString();
	}

	public void seleccionarFilaCentroCosto(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		centroCosto = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreCentro = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		centroCosto1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreCc1= SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	public void seleccionarFilaFuente(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		fuente = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		fuente1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreFuente = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
		nombreFuente1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		String parametro = getParametro(
				"HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO",
				"NO");

		if(parametro.equals("SI")) {
			try {

				Map<String, Object> param = new HashMap<>();
				param.put("COMPANIA", compania);
				param.put("ANIO", String.valueOf(SysmanFunciones.ano(
						new Date())));
				param.put("CODIGO",fuente);
				listaFuenteEquiCuipo = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ObligaPedirDatosControladorUrlEnum.URL34063.getValue())
										.getUrl(),
										param));
				for (Registro option : listaFuenteEquiCuipo) {
					codigoFuente     =  SysmanFunciones
							.nvl(option.getCampos().get("EQUIVALENTECUIPO").toString(),"").toString();  
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}

						
		}

	}

	public void seleccionarFilaFuente1(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		fuente1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		fuente = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreFuente = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
		nombreFuente1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	public void seleccionarFilaReferencia(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		referencia = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreReferencia = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		referencia1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreReferencia1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	public void seleccionarFilaAuxiliar(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreAuxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		auxiliar1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreAuxiliar1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliar1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreAuxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		auxiliar1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreAuxiliar1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroCosto1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCosto1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroCosto = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreCentro = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		centroCosto1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreCc1= SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferencia1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferencia1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();


		referencia = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreReferencia = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();

		referencia1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoCodigo), "")
				.toString();
		nombreReferencia1 = SysmanFunciones
				.nvl(registroAux.getCampos().get(campoNombre), "")
				.toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_BOTONES>
	public void oprimirAceptar()
	{
		// <CODIGO_DESARROLLADO>
		if (SysmanFunciones.validarVariableVacio(centroCosto))
		{
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2774"));
			return;
		}
		Map<String, Object> parametrosSalida = new HashMap<>();
		parametrosSalida.put("tercero", tercero);
		parametrosSalida.put("fuente", mostrarTodas ? fuente1 : fuente);
		parametrosSalida.put("centroCosto", mostrarCentroCosto ? centroCosto1 : centroCosto);
		parametrosSalida.put("sucursal", sucursal);
		parametrosSalida.put("referencia", mostrarReferencia ? referencia1 : referencia);
		parametrosSalida.put("auxiliar", mostrarAuxiliar ? auxiliar1 : auxiliar);
		
		String parametro = getParametro(
				"HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO",
				"NO");

		if(parametro.equals("SI") && ("".equals(codigoFuente) || codigoFuente == null)) {
			try {

				Map<String, Object> param = new HashMap<>();
				param.put("COMPANIA", compania);
				param.put("ANIO", String.valueOf(SysmanFunciones.ano(
						new Date())));
				param.put("CODIGO",mostrarTodas ? fuente1 : fuente);
				listaFuenteEquiCuipo = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ObligaPedirDatosControladorUrlEnum.URL34063.getValue())
										.getUrl(),
										param));
				for (Registro option : listaFuenteEquiCuipo) {					
					codigoFuente     =  SysmanFunciones
							.nvl(option.getCampos().get("EQUIVALENTECUIPO").toString(),"").toString();  
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}

						
		}
				parametrosSalida.put("codigoFuente", codigoFuente);
		//		parametrosSalida.put("codigoCCPETRega", codigoCCPETRega);
		SessionUtil.setFlash(parametrosSalida);
		RequestContext.getCurrentInstance().closeDialog(null);
		// </CODIGO_DESARROLLADO>
	}

	public void cerrarFormulario()
	{
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	public void agregarRegistroSubSubcentrocosto()
	{
		// No se permite agregar registros
	}

	public void editarRegSubSubcentrocosto(RowEditEvent event)
	{

		Registro reg = (Registro) event.getObject();
		reg.getCampos().put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
		reg.getCampos().put("DATE_MODIFIED", new Date());

		reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
		reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
		reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

		try
		{

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ObligaPedirDatosControladorUrlEnum.URL16075
							.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_MODIFICADO"));
		}
		catch (SystemException ex)
		{
			Logger.getLogger(ObligaPedirDatosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		finally
		{
			cargarListaSubcentrocosto();
		}
	}

	public void eliminarRegSubSubcentrocosto(Registro reg)
	{
		// No se permite eliminar
	}

	public void cancelarEdicionSubcentrocosto()
	{
		cargarListaSubcentrocosto();
	}

	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>

	@Override
	public void abrirFormulario()
	{
		// <CODIGO_DESARROLLADO>
		visibleFuente = true;
		visibleSub = false;
		visibleCentroCosto = true;
		visibleAuxiliar = true;
		visibleReferencia = true;
		iniciarListas();
		iniciarListasSub();
		cargarValoresPredeterminados();
		evaluarDistribucionCentrosCosto();
		consultarAuxiliaresApropiacion();
		bloquearControles();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Trae los auxiliares configurados en la apropiac&iacute;on
	 * inicial.
	 */
	private void consultarAuxiliaresApropiacion()
	{
		try
		{
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(ObligaPedirDatosControladorEnum.ANO.getValue(), ano);
			param.put(ObligaPedirDatosControladorEnum.CODCUENTA.getValue(),
					codigoCuenta);
			String urlEnumId = ObligaPedirDatosControladorUrlEnum.URL5829
					.getValue();
			UrlBean urlReg = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(urlEnumId);
			Registro regAuxiliares = RegistroConverter.toRegistro(
					requestManager.get(urlReg.getUrl(), param));
			if ((regAuxiliares != null)
					&& (regAuxiliares.getCampos() != null))
			{
				asigarAuxiliares(regAuxiliares);
			}
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Asigna el valor a cada auxiliar,si la cuenta tiene activo el
	 * indicador de manejo para cada uno.
	 *
	 * @param regAuxiliares
	 * Registro que contiene los auxliares de la apropiaci&oacute;n
	 * inicial.
	 */
	private void asigarAuxiliares(Registro regAuxiliares)
	{
		if (manejaAuxiliar)
		{
			auxiliar = extraerString(regAuxiliares.getCampos().get(
					GeneralParameterEnum.AUXILIAR.getName()));
			auxiliar = auxiliar == null ? "0" : auxiliar;
			auxiliar1 = auxiliar;
			nombreAuxiliar = traerNombreAuxiliar(listaAuxiliar,
					campoCodigo,
					auxiliar);
			nombreAuxiliar1 = nombreAuxiliar;
			setMostrarAuxiliar(false);
			cambiarSoloAuxiliar();
		}
		if (manejaFuente)
		{
			fuente1 = extraerString(
					regAuxiliares.getCampos().get("FUENTE_RECURSO"));
			fuente1 = fuente1 == null ? "0" : fuente1;
			fuente = fuente1;
			nombreFuente1 = traerNombreAuxiliar(listaFuente1,
					campoCodigo,
					fuente1);
			setMostrarTodas(false);
			cambiarSoloR();
		}
		if (manejaCentroCosto)
		{
			centroCosto = extraerString(regAuxiliares.getCampos()
					.get(GeneralParameterEnum.CENTRO_COSTO
							.getName()));
			centroCosto = centroCosto == null ? "0" : centroCosto;
			centroCosto1 = centroCosto;
			nombreCentro = traerNombreAuxiliar(listaCentroCosto,
					campoCodigo,
					centroCosto);
			nombreCc1 = nombreCentro;
			setMostrarCentroCosto(false);
			cambiarSoloCc();

		}
		if (manejaReferencia)
		{
			referencia = extraerString(regAuxiliares.getCampos().get(
					GeneralParameterEnum.REFERENCIA.getName()));
			referencia = referencia == null ? "0" : referencia;
			referencia1 = referencia;
			nombreReferencia = traerNombreAuxiliar(listaReferencia,
					campoCodigo, referencia);
			nombreReferencia1 = nombreReferencia;
			setMostrarReferencia(false);
			cambiarSoloReferencia();
		}
	}

	/**
	 * Dependiendo del indicador de maneja auxiliar, bloquea los
	 * controles necesarios.
	 */
	private void bloquearControles()
	{
		bloqTercero = !manejaTercero;

		if (!manejaFuente)
		{
			bloqFuente = true;
			bloqFuente1 = true;
			bloqSoloR = true;
		}
		if(!manejaCentroCosto) {
			setBloqSoloCc(true);
			bloqCentroCosto = true;

		}
		if(!manejaAuxiliar) {
			bloqSoloAuxiliar = true;
			bloqAuxiliar = true;
		}
		if(!manejaReferencia) {
			setBloqSoloReferencia(true);
			bloqReferencia = true;
		}
	}

	/**
	 * Eval&uacute;a el comportamiento que debe tomar el control
	 * gr&aacute;fico en el que se asocia el centro de costo a la
	 * cuenta.
	 */
	private void evaluarDistribucionCentrosCosto()
	{
		String parametro = getParametro(
				"MANEJA DISTRIBUCION POR CENTROS DE COSTO EN GASTOS",
				"NO");
		if (validarParametroCentroCosto(parametro))
		{
			parametro = getParametro(
					"MANEJA DISTRIBUCION POR CENTROS DE COSTO MANUAL",
					"NO");
			if ("SI".equals(parametro))
			{
				visibleSub = true;
			}
			visibleDistribuir = false;
			visibleCentroCosto = true;
		}
	}

	/**
	 * Extrae el centro de costo almacenado en el registro de
	 * Apropiaciones Iniciales
	 *
	 * @param rs
	 * Objeto tipo Registro
	 */
	public void asignarCentroCosto(Registro rs)
	{
		if ((rs != null) && (!"".equals(rs.getCampos()
				.get(GeneralParameterEnum.CENTRO_COSTO.getName()))))
		{
			centroCosto = SysmanFunciones.nvl(rs.getCampos().get(
					GeneralParameterEnum.CENTRO_COSTO.getName()), "0")
					.toString();
			nombreCentro = traerNombreAuxiliar(listaCentroCosto, campoCodigo,
					centroCosto);
		}
	}

	/**
	 * Trae el nombre del auxiliar cargado en su respectiva lista
	 *
	 * @param lista
	 * lista de registros en la que se busca el registro
	 * @param nombreLlave
	 * nombre del campo clave para buscar el nombre
	 * @param valorLlave
	 * valor del campo clave
	 * @return nombre del auxiliar
	 */
	private String traerNombreAuxiliar(RegistroDataModelImpl lista,
			String nombreLlave, String valorLlave)
	{
		String nombre = null;
		Map<String, Object> params = new HashMap<>();
		params.put(nombreLlave, valorLlave);
		try
		{
			Registro reg = lista.getRegistroUnico(params);
			if (reg != null)
			{
				nombre = extraerString(reg.getCampos().get(campoNombre));
			}
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return nombre;
	}

	/**
	 * Valida el valor del par&aacute;metro de Centro de Costo.
	 *
	 * @param parametro
	 * Cadena con el valor del par&aacute;metro.
	 * @return
	 */
	private boolean validarParametroCentroCosto(String parametro)
	{
		boolean centrocosto = ("SI").equals(parametro) && manejaCentroCosto;
		return centrocosto
				&& !("TRA").equals(claseComprobante)
				&& !("ADC").equals(claseComprobante)
				&& !("RED").equals(claseComprobante);
	}

	/**
	 * Carga los valores predeterminados para cada auxiliar.
	 */
	private void cargarValoresPredeterminados()
	{
		fuente = SysmanConstantes.CONS_FUENTE;
		nombreFuente = ObligaPedirDatosControladorEnum.NOMBRE99.getValue();
		fuente1 = SysmanConstantes.CONS_FUENTE;
		nombreFuente1 = ObligaPedirDatosControladorEnum.NOMBRE99.getValue();
		centroCosto = SysmanConstantes.CONS_CENTRO;
		nombreCentro = ObligaPedirDatosControladorEnum.NOMBRE99.getValue();
		auxiliar = SysmanConstantes.CONS_AUXILIAR;
		nombreAuxiliar = ObligaPedirDatosControladorEnum.NOMBRE99
				.getValue();
		referencia = SysmanConstantes.CONS_REFERENCIA;
		nombreReferencia = ObligaPedirDatosControladorEnum.NOMBRE99.getValue();
		verificarAuxiliares(); //JM CC 4404
	}
	
	
	public void verificarAuxiliares() { //JM CC 4404
		
		
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(), claseComprobante);
        
        Registro rs = null;
		try {
			rs = RegistroConverter
			        .toRegistro(requestManager.get(
			                        UrlServiceUtil.getInstance()
			                                        .getUrlServiceByUrlByEnumID(
			                                        		ObligaPedirDatosControladorUrlEnum.URL124005.getValue())
			                                        .getUrl(),
			                        param));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (rs != null)
        {	
        	if(Double.parseDouble(rs.getCampos().get("AFECTA").toString()) < 0) {
        		
        		bloqSoloAuxiliar = true;
     			bloqSoloR  = true;
     			bloqSoloCc  = true;
     			bloqSoloReferencia  = true;
  			  
        	}
           
        }
	}

	@Override
	public void cargarRegistro()
	{
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean insertarAntes()
	{
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean insertarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarAntes()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	public boolean getMostrarTodas()
	{
		return mostrarTodas;
	}

	/**
	 * Trae el valor almacenado en la base de datos para el parametro
	 * ingresado.
	 *
	 * @param nombreParametro
	 * Nombre del parametro en la base de datos.
	 * @param valorDefault
	 * Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String getParametro(String nombreParametro, String valorDefault)
	{
		String parametro = null;
		try
		{
			parametro = ejbSysmanUtil.consultarParametro(compania,
					nombreParametro, SessionUtil.getModulo(),
					new Date(), true);
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	/**
	 * Extrae el boolean que representa el objeto.
	 *
	 * @param object
	 * @return
	 */
	private boolean extraerBoolean(Object object)
	{
		boolean valor;
		if (object == null)
		{
			valor = Boolean.FALSE;
		}
		else if (object instanceof String)
		{
			valor = Boolean.parseBoolean((String) object);
		}
		else
		{
			valor = (boolean) object;
		}
		return valor;
	}

	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente
	 * de nulo.
	 *
	 * @param object
	 * Un Objeto
	 * @return String que representa al objeto
	 */

	private String extraerString(Object object)
	{
		return object != null ? object.toString() : null;
	}

	public void setMostrarTodas(boolean mostrarTodas)
	{
		this.mostrarTodas = mostrarTodas;
	}

	public String getTercero()
	{
		return tercero;
	}

	public void setTercero(String tercero)
	{
		this.tercero = tercero;
	}

	public String getCentroCosto()
	{
		return centroCosto;
	}

	public void setCentroCosto(String centroCosto)
	{
		this.centroCosto = centroCosto;
	}

	public String getDistribuir()
	{
		return distribuir;
	}

	public void setDistribuir(String distribuir)
	{
		this.distribuir = distribuir;
	}

	public String getAuxiliar()
	{
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar)
	{
		this.auxiliar = auxiliar;
	}

	public String getFuente1()
	{
		return fuente1;
	}

	public void setFuente1(String auxiliar1)
	{
		this.fuente1 = auxiliar1;
	}

	public boolean isVisibleFuente()
	{
		return visibleFuente;
	}

	public void setVisibleFuente(boolean visibleFuente)
	{
		this.visibleFuente = visibleFuente;
	}

	public boolean isVisibleFuente1()
	{
		return visibleFuente1;
	}

	public void setVisibleFuente1(boolean visibleFuente1)
	{
		this.visibleFuente1 = visibleFuente1;
	}

	public boolean isVisibleSub()
	{
		return visibleSub;
	}

	public void setVisibleSub(boolean visibleSub)
	{
		this.visibleSub = visibleSub;
	}

	public boolean isVisibleDistribuir()
	{
		return visibleDistribuir;
	}

	public void setVisibleDistribuir(boolean visibleDistribuir)
	{
		this.visibleDistribuir = visibleDistribuir;
	}

	public boolean isVisibleCentroCosto()
	{
		return visibleCentroCosto;
	}

	public void setVisibleCentroCosto(boolean visibleCentroCosto)
	{
		this.visibleCentroCosto = visibleCentroCosto;
	}

	public boolean isBloqCentroCosto()
	{
		return bloqCentroCosto;
	}

	public void setBloqCentroCosto(boolean bloqCentroCosto)
	{
		this.bloqCentroCosto = bloqCentroCosto;
	}
	//	/**
	//	 * Retorna la variable programa
	//	 * 
	//	 * @return  programa
	//	 */
	//	public String getPrograma() {
	//		return programa;
	//	}
	//	/**
	//	 * Asigna la variable  programa
	//	 * 
	//	 * @param  programa
	//	 * Variable a asignar en  programa
	//	 */
	//	public void setPrograma(String programa) {
	//		this.programa = programa;
	//	}
	//	/**
	//	 * Retorna la variable supPrograma
	//	 * 
	//	 * @return  supPrograma
	//	 */
	//	public String getSupPrograma() {
	//		return supPrograma;
	//	}
	//	/**
	//	 * Asigna la variable  supPrograma
	//	 * 
	//	 * @param  supPrograma
	//	 * Variable a asignar en  supPrograma
	//	 */
	//	public void setSupPrograma(String supPrograma) {
	//		this.supPrograma = supPrograma;
	//	}
	//	/**
	//	 * Retorna la variable codigoProducto
	//	 * 
	//	 * @return  codigoProducto
	//	 */
	//	public String getCodigoProducto() {
	//		return codigoProducto;
	//	}
	//	/**
	//	 * Asigna la variable  codigoProducto
	//	 * 
	//	 * @param  codigoProducto
	//	 * Variable a asignar en  codigoProducto
	//	 */
	//	public void setCodigoProducto(String codigoProducto) {
	//		this.codigoProducto = codigoProducto;
	//	}
	//	/**
	//	 * Retorna la variable codigoBPIN
	//	 * 
	//	 * @return  codigoBPIN
	//	 */
	//	public String getCodigoBPIN() {
	//		return codigoBPIN;
	//	}
	//	/**
	//	 * Asigna la variable  codigoBPIN
	//	 * 
	//	 * @param  codigoBPIN
	//	 * Variable a asignar en  codigoBPIN
	//	 */
	//	public void setCodigoBPIN(String codigoBPIN) {
	//		this.codigoBPIN = codigoBPIN;
	//	}
	//	/**
	//	 * Retorna la variable codigoCCPET
	//	 * 
	//	 * @return  codigoCCPET
	//	 */
	//	public String getCodigoCCPET() {
	//		return codigoCCPET;
	//	}
	//	/**
	//	 * Asigna la variable  codigoCCPET
	//	 * 
	//	 * @param  codigoCCPET
	//	 * Variable a asignar en  codigoCCPET
	//	 */
	//	public void setCodigoCCPET(String codigoCCPET) {
	//		this.codigoCCPET = codigoCCPET;
	//	}
	//	/**
	//	 * Retorna la variable codigoCPCDANE
	//	 * 
	//	 * @return  codigoCPCDANE
	//	 */
	//	public String getCodigoCPCDANE() {
	//		return codigoCPCDANE;
	//	}
	//	/**
	//	 * Asigna la variable  codigoCPCDANE
	//	 * 
	//	 * @param  codigoCPCDANE
	//	 * Variable a asignar en  codigoCPCDANE
	//	 */
	//	public void setCodigoCPCDANE(String codigoCPCDANE) {
	//		this.codigoCPCDANE = codigoCPCDANE;
	//	}
	//	/**
	//	 * Retorna la variable codigoUnidEje
	//	 * 
	//	 * @return  codigoUnidEje
	//	 */
	//	public String getCodigoUnidEje() {
	//		return codigoUnidEje;
	//	}
	//	/**
	//	 * Asigna la variable  codigoUnidEje
	//	 * 
	//	 * @param  codigoUnidEje
	//	 * Variable a asignar en  codigoUnidEje
	//	 */
	//	public void setCodigoUnidEje(String codigoUnidEje) {
	//		this.codigoUnidEje = codigoUnidEje;
	//	}
		/**
		 * Retorna la variable codigoFuente
		 * 
		 * @return  codigoFuente
		 */
		public String getCodigoFuente() {
			return codigoFuente;
		}
		/**
		 * Asigna la variable  codigoFuente
		 * 
		 * @param  codigoFuente
		 * Variable a asignar en  codigoFuente
		 */
		public void setCodigoFuente(String codigoFuente) {
			this.codigoFuente = codigoFuente;
		}
	//	/**
	//	 * Retorna la variable codigoCCPETRega
	//	 * 
	//	 * @return  codigoCCPETRega
	//	 */
	//	public String getCodigoCCPETRega() {
	//		return codigoCCPETRega;
	//	}
	//	/**
	//	 * Asigna la variable  codigoCCPETRega
	//	 * 
	//	 * @param  codigoCCPETRega
	//	 * Variable a asignar en  codigoCCPETRega
	//	 */
	//	public void setCodigoCCPETRega(String codigoCCPETRega) {
	//		this.codigoCCPETRega = codigoCCPETRega;
	//	}
	public int getModelo()
	{
		return modelo;
	}

	public void setModelo(int modelo)
	{
		this.modelo = modelo;
	}

	public boolean isBloqTercero()
	{
		return bloqTercero;
	}

	public void setBloqTercero(boolean bloqTercero)
	{
		this.bloqTercero = bloqTercero;
	}

	public boolean isBloqFuente()
	{
		return bloqFuente;
	}

	public void setBloqFuente(boolean bloqAuxiliar)
	{
		this.bloqFuente = bloqAuxiliar;
	}

	public boolean isBloqFuente1()
	{
		return bloqFuente1;
	}

	public void setBloqFuente1(boolean bloqAuxiliar1)
	{
		this.bloqFuente1 = bloqAuxiliar1;
	}

	public boolean isBloqSoloR()
	{
		return bloqSoloR;
	}
	public void setBloqSoloR(boolean bloqSoloR)
	{
		this.bloqSoloR = bloqSoloR;
	}

	//	public void setbloqSector(boolean bloqSector)
	//	{
	//		this.bloqSector = bloqSector;
	//	}
	//
	//
	//
	//	public boolean getbloqSector()
	//	{
	//		return bloqSector;
	//	}
	//
	//	public void setbloqPrograma(boolean bloqPrograma)
	//	{
	//		this.bloqPrograma = bloqPrograma;
	//	}
	//	public boolean getbloqPrograma()
	//	{
	//		return bloqPrograma;
	//	}
	//
	//	public void setbloqSupPrograma(boolean bloqSupPrograma)
	//	{
	//		this.bloqSupPrograma = bloqSupPrograma;
	//	}
	//	public boolean getbloqSupPrograma()
	//	{
	//		return bloqSupPrograma;
	//	}
	//
	//	public void setbloqCodigoProducto(boolean bloqCodigoProducto)
	//	{
	//		this.bloqCodigoProducto = bloqCodigoProducto;
	//	}
	//	public boolean getbloqCodigoProducto()
	//	{
	//		return bloqCodigoProducto;
	//	}
	//
	//
	//	public void setbloqCodigoBPIN(boolean bloqCodigoBPIN)
	//	{
	//		this.bloqCodigoBPIN = bloqCodigoBPIN;
	//	}
	//	public boolean getbloqCodigoBPIN()
	//	{
	//		return bloqCodigoBPIN;
	//	}
	//
	//	public void setbloqCodigoCCPET(boolean bloqCodigoCCPET)
	//	{
	//		this.bloqCodigoCCPET = bloqCodigoCCPET;
	//	}
	//	public boolean getbloqCodigoCCPET()
	//	{
	//		return bloqCodigoCCPET;
	//	}
	//
	//	public void setbloqCodigoCPCDANE(boolean bloqCodigoCPCDANE)
	//	{
	//		this.bloqCodigoCPCDANE = bloqCodigoCPCDANE;
	//	}
	//	public boolean getbloqCodigoCPCDANE()
	//	{
	//		return bloqCodigoCPCDANE;
	//	}
	//
	//
	//	public void setbloqCodigoUnidEje(boolean bloqCodigoUnidEje)
	//	{
	//		this.bloqCodigoUnidEje = bloqCodigoUnidEje;
	//	}
	//	public boolean getbloqCodigoUnidEje()
	//	{
	//		return bloqCodigoUnidEje;
	//	}
	//
		public void setbloqCodigoFuente(boolean bloqCodigoFuente)
		{
			this.bloqCodigoFuente = bloqCodigoFuente;
		}
		public boolean getbloqCodigoFuente()
		{
			return bloqCodigoFuente;
		}
	//
	//	public void setbloqCodigoCCPETRega(boolean bloqCodigoCCPETRega)
	//	{
	//		this.bloqCodigoCCPETRega = bloqCodigoCCPETRega;
	//	}
	//	public boolean getbloqCodigoCCPETRega()
	//	{
	//		return bloqCodigoCCPETRega;
	//	}
	//

	public String getNombreCentro()
	{
		return nombreCentro;
	}

	public void setNombreCentro(String nombreCentro)
	{
		this.nombreCentro = nombreCentro;
	}

	public String getNombreTercero()
	{
		return nombreTercero;
	}

	public void setNombreTercero(String nombreTercero)
	{
		this.nombreTercero = nombreTercero;
	}

	public String getNombreAuxiliar()
	{
		return nombreAuxiliar;
	}

	public void setNombreAuxiliar(String nombreAuxiliar)
	{
		this.nombreAuxiliar = nombreAuxiliar;
	}

	public String getNombreFuente1()
	{
		return nombreFuente1;
	}

	public void setNombreFuente1(String nombreAuxiliar1)
	{
		this.nombreFuente1 = nombreAuxiliar1;
	}

	//	/**
	//	 * Retorna la variable sector
	//	 * 
	//	 * @return  sector
	//	 */
	//	public String getSector() {
	//		return sector;
	//	}
	//	/**
	//	 * Asigna la variable  sector
	//	 * 
	//	 * @param  sector
	//	 * Variable a asignar en  sector
	//	 */
	//	public void setSector(String sector) {
	//		this.sector = sector;
	//	}
	public String getReferencia()
	{
		return referencia;
	}

	public void setReferencia(String referencia)
	{
		this.referencia = referencia;
	}

	public String getFuente()
	{
		return fuente;
	}

	public void setFuente(String fuente)
	{
		this.fuente = fuente;
	}

	public boolean isBloqAuxiliar()
	{
		return bloqAuxiliar;
	}

	public void setBloqAuxiliar(boolean bloqAuxiliar)
	{
		this.bloqAuxiliar = bloqAuxiliar;
	}

	public boolean isBloqReferencia()
	{
		return bloqReferencia;
	}

	public void setBloqReferencia(boolean bloqReferencia)
	{
		this.bloqReferencia = bloqReferencia;
	}

	public String getNombreFuente()
	{
		return nombreFuente;
	}

	public void setNombreFuente(String nombreFuente)
	{
		this.nombreFuente = nombreFuente;
	}

	public String getNombreReferencia()
	{
		return nombreReferencia;
	}

	public void setNombreReferencia(String nombreReferencia)
	{
		this.nombreReferencia = nombreReferencia;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaTercero()
	{
		return listaTercero;
	}

	public void setListaTercero(RegistroDataModelImpl listaTercero)
	{
		this.listaTercero = listaTercero;
	}

	public RegistroDataModelImpl getListaCentroCosto()
	{
		return listaCentroCosto;
	}

	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto)
	{
		this.listaCentroCosto = listaCentroCosto;
	}

	public RegistroDataModelImpl getListaAuxiliar()
	{
		return listaAuxiliar;
	}

	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
	{
		this.listaAuxiliar = listaAuxiliar;
	}

	public RegistroDataModelImpl getListaFuente1()
	{
		return listaFuente1;
	}

	public void setListaFuente1(RegistroDataModelImpl listaAuxiliar1)
	{
		this.listaFuente1 = listaAuxiliar1;
	}

	public RegistroDataModelImpl getListaReferencia()
	{
		return listaReferencia;
	}






	public List<Registro> getListaTipoClasificador() {
		return listaTipoClasificador;
	}

	public void setListaTipoClasificador(List<Registro> listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}

	public void setListaReferencia(RegistroDataModelImpl listaReferencia)
	{
		this.listaReferencia = listaReferencia;
	}

	public RegistroDataModelImpl getListaFuente()
	{
		return listaFuente;
	}

	public void setListaFuente(RegistroDataModelImpl listaFuente)
	{
		this.listaFuente = listaFuente;
	}
	public RegistroDataModelImpl getListaModelo()
	{
		return listaModelo;
	}

	public void setListaModelo(RegistroDataModelImpl listaModelo)
	{
		this.listaModelo = listaModelo;
	}




	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	public List<Registro> getListaSubcentrocosto()
	{
		return listaSubcentrocosto;
	}

	public void setListaSubcentrocosto(List<Registro> listaSubcentrocosto)
	{
		this.listaSubcentrocosto = listaSubcentrocosto;
	}

	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	public Registro getRegistroSub()
	{
		return registroSub;
	}

	public void setRegistroSub(Registro registroSub)
	{
		this.registroSub = registroSub;
	}

	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSector() {
		return listaSector;
	}
	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en  listaSector
	 */
	public void setListaSector(RegistroDataModelImpl listaSector) {
		this.listaSector = listaSector;
	}

	/**
	 * Retorna la lista listaPrograma
	 * 
	 * @return listaPrograma
	 */
	public RegistroDataModelImpl getListaPrograma() {
		return listaPrograma;
	}
	/**
	 * Asigna la lista listaPrograma
	 * 
	 * @param listaPrograma
	 * Variable a asignar en  listaPrograma
	 */
	public void setListaPrograma(RegistroDataModelImpl listaPrograma) {
		this.listaPrograma = listaPrograma;
	}
	/**
	 * Retorna la lista listaSupPrograma
	 * 
	 * @return listaSupPrograma
	 */
	public RegistroDataModelImpl getListaSupPrograma() {
		return listaSupPrograma;
	}
	/**
	 * Asigna la lista listaSupPrograma
	 * 
	 * @param listaSupPrograma
	 * Variable a asignar en  listaSupPrograma
	 */
	public void setListaSupPrograma(RegistroDataModelImpl listaSupPrograma) {
		this.listaSupPrograma = listaSupPrograma;
	}
	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProducto() {
		return listaCodigoProducto;
	}
	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en  listaCodigoProducto
	 */
	public void setListaCodigoProducto(RegistroDataModelImpl listaCodigoProducto) {
		this.listaCodigoProducto = listaCodigoProducto;
	}
	/**
	 * Retorna la lista listaCodigoBPIN
	 * 
	 * @return listaCodigoBPIN
	 */
	public RegistroDataModelImpl getListaCodigoBPIN() {
		return listaCodigoBPIN;
	}
	/**
	 * Asigna la lista listaCodigoBPIN
	 * 
	 * @param listaCodigoBPIN
	 * Variable a asignar en  listaCodigoBPIN
	 */
	public void setListaCodigoBPIN(RegistroDataModelImpl listaCodigoBPIN) {
		this.listaCodigoBPIN = listaCodigoBPIN;
	}
	/**
	 * Retorna la lista listaCodigoCCPET
	 * 
	 * @return listaCodigoCCPET
	 */
	public RegistroDataModelImpl getListaCodigoCCPET() {
		return listaCodigoCCPET;
	}
	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigoCCPET
	 */
	public void setListaCodigoCCPET(RegistroDataModelImpl listaCodigoCCPET) {
		this.listaCodigoCCPET = listaCodigoCCPET;
	}
	/**
	 * Retorna la lista listaCodigoCPCDANE
	 * 
	 * @return listaCodigoCPCDANE
	 */
	public RegistroDataModelImpl getListaCodigoCPCDANE() {
		return listaCodigoCPCDANE;
	}
	/**
	 * Asigna la lista listaCodigoCPCDANE
	 * 
	 * @param listaCodigoCPCDANE
	 * Variable a asignar en  listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANE(RegistroDataModelImpl listaCodigoCPCDANE) {
		this.listaCodigoCPCDANE = listaCodigoCPCDANE;
	}
	/**
	 * Retorna la lista listaCodigoUnidEje
	 * 
	 * @return listaCodigoUnidEje
	 */
	public RegistroDataModelImpl getListaCodigoUnidEje() {
		return listaCodigoUnidEje;
	}
	/**
	 * Asigna la lista listaCodigoUnidEje
	 * 
	 * @param listaCodigoUnidEje
	 * Variable a asignar en  listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEje(RegistroDataModelImpl listaCodigoUnidEje) {
		this.listaCodigoUnidEje = listaCodigoUnidEje;
	}
	/**
	 * Retorna la lista listaCodigoFuente
	 * 
	 * @return listaCodigoFuente
	 */
	public RegistroDataModelImpl getListaCodigoFuente() {
		return listaCodigoFuente;
	}
	/**
	 * Asigna la lista listaCodigoFuente
	 * 
	 * @param listaCodigoFuente
	 * Variable a asignar en  listaCodigoFuente
	 */
	public void setListaCodigoFuente(RegistroDataModelImpl listaCodigoFuente) {
		this.listaCodigoFuente = listaCodigoFuente;
	}
	/**
	 * Retorna la lista listaCodigoCCPETRega
	 * 
	 * @return listaCodigoCCPETRega
	 */
	public RegistroDataModelImpl getListaCodigoCCPETRega() {
		return listaCodigoCCPETRega;
	}
	/**
	 * Asigna la lista listaCodigoCCPETRega
	 * 
	 * @param listaCodigoCCPETRega
	 * Variable a asignar en  listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRega(RegistroDataModelImpl listaCodigoCCPETRega) {
		this.listaCodigoCCPETRega = listaCodigoCCPETRega;
	}


	public List<Registro> getListaFuenteEquiCuipo() {
		return listaFuenteEquiCuipo;
	}

	public void setListaFuenteEquiCuipo(List<Registro> listaFuenteEquiCuipo) {
		this.listaFuenteEquiCuipo = listaFuenteEquiCuipo;
	}
	// </SET_GET_ADICIONALES>

	public boolean isMostrarAuxiliar() {
		return mostrarAuxiliar;
	}

	public void setMostrarAuxiliar(boolean mostrarAuxiliar) {
		this.mostrarAuxiliar = mostrarAuxiliar;
	}

	public boolean isBloqSoloAuxiliar() {
		return bloqSoloAuxiliar;
	}

	public void setBloqSoloAuxiliar(boolean bloqSoloAuxiliar) {
		this.bloqSoloAuxiliar = bloqSoloAuxiliar;
	}

	public RegistroDataModelImpl getListaAuxiliar1() {
		return listaAuxiliar1;
	}

	public void setListaAuxiliar1(RegistroDataModelImpl listaAuxiliar1) {
		this.listaAuxiliar1 = listaAuxiliar1;
	}

	public RegistroDataModelImpl getListaCentroCosto1() {
		return listaCentroCosto1;
	}

	public void setListaCentroCosto1(RegistroDataModelImpl listaCentroCosto1) {
		this.listaCentroCosto1 = listaCentroCosto1;
	}

	public RegistroDataModelImpl getListaReferencia1() {
		return listaReferencia1;
	}

	public void setListaReferencia1(RegistroDataModelImpl listaReferencia1) {
		this.listaReferencia1 = listaReferencia1;
	}

	public boolean isVisibleCc1() {
		return visibleCc1;
	}

	public void setVisibleCc1(boolean visibleCc1) {
		this.visibleCc1 = visibleCc1;
	}

	public boolean isVisibleReferencia1() {
		return visibleReferencia1;
	}

	public void setVisibleReferencia1(boolean visibleReferencia1) {
		this.visibleReferencia1 = visibleReferencia1;
	}

	public boolean isVisibleAuxiliar1() {
		return visibleAuxiliar1;
	}

	public void setVisibleAuxiliar1(boolean visibleAuxiliar1) {
		this.visibleAuxiliar1 = visibleAuxiliar1;
	}

	public boolean isVisibleAuxiliar() {
		return visibleAuxiliar;
	}

	public void setVisibleAuxiliar(boolean visibleAuxiliar) {
		this.visibleAuxiliar = visibleAuxiliar;
	}

	public boolean isVisibleReferencia() {
		return visibleReferencia;
	}

	public void setVisibleReferencia(boolean visibleReferencia) {
		this.visibleReferencia = visibleReferencia;
	}

	public String getCentroCosto1() {
		return centroCosto1;
	}

	public void setCentroCosto1(String centroCosto1) {
		this.centroCosto1 = centroCosto1;
	}

	public Object getAuxiliar1() {
		return auxiliar1;
	}

	public void setAuxiliar1(Object auxiliar1) {
		this.auxiliar1 = auxiliar1;
	}

	public String getReferencia1() {
		return referencia1;
	}

	public void setReferencia1(String referencia1) {
		this.referencia1 = referencia1;
	}

	public boolean isMostrarReferencia() {
		return mostrarReferencia;
	}

	public void setMostrarReferencia(boolean mostrarReferencia) {
		this.mostrarReferencia = mostrarReferencia;
	}

	public boolean isMostrarCentroCosto() {
		return mostrarCentroCosto;
	}

	public void setMostrarCentroCosto(boolean mostrarCentroCosto) {
		this.mostrarCentroCosto = mostrarCentroCosto;
	}

	public boolean isBloqAuxiliar1() {
		return bloqAuxiliar1;
	}

	public void setBloqAuxiliar1(boolean bloqAuxiliar1) {
		this.bloqAuxiliar1 = bloqAuxiliar1;
	}

	public boolean isBloqReferencia1() {
		return bloqReferencia1;
	}

	public void setBloqReferencia1(boolean bloqReferencia1) {
		this.bloqReferencia1 = bloqReferencia1;
	}

	public boolean isBloqCc1() {
		return bloqCc1;
	}

	public void setBloqCc1(boolean bloqCc1) {
		this.bloqCc1 = bloqCc1;
	}

	public String getNombreReferencia1() {
		return nombreReferencia1;
	}

	public void setNombreReferencia1(String nombreReferencia1) {
		this.nombreReferencia1 = nombreReferencia1;
	}

	public String getNombreAuxiliar1() {
		return nombreAuxiliar1;
	}

	public void setNombreAuxiliar1(String nombreAuxiliar1) {
		this.nombreAuxiliar1 = nombreAuxiliar1;
	}

	public String getNombreCc1() {
		return nombreCc1;
	}

	public void setNombreCc1(String nombreCc1) {
		this.nombreCc1 = nombreCc1;
	}

	public boolean isBloqSoloCc() {
		return bloqSoloCc;
	}

	public void setBloqSoloCc(boolean bloqSoloCc) {
		this.bloqSoloCc = bloqSoloCc;
	}

	public boolean isBloqSoloReferencia() {
		return bloqSoloReferencia;
	}

	public void setBloqSoloReferencia(boolean bloqSoloReferencia) {
		this.bloqSoloReferencia = bloqSoloReferencia;
	}


}
