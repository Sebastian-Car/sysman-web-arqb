package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.PlanpresupuestalptosControladorEnum;
import com.sysman.presupuesto.enums.PlanpresupuestalptosControladorUrlEnum;
import com.sysman.presupuesto.enums.SeleccionRubrosUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.kernel.api.clientwso2.beans.Parameter;

import java.io.IOException;
import java.text.ParseException;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 16/06/2016
 * @author yrojas
 * @version 2, 18/04/2017 Se cambiaron las consultas por la invocacion de los
 *          DSS. Se cambio controlador segun especificaciones del SonarLint.
 * @author yrojas
 * @version 3, 24/04/2017 Se cambiaron los llamados a Acciones por invocacion de
 *          ejb.
 *
 *          -- Modificado por lcortes 28/04 - 02,03/05/2017. Se cambia la
 *          validacion para la condicion movBloqueadoIni de los indicadores para
 *          que solo se bloqueen cuando la cuenta tenga una apropiacion inicial
 *          o cuando tenga movimiento en el metodo validarAuxiliares(). - Se
 *          crea el booleano tieneCuentasHijas y se valida en el metodo
 *          eliminarAntes para que no se puedean eliminar cuentas que tienen
 *          cuentas hijas.
 *
 *          -- Modificado por lcortes 04/05/2017. Se agrega validacion del
 *          estado del anio seleccionado para el plan.
 *
 *          -- Modificado por lcortes 11/05/2017. Se agrega validacion del campo
 *          codigo para que no permite ingresar letras ni caracteres especiales
 *          y se verifica la revision de los indicadores cuando se agrega un
 *          registro.
 *
 *          -- Modificado por lcortes 27/07/2017. Se agrega la validacion para
 *          que la fecha activacion del rubro y la vigencia corresponda al anio
 *          seleccionado al ingresar al plan presupuestal.
 */
@ManagedBean
@ViewScoped
public class PlanpresupuestalptosControlador extends BeanBaseDatosAcmeImpl {

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbPresupuestoDosRemote ejbPresupuestoDos;

	@EJB
	private EjbPresupuestoTresRemote ejbPresupuestoTres;

	private final String compania;

	private final String modulo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String tipoclaseclasificador;

	/**
	 * Constante que almacenara la cadena "naturaleza"
	 */
	private final String naturalezaMinC;

	/**
	 * Constante que almacenara la cadena "codigo"
	 */
	private final String codigoMinC;

	/**
	 * Constante que almacenara la cadena "TIPOVIGENCIA"
	 */
	private final String tipoVigenciaC;

	/**
	 * Constante que almacenara la cadena "PLAN_PPTAL_CUENTACNT"
	 */
	private final String planPptalCtaC;

	/**
	 * Constante que almacenara la cadena "MAN_PAC"
	 */
	private final String manPacC;

	/**
	 * constante que alamacenara la cadena "MAN_CEN_CTO"
	 */
	private final String manCenCtoC;

	/**
	 * Constante que almacenara la cadena "MAN_AUX_TER"
	 */
	private final String manAuxTerC;

	/**
	 * Constante que almacenara la cadena "MAN_AUX_REF"
	 */
	private final String manAuxRefC;

	/**
	 * Constante que almacenara la cadena "MAN_AUX_GEN"
	 */
	private final String manAuxGenC;

	/**
	 * Constante que almacenara la cadena "MAN_AUX_FUE"
	 */
	private final String manAuxFueC;

	/**
	 * constante que almacenara la cadena "CODIGO_EQUIV"
	 */
	private final String codigoEquiC;
	/**
	 * constante que almacenara la cadena "FECHA_ACTIVACION"
	 */
	private final String fechaActivacionC;

	// <DECLARAR_ATRIBUTOS>
	private String etiNivel1;
	private String etiNivel2;
	private String etiNivel3;
	private String etiNivel4;
	private String etiNivel5;
	private String etiNivel6;
	private String etiNivel7;
	private String etiNivel8;
	private String niveles;
	private String cuentaContable;
	private String eliminarContable;
	private String cuentaFinal;
	private String anio;
	private String estadoAnio;
	private String nombreCuentaEqu;
	private String nombre;
	private String equivalenciaContable;
	private String nomAux;
	private String unidadeje;
    private boolean codigo2Visible;
	private boolean manejaProyVisible;
	private boolean manejaCodigo2Visible;
	private boolean regaliasVisible;
	private boolean pasarSaldoBloquado;
	private boolean vigenciaBloqueada;
	private boolean movBloqueado;
	private boolean pacBloqueado;
	private boolean naturalezaBloqueado;
	private boolean indicadoresBloqueado;
	private boolean condicionManAux;
	private boolean condicionPacBloquedo;
	private boolean esAnioCerrado;
	private boolean fuenteVisible;
	private String visibleFuente;
	private String nivel1;
	private String nivel2;
	private String nivel3;
	private String nivel4;
	private String nivel5;
	private String nivel6;
	private String nivel7;
	private String nivel8;
	private StreamedContent archivoDescarga;
    private boolean validarPacConIndicadores;
    private String naturalezainicial; //JM CC 4401
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>

	private List<Registro> listaVigencia;
	private List<Registro> listaTipoVigencia;
	/**
	 * lista del tipo de recurso
	 */
	private List<Registro> listaTipoRecurso;
	/**
	 * lista del destino
	 */
	private List<Registro> listadestino;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaComboCuenta;
	private RegistroDataModelImpl listaComboCuentaE;
	private String auxiliar;
	private RegistroDataModelImpl listaCentroCosto;
	private RegistroDataModelImpl listaTercero;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaDependenciaAsociada;
	private RegistroDataModelImpl listaSigVigencia;
	private RegistroDataModelImpl listaDestinoRecursos;
	private RegistroDataModelImpl listaEquivGasto;
	private RegistroDataModelImpl listaCuentaGastoCnt;
	private RegistroDataModelImpl listaTipoClasificador;
	private RegistroDataModelImpl listaCuentaContable;
	private RegistroDataModelImpl listaCEliminarContable;
	private RegistroDataModelImpl listaCuentafinal;
	private RegistroDataModelImpl listaFuente;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaCuentabancaria;
	private RegistroDataModelImpl listaunidadEjecutora;
	



	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	private Registro registroSub;
	private boolean movBloqueadoIni;

	// </DECLARAR_ADICIONALES>
	@SuppressWarnings("unchecked")
	public PlanpresupuestalptosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		naturalezaMinC = "naturaleza";
		codigoMinC = "codigo";
		tipoVigenciaC = "TIPOVIGENCIA";
		planPptalCtaC = "PLAN_PPTAL_CUENTACNT";
		manPacC = "MAN_PAC";
		manCenCtoC = "MAN_CEN_CTO";
		manAuxTerC = "MAN_AUX_TER";
		manAuxRefC = "MAN_AUX_REF";
		manAuxGenC = "MAN_AUX_GEN";
		manAuxFueC = "MAN_AUX_FUE";
		codigoEquiC = "CODIGO_EQUIV";
		fechaActivacionC = "FECHA_ACTIVACION";
		validarPacConIndicadores = false;

		try {
			numFormulario = GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR.getCodigo();
			validarPermisos();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				anio = parametrosEntrada.get("anio").toString();
				if (parametrosEntrada.get("rid") != null) {
					rid = (Map<String, Object>) parametrosEntrada.get("rid");
				}
				if (parametrosEntrada.get("estado") != null) {
					estadoAnio = parametrosEntrada.get("estado").toString();
				}

				if ("C".equals(estadoAnio)) {
					esAnioCerrado = true;
				}
			}
			// <INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			SessionUtil.cleanFlash();
		}
	}

	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaComboCuenta();
		cargarListaComboCuentaE();
		cargarListaCentroCosto();
		cargarListaTercero();
		cargarListaAuxiliar();
		cargarListaDependenciaAsociada();
		cargarListaSigVigencia();
		cargarListaDestinoRecursos();
		cargarListaEquivGasto();
		cargarListaCuentaGastoCnt();
		cargarListaTipoClasificador();
		cargarListaCuentacontable();
		cargarListaCEliminarContable();
		cargarListaCuentafinal();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaVigencia();
		cargarListaFuente();
		cargarListaTipoVigencia();
		cargarListadestino();
		cargarListaunidadEjecutora();
		//iniciarListasSubNulo();
		// </CARGAR_LISTA>
	}

	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		tipoclaseclasificador = registro.getCampos().get("TIPOCLASECLASIFICADOR").toString();
		cargarListaCuentabancaria();
		cargarListaComboCuenta();
		// </CARGAR_LISTAS_SUBFORM>
	}

	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaCuentabancaria = null;
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.PLAN_PRESUPUESTAL;
		buscarLlave();
		verificarEstado();
		if ("C".equals(estadoAnio)) {
			esAnioCerrado = true;
		}
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
		
	}

	/**
	 * 
	 * Carga la lista listaCuentabancaria
	 *
	 */
	public void cargarListaCuentabancaria() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

		try {
			listaCuentabancaria = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GenericUrlEnum.PLAN_PPTAL_CTABANCARIA.getGridKey())
									.getUrl(),
									param),
							CacheUtil.getLlaveServicio(urlConexionCache,
									GenericUrlEnum.PLAN_PPTAL_CTABANCARIA.getTable()));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaVigencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaVigencia = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PlanpresupuestalptosControladorUrlEnum.URL10815.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipoVigencia
	 *
	 */
	public void cargarListaTipoVigencia() {
		try {
			listaTipoVigencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											PlanpresupuestalptosControladorUrlEnum.URL8542.getValue())
									.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaComboCuenta() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL12558.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaComboCuenta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaComboCuentaE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL12558.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaComboCuentaE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCentroCosto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL13540.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTercero() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL14229.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "NIT");
	}

	public void cargarListaAuxiliar() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL14917.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaDependenciaAsociada() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL15634.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaDependenciaAsociada = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSigVigencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL16179.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), Integer.parseInt(anio) + 1);

		listaSigVigencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaDestinoRecursos() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL17068.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaDestinoRecursos = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaGastoCnt() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL45082.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaGastoCnt = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTipoClasificador() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL45084.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaTipoClasificador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		//tipoclaseclasificador =  "";
	}

	public void cargarListaEquivGasto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL45080.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaEquivGasto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentacontable() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL12558.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaContable = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCEliminarContable() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL12558.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCEliminarContable = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentafinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL19693.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentafinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaFuente() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL11149.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	/**
     * 
     * Carga la lista listadestino
     *
     */
	public void cargarListadestino() {
		Map<String,Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		try {
            listadestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID("1977001")
                                            .getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
	
	public void cargarListaunidadEjecutora() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL1992001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaunidadEjecutora = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>

	public void cambiarCodigoEquiv() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("CODIGO_EQUIVALENTE2", registro.getCampos().get(codigoEquiC));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMov() {
		// <CODIGO_DESARROLLADO>
		if (Boolean.parseBoolean(registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName()).toString())) {

			condicionManAux = (boolean) registro.getCampos().get(manCenCtoC)
					|| (boolean) registro.getCampos().get(manAuxTerC) || (boolean) registro.getCampos().get(manAuxGenC);

			if (condicionManAux || (boolean) registro.getCampos().get(manAuxRefC)
					|| (boolean) registro.getCampos().get(manAuxFueC)) {
				registro.getCampos().put(GeneralParameterEnum.MOVIMIENTO.getName(), "0");
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB952"));
			}
			indicadoresBloqueado = true;
			pacBloqueado = false;
		} else {
			if (Boolean.parseBoolean(registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName()).toString())) {
				indicadoresBloqueado = true;
			} else {
				indicadoresBloqueado = false;
			}
		}

		pasarSaldoBloquado = !Boolean
				.parseBoolean(registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName()).toString());
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAuxTer() {
		// <CODIGO_DESARROLLADO>
		boolean manCenCto = (boolean) registro.getCampos().get(manCenCtoC);
		boolean manAuxTer = (boolean) registro.getCampos().get(manAuxTerC);
		boolean manAuxGen = (boolean) registro.getCampos().get(manAuxGenC);
		boolean manAuxFue = (boolean) registro.getCampos().get(manAuxFueC);
		boolean manAuxRef = (boolean) registro.getCampos().get(manAuxRefC);
		boolean movimiento = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		condicionManAux = manCenCto || manAuxTer || manAuxGen;
		condicionPacBloquedo = movimiento || manCenCto || manAuxTer;

		movBloqueado = condicionManAux || manAuxFue || manAuxRef;
		pacBloqueado = !(condicionPacBloquedo || manAuxGen || manAuxFue || manAuxRef);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCenCos() {
		// <CODIGO_DESARROLLADO>
		boolean manCenCto = (boolean) registro.getCampos().get(manCenCtoC);
		boolean manAuxTer = (boolean) registro.getCampos().get(manAuxTerC);
		boolean manAuxGen = (boolean) registro.getCampos().get(manAuxGenC);
		boolean manAuxFue = (boolean) registro.getCampos().get(manAuxFueC);
		boolean manAuxRef = (boolean) registro.getCampos().get(manAuxRefC);
		boolean movimiento = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		condicionManAux = manCenCto || manAuxTer || manAuxGen;
		condicionPacBloquedo = movimiento || manCenCto || manAuxTer;

		movBloqueado = condicionManAux || manAuxFue || manAuxRef;
		pacBloqueado = !(condicionPacBloquedo || manAuxGen || manAuxFue || manAuxRef);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAuxGen() {
		// <CODIGO_DESARROLLADO>
		boolean manCenCto = (boolean) registro.getCampos().get(manCenCtoC);
		boolean manAuxTer = (boolean) registro.getCampos().get(manAuxTerC);
		boolean manAuxGen = (boolean) registro.getCampos().get(manAuxGenC);
		boolean manAuxFue = (boolean) registro.getCampos().get(manAuxFueC);
		boolean manAuxRef = (boolean) registro.getCampos().get(manAuxRefC);
		boolean movimiento = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		condicionManAux = manCenCto || manAuxTer || manAuxGen;
		condicionPacBloquedo = movimiento || manCenCto || manAuxTer;

		movBloqueado = condicionManAux || manAuxFue || manAuxRef;
		pacBloqueado = !(condicionPacBloquedo || manAuxGen || manAuxFue || manAuxRef);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarManejaReferencia() {
		// <CODIGO_DESARROLLADO>
		boolean manCenCto = (boolean) registro.getCampos().get(manCenCtoC);
		boolean manAuxTer = (boolean) registro.getCampos().get(manAuxTerC);
		boolean manAuxGen = (boolean) registro.getCampos().get(manAuxGenC);
		boolean manAuxFue = (boolean) registro.getCampos().get(manAuxFueC);
		boolean manAuxRef = (boolean) registro.getCampos().get(manAuxRefC);
		boolean movimiento = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		condicionManAux = manCenCto || manAuxTer || manAuxGen;
		condicionPacBloquedo = movimiento || manCenCto || manAuxTer;

		movBloqueado = condicionManAux || manAuxFue || manAuxRef;
		pacBloqueado = !(condicionPacBloquedo || manAuxGen || manAuxFue || manAuxRef);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarauxFue() {
		// <CODIGO_DESARROLLADO>
		boolean manCenCto = (boolean) registro.getCampos().get(manCenCtoC);
		boolean manAuxTer = (boolean) registro.getCampos().get(manAuxTerC);
		boolean manAuxGen = (boolean) registro.getCampos().get(manAuxGenC);
		boolean manAuxFue = (boolean) registro.getCampos().get(manAuxFueC);
		boolean manAuxRef = (boolean) registro.getCampos().get(manAuxRefC);
		boolean movimiento = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		condicionManAux = manCenCto || manAuxTer || manAuxGen;

		condicionPacBloquedo = movimiento || manCenCto || manAuxTer;

		movBloqueado = condicionManAux || manAuxFue || manAuxRef;
		pacBloqueado = !(condicionPacBloquedo || manAuxGen || manAuxFue || manAuxRef);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarRegalias() {
		// <CODIGO_DESARROLLADO>
		regaliasVisible = (boolean) registro.getCampos().get("REGALIAS");
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTipoVigencia() {
		// <CODIGO_DESARROLLADO>
		if ("VA".equals(registro.getCampos().get(tipoVigenciaC))) {
			registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), anio);
			vigenciaBloqueada = true;
		} else {
			vigenciaBloqueada = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarmen() {
		// <CODIGO_DESARROLLADO>
		if (!(boolean) registro.getCampos().get("MEN") && (registro.getCampos().get("CODIGO_MEN") != null)
				&& !"".equals(registro.getCampos().get("CODIGO_MEN"))) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB951"));
			registro.getCampos().put("MEN", true);
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarcmbCodigo() {
		// <CODIGO_DESARROLLADO>
		niveles = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();

		try {
			if ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"DISCRIMINAR RUBRO PRESUPUESTO POR NIVELES", modulo, new Date(), true), "NO"))) {

				int niv2 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL2", modulo, new Date(), true), "-1")
								.toString());
				int niv3 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL3", modulo, new Date(), true), "-1")
								.toString());
				int niv4 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL4", modulo, new Date(), true), "-1")
								.toString());
				int niv5 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL5", modulo, new Date(), true), "-1")
								.toString());
				int niv6 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL6", modulo, new Date(), true), "-1")
								.toString());
				int niv7 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL7", modulo, new Date(), true), "-1")
								.toString());
				int niv8 = Integer
						.parseInt(SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania,
										"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL8", modulo, new Date(), true), "-1")
								.toString());

				validaCondicionNivel(niv2, niv3, niv4, niv5, niv6, niv7, niv8);
			}

		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		if (css == null) {
			if (registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString().startsWith("1")) {
				registro.getCampos().put(GeneralParameterEnum.DESTINO.getName(), "I");
				registro.getCampos().put(GeneralParameterEnum.NATURALEZA.getName(), "C");
			}
			if (registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString().startsWith("2")) {
				registro.getCampos().put(GeneralParameterEnum.DESTINO.getName(), "F");
				registro.getCampos().put(GeneralParameterEnum.NATURALEZA.getName(), "D");
			}
			registro.getCampos().put(tipoVigenciaC, "VA");
			registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), anio);
		}

		// </CODIGO_DESARROLLADO>
	}

	public void validaCondicionNivel(int niv2, int niv3, int niv4, int niv5, int niv6, int niv7, int niv8) {
		int niv1;
		try {
			niv1 = Integer
					.parseInt(
							SysmanFunciones
							.nvl(ejbSysmanUtil.consultarParametro(compania,
									"NUMERO DIGITOS RUBRO PRESUPUESTO NIVEL1", modulo, new Date(), true), "-1")
							.toString());

			boolean condicionNivelI = (niv1 != -1) && (niv2 != -1);

			boolean condicionNivel = condicionNivelI && (niv3 != -1) && (niv4 != -1) && (niv5 != -1);

			if (condicionNivel && (niv6 != -1) && (niv7 != -1) && (niv8 != -1)) {
				String cod = parametroCoDEquivalente();

				cod = SysmanFunciones.padr(cod, 32, " ");

				registro.getCampos().put("NIVEL1", mid(cod, 0, niv1));
				registro.getCampos().put("NIVEL2", mid(cod, niv1, niv2));
				registro.getCampos().put("NIVEL3", mid(cod, niv1 + niv2, niv3));
				registro.getCampos().put("NIVEL4", mid(cod, niv1 + niv2 + niv3, niv4));
				registro.getCampos().put("NIVEL5", mid(cod, niv1 + niv2 + niv3 + niv4, niv5));
				registro.getCampos().put("NIVEL6", mid(cod, niv1 + niv2 + niv3 + niv4 + niv5, niv6));
				registro.getCampos().put("NIVEL7", mid(cod, niv1 + niv2 + niv3 + niv4 + niv5 + niv6, niv7));
				registro.getCampos().put("NIVEL8", mid(cod, niv1 + niv2 + niv3 + niv4 + niv5 + niv6 + niv7, niv8));

			} else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB953"));
			}

		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo usado para validar el parametro "DISTRIBUIR NIVELES POR CODIGO
	 * EQUIVALENTE"
	 *
	 * @return
	 */
	public String parametroCoDEquivalente() {
		String cod = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		try {
			if ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"DISTRIBUIR NIVELES POR CODIGO EQUIVALENTE", modulo, new Date(), true), "NO"))
					&& (registro.getCampos().get(codigoEquiC) != null)
					&& !"".equals(registro.getCampos().get(codigoEquiC))) {
				cod = registro.getCampos().get(codigoEquiC).toString();
			}
		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return cod;
	}

	public String mid(String cadena, int inicia, int fin) {
		return cadena.substring(inicia, inicia + fin);
	}

	/**
	 * Verifica que el anio tenga estado activo.
	 *
	 * @return retorna A si esta Activo o C si esta Cerrado
	 */
	public String verificarEstado() {
		try {

			estadoAnio = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania, Integer.valueOf(anio),
					Integer.valueOf(modulo), 1);

		} catch (NumberFormatException | SystemException ex) {

			Logger.getLogger(PeriodoplanptoControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

		return estadoAnio;

	}

	public void cambiarNombre() {
		// <CODIGO_DESARROLLADO>
		nombre = registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
		// </CODIGO_DESARROLLADO>
	}
	
	public void cambiarNiveles() {
		// <CODIGO_DESARROLLADO>
		
		nivel1 = registro.getCampos().get(GeneralParameterEnum.NIVEL1.getName()).toString();
		nivel2 = registro.getCampos().get(GeneralParameterEnum.NIVEL2.getName()).toString();
		nivel3 = registro.getCampos().get(GeneralParameterEnum.NIVEL3.getName()).toString();
		nivel4 = registro.getCampos().get(GeneralParameterEnum.NIVEL4.getName()).toString();
		nivel5 = registro.getCampos().get(GeneralParameterEnum.NIVEL5.getName()).toString();
		nivel6 = registro.getCampos().get(GeneralParameterEnum.NIVEL6.getName()).toString();
		nivel7 = registro.getCampos().get(GeneralParameterEnum.NIVEL7.getName()).toString();
		nivel8 = registro.getCampos().get(GeneralParameterEnum.NIVEL8.getName()).toString();
		// </CODIGO_DESARROLLADO>
	}
	
	
	public void cambiarComboCuentaC(int rowNum) {

		// <CODIGO_DESARROLLADO>
		listaCuentabancaria.get(rowNum).getCampos().put(GeneralParameterEnum.NOMBRE.getName(), nomAux);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaComboCuenta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CUENTA_CONTABLE",
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	public void seleccionarFilaComboCuentaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nomAux = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEquivGasto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEquivGasto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.EQUIV_GASTO.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaGastoCnt
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaGastoCnt(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CUENTAGASTOCNT.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaTipoClasificador(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(GeneralParameterEnum.CLASECLASIFICADOR.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGOCLASE.getName()));
		tipoclaseclasificador =  SysmanFunciones.concatenar(SysmanFunciones.concatenar(SysmanFunciones.concatenar(SysmanFunciones.concatenar(SysmanFunciones.concatenar(SysmanFunciones.concatenar( registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString() ,
				                          "-") ,
				                          registroAux.getCampos().get("NOMBRETIPO").toString()) ,
				                          "      "),
				                          registroAux.getCampos().get(GeneralParameterEnum.CODIGOCLASE.getName()).toString()) ,
				                          "-") ,  
				                          registroAux.getCampos().get("NOMBRECLASE") .toString()) ;	
	}

	public void seleccionarFilaCentroCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaTercero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));
	}

	public void seleccionarFilaFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSOS.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

		registro.getCampos().put(GeneralParameterEnum.NOMBRE_FUENTE.getName(), registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaDependenciaAsociada(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.DEPENDENCIAASOCIADA.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaSigVigencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.COD_SIGUIENTE_VIGENCIA.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaDestinoRecursos(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.DESTINO_RECURSOS.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaCuentaContable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaContable = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nombreCuentaEqu = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
	}

	public void seleccionarFilaCEliminarContable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		eliminarContable = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	public void seleccionarFilaCuentafinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}
	
	public void seleccionarFilaunidadEjecutora (SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		unidadeje = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("UNIDAD_EJECUTORA", SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), ""));

		
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_BOTONES>
	public void oprimircmdApropiado() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.APROPIACIONSALDOPPTALS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	public void oprimircmdVerpac() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.PACSALDOPPTALS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	public void oprimircndregistro() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.REGISTROSALDOPPTALS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	public void oprimircmdEjecucion() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.EJECUCIONSALDOPPTALS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	private void oprimirBotonGeneralSeleccionRubro(int formulario) {
		Direccionador dir = crearDireccionadorBoton(formulario);
	//	agregarRegistroNuevo(false);
		if (validarRubroSinAuxiliares()) {
			SessionUtil.redireccionarForma(dir, modulo);
		} else {
			String[] campos = { "direccionador" };
			Object[] valores = { dir };
			SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(GeneralCodigoFormaEnum.SELECCION_RUBROS.getCodigo()),
					modulo, campos, valores);
		}
	}

	private void oprimirBotonGeneralSeleccionRubro(String reporte) {
		archivoDescarga = null;
		Direccionador dir = crearDireccionadorBoton(reporte);
		//agregarRegistroNuevo(false);
		if (validarRubroSinAuxiliares()) {

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put(codigoMinC, registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
			reemplazar.put("anio", anio);
			reemplazar.put(naturalezaMinC,
					registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()).toString());
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_TITULO",
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString() + " " + nombre);
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

			try {
				archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
						FORMATOS.PDF);
			} catch (JRException | IOException | SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} else {
			String[] campos = { "direccionador" };
			Object[] valores = { dir };
			SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(GeneralCodigoFormaEnum.SELECCION_RUBROS.getCodigo()),
					modulo, campos, valores);
		}
	}

	private Direccionador crearDireccionadorBoton(int formulario) {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", css);
		parametros.put("anio", anio);
		parametros.put(codigoMinC, registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
		parametros.put("nombre", nombre);
		parametros.put(naturalezaMinC, registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()).toString());
		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(String.valueOf(formulario));
		direccionador.setParametros(parametros);
		return direccionador;
	}

	private Direccionador crearDireccionadorBoton(String reporte) {
		Direccionador direccionador = crearDireccionadorBoton(-1);
		direccionador.getParametros().put("reporte", reporte);
		return direccionador;
	}

	public void oprimircmdpac() {
		// <CODIGO_DESARROLLADO>
		if ((boolean) registro.getCampos().get(manPacC)) {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

			Registro regAux;
			try {
				regAux = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										PlanpresupuestalptosControladorUrlEnum.URL58223.getValue())
								.getUrl(),
								param));

				if ("0".equals(regAux.getCampos().get(GeneralParameterEnum.CUENTA.getName()))) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB965"));
				} else {
					oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.PROGRAMARPACS_CONTROLADOR.getCodigo());
				}
			} catch (SystemException e) {
				Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		} else {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB680"));
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirResumen() {
		// <CODIGO_DESARROLLADO>
		int form;
		if ("D".equals(registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()))) {
			form = GeneralCodigoFormaEnum.RESUMENPPTOS_CONTROLADOR.getCodigo();
		} else {
			form = GeneralCodigoFormaEnum.RESUMENPPTOINGS_CONTROLADOR.getCodigo();
		}

		oprimirBotonGeneralSeleccionRubro(form);

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirGrafica1() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro("000939GraficaAPlanPPTAL");

		// </CODIGO_DESARROLLADO>
	}

	public void oprimircmdMovimiento() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.SUBFORMMOVPPTALS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirGrafica2() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro("000941GraficaBPlanPPTAL");
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirMovContab() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.SUBFORMPPTALCONTS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirAcumulado() {
		// <CODIGO_DESARROLLADO>

		int form;

		try {
			if ("NO".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"ESPECIAL DE ACUMULADO INGRESOS", modulo, new Date(), true), "NO"))) {
				form = GeneralCodigoFormaEnum.ACUMULADOPPTALS_CONTROLADOR.getCodigo();
			} else {
				if ("C".equals(registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()))) {
					form = GeneralCodigoFormaEnum.ACUMULADOPPTALING_CONTROLADOR.getCodigo();
				} else {
					form = GeneralCodigoFormaEnum.ACUMULADOPPTALS_CONTROLADOR.getCodigo();
				}
			}

			oprimirBotonGeneralSeleccionRubro(form);
		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirReconocimientos() {
		// <CODIGO_DESARROLLADO>
		oprimirBotonGeneralSeleccionRubro(GeneralCodigoFormaEnum.RECONOCIMIENTOS_CONTROLADOR.getCodigo());
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando213() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando215() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	/**
	 * Metodo de insercion del formulario Cuentabancaria
	 * 
	 */
	public void agregarRegistroSubCuentabancaria() {
		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
					registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
			registroSub.getCampos().put(PlanpresupuestalptosControladorEnum.RUBRO_PPTAL.getValue(),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
			registroSub.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PPTAL_CTABANCARIA.getCreateKey());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
			cargarListaCuentabancaria();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
			agregarRubrosNivelInferior();

		} catch (SystemException ex) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	/**
	 * Metodo de edicion del formulario Cuentabancaria
	 * 
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubCuentabancaria(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
			reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PPTAL_CTABANCARIA.getUpdateKey());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
			agregarRubrosNivelInferior();
		} catch (SystemException ex) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaCuentabancaria();
		}
	}

	/**
	 * Metodo de eliminacion del formulario Cuentabancaria
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubCuentabancaria(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PPTAL_CTABANCARIA.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			cargarListaCuentabancaria();
			agregarRubrosNivelInferior();
		} catch (SystemException ex) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	public void cancelarEdicionSubequcontable() {
		cargarListaCuentabancaria();
	}

	public void agregarRubrosNivelInferior() {
		try {
			if ("SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania,
							"ACTUALIZAR RUBROS DE NIVEL INFERIOR EQUIVALENTE BANCARIO", modulo, new Date(), true),
					"NO")) && !SysmanFunciones.validarVariableVacio(cuentaFinal)) {
				ejbPresupuestoDos.agregarRubrosInferiores(compania, Integer.parseInt(anio),
						registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString(), cuentaFinal);
			}
		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	public void actualizarUnidadEjecutora() {
		try {
		 UrlBean urlUpdate = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL1992003.getValue());
		 Map<String, Object> fields = new TreeMap<>();
		fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		fields.put(GeneralParameterEnum.ANO.getName(), anio);
		fields.put(GeneralParameterEnum.UNIDAD_EJECUTORA.getName(), unidadeje);
		fields.put(GeneralParameterEnum.CODIGO.getName(),registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		Parameter parameter = new Parameter();
        parameter.setFields(fields);
        requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	
		
		
	}

	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		etiNivel1 = "Nivel 1";
		etiNivel2 = "Nivel 2";
		etiNivel3 = "Nivel 3";
		etiNivel4 = "Nivel 4";
		etiNivel5 = "Nivel 5";
		etiNivel6 = "Nivel 6";
		etiNivel7 = "Nivel 7";
		etiNivel8 = "Nivel 8";

		try {
			codigo2Visible = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					parametros.getString("PR_PRESENTAR_EQUI_2"), modulo, new Date(), true), "NO"));
			manejaProyVisible = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA INDICADOR DE PROYECTOS EN PRESUPUESTO", modulo, new Date(), true), "NO"));

			manejaCodigo2Visible = "SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CODIGO EQUIVALENTE 2", modulo, new Date(), true),
					"NO"));
			regaliasVisible = true;
			
			validarPacConIndicadores = "SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania,
							"MANEJA PAC A RUBROS HIJOS",
							SessionUtil.getModulo(), new Date(),true), "NO"));
		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();

		if (css != null) {
			nombre = registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
			indicadoresBloqueado = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());
			validaAuxiliaresRegis();
			validarRegHijas();
			validarAuxiliares();
			validaVigenciaBloqueada();
		} else {
			StringBuilder fechaAux = new StringBuilder();
			fechaAux.append(SysmanFunciones.dia(new Date())).append("/").append(SysmanFunciones.mes(new Date()))
			.append("/").append(anio);
			registro.getCampos().put(manCenCtoC, false);
			registro.getCampos().put(manAuxTerC, false);
			registro.getCampos().put(manAuxGenC, false);
			registro.getCampos().put(manAuxFueC, false);
			registro.getCampos().put(manAuxRefC, false);
			registro.getCampos().put(GeneralParameterEnum.MOVIMIENTO.getName(), false);
			registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), anio);
			try {
				registro.getCampos().put(fechaActivacionC, SysmanFunciones.convertirAFecha(fechaAux.toString()));
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

			validaCssNoReg();
		}

		try {
			
			naturalezainicial =  SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()), "NO").toString();
			
			visibleFuente = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"VISUALIZAR FUENTE RECURSOS EN PLAN PRESUPUESTAL", modulo, new Date(), true), "NO").toString();

			fuenteVisible = visibleFuente.equals("SI") ? true : false;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void validaAuxiliaresRegis() {
		boolean manCenCto = (boolean) registro.getCampos().get(manCenCtoC);
		boolean manAuxTer = (boolean) registro.getCampos().get(manAuxTerC);
		boolean manAuxGen = (boolean) registro.getCampos().get(manAuxGenC);
		boolean manAuxFue = (boolean) registro.getCampos().get(manAuxFueC);
		boolean manAuxRef = (boolean) registro.getCampos().get(manAuxRefC);
		boolean movimiento = (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		condicionManAux = manCenCto || manAuxTer || manAuxGen;
		condicionPacBloquedo = movimiento || manCenCto || manAuxTer;

		movBloqueado = condicionManAux || manAuxFue || manAuxRef;
		pacBloqueado = !(condicionPacBloquedo || manAuxGen || manAuxFue || manAuxRef);
	}

	/**
	 * Metodo usado para cargar registro cuando el css es null
	 */
	public void validaCssNoReg() {
		movBloqueado = false;
		indicadoresBloqueado = false;
		movBloqueadoIni = false;
		registro.getCampos().put("INDECONOMICO", 0);
		registro.getCampos().put("INFORME", true);
		registro.getCampos().put(manCenCtoC, false);
		registro.getCampos().put(manAuxTerC, false);
		registro.getCampos().put(manAuxGenC, false);
		registro.getCampos().put(manAuxFueC, false);
		registro.getCampos().put(manAuxRefC, false);
		registro.getCampos().put(manPacC, false);
	}

	/**
	 * Metodo usado para validar los auxiliares cuando el registro es diferente de
	 * null
	 * 
	 * Valida si existen registros para el rubro en el detalle presupuestal y si el
	 * rubro tiene apropiacion inicial, si alguno de estos casos se cumple no dejara
	 * manipular los indicadores de movimiento y auxiliares.
	 */
	public void validarAuxiliares() {
		boolean manejaAuxiliar = (boolean) registro.getCampos().get(manCenCtoC)
				|| (boolean) registro.getCampos().get(manAuxTerC) || (boolean) registro.getCampos().get(manAuxGenC);
		manejaAuxiliar = manejaAuxiliar || (boolean) registro.getCampos().get(manAuxFueC)
				|| (boolean) registro.getCampos().get(manAuxRefC);
		if (manejaAuxiliar || (boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName())) {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

			Registro regAux1;
			Registro regAux2;

			try {
				regAux1 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										PlanpresupuestalptosControladorUrlEnum.URL51935.getValue())
								.getUrl(),
								param));

				regAux2 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										PlanpresupuestalptosControladorUrlEnum.URL53056.getValue())
								.getUrl(),
								param));

				if (!("0").equals(regAux1.getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString())
						|| !("0").equals(regAux2.getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString())) {

					movBloqueadoIni = true;

				} else {
					movBloqueadoIni = false;
				}
			} catch (SystemException e) {
				Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
	}

	/**
	 * Metodo usado para validar el registro hijas, consulta si el rubro tiene
	 * cuentas hijas en tal caso no permitira que se cambien los indicadores de
	 * movimiento y manejo de auxiliares
	 */
	public void validarRegHijas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

		Registro regHijas;
		try {
			regHijas = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PlanpresupuestalptosControladorUrlEnum.URL57369.getValue())
							.getUrl(),
							param));

			if ((regHijas != null)
					&& !"0".equals(regHijas.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString())) {
				movBloqueado = true;
				indicadoresBloqueado = true;
			}

		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo usado para validar si se bloquea la vigencia o no
	 */
	public void validaVigenciaBloqueada() {

		cargarNombresNiveles(
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()), "").toString(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESTINO.getName()), "").toString());
		niveles = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nombre = registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();

		regaliasVisible = (boolean) registro.getCampos().get("REGALIAS");

		pasarSaldoBloquado = !(boolean) registro.getCampos().get(GeneralParameterEnum.MOVIMIENTO.getName());

		if ("VA".equals(registro.getCampos().get(tipoVigenciaC))) {
			registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), anio);
			vigenciaBloqueada = true;
		} else {
			vigenciaBloqueada = false;
		}
		cuentaFinal = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	public void cargarNombresNiveles(String naturaleza, String destino) {
		String tipo;
		if ("C".equals(naturaleza)) {
			tipo = "G";
		} else if ("I".equals(destino)) {
			tipo = "I";
		} else {
			tipo = "F";
		}

		try {
			etiNivel1 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 1" + tipo, modulo, new Date(), true), "")
					.toString();
			etiNivel2 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 2" + tipo, modulo, new Date(), true), "")
					.toString();

			etiNivel3 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 3" + tipo, modulo, new Date(), true), "")
					.toString();

			etiNivel4 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 4" + tipo, modulo, new Date(), true), "")
					.toString();

			etiNivel5 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 5" + tipo, modulo, new Date(), true), "")
					.toString();

			etiNivel6 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 6" + tipo, modulo, new Date(), true), "")
					.toString();

			etiNivel7 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 7" + tipo, modulo, new Date(), true), "")
					.toString();

			etiNivel8 = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NIVEL 8" + tipo, modulo, new Date(), true), "")
					.toString();

		} catch (SystemException e) {
			Logger.getLogger(PlanpresupuestalptosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
		registro.getCampos().remove("NOMBRE_FUENTE");
		registro.getCampos().remove("TIPOCLASECLASIFICADOR");
		if (Integer.parseInt( SysmanFunciones.nvl( registro.getCampos().get(GeneralParameterEnum.UNIDAD_EJECUTORA.getName()),"0").toString()) > 0){
			actualizarUnidadEjecutora() ;
		}
	
		return true;
		// </CODIGO_DESARROLLADO>
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
		registro.getCampos().put(manPacC, pacBloqueado ? false : (boolean) registro.getCampos().get(manPacC));
		registro.getCampos().remove("CODIGO_BPIN_SGR");
		registro.getCampos().remove("NOMBRE_FUENTE");
		registro.getCampos().remove("TIPOCLASECLASIFICADOR");
		// se quitan los campos por contigencia campos aun no creados
		registro.getCampos().remove("CODIGO_BPIN_SGR");
		registro.getCampos().remove("NOMBRE_FUENTE");
		if (Integer.parseInt( SysmanFunciones.nvl( registro.getCampos().get(GeneralParameterEnum.UNIDAD_EJECUTORA.getName()),"0").toString()) > 0){
			actualizarUnidadEjecutora() ;
		}
		if (!SysmanFunciones.validarCampoVacio(registro.getCampos(), fechaActivacionC) && !anio
				.equals(String.valueOf(SysmanFunciones.ano((Date) registro.getCampos().get(fechaActivacionC))))) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3343"));
			return false;
		}
		//JM CC 4401
		if(!naturalezainicial.equalsIgnoreCase(registro.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()).toString())){
			int rta = 0;
			try {
				
				rta = ejbPresupuestoTres.validarAprociacion(compania,
						registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString(),
						Integer.valueOf(anio));
				
				if (rta != 0) { 
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4511"));
					return false;
				}
				
			} catch (NumberFormatException | SystemException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
				logger.error(e.getMessage(), e);
				return false;
			}
		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>

		try {
			ejbPresupuestoTres.eliminarCuentaPresupuestal(compania, Integer.valueOf(anio),
					registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
			return false;

		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Valida si el rubro tiene auxiliares relacionados
	 * 
	 * @return
	 */
	private boolean validarRubroSinAuxiliares() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		int total = 0;
		try {
			Registro numeroAuxiliares = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(SeleccionRubrosUrlEnum.URL94117.getValue()).getUrlConteo().getUrl(),
					param));
			total = (int) numeroAuxiliares.getCampos().get("TOTAL");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return total <= 1;
	}
	/**
     * Método que verifica si el parámetro MANEJA PAC A RUBROS HIJOS está activo
     * @return
     */
	public boolean puedeManejarPac() {

	    if (!validarPacConIndicadores) {
	        return true;
	    }

	    if (registro == null || registro.getCampos() == null) {
	        return false;
	    }

	    return tieneIndicadorActivo();
	}

    /**
     * Método que permite saber si el check de Maneja PAC 
     * debe estar habilitado o no
     * @return
     */
	private boolean tieneIndicadorActivo() {

	    return Boolean.TRUE.equals(registro.getCampos().get("MOVIMIENTO"))
	        || Boolean.TRUE.equals(registro.getCampos().get("MAN_CEN_CTO"))
	        || Boolean.TRUE.equals(registro.getCampos().get("MAN_AUX_GEN"))
	        || Boolean.TRUE.equals(registro.getCampos().get("REGALIAS"))
	        || Boolean.TRUE.equals(registro.getCampos().get("MAN_AUX_FUE"))
	        || Boolean.TRUE.equals(registro.getCampos().get("MAN_AUX_TER"))
	        || Boolean.TRUE.equals(registro.getCampos().get("MAN_AUX_REF"))
	        || Boolean.TRUE.equals(registro.getCampos().get("CONSITUACIONFONDOS"))
	        || Boolean.TRUE.equals(registro.getCampos().get("MEN"));
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Retorna la variable tipoclaseclasificador
	 * 
	 * @return  tipoclaseclasificador
	 */
	public String getTipoclaseclasificador() {
		return tipoclaseclasificador;
	}
	/**
	 * Asigna la variable  tipoclaseclasificador
	 * 
	 * @param  tipoclaseclasificador
	 * Variable a asignar en  tipoclaseclasificador
	 */
	public void setTipoclaseclasificador(String tipoclaseclasificador) {
		this.tipoclaseclasificador = tipoclaseclasificador;
	}
	// <SET_GET_ATRIBUTOS>
	public String getEtiNivel1() {
		return etiNivel1;
	}

	public void setEtiNivel1(String etiNivel1) {
		this.etiNivel1 = etiNivel1;
	}

	public String getEtiNivel2() {
		return etiNivel2;
	}

	public void setEtiNivel2(String etiNivel2) {
		this.etiNivel2 = etiNivel2;
	}

	public String getEtiNivel3() {
		return etiNivel3;
	}

	public void setEtiNivel3(String etiNivel3) {
		this.etiNivel3 = etiNivel3;
	}

	public String getEtiNivel4() {
		return etiNivel4;
	}

	public void setEtiNivel4(String etiNivel4) {
		this.etiNivel4 = etiNivel4;
	}

	public String getEtiNivel5() {
		return etiNivel5;
	}

	public void setEtiNivel5(String etiNivel5) {
		this.etiNivel5 = etiNivel5;
	}

	public String getEtiNivel6() {
		return etiNivel6;
	}

	public void setEtiNivel6(String etiNivel6) {
		this.etiNivel6 = etiNivel6;
	}

	public String getEtiNivel7() {
		return etiNivel7;
	}

	public void setEtiNivel7(String etiNivel7) {
		this.etiNivel7 = etiNivel7;
	}

	public String getEtiNivel8() {
		return etiNivel8;
	}

	public void setEtiNivel8(String etiNivel8) {
		this.etiNivel8 = etiNivel8;
	}

	public String getNiveles() {
		return niveles;
	}

	public void setNiveles(String niveles) {
		this.niveles = niveles;
	}

	public String getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String getEliminarContable() {
		return eliminarContable;
	}

	public void setEliminarContable(String eliminarContable) {
		this.eliminarContable = eliminarContable;
	}

	public String getCuentaFinal() {
		return cuentaFinal;
	}

	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public boolean isMovBloqueadoIni() {
		return movBloqueadoIni;
	}

	public void setMovBloqueadoIni(boolean movBloqueadoIni) {
		this.movBloqueadoIni = movBloqueadoIni;
	}

	public String getNombreCuentaEqu() {
		return nombreCuentaEqu;
	}

	public void setNombreCuentaEqu(String nombreCuentaEqu) {
		this.nombreCuentaEqu = nombreCuentaEqu;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEquivalenciaContable() {
		return equivalenciaContable;
	}

	public void setEquivalenciaContable(String equivalenciaContable) {
		this.equivalenciaContable = equivalenciaContable;
	}

	public boolean isCodigo2Visible() {
		return codigo2Visible;
	}

	public void setCodigo2Visible(boolean codigo2Visible) {
		this.codigo2Visible = codigo2Visible;
	}

	public boolean isManejaProyVisible() {
		return manejaProyVisible;
	}

	public void setManejaProyVisible(boolean manejaProyVisible) {
		this.manejaProyVisible = manejaProyVisible;
	}

	public boolean isManejaCodigo2Visible() {
		return manejaCodigo2Visible;
	}

	public void setManejaCodigo2Visible(boolean manejaCodigo2Visible) {
		this.manejaCodigo2Visible = manejaCodigo2Visible;
	}

	public boolean isRegaliasVisible() {
		return regaliasVisible;
	}

	public void setRegaliasVisible(boolean regaliasVisible) {
		this.regaliasVisible = regaliasVisible;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getNomAux() {
		return nomAux;
	}

	public void setNomAux(String nomAux) {
		this.nomAux = nomAux;
	}

	public boolean isPasarSaldoBloquado() {
		return pasarSaldoBloquado;
	}

	public void setPasarSaldoBloquado(boolean pasarSaldoBloquado) {
		this.pasarSaldoBloquado = pasarSaldoBloquado;
	}

	public boolean isVigenciaBloqueada() {
		return vigenciaBloqueada;
	}

	public void setVigenciaBloqueada(boolean vigenciaBloqueada) {
		this.vigenciaBloqueada = vigenciaBloqueada;
	}

	public boolean isMovBloqueado() {
		return movBloqueado;
	}

	public void setMovBloqueado(boolean movBloqueado) {
		this.movBloqueado = movBloqueado;
	}

	public boolean isNaturalezaBloqueado() {
		return naturalezaBloqueado;
	}

	public void setNaturalezaBloqueado(boolean naturalezaBloqueado) {
		this.naturalezaBloqueado = naturalezaBloqueado;
	}

	public boolean isIndicadoresBloqueado() {
		return indicadoresBloqueado;
	}

	public void setIndicadoresBloqueado(boolean indicadoresBloqueado) {
		this.indicadoresBloqueado = indicadoresBloqueado;
	}

	public boolean isEsAnioCerrado() {
		return esAnioCerrado;
	}

	public void setEsAnioCerrado(boolean esAnioCerrado) {
		this.esAnioCerrado = esAnioCerrado;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	public RegistroDataModelImpl getListaFuente() {
		return listaFuente;
	}

	public void setListaFuente(RegistroDataModelImpl listaFuente) {
		this.listaFuente = listaFuente;
	}

	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}

	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
	}

	public List<Registro> getListadestino() {
        return listadestino;
    }

	public void setListadestino(List<Registro> listadestino) {
        this.listadestino = listadestino;
    }
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>

	public RegistroDataModelImpl getListaComboCuenta() {
		return listaComboCuenta;
	}

	public void setListaComboCuenta(RegistroDataModelImpl listaComboCuenta) {
		this.listaComboCuenta = listaComboCuenta;
	}

	public RegistroDataModelImpl getListaComboCuentaE() {
		return listaComboCuentaE;
	}

	public void setListaComboCuentaE(RegistroDataModelImpl listaComboCuentaE) {
		this.listaComboCuentaE = listaComboCuentaE;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public boolean isPacBloqueado() {
		return pacBloqueado;
	}

	public void setPacBloqueado(boolean pacBloqueado) {
		this.pacBloqueado = pacBloqueado;
	}

	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}

	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}

	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	public RegistroDataModelImpl getListaDependenciaAsociada() {
		return listaDependenciaAsociada;
	}

	public void setListaDependenciaAsociada(RegistroDataModelImpl listaDependenciaAsociada) {
		this.listaDependenciaAsociada = listaDependenciaAsociada;
	}

	public RegistroDataModelImpl getListaSigVigencia() {
		return listaSigVigencia;
	}

	public void setListaSigVigencia(RegistroDataModelImpl listaSigVigencia) {
		this.listaSigVigencia = listaSigVigencia;
	}

	public RegistroDataModelImpl getListaDestinoRecursos() {
		return listaDestinoRecursos;
	}

	public RegistroDataModelImpl getListaEquivGasto() {
		return listaEquivGasto;
	}

	public RegistroDataModelImpl getListaCuentaGastoCnt() {
		return listaCuentaGastoCnt;
	}

	public RegistroDataModelImpl getListaTipoClasificador() {
		return listaTipoClasificador;
	}

	public void setListaTipoClasificador(RegistroDataModelImpl listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}

	public void setListaCuentaGastoCnt(RegistroDataModelImpl listaCuentaGastoCnt) {
		this.listaCuentaGastoCnt = listaCuentaGastoCnt;
	}

	public void setListaEquivGasto(RegistroDataModelImpl listaEquivGasto) {
		this.listaEquivGasto = listaEquivGasto;
	}

	public void setListaDestinoRecursos(RegistroDataModelImpl listaDestinoRecursos) {
		this.listaDestinoRecursos = listaDestinoRecursos;
	}

	public RegistroDataModelImpl getListaCuentaContable() {
		return listaCuentaContable;
	}

	public void setListaCuentaContable(RegistroDataModelImpl listaCuentaContable) {
		this.listaCuentaContable = listaCuentaContable;
	}

	public RegistroDataModelImpl getListaCEliminarContable() {
		return listaCEliminarContable;
	}

	public void setListaCEliminarContable(RegistroDataModelImpl listaCEliminarContable) {
		this.listaCEliminarContable = listaCEliminarContable;
	}

	public RegistroDataModelImpl getListaCuentafinal() {
		return listaCuentafinal;
	}

	public void setListaCuentafinal(RegistroDataModelImpl listaCuentafinal) {
		this.listaCuentafinal = listaCuentafinal;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>

	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	public Registro getRegistroSub() {
		return registroSub;
	}

	/**
	 * Retorna la lista listaCuentabancaria
	 * 
	 * @return listaCuentabancaria
	 */
	public List<Registro> getListaCuentabancaria() {
		return listaCuentabancaria;
	}

	/**
	 * Asigna la lista listaCuentabancaria
	 * 
	 * @param listaCuentabancaria Variable a asignar en listaCuentabancaria
	 */
	public void setListaCuentabancaria(List<Registro> listaCuentabancaria) {
		this.listaCuentabancaria = listaCuentabancaria;
	}

	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}
	// </SET_GET_ADICIONALES>

	public void retornarFormulariocmdpac(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormulariocmdApropiado(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormulariocndregistro(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormulariocmdVerpac(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormulariocmdEjecucion(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioResumen(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioGrafica1(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormulariocmdMovimiento(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioGrafica2(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioMovContab(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioAcumulado(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioReconocimientos(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		retornarFormaGeneral(event);
		// </CODIGO_DESARROLLADO>
	}

	private void retornarFormaGeneral(SelectEvent event) {
		if (event.getObject() != null) {
			SessionUtil.redireccionarForma((Direccionador) event.getObject(), modulo);
		}
	}

	public List<Registro> getListaTipoVigencia() {
		return listaTipoVigencia;
	}

	public void setListaTipoVigencia(List<Registro> listaTipoVigencia) {
		this.listaTipoVigencia = listaTipoVigencia;
	}

	public boolean isFuenteVisible() {
		return fuenteVisible;
	}

	public void setFuenteVisible(boolean fuenteVisible) {
		this.fuenteVisible = fuenteVisible;
	}

	public List<Registro> getListaTipoRecurso() {
		return listaTipoRecurso;
	}

	public void setListaTipoRecurso(List<Registro> listaTipoRecurso) {
		this.listaTipoRecurso = listaTipoRecurso;
	}
	
	public RegistroDataModelImpl getListaunidadEjecutora() {
		return listaunidadEjecutora;
	}

	public void setListaunidadEjecutora(RegistroDataModelImpl listaunidadEjecutora) {
		this.listaunidadEjecutora = listaunidadEjecutora;
	}
	
	public String getUnidadeje() {
		return unidadeje;
	}

	public void setUnidadeje(String unidadeje) {
		this.unidadeje = unidadeje;
	}

	public boolean isValidarPacConIndicadores() {
		return validarPacConIndicadores;
	}

	public void setValidarPacConIndicadores(boolean validarPacConIndicadores) {
		this.validarPacConIndicadores = validarPacConIndicadores;
	}
	


}
