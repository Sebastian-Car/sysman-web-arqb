package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoDosRemote;
import com.sysman.bancoproyectos.enums.FrmsubdnovedadesproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.FrmsubdnovedadesproyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 10/09/2015
 * 
 * @author eamaya
 * @version 2.0, 21/09/2017, Proceso de Refactorinfg DSS, Manejo de EJBs, cambio
 *          de numero de formulario por enum y correcciones SonarQube. Se
 *          termino de migrar algunas funcionalidades desde Access. Cambio de
 *          texto quemado por texto en bean
 * 
 */
@ManagedBean
@ViewScoped
public class FrmsubdnovedadesproyectosControlador extends BeanBaseContinuoAcmeImpl {

	private static final String TB_TB3654 = "TB_TB3654";
	private final String compania;
	private final String modulo;

	/** Constante que almacena la cadena de reemplazo #$cantidad#$ */
	private final String reemplazoCantidad;

	/**
	 * Constante que almacena la cadena de reemplazo #$valorSolicitud#$
	 */
	private final String reemplazoValorSolicitud;

	/**
	 * Constante que almacena la cadena de reemplazo #$saldoRubro#$
	 */
	private final String reemplazoSaldoRubro;

	/** Constante a nivel de clase que aloja el valor codigo */
	private final String cCodigo1;

	/**
	 * Constante a nivel de clase que aloja el valor DEPARTAMENTO_L
	 */
	private final String cDepartamentoL;

	/**
	 * Constante a nivel de clase que aloja el valor ID_META_PRODUCTO
	 */
	private final String cIdMeta;

	/** Constante a nivel de clase que aloja el valor ID_PLAN_P */
	private final String cIdPlanP;

	/** Constante a nivel de clase que aloja el valor PAIS_L */
	private final String cPaisL;

	/**
	 * Constante que almacena el codigo de error de rubro TB_TB3653
	 */
	private final String mensajeErrRubro;

	private RegistroDataModelImpl listaProyecto;
	private RegistroDataModelImpl listaProyectoE;
	private RegistroDataModelImpl listaComponente;
	private RegistroDataModelImpl listaComponenteE;
	private RegistroDataModelImpl listaActividad;
	private RegistroDataModelImpl listaActividadE;
	private RegistroDataModelImpl listaActividad1;
	private RegistroDataModelImpl listaActividad1E;
	private RegistroDataModelImpl listaRubroPresupuestal;
	private RegistroDataModelImpl listaRubroPresupuestalE;
	private RegistroDataModelImpl listaBarrioL;
	private RegistroDataModelImpl listaBarrioLE;
	private RegistroDataModel listaINDICADORE;
	private RegistroDataModelImpl listaMetaProducto;
	private RegistroDataModelImpl listaMetaProductoE;
	// listas para modelo ARBOL
	private RegistroDataModelImpl listacodigoCPCDANE;
	private RegistroDataModelImpl listacodigoCPCDANEE;
	private RegistroDataModelImpl listacodigoUnidadEjecutora;
	private RegistroDataModelImpl listacodigoUnidadEjecutoraE;
	private RegistroDataModelImpl listacodigoFuente;
	private RegistroDataModelImpl listacodigoFuenteE;
	private RegistroDataModelImpl listacodigoCCPETRegalias;
	private RegistroDataModelImpl listacodigoCCPETRegaliasE;
	private RegistroDataModelImpl listadetalleSectorial;
	private RegistroDataModelImpl listadetalleSectorialE;
	private RegistroDataModelImpl listacodigoCCPET;
	private RegistroDataModelImpl listacodigoCCPETE;
	private RegistroDataModelImpl listacodigoBpin;
	private RegistroDataModelImpl listacodigoBpinE;
	private RegistroDataModelImpl listacodigoProducto;
	private RegistroDataModelImpl listacodigoProductoE;
	private RegistroDataModelImpl listasubPrograma;
	private RegistroDataModelImpl listasubProgramaE;
	private RegistroDataModelImpl listaprograma;
	private RegistroDataModelImpl listaprogramaE;
	private RegistroDataModelImpl listasectorCombo;
	private RegistroDataModelImpl listasectorComboE;
	// listas para modelo CON AUXILIAR
	private RegistroDataModelImpl listafuenteCuipo;
	private RegistroDataModelImpl listaproductoCuipo;
	private RegistroDataModelImpl listacodigoCCPETCuipo;
	private RegistroDataModelImpl listaCodigoCPC;
	private RegistroDataModelImpl listaCodigoCPCE;
	private RegistroDataModelImpl listafuenteCuipoE;
	private RegistroDataModelImpl listaproductoCuipoE;
	private RegistroDataModelImpl listacodigoCCPETCuipoE;

	private String auxiliar;
	private String tipoT;
	private String claseT;
	private String codigo;
	private String dependencia;
	private String vigencia;
	private String proyecto;
	private String actividad;
	private String actividadMeta;
	private String componente;
	private String componenteMeta;
	private String tipoComponente;
	private String fuenteRecursos;
	private String departamentoL;
	private String ciudadL;
	private String paisL;
	private String indicador;
	private String nombreMetaProducto;
	private String codigoMetaProducto;
	private String tipoMetaProducto;
	private String naturalezaPlanPptal;
	private String valorSolicitado1;
	private String valorAprobado1;
	private String idMetaProducto;
	private String nombreProyecto;
	private String especificacion;
	private String periodicidadProyecto;
	private String vigenciaComponente;
	private String infoDetalles;
	private String infoDetallesPlan;
	private String idPlanMeta;
	private String nombreComponente;
	private String auxTotales;
	private String consulta;
	private String responsable;
	private String cargoResponsable;
	private String obtejoComponente;
	private String periodo;
	private String rubroPresupuestal;
	private String fuenteRubroPresupuestal;
	private String programaProyecto;
	private String componenteE;
	private String programaProyectoAux;
	private HashMap<String, Object> ridSolicitudS;
	private String argNovedades;
	private int indice;
	private double cantidad;
	private double cantidadProgramadaMeta;
	private double cantidadEjecutadaMeta;
	private double componenteValorTotal;
	private double componenteValorSolicitado;
	private double valorProgramadoMeta;
	private double valorProgramadoMetaOtros;
	private double valorEjecutadoMeta;
	private double valorProgramadocomponente;
	private double valorEjecutadoComponente;
	private double saldoRubro;
	private double cantidadActividad;
	private double cantidadEjeActividad;
	private Double valorSolicitado;
	private Double valorDisminuido;
	private Double valorAprobado;
	private double saldoComponente;
	private double costoTotalActividad;
	private Double valComponente;
	private Double svalorSolicitado;
	private Double saldoRubroAux;
	private int vigenciaMetaP;
	private int vigenciaPlanIndicativo;
	private int modelo;
        private int modeloN;
	private boolean soloComponente;
	private boolean bloqueaProyecto;
	private boolean bloqueaComponente;
	private boolean bloqueaMetaProducto;
	private boolean bloqueaActividad;
	private boolean actualizadoEnPlan;
	private double cantidadPlan;
	private boolean bloqueaProyectoContinuo;
	private boolean bloqueaComponenteContinuo;
	private boolean bloqueaMetaProductoContinuo;
	private boolean bloqueaActividadContinuo;
	private boolean bloqueaImprimir;
	private boolean editarFormulario;
	private boolean eliminarFormulario;
	private boolean insertarFormulario;
	private String sector;
	private String nombreSector;
	private String voBo;
	private String consecutivoNovedad;
	private String dependenciaSec;
	private int mes;
	private List<Registro> listaPeriodo;
	private List<Registro> listaINDICADOR;
	private RegistroDataModelImpl listaFuente;
	private RegistroDataModelImpl listaFuenteE;
	private List<Registro> listaTotales;
	private String fechaInicialM;
	private String fechaFinalM;
	private String filtraPrimeroMetaProd;
	private StreamedContent archivoDescarga;
	private Registro rs;
	private Registro rsProgramado;
	private Double valorSolicitadoAux;
	private String auxiliarGeneral;
	private String referencia;
	private String centroCosto;

	private String pieComponente;
	private String pieActividad;
	private String pieMetaProd;
	private String pieLineaBase;
	private String pieMetaTotal;
	private String pieMetaVigencia;
	private String pieMetaEjecutada;
	private String pieSector;
	private String actividadE;
	private String ano;
	
	private Registro anoSeleccinadoModelo;
	private List<Registro> listaAnosClasificacion;
	private List<Registro> listaTipoClasificador;
	// modelo año periodo "ARBOL"
	// indican si los campos se muestran o no de acuerdo a el modelo del perdiodo del año
	private boolean sectorComboMostar = false;
	private boolean programaMostar = false;
	private boolean subprogramaMostar = false;
	private boolean codigoProductoMostar = false;
	private boolean codigoBpinMostar = false;
	private boolean codigoCCPETMostar = false;
	private boolean codigoCPCDANEMostar = false;
	private boolean codigoUnidadEjecutoraMostar = false;
	private boolean codigoFuenteMostar = false;
	private boolean codigoCCPETRegaliasMostar = false;
	private boolean codigodetalleSectorialMostar = false;
	//indican si los campos se habilitan o no, de acuerdo a la lista de clasificacion
	private boolean campobloqueadosector = false;
	private boolean campobloqueadoprograma = false;
	private boolean campobloqueadosubprograma = false;
	private boolean campobloqueadocodproducto = false;
	private boolean campobloqueadocodbpin = false;
	private boolean campobloqueadocodCCPET = false;
	private boolean campobloqueadocodCPCDANE = false;
	private boolean campobloqueadocodunidadejecutora = false;
	private boolean campobloqueadocodfuente = false;
	private boolean campobloqueadocodCCPETregalias = false;
	private boolean campobloqueadocoddetalleSectorial = false;
	// modelo año periodo "CON CUPO"
	// indican si los campos se muestran o no de acuerdo a el modelo del perdiodo del año
	private boolean codigoCPCMostar = false;
	private boolean fuenteCuipoMostar = false;
	private boolean productoCuipoMostar = false;
	private boolean codigoCCPETCuipoMostar = false;
	private boolean codigoBpinCampoMostrar = false;
	//indican si los campos se habilitan o no, de acuerdo a la lista de clasificacion
	private boolean campobloqueadocodigoCPC;
	private boolean campobloqueadoFuenteCuipo;
	private boolean campobloqueadoProductoCuipo;
	private boolean campobloqueadoCodigoCCPETCuipo;
	private boolean campobloqueadoCodigoBPINCampo;
	// campos para las listas
	private String codigoCPC;
	private String codigoCPCE;
	private String fuenteCuipo;
	private String productoCuipo;
	private String codigoCCPETCuipo;
	private String codigoBPINCampo;
	private String codCCPETregalias;
	private String codigoBpim;
	/**
	 * valida si el campo puede ser editado
	 **/
	private boolean campobloqueado;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtl;

	@EJB
	private EjbBancoProyectoDosRemote ejbBancoProyectosDos;

	@EJB
	private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;
	private String fuenteCuipoE;
	private String productoCuipoE;
	private String codigoCCPETCuipoE;
	private String codigoCPCDANE;
	private String codigoCPCDANEE;
	private String codigoUnidadEjecutora;
	private String codigoUnidadEjecutoraE;
	private String codigoFuente;
	private String codigoFuenteE;
	private String codigoCCPETRegalias;
	private String codigoCCPETRegaliasE;
	private String codigoDetalleSectorial;
	private String codigoDetalleSectorialE;
	private String codigoCCPET;
	private String codigoCCPETE;
	private String codigoBpinE;
	private String codigoProducto;
	private String codigoProductoE;
	private String codigoBpin;
	private String subPrograma;
	private String subProgramaE;
	private String programa;
	private String programaE;
	private String sectorCombo;
	private String sectorComboE;
	private String idpadre;
	private String idpadrepro;
	private String idpadrecpc;
	private String claseClasificador;
	private int aplicacionGastos;
	private int aplicacionIngr;
	private String manejaCampObligSCDP;
	private Map<String,String> camposBorde;
	private String obligaCampos1;
	private String obligaCampos2;
	private String obligaCampos3;
	private String obligaCampos4;
	private String obligaCampos5;
	private String obligaCampos6;
	private String obligaCampos7;
	private String obligaCampos8;
	private String obligaCampos9;
	private String obligaCampos10;
	private String obligaCampos11;
	private boolean validacionExitosa = true;
	/**
	 * Creates a new instance of FrmsubdnovedadesproyectosControlador
	 */
	@SuppressWarnings("unchecked")
	public FrmsubdnovedadesproyectosControlador() {
		super();
		numFormulario = GeneralCodigoFormaEnum.FRMSUBDNOVEDADESPROYECTOS_CONTROLADOR.getCodigo();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		paisL = SessionUtil.getCompaniaIngreso().getCodigoPais();
		departamentoL = SessionUtil.getCompaniaIngreso().getCodigoDepartamento();
		ciudadL = SessionUtil.getCompaniaIngreso().getCodigoCiudad();
		mes = SysmanFunciones.getParteFecha(new Date(), Calendar.MONTH) + 1;
		cCodigo1 = "codigo";
		cDepartamentoL = "DEPARTAMENTO_L";
		cIdMeta = "ID_META_PRODUCTO";
		cIdPlanP = "ID_PLAN_P";
		cPaisL = "PAIS_L";
		reemplazoCantidad = "#$cantidad#$";
		reemplazoValorSolicitud = "#$valorSolicitud#$";
		reemplazoSaldoRubro = "#$saldoRubro#$";
		mensajeErrRubro = "TB_TB3653";

		try {
			validarPermisos();

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				tipoT = (String) parametrosEntrada.get("tipoTFiltrarM");
				claseT = (String) parametrosEntrada.get("claseTS");
				proyecto = (String) parametrosEntrada.get("proyectoM");
				dependencia = (String) parametrosEntrada.get("dependencia");
				vigencia = (String) parametrosEntrada.get("ano");

				ridSolicitudS = (HashMap<String, Object>) parametrosEntrada.get("ridSolicitud");

				parametrosEntrada.put("rid", ridSolicitudS);
				fechaInicialM = (String) parametrosEntrada.get("fechaInicialM");
				fechaFinalM = (String) parametrosEntrada.get("fechaFinalM");
				argNovedades = (String) parametrosEntrada.get("argNovedades");
				codigo = (String) parametrosEntrada.get(cCodigo1);
				voBo = (String) parametrosEntrada.get("voBo");
				dependenciaSec = (String) parametrosEntrada.get("dependenciaSe");

				if ("true".equals(voBo)) {
					editarFormulario = true;
					campobloqueado = true;
					eliminarFormulario = false;
					insertarFormulario = false;
				} else {
					campobloqueado = false;
					editarFormulario = true;
					eliminarFormulario = true;
					insertarFormulario = true;
				}

				parametrosEntrada.remove("claseTS");
				parametrosEntrada.remove("ridSolicitud");
			}
		} catch (SysmanException ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			SessionUtil.cleanFlash();
		}

	}

	@PostConstruct
	public void inicializar() {
		try {
		tabla = GenericUrlEnum.BP_D_NOVEDADPROYECTO.getTable();
		buscarLlave();
		reasignarOrigen();
		registro = new Registro(new HashMap<String, Object>());
		cambiarinfoDetalles();
		cambiarinfoDetallesPlan();
		abrirFormulario();
		calcularTotales();
		cargarListaProyecto();
		cargarListaProyectoE();
		cargarListaBarrioL();
		cargarListaBarrioLE();
		cargarListaMetaProductoE();
		ingresarPeriodo();
		obtenerModeloAno();
		obtenerClasificacionAno("");

		bloqueaImprimir = false;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		cargarBorde();
		cantidad = 0;

		try {
			filtraPrimeroMetaProd = ejbSysmanUtl.consultarParametro(compania,
					"FILTRA PRIMERO META PRODUCTO EN SOLICITUD DE CDP", SessionUtil.getModulo(), new Date(), true);
			
			manejaCampObligSCDP = ejbSysmanUtl.consultarParametro(compania,
					"MANEJA CAMPOS OBLIGATORIOS EN SOLICITUD DE DISPONIBILIDAD (CLASIFICADORES)",SessionUtil.getModulo(),new Date(),true);

			filtraPrimeroMetaProd = filtraPrimeroMetaProd == null ? "NO" : filtraPrimeroMetaProd;

			if ("SI".equals(filtraPrimeroMetaProd)) {
				bloqueaActividad = true;
				bloqueaActividadContinuo = true;
				bloqueaComponente = true;
			}

			else {
				bloqueaActividad = false;
				bloqueaActividadContinuo = false;
				bloqueaComponente = false;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}
	
	public void cargarBorde() {
    	obligaCampos1 = obligaCampos2 = obligaCampos3 = obligaCampos4 = obligaCampos5 = 
    	obligaCampos6 = obligaCampos7 = obligaCampos8 = obligaCampos9 = obligaCampos10 = 
    	obligaCampos11 = "#A9A9A9 solid 2px";
    }

	private void reiniciarTotales() {
		valorSolicitado1 = "0";
		valorAprobado1 = "0";
	}

	@Override
	public void reasignarOrigen() {
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		parametrosListado.put(GeneralParameterEnum.CLASE.getName(), claseT);

		parametrosListado.put(GeneralParameterEnum.NOVEDAD.getName(), codigo);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL0001.getValue());

		urlCreacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL0002.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL0003.getValue());

		urlEliminacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL0004.getValue());

	}

	private void calcularTotales() {

		reiniciarTotales();

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		param.put(GeneralParameterEnum.CLASE.getName(), claseT);

		param.put(GeneralParameterEnum.NOVEDAD.getName(), codigo);

		try {
			listaTotales = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL16323.getValue())
							.getUrl(),
							param));

			if ((listaTotales == null) || listaTotales.isEmpty()) {
				valorSolicitado1 = "0";
				valorAprobado1 = "0";
			} else if ((listaTotales.get(0).getCampos()
					.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()) != null)
					|| (listaTotales.get(0).getCampos().get("VALORAPROBADO") != null)) {
				DecimalFormat dblDF = new DecimalFormat("##,###.00");

				valorSolicitado1 = dblDF.format(listaTotales.get(0).getCampos()
						.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()));

				valorAprobado1 = dblDF.format(listaTotales.get(0).getCampos().get("VALORAPROBADO"));
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaFuente() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL17167.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOSRUBRO.getValue());

	}

	public void cargarListaFuenteE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL17167.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOSRUBRO.getValue());

	}

	public void cargarListaMetaProducto() {
		Map<String, Object> param = new TreeMap<>();
		UrlBean urlBean;

		if ("SI".equals(filtraPrimeroMetaProd)) {

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL001.getValue());

			if (validarComponenteYMetaPorAnio()) {
				param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
			} else {
				param.put(GeneralParameterEnum.VIGENCIA.getName(), null);
			}

		}

		else {

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL18376.getValue());

			param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
			param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);
		}

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		// cIdPlanP
		try {
			listaMetaProducto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, CacheUtil.getLlaveServicio(urlConexionCache, "BP_PROYECTO_PLAN_INDICATIVO"));
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaMetaProductoE() {

		Map<String, Object> param = new TreeMap<>();
		UrlBean urlBean;

		if ("SI".equals(filtraPrimeroMetaProd)) {

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL001.getValue());
		}

		else {

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL18376.getValue());

			param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividadE);
			param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);
		}
		param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividadE);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		// cIdPlanP
		try {
			listaMetaProductoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, CacheUtil.getLlaveServicio(urlConexionCache, "BP_PROYECTO_PLAN_INDICATIVO"));
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaProyecto() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL24537.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
		param.put("DEPENDENCIA_SEC", dependenciaSec);

		listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaProyectoE() {
		listaProyectoE = listaProyecto;

	}

	public void cargarListaComponente() {
	    Map<String, Object> param = new TreeMap<>();
	    UrlBean urlBean;
	    
	    if ("SI".equals(filtraPrimeroMetaProd)) {
	        urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL002.getValue());
	        param.put(FrmsubdnovedadesproyectosControladorEnum.METAPRODUCTO.getValue(), codigoMetaProducto);
	    } else {
	        urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL26124.getValue());
	        
	        if (validarComponenteYMetaPorAnio()) {
	            param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
	        } else {
	            param.put(GeneralParameterEnum.VIGENCIA.getName(), null);
	        }
	    }
	    
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
	    
	    listaComponente = new RegistroDataModelImpl(
	            urlBean.getUrl(), 
	            urlBean.getUrlConteo().getUrl(), 
	            param, 
	            true,
	            FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()
	    );
	}

	public void cargarListaComponenteE() {

		Map<String, Object> param = new TreeMap<>();
		UrlBean urlBean;

		if ("SI".equals(filtraPrimeroMetaProd)) {

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL002.getValue());
			param.put(FrmsubdnovedadesproyectosControladorEnum.METAPRODUCTO.getValue(), codigoMetaProducto);
		}

		else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL26124.getValue());

		}

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		listaComponenteE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue());

	}

	public void cargarListaActividad() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL29999.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		listaActividad = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue());

	}

	public void cargarListaActividad1() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL29999.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		setListaActividad1(new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()));

	}

	public void cargarListaActividad1E() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL29999.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componenteE);

		listaActividad1E = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue());

	}

	public void cargarListaPeriodo() {
		listaPeriodo = null;
		listaPeriodo = new ArrayList<>();

		for (int i = 1; i <= Integer.parseInt(periodicidadProyecto); i++) {
			HashMap<String, Object> aux = new HashMap<>();
			aux.put(GeneralParameterEnum.CODIGO.getName(), i);
			aux.put(GeneralParameterEnum.NOMBRE.getName(), i);
			listaPeriodo.add(new Registro(i - 1, aux));
		}
	}

	public void cargarListaPeriodoE() {
		listaPeriodo = null;
		listaPeriodo = new ArrayList<>();

		for (int i = 1; i <= Integer.parseInt(periodicidadProyecto); i++) {
			HashMap<String, Object> aux = new HashMap<>();
			aux.put(GeneralParameterEnum.CODIGO.getName(), i);
			aux.put(GeneralParameterEnum.NOMBRE.getName(), i);
			listaPeriodo.add(new Registro(i - 1, aux));
		}
	}

	public void cargarListaRubroPresupuestal() throws SysmanException {

		String parametro = "";
		try {
			parametro = ejbSysmanUtl.consultarParametro(compania,
					"CONTROLA FUENTE SEGUN META PRODUCTO EN SOLICITUD DE CDP", modulo, new Date(), false);
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (parametro.equals("NO")) {

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL34123.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
			param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
			param.put(FrmsubdnovedadesproyectosControladorEnum.FUENTE.getValue(), fuenteRecursos);


			listaRubroPresupuestal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					FrmsubdnovedadesproyectosControladorEnum.ID.getValue());
		} else {

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL1030.getValue());

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(FrmsubdnovedadesproyectosControladorEnum.ID_META.getValue(), codigoMetaProducto);

			param.put(FrmsubdnovedadesproyectosControladorEnum.FUENTE.getValue(), fuenteRecursos);

			param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

			param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

			listaRubroPresupuestal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					FrmsubdnovedadesproyectosControladorEnum.ID.getValue());
		}
	}

	public void cargarListaRubroPresupuestalE() {

		String parametro = "";
		try {
			parametro = ejbSysmanUtl.consultarParametro(compania,
					"CONTROLA FUENTE SEGUN META PRODUCTO EN SOLICITUD DE CDP", modulo, new Date(), false);
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (parametro.equals("NO")) {

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL34123.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
			param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
			param.put(FrmsubdnovedadesproyectosControladorEnum.FUENTE.getValue(), fuenteRecursos);

			listaRubroPresupuestalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
					param, true, "ID");
		} else {

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL1030.getValue());

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(FrmsubdnovedadesproyectosControladorEnum.ID_META.getValue(), codigoMetaProducto);

			param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

			listaRubroPresupuestalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
					param, true, "ID");
		}

	}

	public void cargarListaBarrioL() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL44624.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmsubdnovedadesproyectosControladorEnum.PAIS.getValue(), paisL);
		param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamentoL);
		param.put(GeneralParameterEnum.CIUDAD.getName(), ciudadL);

		listaBarrioL = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaBarrioLE() {

		listaBarrioLE = listaBarrioL;
	}
	public void cargarListacodigoCPCDANE() {
            idpadrecpc = "006"+codigoCCPET;
            
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL022.getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), vigencia);
            param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(),idpadrecpc);
            
            listacodigoCPCDANE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCPCDANEE() {
	    
            idpadrecpc = "006"+codigoCCPETE;
            
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL022.getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), vigencia);
            param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(),idpadrecpc);
            
            listacodigoCPCDANEE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoUnidadEjecutora() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.TPCUNIDADEJECUTORA.getValue());


		listacodigoUnidadEjecutora = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoUnidadEjecutoraE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.TPCUNIDADEJECUTORA.getValue());


		listacodigoUnidadEjecutoraE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoFuente() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.TPCFUENTERUBRO.getValue());


		listacodigoFuente = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoFuenteE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.TPCFUENTERUBRO.getValue());


		listacodigoFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCCPETRegalias() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.TPCCPETR.getValue());


		listacodigoCCPETRegalias = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCCPETRegaliasE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.TPCCPETR.getValue());


		listacodigoCCPETRegaliasE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListacodigoDetalleSectorial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.CODIGODETALLESECTORIAL.getValue());


		listadetalleSectorial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListacodigoDetalleSectorialE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), FrmsubdnovedadesproyectosControladorEnum.CODIGODETALLESECTORIAL.getValue());


		listadetalleSectorialE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListacodigoCPC() {
	    
	        idpadrecpc = "006"+codigoCCPET;
	    
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL022.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(),idpadrecpc);
		
		listacodigoCPCDANE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCPCE() {
	        
	        idpadrecpc = "006"+codigoCCPETE;
	    
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL022.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(),idpadrecpc);

		listacodigoCPCDANEE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListafuenteCuipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL012.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listafuenteCuipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListafuenteCuipoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL012.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listafuenteCuipoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListaproductoCuipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL013.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listaproductoCuipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListaproductoCuipoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL013.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listaproductoCuipoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCCPETCuipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL014.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listacodigoCCPETCuipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCCPETCuipoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmsubdnovedadesproyectosControladorUrlEnum.URL014.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);

		listacodigoCCPETCuipoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCCPET() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "006");


		listacodigoCCPET = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoCCPETE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "006");


		listacodigoCCPETE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoBpin() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "005");

		listacodigoBpin = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoBpinE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "005");

		listacodigoBpinE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoProducto() {
	    
	        idpadrepro = "002"+ programa;
	    
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL022
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		//param.put(SubdetallecomprobantepptalsControladorEnum.CODCUENTA.getValue(),codigoCuenta);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), idpadrepro);


		listacodigoProducto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListacodigoProductoE() {
	    
	        idpadrepro = "002"+ programaE;
	    
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL022
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		//param.put(SubdetallecomprobantepptalsControladorEnum.CODCUENTA.getValue(),codigoCuenta);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), idpadrepro);


		listacodigoProductoE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListasubPrograma() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		//param.put(SubdetallecomprobantepptalsControladorEnum.CODCUENTA.getValue(),codigoCuenta);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "003");


		listasubPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListasubProgramaE() {
	    
	        UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		//param.put(SubdetallecomprobantepptalsControladorEnum.CODCUENTA.getValue(),codigoCuenta);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "003");


		listasubProgramaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListaprograma() {
	    
	           idpadre = "001"+sectorCombo;
	            
	                UrlBean urlBean = UrlServiceUtil.getInstance()
	                                .getUrlServiceByUrlByEnumID(
	                                                FrmsubdnovedadesproyectosControladorUrlEnum.URL022
	                                                .getValue());
	                Map<String, Object> param = new TreeMap<>();
	                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	                param.put(GeneralParameterEnum.ANO.getName(), vigencia);
	                //param.put(SubdetallecomprobantepptalsControladorEnum.CODCUENTA.getValue(),codigoCuenta);
	                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), idpadre);


	                listaprograma = new RegistroDataModelImpl(urlBean.getUrl(),
	                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	
	}
	public void cargarListaprogramaE() {
	    
	        idpadre = "001"+sectorComboE;
	    
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL022
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), idpadre);


		listaprogramaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
	}
	public void cargarListasectorCombo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia); 
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "001");
		listasectorCombo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void cargarListasectorComboE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmsubdnovedadesproyectosControladorUrlEnum.URL015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "001");
		
		listasectorComboE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	public void getCertificadoNoFondos(FORMATOS formato) {
		// </CODIGO_DESARROLLADO>
		/*
		 * CONSULTA PARA GENERAR EL REPORTE CETIFICADO DE NO FONDOS.
		 */

		try {

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put(cCodigo1, codigo);

			// MANEJO DE PARAMETROS DE REEMPLAZO
			Map<String, Object> parametros = new HashMap<>();

			String reporte = "000199CERTIFICADONOFONDOS";

			// MANEJO DE PARAMETROS DEL REPORTE
			String strSql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);
			String cargoJefeBancoProyectos = ejbSysmanUtl.consultarParametro(compania, "CARGO JEFE DE BANCO PROYECTOS",
					modulo, new Date(), false);

			String jefeBancoProyectos = ejbSysmanUtl.consultarParametro(compania, "JEFE DEL BANCO DE PROYECTOS", modulo,
					new Date(), false);

			parametros.put("PR_STRSQL", strSql);
			parametros.put("PR_CARGO_JEFE_BANCO_PROYECTOS", cargoJefeBancoProyectos);
			parametros.put("PR_JEFE_BANCO_PROYECTOS", jefeBancoProyectos);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1766"));
			Logger.getLogger(FrmsubdnovedadesproyectosControlador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JRException | IOException | SysmanException ex) {
			JsfUtil.agregarMensajeError(idioma.getString("MSM_TRANS_INTERRUMPIDA") + ex.getMessage());
			Logger.getLogger(FrmsubdnovedadesproyectosControlador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void ingresarPeriodo() {
		// <CODIGO_DESARROLLADO>
		int i;

		if (periodicidadProyecto == null) {
			periodicidadProyecto = "0";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("");

		for (i = 0; i < 10; i++) {
			builder.append(i);
		}

		periodo = builder.toString();

		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCantidadActividad() {
		// <CODIGO_DESARROLLADO>
		Registro rs1;
		double dispo = 0;
		String clase;
		double cant = 0;
		String cantAnt;
		clase = "";

		cantAnt = registro.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString();

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		try {
			rs1 = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL31673.getValue())
							.getUrl(),
							param));

			if (rs1 != null) {
				clase = SysmanFunciones.nvl(rs1.getCampos().get("CLASENOVEDAD"), "").toString();
			} else {
				clase = "";
			}

			if ("S".equals(clase)) {

				dispo = Double.parseDouble(SysmanFunciones.nvl(ejbBancoProyectosDos.getCantidadPorActividad(compania,
						Integer.parseInt(vigencia), proyecto, componente, tipoComponente, actividad), "0").toString());

				param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

				param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

				param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

				rs1 = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FrmsubdnovedadesproyectosControladorUrlEnum.URL35319.getValue())
						.getUrl(),
						param));

				String cantidadn = SysmanFunciones.nvl(rs1.getCampos().get("CANT"), "0").toString();
				cantidad = Double.parseDouble(cantidadn);

				dispo = dispo - cantidad;

				cant = Double.parseDouble(cantAnt);

				if ((cantidad - cant - dispo) > 0) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2434").replace(reemplazoCantidad,
							String.valueOf(cantidad - cant - dispo)));
					registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), "0");
				}
			} else if ("E".equals(clase)) {

				dispo = Double.parseDouble(ejbBancoProyectosDos.getCantidadDisponible(compania, clase,
						Integer.parseInt(vigencia), proyecto, componente, tipoComponente, actividad,
						Integer.parseInt(Double.toString(cant)), Integer.parseInt(Double.toString(cantidad)))
						.toString());

				if (dispo > 0) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2434").replace(reemplazoCantidad,
							String.valueOf(cantidad - cant - dispo)));

					registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), "0");
				}

			}
			registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), cantAnt);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarinfoDetalles() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	public void cambiarinfoDetallesPlan() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCantidadPlan() {
		// <CODIGO_DESARROLLADO>

		Double dispo;
		Double cantidadPlanIndicativo;

		if (SysmanFunciones.validarVariableVacio(indicador)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2436"));
			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_PLAN.getValue(), "0");
			return;
		}

		try {
			dispo = Double.parseDouble(ejbBancoProyectoCinco.saldoPlanIndicativoMeta(compania, indicador,
					new BigDecimal(vigenciaPlanIndicativo), new BigDecimal(vigenciaMetaP)).toString());

			cantidadPlanIndicativo = Double
					.parseDouble(SysmanFunciones
							.nvl(registro.getCampos()
									.get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_PLAN.getValue()), "0")
							.toString());

			if (cantidadPlanIndicativo - dispo > 0) {

				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3650")
						.replace(reemplazoCantidad, String.valueOf(cantidadPlanIndicativo - dispo))
						.replace("#$indicador#$", indicador));
				registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_PLAN.getValue(), "0");
			}

		} catch (NumberFormatException | SystemException ex) {
			Logger.getLogger(FrmsubdnovedadesproyectosControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void consecutivoNovedad() {

		try {
			consecutivoNovedad = Long.toString(ejbSysmanUtl.generarConsecutivoConValorInicial("BP_D_NOVEDADPROYECTO",
					SysmanFunciones.concatenar("COMPANIA = ''", compania, "'' " + " AND TIPOT = ''", tipoT,
							"'' " + " AND CLASET = ''", claseT, "'' " + " AND NOVEDAD = ", codigo,
							" " + " AND DEPENDENCIA = ''", dependencia, "''"),
					"CODIGO", "1"));

		} catch (SystemException ex) {
			JsfUtil.agregarMensajeError(ex.getMessage());
			Logger.getLogger(FrmsubdnovedadesproyectosControlador.class.getName()).log(Level.SEVERE, null, ex);

		}
	}

	public void cambiarVALORSOLICITADO() {

		Registro registroR;
		Double valorSolicitudTotal;

		rubroPresupuestal = SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue()),
						"")
				.toString();

		fuenteRubroPresupuestal = SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue()), "")
				.toString();

		if (SysmanFunciones.validarVariableVacio(rubroPresupuestal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB3654));

			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

			return;
		}

		// VALOR solicitDO DIGITADO POR EL USUARIO
		valorSolicitadoAux = Double.parseDouble(SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()), "0")
				.toString());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		param.put(GeneralParameterEnum.CLASE.getName(), claseT);

		param.put(GeneralParameterEnum.NOVEDAD.getName(), codigo);

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		param.put(GeneralParameterEnum.RUBRO.getName(), rubroPresupuestal);

		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(), fuenteRubroPresupuestal);
		
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CENTRO_COSTO.getValue()), "0")
				.toString());
		
		param.put(GeneralParameterEnum.REFERENCIA.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.REFERENCIA.getValue()), "0")
				.toString());
		
		param.put(GeneralParameterEnum.AUXILIAR.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.AUXILIAR.getValue()), "0")
				.toString());

		try {
			registroR = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL21259.getValue())
							.getUrl(),
							param));

			if (registroR != null) {
				valorSolicitudTotal = Double
						.parseDouble(SysmanFunciones.nvl(registroR.getCampos().get("SVALORSOLICITADO"), "0").toString())
						+ valorSolicitadoAux;
			} else {
				valorSolicitudTotal = valorSolicitadoAux;
			}

			DecimalFormat format = new DecimalFormat("#0.##");

			long valorRubro = Long.parseLong(format.format(saldoRubroAux).replace(",", ""));
			long valorSolicitud = Long.parseLong(format.format(valorSolicitudTotal).replace(",", ""));

			if (valorSolicitud > valorRubro) {
				JsfUtil.agregarMensajeInformativoDialogo(idioma.getString(mensajeErrRubro)
						.replace(reemplazoValorSolicitud, String.valueOf(valorSolicitud)));

				registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

				valorSolicitadoAux = 0.0;

				return;
			}

			if (!controlarSaldoActividad(valorSolicitadoAux)) {
				return;
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cambiarVALORSOLICITADOC(int rowNum) {
		if ("NO".equals(filtraPrimeroMetaProd)) {
			validarActividad(rowNum);
		}

		Registro registroR;
		Double valorSolicitudTotal;
		Double valorSolicitadoAux;

		rubroPresupuestal = SysmanFunciones.nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue()), "").toString();

		if (SysmanFunciones.validarVariableVacio(rubroPresupuestal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB3654));

			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

			return;
		}

		valorSolicitadoAux = Double
				.parseDouble(SysmanFunciones
						.nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()), "0")
						.toString());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		param.put(GeneralParameterEnum.CLASE.getName(), claseT);

		param.put(GeneralParameterEnum.NOVEDAD.getName(), codigo);

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		param.put(GeneralParameterEnum.RUBRO.getName(), rubroPresupuestal);
		
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CENTRO_COSTO.getValue()), "0")
				.toString());
		
		param.put(GeneralParameterEnum.REFERENCIA.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.REFERENCIA.getValue()), "0")
				.toString());
		
		param.put(GeneralParameterEnum.AUXILIAR.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.AUXILIAR.getValue()), "0")
				.toString());

		
		try {
			registroR = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL21259.getValue())
							.getUrl(),
							param));

			if (registroR != null) {

				valorSolicitudTotal = Double
						.parseDouble(SysmanFunciones.nvl(registroR.getCampos().get("SVALORSOLICITADO"), "0").toString())
						+ valorSolicitadoAux;
			} else {
				valorSolicitudTotal = valorSolicitadoAux;
			}

			Map<String, Object> paramAux = new TreeMap<>();

			paramAux.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			paramAux.put(FrmsubdnovedadesproyectosControladorEnum.ID_META.getValue(),
					listaInicial.getDatasource().get(rowNum % 10).getCampos().get(cIdMeta));

			paramAux.put(FrmsubdnovedadesproyectosControladorEnum.FUENTE.getValue(), fuenteRecursos);
			
			paramAux.put("CENTRO_COSTO", listaInicial.getDatasource().get(rowNum % 10).getCampos().get("CENTRO_COSTO"));
			
			paramAux.put("REFERENCIA", listaInicial.getDatasource().get(rowNum % 10).getCampos().get("REFERENCIA"));
			
			paramAux.put("AUXILIAR", listaInicial.getDatasource().get(rowNum % 10).getCampos().get("AUXILIAR"));
			
			paramAux.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

			paramAux.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

			paramAux.put("RUBRO", listaInicial.getDatasource().get(rowNum % 10).getCampos().get("RUBROPRESUPUESTAL"));

			rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL004.getValue())
							.getUrl(),
							paramAux));

			if (rs != null) {

				saldoRubroAux = Double.parseDouble(
						rs.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.SALDO.getValue()).toString());
			}

			if (valorSolicitudTotal > saldoRubroAux) {

				JsfUtil.agregarMensajeInformativoDialogo(idioma.getString(mensajeErrRubro)
						.replace(reemplazoValorSolicitud, valorSolicitudTotal.toString())
						.replace(reemplazoSaldoRubro, saldoRubroAux.toString()));

				listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

				valorSolicitadoAux = 0.0;

				return;
			}

			if (!controlarSaldoActividadC(valorSolicitadoAux, indice)) {
				return;
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cambiarVALORAPROBADO() {
		Double valorAprobado;

		rubroPresupuestal = SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue()),
						"")
				.toString();

		if (SysmanFunciones.validarVariableVacio(rubroPresupuestal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB3654));

			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), 0);

			return;
		}

		valorAprobado = Double.parseDouble(SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue()), "0")
				.toString());

		if (valorAprobado > Double.parseDouble(registro.getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()).toString())) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4242"));
			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), 0);
		} else {
			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), valorAprobado);
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control VALORAPROBADO en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarVALORAPROBADOC(int rowNum) {
		// <CODIGO_DESARROLLADO>

		Double valorAprobado;

		rubroPresupuestal = SysmanFunciones.nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue()), "").toString();

		if (SysmanFunciones.validarVariableVacio(rubroPresupuestal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB3654));

			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), 0);

			return;
		}

		valorAprobado = Double
				.parseDouble(SysmanFunciones
						.nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue()), "0")
						.toString());
		
		valorSolicitadoAux = Double
                                .parseDouble(SysmanFunciones
                                                .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                                                .get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()), "0")
                                                .toString());

		if (valorAprobado > valorSolicitadoAux) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4242"));
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), 0);
		} else {
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), valorAprobado);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control VALORDISMINUIDO en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */

	public void cambiarVALORDISMINUIDOC(int rowNum) {
		// <CODIGO_DESARROLLADO>

		Double valorDisminuidoPpto = 0.0;

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put("TIPOT", tipoT);

		param.put("CLASET", claseT);

		param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		param.put(GeneralParameterEnum.CONSECUTIVO.getName(), listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()).toString());

		try {
			rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL008.getValue())
							.getUrl(),
							param));

			if (rs != null) {

				valorDisminuidoPpto = Double
						.parseDouble(SysmanFunciones.nvl(rs.getCampos().get("VALOR_DISMINUIDO_PPTO"), "0").toString());
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		valorSolicitado = Double.valueOf(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()).toString());

		valorDisminuido = Double.valueOf(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.VALORDISMINUIDO.getValue()).toString());

		if (valorDisminuido > valorDisminuidoPpto) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORDISMINUIDO.getValue(), 0);
			JsfUtil.agregarMensajeAlerta("El valor disminuido no puede superar el valor disminuido desde presupuesto");
		} else {
			valorAprobado = valorSolicitado - valorDisminuido;

			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORAPROBADO.getValue(), valorAprobado);

		}

		// </CODIGO_DESARROLLADO>
	}

	private boolean controlarSaldoActividad(Double valorSolicitadoAux) {

		Double saldoActividad = 0.0;
		Double valorSolicitadoActividad = 0.0;
		Double valorProgramadoActividad = 0.0;
		Double valorDisminuidoActividad = 0.0;

		if (valorSolicitadoAux > saldoRubroAux) {
		    
		    
		        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		        String valorSolicitadocov = decimalFormat.format(valorSolicitadoAux);
		    
			JsfUtil.agregarMensajeInformativoDialogo(
					idioma.getString(mensajeErrRubro).replace(reemplazoValorSolicitud, valorSolicitadocov.toString())
					.replace(reemplazoSaldoRubro, saldoRubroAux.toString()));

			bloqueaImprimir = true;

			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

			valorSolicitadoAux = 0.0;

			return false;
		}

		Map<String, Object> param = new TreeMap<>();
		Map<String, Object> paramProgramado = new TreeMap<>();

		param.put(FrmsubdnovedadesproyectosControladorEnum.METAPRODUCTO.getValue(), codigoMetaProducto);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
		param.put(GeneralParameterEnum.NOVEDAD.getName(), codigo);
		param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		paramProgramado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		paramProgramado.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		paramProgramado.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
		paramProgramado.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		try {

			rsProgramado = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL005.getValue())
							.getUrl(),
							paramProgramado));
			 
			if ("CDP".equals(tipoT)) {
				rs = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FrmsubdnovedadesproyectosControladorUrlEnum.URL007.getValue())
						.getUrl(),
						param));

			} else {
				rs = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FrmsubdnovedadesproyectosControladorUrlEnum.URL006.getValue())
						.getUrl(),
						param));
			}

			
			if (rs != null) {

				valorSolicitadoActividad = Double
						.parseDouble(SysmanFunciones
								.nvl(rs.getCampos()
										.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()), "0")
								.toString());

				valorDisminuidoActividad = Double
						.parseDouble(SysmanFunciones
								.nvl(rs.getCampos()
										.get(FrmsubdnovedadesproyectosControladorEnum.VALORDISMINUIDO.getValue()), "0")
								.toString());

			}
			
			if (rsProgramado != null) {

				valorProgramadoActividad = Double.parseDouble(SysmanFunciones
						.nvl(rsProgramado.getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.VALORPROGRAMADO.getValue()), "0")
						.toString());
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// revisar operación cálculo saldoActividad

		saldoActividad = valorProgramadoActividad - (valorSolicitadoActividad - valorDisminuidoActividad);

		if (Double.doubleToRawLongBits(saldoActividad) == 0) {
			JsfUtil.agregarMensajeInformativoDialogo(idioma.getString("TB_TB4235"));

			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

			return false;
		} else {
			if (valorSolicitadoAux > saldoActividad) {
				DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		        String valorsaldoActividadcov = decimalFormat.format(saldoActividad);
				JsfUtil.agregarMensajeInformativoDialogo(
						idioma.getString("TB_TB4236").replace("#$saldoActividad#$", valorsaldoActividadcov));

				registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

				valorSolicitadoAux = 0.0;

				return false;
			} else {
				registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(),
						valorSolicitadoAux);
			}
		}
		return true;
	}

	private boolean controlarSaldoActividadC(Double valorSolicitadoAux, int indice) {

		Double saldoActividad = 0.0;
		Double valorSolicitadoActividad = 0.0;
		Double valorDisminuidoActividad = 0.0;
		Double valorProgramadoActividad = 0.0;
		Double valorDisminuidoDigitado = 0.0;

		valorDisminuidoDigitado = Double.parseDouble(SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.VALORDISMINUIDO.getValue()), "0")
				.toString());

		if (valorSolicitadoAux > saldoRubroAux) {

			JsfUtil.agregarMensajeInformativoDialogo(
					idioma.getString(mensajeErrRubro).replace(reemplazoValorSolicitud, valorSolicitadoAux.toString())
					.replace(reemplazoSaldoRubro, saldoRubroAux.toString()));

			bloqueaImprimir = true;

			listaInicial.getDatasource().get(indice % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

			valorSolicitadoAux = 0.0;

			return false;
		}

		Map<String, Object> param = new TreeMap<>();
		Map<String, Object> paramProgramado = new TreeMap<>();

		param.put(FrmsubdnovedadesproyectosControladorEnum.METAPRODUCTO.getValue(), codigoMetaProducto);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
		param.put(GeneralParameterEnum.NOVEDAD.getName(), codigo);

		paramProgramado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		paramProgramado.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
		paramProgramado.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
		paramProgramado.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		try {

			rsProgramado = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL005.getValue())
							.getUrl(),
							paramProgramado));

			if ("CDP".equals(tipoT)) {
				rs = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FrmsubdnovedadesproyectosControladorUrlEnum.URL007.getValue())
						.getUrl(),
						param));

			} else {
				rs = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FrmsubdnovedadesproyectosControladorUrlEnum.URL006.getValue())
						.getUrl(),
						param));
			}

			if (rs != null) {

				valorSolicitadoActividad = Double
						.parseDouble(SysmanFunciones
								.nvl(rs.getCampos()
										.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()), "0")
								.toString());

				valorDisminuidoActividad = Double
						.parseDouble(SysmanFunciones
								.nvl(rs.getCampos()
										.get(FrmsubdnovedadesproyectosControladorEnum.VALORDISMINUIDO.getValue()), "0")
								.toString());

			}
			if (rsProgramado != null) {

				valorProgramadoActividad = Double.parseDouble(SysmanFunciones
						.nvl(rsProgramado.getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.VALORPROGRAMADO.getValue()), "0")
						.toString());
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		saldoActividad = valorProgramadoActividad
				- (valorSolicitadoActividad - valorDisminuidoDigitado - valorDisminuidoActividad);

		if (Double.doubleToRawLongBits(saldoActividad) == 0) {
			JsfUtil.agregarMensajeInformativoDialogo(idioma.getString("TB_TB4235"));

			listaInicial.getDatasource().get(indice % 10).getCampos()
			.put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

			return false;
		} else {
			if (valorSolicitadoAux > saldoActividad) {
				JsfUtil.agregarMensajeInformativoDialogo(idioma.getString("TB_TB4236"));

				listaInicial.getDatasource().get(indice % 10).getCampos()
				.put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), 0);

				valorSolicitadoAux = 0.0;

				return false;
			}

			else {
				listaInicial.getDatasource().get(indice % 10).getCampos()
				.put(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue(), valorSolicitadoAux);
			}
		}
		return true;
	}

	public void cambiarDependencia() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	// </CODIGO_DESARROLLADO>
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>

		getCertificadoNoFondos(FORMATOS.PDF);

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirVerResumen(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.PROYECTO.getName(),
					reg.getCampos().get(GeneralParameterEnum.PROYECTO.getName()));
			param.put(GeneralParameterEnum.ACTIVIDAD.getName(),
					reg.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()));
			param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(),
					reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()));

			Registro regCompAct = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL5768.getValue())
							.getUrl(),
							param));

			actualizarDetalles(regCompAct);

			param.put(GeneralParameterEnum.ACTIVIDAD.getName(),
					reg.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()));
			param.put(GeneralParameterEnum.ID_PLAN.getName(), reg.getCampos().get(cIdMeta));

			Registro regProy = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL6471.getValue())
							.getUrl(),
							param));
			actualizarPlan(regProy);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void actualizarDetalles(Registro reg) {

		String valProgramadoComponente;
		String compValorTotal;
		String compValorSolicitado;
		String cantActividad;
		String cantEjeActividad;
		String cadenaFormato = "##,##0.00";

		pieComponente = reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.NOMBRECOMPONENTE.getValue())
				.toString();
		pieActividad = reg.getCampos().get("NOMBREACTIVIDAD").toString();

		String resultado = new DecimalFormat(cadenaFormato)
				.format(reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.SALDO.getValue()));

		valProgramadoComponente = new DecimalFormat(cadenaFormato).format(reg.getCampos().get("PROGRAMADO"));

		compValorTotal = new DecimalFormat(cadenaFormato).format(reg.getCampos().get("TOTAL"));

		compValorSolicitado = new DecimalFormat(cadenaFormato).format(reg.getCampos().get("SOLICITADO"));

		cantActividad = new DecimalFormat(cadenaFormato)
				.format(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()));

		cantEjeActividad = new DecimalFormat(cadenaFormato)
				.format(reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJE.getValue()));

		String resulRestaCantidad = new DecimalFormat(cadenaFormato).format(reg.getCampos().get("SALDOCANTIDAD"));

		infoDetalles = ""
				+ reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.NOMBRECOMPONENTE.getValue()).toString()
				+ "--->VIGENCIA: " + reg.getCampos().get("VIGENCIA").toString() + "\n"
				+ "PROGRAMADO---------------------------> " + valProgramadoComponente + "\n"
				+ "VALOR COMPONENTE-------------------> " + compValorTotal + "\n"
				+ "VALOR SOLICITADO---------------------> " + compValorSolicitado + "\n"
				+ "SALDO SOLICITADO---------------------> " + resultado + "\n" + "CANTIDAD ACTIVIDAD PROGRAMADA-> "
				+ cantActividad + "\n" + "CANTIDAD ACTIVIDAD EJECUTADA----> " + cantEjeActividad + "\n"
				+ "SALDO CANTIDAD ACTIVIDAD---------> " + resulRestaCantidad + "\n";
	}

	public void actualizarPlan(Registro reg) {
		String cantProgramadaMeta;
		String cantEjecutadaMeta;
		String valEjecutadoMeta;

		String cadenaFormato = "##,##0.00";
		// COMPONENTE COLUMNA 4
		// COMPONENTE COLUMNA 10

		// COLUMNA 6
		pieMetaProd = SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();

		pieSector = SysmanFunciones.nvl(reg.getCampos().get("NOMBRE_SECTOR"), "").toString();
		pieLineaBase = reg.getCampos().get("LB").toString();

		pieMetaTotal = reg.getCampos().get("META").toString();

		pieMetaVigencia = reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_PROGRAMADA.getValue())
				.toString();

		pieMetaEjecutada = reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJECUTADA.getValue())
				.toString();

		switch (SysmanFunciones.nvl(reg.getCampos().get("TIPO_META_INDICADOR"), "").toString()) {
		case "MI":
			tipoMetaProducto = idioma.getString("TB_TB3670");
			break;
		case "MM":
			tipoMetaProducto = idioma.getString("TB_TB3671");
			break;
		case "MR":
			tipoMetaProducto = idioma.getString("TB_TB3672");
			break;
		default:
			tipoMetaProducto = "";
			break;
		}
		cantProgramadaMeta = new DecimalFormat(cadenaFormato)
				.format(reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_PROGRAMADA.getValue()));

		new DecimalFormat(cadenaFormato).format(valorProgramadoMetaOtros);

		String resultadoSuma = new DecimalFormat(cadenaFormato)
				.format(reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.VALORPROGRAMADO.getValue()));

		cantEjecutadaMeta = new DecimalFormat(cadenaFormato)
				.format(reg.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJECUTADA.getValue()));

		valEjecutadoMeta = new DecimalFormat(cadenaFormato).format(reg.getCampos().get("VALOR_EJECUTADO_META"));

		infoDetallesPlan = "META PRODUCTO---> " + reg.getCampos().get("NOMBRE") + "\n" + "TIPO META------------------> "
				+ tipoMetaProducto + "\n" + "CANTIDAD PROGRAMADA--> " + cantProgramadaMeta + "\n"
				+ "VALOR PROGRAMADO------> " + resultadoSuma + "\n" + "CANTIDAD EJECUTADA-----> " + cantEjecutadaMeta
				+ "\n" + "VALOR EJECUTADO---------> " + valEjecutadoMeta + "\n";
	}
	/**
	 * @author ljdiaz
	 * Este metodo conuslta las clasificaciones para el año seleccionado, para consigo taer la lista que de terminara los campos que se inhabilitaran
	 */
	private void obtenerClasificacionAno(String cuenta) throws SystemException {
		// TODO Auto-generated method stub
	     ano = vigencia;

	    modelo =   ejbSysmanUtl.consultarModeloAno(compania,ano);
            modeloN =  modelo;
            campobloqueadosector =  true;

            int aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "001",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadosector =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadosector =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadosector   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "002",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadoprograma =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadoprograma =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadoprograma   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "003",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadosubprograma =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadosubprograma =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadosubprograma   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "004",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodproducto =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodproducto =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodproducto   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "005",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodbpin =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodbpin =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodbpin   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "006",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodCCPET =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodCCPET =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodCCPET   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "007",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodCPCDANE =  true; 
            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodCPCDANE =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodCPCDANE   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "008",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodunidadejecutora =  true; 

            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodunidadejecutora =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodunidadejecutora   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "009",cuenta);

            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodfuente =  true; 
            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodfuente =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodfuente   =  false; 
            }

            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "010",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocodCCPETregalias =  true; 
            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocodCCPETregalias =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocodCCPETregalias   =  false; 
            }
            
            aplicacionCuenta =   ejbSysmanUtl.aplicacionCuenta(compania,ano, "012",cuenta);
            if(aplicacionCuenta ==  0) { //bloqueado
                campobloqueadocoddetalleSectorial =  true; 
            }
            else if(aplicacionCuenta ==  1){//bloqueado
                campobloqueadocoddetalleSectorial =  true; 
            }
            else if(aplicacionCuenta ==  2){ //habilitado
                campobloqueadocoddetalleSectorial   =  false; 
            }
            
    
	}
	
	public void validacionVigencia() {
	    String mensaje = "";
	    
	    // Validar vigencia de Componente
	    if("NO".equalsIgnoreCase(filtraPrimeroMetaProd)) {
	        // Solo validar si se ha seleccionado un componente y tiene vigencia
	        if(vigencia != null && vigenciaComponente != null && !vigenciaComponente.isEmpty()) {
	            if(!vigencia.equals(vigenciaComponente)) {
	                mensaje = "El componente";
	                JsfUtil.agregarMensajeAlerta(
	                    idioma.getString("TB_TB4499")
	                        .replace("$#origen#$", mensaje)
	                        .replace("$#vigencia#$", vigencia));
	            }
	        }
	    }
	    
	    // Validar vigencia de Meta producto 
	    if("SI".equalsIgnoreCase(filtraPrimeroMetaProd)) {
	        // Solo validar si se ha seleccionado una meta producto 
	        if(vigencia != null && vigenciaMetaP != 0) {
	            if(!vigencia.equals(String.valueOf(vigenciaMetaP))) {
	                mensaje = "La meta producto";
	                JsfUtil.agregarMensajeAlerta(
	                    idioma.getString("TB_TB4499")
	                        .replace("$#origen#$", mensaje)
	                        .replace("$#vigencia#$", vigencia));
	            }
	        }
	    }
	}
	/**
	 * Valida si esta habilitada la restricción de componentes y metas por vigencia (anio).
	 * @return true si la restricción esta activa (EXISTE = "1.0"), false en caso contrario
	 * CC:3311 CFBARRERA
	 */
	private boolean validarComponenteYMetaPorAnio() {
	    Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put("TIPOT", tipoT);
	    
	    try {
	        Registro rsExiste = RegistroConverter.toRegistro(
	            requestManager.get(
	                UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                        FrmsubdnovedadesproyectosControladorUrlEnum.URL218019.getValue())
	                    .getUrl(),
	                param
	            )
	        );
	        
	        return rsExiste != null && rsExiste.getCampos().get("EXISTE") != null
	            && "1.0".equals(rsExiste.getCampos().get("EXISTE").toString());
	        
	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	        return false; 
	    }
	}
	

	/**
	 * @author ljdiaz
	 * Este metodo consulta el modelo del año seleccionado, llamado en el controlador como vigencia.
	 */
	private void obtenerModeloAno() {
		// TODO Auto-generated method stub
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), vigencia);
		try {
			anoSeleccinadoModelo = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL009.getValue())
							.getUrl(),
							param));
			System.out.println("modelo: "+anoSeleccinadoModelo.getCampos().get("MODELO"));
			if (anoSeleccinadoModelo != null) {
				// cargar los campos a mostrar de acuerdo al modelo
				if(anoSeleccinadoModelo.getCampos().get("MODELO").toString().equals(FrmsubdnovedadesproyectosControladorEnum.ARBOL.getValue())) {
					sectorComboMostar = true;
					programaMostar = true;
					subprogramaMostar = true;
					codigoProductoMostar = true;
					codigoBpinMostar = true;
					codigoCCPETMostar = true;
					codigoCPCDANEMostar = true;
					codigoUnidadEjecutoraMostar = true;
					codigoFuenteMostar = true;
					codigoCCPETRegaliasMostar = true;
					codigodetalleSectorialMostar = true;
					cargarListacodigoCCPET();
					cargarListacodigoCCPETE();
					cargarListasectorCombo();
					cargarListasectorComboE();
					cargarListaprograma();
					cargarListaprogramaE();
					cargarListasubPrograma();
					cargarListasubProgramaE();
					cargarListacodigoProducto();
					cargarListacodigoProductoE();
					cargarListacodigoBpin();
					cargarListacodigoBpinE();
					cargarListacodigoCPCDANE();
					cargarListacodigoCPCDANEE();
					cargarListacodigoUnidadEjecutora();
					cargarListacodigoUnidadEjecutoraE();
					cargarListacodigoFuente();
					cargarListacodigoFuenteE();
					cargarListacodigoCCPETRegalias();
					cargarListacodigoCCPETRegaliasE();
					cargarListacodigoDetalleSectorial();
					cargarListacodigoDetalleSectorialE();
					
				}else if(anoSeleccinadoModelo.getCampos().get("MODELO").toString().equals(FrmsubdnovedadesproyectosControladorEnum.CONAUXILIAR.getValue())) {
					//se ocultan los campos que se muestren correspondientes al modelo arbol
					sectorComboMostar = false;
					programaMostar = false;
					subprogramaMostar = false;
					codigoUnidadEjecutoraMostar = false;
					codigoBpinMostar = false;
					codigoCCPETMostar = false;
					codigoCPCDANEMostar = false;
					codigoProductoMostar = false;
					codigoFuenteMostar = false;
					//se muestran los campos correspondientes al modelo Con cupo
					codigoCPCMostar = true;
					fuenteCuipoMostar = true;
					productoCuipoMostar = true;
					codigoCCPETCuipoMostar = true;
					codigoBpinCampoMostrar = true;
					cargarListacodigoCPC();
					cargarListacodigoCPCE();
					cargarListafuenteCuipo();
					cargarListafuenteCuipoE();
					cargarListaproductoCuipo();
					cargarListaproductoCuipoE();
					cargarListacodigoCCPETCuipo();
					cargarListacodigoCCPETCuipoE();
				}else { // caso en el que sea sin cupo
					//se ocultan los campos que se muestren correspondientes al modelo arbol
					sectorComboMostar = false;
					programaMostar = false;
					subprogramaMostar = false;
					codigoUnidadEjecutoraMostar = false;
					codigoBpinMostar = false;
					codigoCCPETMostar = false;
					codigoCPCDANEMostar = false;
					codigoProductoMostar = false;
					codigoFuenteMostar = false;
					//se ocultan los campos que se muestren correspondientes al modelo con Auxiliar
					codigoCPCMostar = false;
					fuenteCuipoMostar = false;
					productoCuipoMostar = false;
					codigoCCPETCuipoMostar = false;
					codigoBpinCampoMostrar = false;
				}
				System.out.println("respuesta modelo: "+SysmanFunciones.nvl(anoSeleccinadoModelo.getCampos().get("MODELO"), "").toString());
			} else {
				// esconder los campos que no van de acuerdo al modelo
				JsfUtil.agregarMensajeError("NO HAY DATOS PARA EL AÑO SELECIONADO");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}		
	}
	
	public void seleccionarFilaProyecto(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.PROYECTO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		proyecto = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.PROYECTO.getName()), "")
				.toString();
		nombreProyecto = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "").toString();
		periodicidadProyecto = SysmanFunciones.nvl(registroAux.getCampos().get("PERIOCIDAD"), "0").toString();
		programaProyectoAux = SysmanFunciones.nvl(registroAux.getCampos().get("PROGRAMA"), "").toString();
		codigoBpim = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGOBPIM"), "").toString();

		listaComponente = null;
		listaFuente = null;
		listaRubroPresupuestal = null;

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), null);

		registro.getCampos().put(GeneralParameterEnum.SECTOR.getName(), null);

		registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), null);

		registro.getCampos().put(cIdMeta, null);

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue(), null);

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue(), null);

		registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);

		registro.getCampos().put("SOLOCOMPONENTE", null);

		registro.getCampos().put("CANTIDAD_PLAN", null);

		registro.getCampos().put("CANTIDAD", null);
		
		registro.getCampos().put("CODIGOBPIN",codigoBpim);

		naturalezaPlanPptal = null;

		if ("SI".equals(filtraPrimeroMetaProd)) {
			cargarListaMetaProducto();
		}

		else {
			cargarListaComponente();
		}

		cargarListaPeriodo();
		cargarListaFuente();
	}

	public void seleccionarFilaProyectoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		proyecto = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		cargarListaComponenteE();

		cargarListaPeriodo();
		cargarListaFuenteE();

	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuente
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 * @throws SysmanException 
	 */
	public void seleccionarFilaFuente(SelectEvent event) throws SysmanException {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOSRUBRO.getValue()));

		fuenteRecursos = SysmanFunciones.nvl(
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOSRUBRO.getValue()),
				"").toString();

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue(), null);

		cargarListaRubroPresupuestal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuente
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteE(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOSRUBRO.getValue()),
				"").toString();

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue(), auxiliar);

		fuenteRecursos = SysmanFunciones.nvl(
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOSRUBRO.getValue()),
				"").toString();

		validarProyecto(indice);

		if ("NO".equals(filtraPrimeroMetaProd)) {
			validarActividad(indice);
		}

		cargarListaRubroPresupuestalE();

	}

	public void seleccionarFilaComponente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()));

		componente = SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "")
				.toString();

		componenteValorTotal = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("TOTAL"), "0").toString());

		componenteValorSolicitado = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("SOLICITADO"), "0").toString());

		nombreComponente = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.NOMBRECOMPONENTE.getValue()),
						"")
				.toString();

		vigenciaComponente = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()), "").toString();

		valorProgramadocomponente = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("PROGRAMADO"), "0").toString());

		valorEjecutadoComponente = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("VALOREJECUTADO"), "0").toString());

		saldoComponente = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.SALDO.getValue()), "0")
				.toString());

		tipoComponente = SysmanFunciones.nvl(registroAux.getCampos().get("TIPOCOMPONENTE"), "").toString();

		obtejoComponente = SysmanFunciones.nvl(registroAux.getCampos().get("OBJETO"), "").toString();

		// COMPONENTE BEFOREUPDATE
		verificarComponente();

		boolean key = tipoT == null || claseT == null || codigo == null || dependencia == null;

		if (key || componente == null || proyecto == null) {
			tipoT = "";
			claseT = "";
			codigo = "0";
			dependencia = "";
			proyecto = "";
			componente = "";
		}

		String valorProgramadoC = String.valueOf(valorProgramadocomponente);

		if (valorProgramadoC == null) {
			valorProgramadocomponente = -1.0;
		}

		registro.getCampos().put("SOLOCOMPONENTE", soloComponente);

		registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), null);

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue(), null);

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue(), null);

		registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);

		cargarListaActividad();
		cargarListaActividad1();

		verificarSoloComponente();

		verificarComponenteValorTotal();

		cambiarinfoDetalles();
		cambiarinfoDetallesPlan();
		
		 if("NO".equalsIgnoreCase(filtraPrimeroMetaProd)) {
			   validacionVigencia();
		   }

	}

	private void verificarComponente() {
		actividad = String.valueOf(registro.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()));
		idMetaProducto = (String) registro.getCampos().get(cIdMeta);

		if (actividad == null) {
			actividad = "";
		}
		if (idMetaProducto == null) {
			idMetaProducto = "";
		}

	}

	private void verificarSoloComponente() {
		if (soloComponente) {
			valComponente = componenteValorTotal;

			especificacion = (String) registro.getCampos().get("ESPECIFICACION");

			especificacion = obtejoComponente;
		} else {
			valComponente = componenteValorTotal;
		}
	}

	private void verificarComponenteValorTotal() {
		if (componenteValorTotal <= 0) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2439"));
		}
	}

	public void seleccionarFilaComponenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "")
				.toString();
		registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()));

		tipoComponente = SysmanFunciones.nvl(registroAux.getCampos().get("TIPOCOMPONENTE"), "").toString();
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()));
		componente = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "")
				.toString();

		componenteE = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "")
				.toString();
		cargarListaActividad1E();
		cargarListaMetaProductoE();

	}

	public void seleccionarFilaActividad1(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()));

		actividad = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()), "")
				.toString();
		actividadE = actividad;

		cantidadActividad = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()), "0").toString());
		cantidadEjeActividad = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJE.getValue()), "0")
				.toString());
		costoTotalActividad = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("COSTOTOTAL"), "0").toString());

		// ACTIVIDAD_AFTERUPDATE

		String valorActividad = "0";
		List<Registro> rs;

		proyecto = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.PROYECTO.getName()), "")
				.toString();

		componente = SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "")
				.toString();
		actividad = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()), "")
				.toString();

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		param.put(GeneralParameterEnum.CLASE.getName(), claseT);

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

		try {
			rs = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL25351.getValue())
							.getUrl(),
							param));

			if (!rs.isEmpty()) {

				valorActividad = SysmanFunciones.nvl(rs.get(0).getCampos().get("TOTAL_VALORAPROBADO"), "0").toString();
			}
			

			 List<Registro> rs1 = RegistroConverter
			            .toListRegistro(requestManager.getList(
			                    UrlServiceUtil.getInstance()
			                    .getUrlServiceByUrlByEnumID(
			                            FrmsubdnovedadesproyectosControladorUrlEnum.URL131037.getValue()) 
			                    .getUrl(),
			                    param));

			 
			 String valorSolicitado = "0";
			 
			 if (!rs1.isEmpty()) {
			     valorSolicitado = SysmanFunciones.nvl(rs1.get(0).getCampos().get("TOTAL_VALORSOLICITADO"), "0").toString();
			 }

			 Double valorSolicitadoDouble = Double.valueOf(valorSolicitado);
			 int valor = (int) (costoTotalActividad - valorSolicitadoDouble);

			Double totVal = Double.valueOf(valorActividad);
			
			if (totVal >= (costoTotalActividad - 1)) {
				
				DecimalFormat df = new DecimalFormat("#,###.##");

				String mensaje = "La actividad "+ actividad + " no tiene saldo disponible.\n" +
				                 "Costo total: " + df.format(costoTotalActividad) + "\n" +
				                 "Valor solicitado total: " + df.format(Double.valueOf(valorSolicitado)) + "\n" +
				                 "Saldo: " + df.format(valor);

				JsfUtil.agregarMensajeAlerta(mensaje);

				
				registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), "");

				return;
			}

			registro.getCampos().put(cIdMeta, null);
			cargarListaMetaProducto();

			cambiarinfoDetalles();

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void seleccionarFilaActividad(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()));

		actividad = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()), "")
				.toString();

		cantidadActividad = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()), "0").toString());
		cantidadEjeActividad = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJE.getValue()), "0")
				.toString());
		costoTotalActividad = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("COSTOTOTAL"), "0").toString());

		// ACTIVIDAD_AFTERUPDATE

		String valorActividad = "0";
		List<Registro> rs;

		proyecto = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.PROYECTO.getName()), "")
				.toString();

		componente = SysmanFunciones
				.nvl(registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "")
				.toString();
		actividad = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()), "")
				.toString();

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(FrmsubdnovedadesproyectosControladorEnum.TIPO.getValue(), tipoT);

		param.put(GeneralParameterEnum.CLASE.getName(), claseT);

		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

		param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);

		param.put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

		param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

		try {
			rs = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL25351.getValue())
							.getUrl(),
							param));

			if (!rs.isEmpty()) {

				valorActividad = SysmanFunciones.nvl(rs.get(0).getCampos().get("TOTAL_VALORAPROBADO"), "0").toString();
			}

			Double totVal = Double.valueOf(valorActividad);

			if (totVal >= (costoTotalActividad - 1)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2441"));
				actividad = "";

				registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), "");

				return;
			}

			registro.getCampos().put(cIdMeta, null);
			cargarListaMetaProducto();

			cambiarinfoDetalles();

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void seleccionarFilaActividad1E(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()), "")
				.toString();
		actividadE = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()), "")
				.toString();

		actividad = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()), "")
				.toString();

		registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue()));

		cantidadActividad = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()), "0").toString());
		cantidadEjeActividad = Double.valueOf(SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJE.getValue()), "0")
				.toString());
		costoTotalActividad = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("COSTOTOTAL"), "0").toString());

		cargarListaMetaProductoE();

	}

	public void seleccionarFilaRubroPresupuestal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.RUBROPRESUPUESTAL.getValue(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CODIGO.getValue()).toString());
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.AUXILIAR.getValue(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.AUXILIAR.getValue()).toString());
		
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.REFERENCIA.getValue(),
				registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.REFERENCIA.getValue()).toString());
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.CENTRO_COSTO.getValue(), registroAux
				.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.CENTRO_COSTO.getValue()).toString());
		registro.getCampos().put("CLASECLASIFICADOR",SysmanFunciones.nvl(registroAux.getCampos().get("CLASECLASIFICADOR"),"").toString());
		naturalezaPlanPptal = SysmanFunciones.nvl(registroAux.getCampos().get("NATURALEZA"), "").toString();
		String saldoRubroAuxn = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.SALDO.getValue()), "")
				.toString();
		saldoRubroAuxn = saldoRubroAuxn.replace(",","");
		saldoRubroAux = Double.parseDouble(saldoRubroAuxn == null ? "0" : saldoRubroAuxn);
		
		String valorSolicitadon = registro.getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()) == null ? "0"
						: registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue())
						.toString();
		valorSolicitado = Double.parseDouble(valorSolicitadon == null ? "0" : valorSolicitadon);
		String valorS = String.valueOf(valorSolicitado);

		if (valorS == null) {
			valorSolicitado = 0.0;
		}

		if (!"0.0".equals(Double.toString(valorSolicitado))) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2442"));
		}
		//carga los datos sector, programa y sub programa del rubro selecionado si es ARBOL
		
		Registro regTemp = null;
		
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		param.put(GeneralParameterEnum.ANIO.getName(), vigencia);

		param.put(FrmsubdnovedadesproyectosControladorEnum.CUENTA.getValue(), registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ID.getValue()).toString());
		
		try {
		    listaTipoClasificador = RegistroConverter
                                    .toListRegistro(
                                                    requestManager.getList(
                                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
									FrmsubdnovedadesproyectosControladorUrlEnum.URL017.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		for (Registro option : listaTipoClasificador) {

                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("001")) {
                        sectorCombo       =  option.getCampos().get("TIPOCLASIFICADOR").toString();  
                    }else if(regTemp != null && regTemp.getCampos().get("SECTOR") != null) {
                        sectorCombo       = regTemp.getCampos().get("SECTOR").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("002")) {
                            programa       =  option.getCampos().get("TIPOCLASIFICADOR").toString();        
                    }else if(regTemp != null && regTemp.getCampos().get("PROGRAMA") != null) {
                            programa       = regTemp.getCampos().get("PROGRAMA").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("003")) {
                            subPrograma       =  option.getCampos().get("TIPOCLASIFICADOR").toString();     
                    }else if(regTemp != null && regTemp.getCampos().get("SUBPROGRAMA") != null) {
                            subPrograma       = regTemp.getCampos().get("SUBPROGRAMA").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("004")) {
                            codigoProducto       =  option.getCampos().get("TIPOCLASIFICADOR").toString();  
                    }else if(regTemp != null && regTemp.getCampos().get("COD_PROD_CUIPO") != null) {
                            codigoProducto       = regTemp.getCampos().get("COD_PROD_CUIPO").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("005")) {
                            codigoBpin       =  option.getCampos().get("TIPOCLASIFICADOR").toString();      
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGO_BPIN") != null) {
                            codigoBpin       = regTemp.getCampos().get("CODIGO_BPIN").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("006")) {
                            codigoCCPET       =  option.getCampos().get("TIPOCLASIFICADOR").toString();     
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGO_CCPET") != null) {
                            codigoCCPET       = regTemp.getCampos().get("CODIGO_CCPET").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("007")) {
                            codigoCPCDANE       =  option.getCampos().get("TIPOCLASIFICADOR").toString();   
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGO_CPC") != null) {
                            codigoCPCDANE       = regTemp.getCampos().get("CODIGO_CPC").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("008")) {
                        codigoUnidadEjecutora       =  option.getCampos().get("TIPOCLASIFICADOR").toString();   
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGOUNIDADEJE") != null) {
                        codigoUnidadEjecutora       = regTemp.getCampos().get("CODIGOUNIDADEJE").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("009")) {
                        codigoFuente       =  option.getCampos().get("TIPOCLASIFICADOR").toString();
                    }else if(regTemp != null && regTemp.getCampos().get("FUENTE_CUIPO") != null) {
                        codigoFuente       =  regTemp.getCampos().get("FUENTE_CUIPO").toString();
                    };

                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("010")) {
                        codigoCCPETRegalias       =  option.getCampos().get("TIPOCLASIFICADOR").toString(); 
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGOCCPETREGA") != null) {
                        codigoCCPETRegalias       = regTemp.getCampos().get("CODIGOCCPETREGA").toString();
                    };


            }


            registro.getCampos().put("SECTORRUBRO", sectorCombo);
            registro.getCampos().put("PROGRAMARUBRO", programa);
            registro.getCampos().put("SUBPROGRAMARUBRO", subPrograma);
            registro.getCampos().put("CODIGOPRODUCTO", codigoProducto);
            registro.getCampos().put("CODIGOBPIN",codigoBpin != null ? codigoBpin : codigoBpim);
            registro.getCampos().put("CODIGOCCPET",codigoCCPET);
            registro.getCampos().put("CODIGOCPCDANE",codigoCPCDANE);
            registro.getCampos().put("CODIGOUNIDADEJECUTORA",codigoUnidadEjecutora);
            registro.getCampos().put("CODIGOFUENTE",codigoFuente);
            registro.getCampos().put("CODIGOCCPETREGALIAS",codigoCCPETRegalias);


		
		
		bloqueaImprimir = true;
		cargarListasectorCombo();
		cargarListaprograma();
		cargarListasubPrograma();
		cargarListacodigoProducto();
		cargarListacodigoBpin();
		cargarListacodigoCCPET();
		cargarListacodigoCPCDANE();
		
	}


    public void cambiarPaisL() {
		// </CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarDepartamentoL() {
		// </CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCiudadL() {
		// </CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTipoComponente() {
		// </CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Rublo en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * Cambiar auxiliar, referencia y centro costo
	 * 
	 * @param rowNum indice de la rublo seleccionada
	 */
	public void cambiarRubroPresupuestalC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put("RUBROPRESUPUESTAL",rubroPresupuestal);
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put(FrmsubdnovedadesproyectosControladorEnum.AUXILIAR.getValue(), auxiliarGeneral);
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put(FrmsubdnovedadesproyectosControladorEnum.REFERENCIA.getValue(), referencia);
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put(FrmsubdnovedadesproyectosControladorEnum.CENTRO_COSTO.getValue(), centroCosto);
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put("CLASECLASIFICADOR",claseClasificador);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Fuente en la fila seleccionada dentro
	 * de la grilla
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFuenteC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		fuenteRecursos = SysmanFunciones.nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue()), "").toString();

		cargarListaFuenteE();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control MetaProducto en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarMetaProductoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaRubroPresupuestalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "").toString();

		rubroPresupuestal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();

		auxiliarGeneral = SysmanFunciones.nvl(registroAux.getCampos().get("AUXILIAR"), "").toString();

		referencia = SysmanFunciones.nvl(registroAux.getCampos().get("REFERENCIA"), "").toString();

		centroCosto = SysmanFunciones.nvl(registroAux.getCampos().get("CENTRO_COSTO"), "").toString();
		
		claseClasificador = SysmanFunciones.nvl(registroAux.getCampos().get("CLASECLASIFICADOR"),"").toString();

		String saldoRubroAuxn = SysmanFunciones
				.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.SALDO.getValue()), "")
				.toString();
		saldoRubroAuxn = saldoRubroAuxn.replace(".","");

		saldoRubroAux = Double.parseDouble(saldoRubroAuxn == null ? "0" : saldoRubroAuxn);
;
		String valorSolicitadon = registro.getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()) == null ? "0"
						: registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue())
						.toString();
		valorSolicitado = Double.parseDouble(valorSolicitadon == null ? "0" : valorSolicitadon);
		String valorS = String.valueOf(valorSolicitado);

		if (valorS == null) {
			valorSolicitado = 0.0;
		}

		if (!"0.0".equals(Double.toString(valorSolicitado))) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2442"));
		}
		//carga los datos sector, programa y sub programa del rubro selecionado si es ARBOL
		Registro regTemp = null;
                
                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                
                param.put(GeneralParameterEnum.ANIO.getName(), vigencia);

                param.put(FrmsubdnovedadesproyectosControladorEnum.CUENTA.getValue(), registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.ID.getValue()).toString());
                
                try {
                    listaTipoClasificador = RegistroConverter
                                    .toListRegistro(
                                                    requestManager.getList(
                                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                        FrmsubdnovedadesproyectosControladorUrlEnum.URL017.getValue())
                                                        .getUrl(),
                                                        param));
                } catch (SystemException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                }
                
                for (Registro option : listaTipoClasificador) {

                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("001")) {
                        sectorCombo       =  option.getCampos().get("TIPOCLASIFICADOR").toString();  
                    }else if(regTemp != null && regTemp.getCampos().get("SECTOR") != null) {
                        sectorCombo       = regTemp.getCampos().get("SECTOR").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("002")) {
                            programa       =  option.getCampos().get("TIPOCLASIFICADOR").toString();        
                    }else if(regTemp != null && regTemp.getCampos().get("PROGRAMA") != null) {
                            programa       = regTemp.getCampos().get("PROGRAMA").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("003")) {
                            subPrograma       =  option.getCampos().get("TIPOCLASIFICADOR").toString();     
                    }else if(regTemp != null && regTemp.getCampos().get("SUBPROGRAMA") != null) {
                            subPrograma       = regTemp.getCampos().get("SUBPROGRAMA").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("004")) {
                            codigoProducto       =  option.getCampos().get("TIPOCLASIFICADOR").toString();  
                    }else if(regTemp != null && regTemp.getCampos().get("COD_PROD_CUIPO") != null) {
                            codigoProducto       = regTemp.getCampos().get("COD_PROD_CUIPO").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("005")) {
                            codigoBpin       =  option.getCampos().get("TIPOCLASIFICADOR").toString();      
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGO_BPIN") != null) {
                            codigoBpin       = regTemp.getCampos().get("CODIGO_BPIN").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("006")) {
                            codigoCCPET       =  option.getCampos().get("TIPOCLASIFICADOR").toString();     
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGO_CCPET") != null) {
                            codigoCCPET       = regTemp.getCampos().get("CODIGO_CCPET").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("007")) {
                            codigoCPCDANE       =  option.getCampos().get("TIPOCLASIFICADOR").toString();   
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGO_CPC") != null) {
                            codigoCPCDANE       = regTemp.getCampos().get("CODIGO_CPC").toString();
                    };
                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("008")) {
                        codigoUnidadEjecutora       =  option.getCampos().get("TIPOCLASIFICADOR").toString();   
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGOUNIDADEJE") != null) {
                        codigoUnidadEjecutora       = regTemp.getCampos().get("CODIGOUNIDADEJE").toString();
                    };

                    if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("010")) {
                        codigoCCPETRegalias       =  option.getCampos().get("TIPOCLASIFICADOR").toString(); 
                    }else if(regTemp != null && regTemp.getCampos().get("CODIGOCCPETREGA") != null) {
                        codigoCCPETRegalias       = regTemp.getCampos().get("CODIGOCCPETREGA").toString();
                    };


            }


            registro.getCampos().put("SECTORRUBRO", sectorCombo);
            registro.getCampos().put("PROGRAMARUBRO", programa);
            registro.getCampos().put("SUBPROGRAMARUBRO", subPrograma);
            registro.getCampos().put("CODIGOPRODUCTO", codigoProducto);
            registro.getCampos().put("CODIGOBPIN",codigoBpin);
            registro.getCampos().put("CODIGOCCPET",codigoCCPET);
            registro.getCampos().put("CODIGOCPCDANE",codigoCPCDANE);
            registro.getCampos().put("CODIGOUNIDADEJECUTORA",codigoUnidadEjecutora);
            registro.getCampos().put("CODIGOFUENTE",codigoFuente);
            registro.getCampos().put("CODIGOCCPETREGALIAS",codigoCCPETRegalias);


                
                
                bloqueaImprimir = true;
                cargarListasectorCombo();
		
	}

	public void seleccionarFilaBarrioL(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("BARRIO_L", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

	}

	public void seleccionarFilaBarrioLE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("BARRIO_L", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

	}

	public void seleccionarFilaMetaProducto(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put(cIdMeta, SysmanFunciones.nvl(registroAux.getCampos().get(cIdPlanP), "").toString());

		codigoMetaProducto = SysmanFunciones.nvl(registroAux.getCampos().get(cIdPlanP), "").toString();

		indicador = SysmanFunciones.nvl(registro.getCampos().get(cIdMeta), "").toString();

		nombreMetaProducto = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();

		tipoMetaProducto = SysmanFunciones.nvl(registroAux.getCampos().get("TIPO_META_INDICADOR"), "").toString();

		cantidadProgramadaMeta = Double
				.valueOf(SysmanFunciones
						.nvl(registroAux.getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_PROGRAMADA.getValue()), "0")
						.toString());

		valorProgramadoMeta = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("VALOR_PROGRAMADO_META"), "0").toString());

		valorProgramadoMetaOtros = Double.valueOf(
				SysmanFunciones.nvl(registroAux.getCampos().get("VALOR_PROGRAMADO_META_OTROS"), "0").toString());

		cantidadEjecutadaMeta = Double
				.valueOf(SysmanFunciones
						.nvl(registroAux.getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.CANTIDAD_EJECUTADA.getValue()), "0")
						.toString());

		valorEjecutadoMeta = Double
				.valueOf(SysmanFunciones.nvl(registroAux.getCampos().get("VALOR_EJECUTADO_META"), "0").toString());

		vigenciaPlanIndicativo = Integer
				.parseInt(SysmanFunciones.nvl(registroAux.getCampos().get("VIGENCIA_PLAN_P"), "0").toString());

		vigenciaMetaP = Integer
				.parseInt(SysmanFunciones.nvl(registroAux.getCampos().get("VIGENCIA_META_P"), "0").toString());

		nombreSector = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE_SECTOR"), "").toString();

		sector = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();

		if ("SI".equals(filtraPrimeroMetaProd)) {

			actividad = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()), "")
					.toString();

			componente = SysmanFunciones
					.nvl(registroAux.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()),
							"")
					.toString();
			//tipoComponente = SysmanFunciones.nvl(registroAux.getCampos().get("TIPOCOMPONENTE"), "").toString();
			//Este cambio es temporal, debido a que en las entidades como tocancipá cuando se usa el parametro 
			//FILTRA PRIMERO META PRODUCTO EN SOLICITUD DE CDP, al no seleccionar fila del listaod de componentes, no
			//se asigna valor.
			//Es posible que haya habido perdida de datos, debido a que soporte indica que el proceso funcionaba correctamente.
			tipoComponente = "001";

			if ("".equals(codigoMetaProducto)) {
				registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), null);

				registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), null);

			} else {
				registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue(), componente);

				registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
				
				registro.getCampos().put("TIPOCOMPONENTE", tipoComponente);
			}
		}

		if ("".equals(codigoMetaProducto)) {
			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.SECTOR.getValue(), null);
		} else {
			registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.SECTOR.getValue(), nombreSector);
		}

		cambiarinfoDetallesPlan();
		
		   if("SI".equalsIgnoreCase(filtraPrimeroMetaProd)) {
			   validacionVigencia();
		   }
		
	}

	public void seleccionarFilaMetaProductoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cIdPlanP), "").toString();

	}
	public void seleccionarFilaCodigoCPC(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoCPC = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		
		registro.getCampos().put("CODIGOCPCCUIPO", codigoCPC);
	}
	public void seleccionarFilaCodigoCPCE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOCPCCUIPO", auxiliar);
	}
	public void seleccionarFilafuenteCuipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteCuipo = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		
		registro.getCampos().put("FUENTECUIPO", fuenteCuipo);
	}
	public void seleccionarFilafuenteCuipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteCuipoE = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("FUENTECUIPO", fuenteCuipoE);
	}
	public void seleccionarFilaproductoCuipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		productoCuipo = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		
		registro.getCampos().put("PRODUCTOCUIPO", productoCuipo);
	}
	public void seleccionarFilaproductoCuipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		productoCuipoE = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
	
		registro.getCampos().put("PRODUCTOCUIPO", productoCuipoE);
	}
	public void seleccionarFilacodigoCCPETCuipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoCCPETCuipo = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
	
		registro.getCampos().put("CODIGOCCPETCUIPO", codigoCCPETCuipo);
	}
	public void seleccionarFilacodigoCCPETCuipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoCCPETCuipoE = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		
		registro.getCampos().put("CODIGOCCPETCUIPO", codigoCCPETCuipoE);
	}
	public void seleccionarFilacodigoCPCDANE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoCPCDANE = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		
		registro.getCampos().put("CODIGOCPCDANE", codigoCPCDANE);
	}
	public void seleccionarFilacodigoCPCDANEE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoCPCDANEE = auxiliar;
		registro.getCampos().put("CODIGOCPCDANE", codigoCPCDANEE);
	}
	public void seleccionarFilacodigoUnidadEjecutora(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoUnidadEjecutora = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOUNIDADEJECUTORA", codigoUnidadEjecutora);
	}
	public void seleccionarFilacodigoUnidadEjecutoraE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoUnidadEjecutoraE = auxiliar;
		registro.getCampos().put("CODIGOUNIDADEJECUTORA", codigoUnidadEjecutoraE);
	}
	public void seleccionarFilacodigoFuente(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoFuente = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOFUENTE", codigoFuente);
	}
	public void seleccionarFilacodigoFuenteE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoFuenteE = auxiliar;
		registro.getCampos().put("CODIGOFUENTE", codigoFuenteE);
	}
	public void seleccionarFilacodigoCCPETRegalias(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoCCPETRegalias = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOCCPETREGALIAS", codigoCCPETRegalias);
	}
	public void seleccionarFilacodigoCCPETRegaliasE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoCCPETRegaliasE = auxiliar;
		registro.getCampos().put("CODIGOCCPETREGALIAS", codigoCCPETRegaliasE);
	}
	public void seleccionarFiladetalleSectorial(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoDetalleSectorial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGODETALLESECTORIAL", codigoDetalleSectorial);
	}
	public void seleccionarFiladetalleSectorialE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoDetalleSectorialE = auxiliar;
		registro.getCampos().put("CODIGODETALLESECTORIAL", codigoDetalleSectorialE);
	}
	public void seleccionarFilacodigoCCPET(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoCCPET = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOCCPET", codigoCCPET);
		registro.getCampos().put("CODIGOCPCDANE",null);
		cargarListacodigoCPCDANE();
		cargarListacodigoCPC();
		
	}
	public void seleccionarFilacodigoCCPETE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoCCPETE = auxiliar;
		registro.getCampos().put("CODIGOCCPET", codigoCCPETE);
		registro.getCampos().put("CODIGOCPCDANE",null);
		cargarListacodigoCPCDANEE();
		cargarListacodigoCPCE();
	}
	public void seleccionarFilacodigoBpin(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoBpin = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOBPIN", codigoBpin);
	}
	public void seleccionarFilacodigoBpinE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoBpinE = auxiliar;
		registro.getCampos().put("CODIGOBPIN", codigoBpinE);
	}
	public void seleccionarFilacodigoProducto(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		codigoProducto = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOPRODUCTO", codigoProducto);
	}
	public void seleccionarFilacodigoProductoE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		codigoProductoE = auxiliar;
		registro.getCampos().put("CODIGOPRODUCTO", codigoProductoE);
	}
	public void seleccionarFilasubPrograma(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		subPrograma = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("SUBPROGRAMARUBRO", subPrograma);
		cargarListacodigoProducto();
	}
	public void seleccionarFilasubProgramaE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		subProgramaE = auxiliar;
		registro.getCampos().put("CODIGOPRODUCTO", subProgramaE);
		cargarListacodigoProducto();
	}
	public void seleccionarFilaprograma(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		programa = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("PROGRAMARUBRO", programa);
		registro.getCampos().put("CODIGOPRODUCTO",null);
		cargarListacodigoProducto();
	}
	public void seleccionarFilaprogramaE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		programaE = auxiliar;
		registro.getCampos().put("PROGRAMARUBRO", programaE);
		registro.getCampos().put("CODIGOPRODUCTO",null);
		cargarListacodigoProductoE();
	}
	public void seleccionarFilasectorCombo(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		sectorCombo = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("SECTORRUBRO", sectorCombo);
		registro.getCampos().put("PROGRAMARUBRO",null);
		registro.getCampos().put("CODIGOPRODUCTO",null);
		cargarListaprograma();
		cargarListacodigoProducto();
		
	}
	public void seleccionarFilasectorComboE(SelectEvent event){
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		sectorComboE = auxiliar;
		registro.getCampos().put("SECTORRUBRO", sectorComboE);
		registroAux.getCampos().put("PROGRAMARUBRO",null);
                cargarListaprogramaE();	
		
	}
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		bloqueaProyecto = false;

		if ("NO".equals(filtraPrimeroMetaProd)) {
			bloqueaActividad = false;
			bloqueaActividadContinuo = false;
		}

		listaInicial.load();
	}

	/**
     * Obtiene la informacion de la tabla CLASECLASIFICADOR para validar 
     * los clasificadores de tipo Detalle
     */
	private void cargaValClasificadores() {
		Map<String,Object> paramEnvio = new TreeMap<>();
        paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        paramEnvio.put(GeneralParameterEnum.ANIO.getName(),vigencia);
        paramEnvio.put(GeneralParameterEnum.CODIGO.getName(),claseClasificador);
        
        Registro rsClasificadores = null;
        try {           
        	rsClasificadores = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                    		FrmsubdnovedadesproyectosControladorUrlEnum.URL1883010.getValue())
                                    .getUrl(),paramEnvio));
        } catch(SystemException e) {
        	  logger.error(e.getMessage(),e);
              JsfUtil.agregarMensajeError(e.getMessage());
        }
        if(rsClasificadores!=null) {
        	aplicacionGastos = (int) SysmanFunciones.nvl(rsClasificadores.getCampos()
        							.get("APLICACION"),0);
        	aplicacionIngr = (int) SysmanFunciones.nvl(rsClasificadores.getCampos()
        							.get("APLICACIONINGRESOS"),0);
        }
	}
	
	public void validadorCampos() {
    	camposBorde = new HashMap<>();
        camposBorde.put("SECTORRUBRO","obligaCampos1");
        camposBorde.put("PROGRAMARUBRO","obligaCampos2");
        camposBorde.put("SUBPROGRAMARUBRO","obligaCampos3");
        camposBorde.put("CODIGOPRODUCTO","obligaCampos4");
        camposBorde.put("CODIGOBPIN","obligaCampos5");
        camposBorde.put("CODIGOCCPET","obligaCampos6");
        camposBorde.put("CODIGOCPCDANE","obligaCampos7");
        camposBorde.put("CODIGOUNIDADEJECUTORA","obligaCampos8");
        camposBorde.put("CODIGOFUENTE","obligaCampos9");
        camposBorde.put("CODIGOCCPETREGALIAS","obligaCampos10");
        camposBorde.put("CODIGODETALLESECTORIAL","obligaCampos11");
    }
	
	private void asignarEstilo(String variable,String estilo) {
        switch (variable) {
            case "obligaCampos1":obligaCampos1 = estilo; break;
            case "obligaCampos2":obligaCampos2 = estilo; break;
            case "obligaCampos3":obligaCampos3 = estilo; break;
            case "obligaCampos4":obligaCampos4 = estilo; break;
            case "obligaCampos5":obligaCampos5 = estilo; break;
            case "obligaCampos6":obligaCampos6 = estilo; break;
            case "obligaCampos7":obligaCampos7 = estilo; break;
            case "obligaCampos8":obligaCampos8 = estilo; break;
            case "obligaCampos9":obligaCampos9 = estilo; break;
            case "obligaCampos10":obligaCampos10 = estilo; break;
            case "obligaCampos11":obligaCampos11 = estilo; break;
        }
    }
	
	public boolean cambiarBorde() {
    	boolean rta = false;
    	validacionExitosa = true;
    	if(aplicacionGastos==2 && aplicacionIngr==2 && manejaCampObligSCDP.equals("SI")) {
    		validadorCampos();
	        for(Map.Entry<String,String> entry : camposBorde.entrySet()) {
	            String campo = entry.getKey();
	            String variable = entry.getValue();
	            if(SysmanFunciones.validarCampoVacio(registro.getCampos(),campo)) {
	                asignarEstilo(variable,"#FF0000 solid 1px");
	                validacionExitosa = false;
	                rta = true;
	            } else {
	            	asignarEstilo(variable,"#A9A9A9 solid 2px");
	            }
	        }
    	}
        return rta;
    }
	
	public void ejecutarvalidarCampos() {}
	
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		claseClasificador = SysmanFunciones.nvl(registro.getCampos().get("CLASECLASIFICADOR"),"").toString();
		if(!claseClasificador.isEmpty()) {
			cargaValClasificadores();
			if(cambiarBorde()) {
				JsfUtil.agregarMensajeError(idioma.getString("TI_MS_ERROR_VALIDACION"));
				return false;
		    }
		}
		consecutivoNovedad();
		registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), consecutivoNovedad);
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("VIGENCIA_PLAN_INDICATIVO", vigenciaPlanIndicativo);
		registro.getCampos().put("TIPOT", tipoT);
		registro.getCampos().put("CLASET", claseT);
		registro.getCampos().put("NOVEDAD", codigo);
		registro.getCampos().put("DEPENDENCIA", dependencia);
		registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
		registro.getCampos().put("ANORUBRO", registro.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()));
		registro.getCampos().remove(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue());
		registro.getCampos().remove(GeneralParameterEnum.VIGENCIA.getName());
		registro.getCampos().remove("FECHA_VALIDEZ");
		registro.getCampos().remove(FrmsubdnovedadesproyectosControladorEnum.SECTOR.getValue());

		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.SECTOR.getValue(), sector);

		registro.getCampos().put("TIPOCOMPONENTE", tipoComponente);
		
		boolean respuesta = true;
		String rta;

		try {

			rta = ejbBancoProyectoCinco.verificarSaldoRubro(compania, claseT,
					Long.parseLong(registro.getCampos().get("NOVEDAD").toString()), dependencia, rubroPresupuestal,
					proyecto, fuenteRecursos, codigoMetaProducto,
					Double.parseDouble(registro.getCampos()
							.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()).toString()),
					0, registro.getCampos().get("CENTRO_COSTO").toString(),
					registro.getCampos().get("REFERENCIA").toString(), registro.getCampos().get("AUXILIAR").toString());

			if (!"-1".equals(rta)) {
				JsfUtil.agregarMensajeInformativoDialogo(rta);
				respuesta = false;
			}

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		registro.getCampos().remove("CLASECLASIFICADOR");
		
		return respuesta;
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		calcularTotales();
		cargarBorde();
		
		return true;
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>

		if (proyecto == null) {
			proyecto = "";
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2433"));
			return false;
		}
		if (componente == null) {
			componente = "";
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2433"));
			return false;

		}
		claseClasificador = SysmanFunciones.nvl(registro.getCampos().get("CLASECLASIFICADOR"),"").toString();
		if(!claseClasificador.isEmpty()) {
			cargaValClasificadores();
			if(cambiarBorde()) {
				JsfUtil.agregarMensajeError(idioma.getString("TI_MS_ERROR_VALIDACION"));
				return false;
		    }
		}
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

		registro.getCampos().remove(FrmsubdnovedadesproyectosControladorEnum.ACTIVIDADES.getValue());

		registro.getCampos().remove(GeneralParameterEnum.VIGENCIA.getName());
		
		registro.getCampos().remove("CLASECLASIFICADOR");

		return true;
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		calcularTotales();
		if (registro.getCampos().get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue()) == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2432"));
			return false;
		}
		cargarBorde();
		validacionVigencia();
		return true;
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		return true;
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		calcularTotales();
		return true;
		// </CODIGO_DESARROLLADO>
	}

	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
		paisL = (String) registro.getCampos().get(cPaisL);
		departamentoL = (String) registro.getCampos().get(cDepartamentoL);
		ciudadL = (String) registro.getCampos().get("");
		cargarListaBarrioL();
		validarProyecto(indice);

		if ("NO".equals(filtraPrimeroMetaProd)) {
			validarActividad(indice);
			System.out.println("__validacion___" + filtraPrimeroMetaProd);
		}
		
		sectorComboE = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("SECTORRUBRO"),
                                "").toString();
		
		programaE = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("PROGRAMARUBRO"),
                                "").toString();
		
		codigoCCPETE = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("CODIGOCCPET"),
                                "").toString();
		
		cargarListaprogramaE();       
		cargarListacodigoProductoE();
		cargarListacodigoCPCDANEE();
                cargarListacodigoCPCE();
		
			
	}

	private void validarProyecto(int indice) {

		proyecto = SysmanFunciones.nvl(
				listaInicial.getDatasource().get(indice % 10).getCampos().get(GeneralParameterEnum.PROYECTO.getName()),
				"").toString();

		componente = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "").toString();

		actividad = SysmanFunciones.nvl(
				listaInicial.getDatasource().get(indice % 10).getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()),
				"").toString();

		idMetaProducto = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice % 10).getCampos().get(cIdMeta), "")
				.toString();
		fuenteRecursos = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue()), "").toString();

		if (!SysmanFunciones.validarVariableVacio(componente) || !SysmanFunciones.validarVariableVacio(actividad)
				|| !SysmanFunciones.validarVariableVacio(idMetaProducto)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2438"));
			bloqueaProyecto = true;
		}

		cargarListaComponenteE();
		cargarListaActividad1E();
		cargarListaMetaProductoE();

	}

	private void validarActividad(int indice) {
		// METODO ACTIVIDAD_BEFOREUPDATE
		valorSolicitado = Double
				.parseDouble(SysmanFunciones
						.nvl(listaInicial.getDatasource().get(indice % 10).getCampos()
								.get(FrmsubdnovedadesproyectosControladorEnum.VALORSOLICITADO.getValue()), "0")
						.toString());

		idMetaProducto = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice % 10).getCampos().get(cIdMeta), "")
				.toString();

		if (valorSolicitado != 0) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2440"));
			bloqueaActividadContinuo = true;

		} else {
		        bloqueaActividadContinuo = false;
		}

		if (SysmanFunciones.validarVariableVacio(idMetaProducto)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2438"));

		}

		actividad = SysmanFunciones.nvl(
				listaInicial.getDatasource().get(indice % 10).getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()),
				"").toString();

		cargarListaMetaProductoE();

	}

	public void cambiarProyectoC(int rowNum) {
		// METODO_NO_IMPLEMENTADO
	}

	public void cambiarComponenteC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		componente = SysmanFunciones.nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(FrmsubdnovedadesproyectosControladorEnum.COMPONENTE.getValue()), "").toString();

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("TIPOCOMPONENTE", tipoComponente);

		cargarListaActividad1E();
		cargarListaMetaProductoE();

		// </CODIGO_DESARROLLADO>
	}

	public void cambiarActividadC(int rowNum) {

		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarVALORDISMINUIDO() {

		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCantidadActividadC(int rowNum) {
		if ("NO".equals(filtraPrimeroMetaProd)) {
			validarActividad(rowNum);
		}
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCantidadPlanC(int rowNum) {

		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>
		String[] campos = { "vigenciaPeriodo", "valorSolicitado1", "valorAprobado1", cCodigo1, "tipo", "clase",
				"TipoTmodal", "rid" };
		Object[] valores = { vigencia, valorSolicitado1, valorAprobado1, codigo, tipoT, claseT, tipoT, ridSolicitudS };
		SessionUtil.redireccionar("/frmsolicitudcdp.sysman", campos, valores);
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void asignarValoresRegistro() {
		// </CODIGO_DESARROLLADO>
		registro = new Registro();
		registro.getCampos().put("PROYECTO", null);
		registro.getCampos().put(cIdMeta, null);
		registro.getCampos().put(FrmsubdnovedadesproyectosControladorEnum.FUENTERECURSOS.getValue(), null);
		registro.getCampos().put("RUBROPRESUPUESTAL", null);
		cargarListaActividad();
		cargarListaActividad1E();
		cargarListaPeriodo();
		cargarListaPeriodoE();
		cargarListaMetaProducto();
		cargarListaFuente();
		cargarListaFuenteE();
		cargarListaMetaProducto();
		cargarListaMetaProductoE();

	}

	@Override
	public void removerCombos() {
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

	}

	public String getFechaInicialM() {
		return fechaInicialM;
	}

	public void setFechaInicialM(String fechaInicialM) {
		this.fechaInicialM = fechaInicialM;
	}

	public String getFechaFinalM() {
		return fechaFinalM;
	}

	public void setFechaFinalM(String fechaFinalM) {
		this.fechaFinalM = fechaFinalM;
	}

	public String getArgNovedades() {
		return argNovedades;
	}

	public void setArgNovedades(String argNovedades) {
		this.argNovedades = argNovedades;
	}

	public Map<String, Object> getRidSolicitudS() {
		return ridSolicitudS;
	}

	public void setRidSolicitudS(Map<String, Object> ridSolicitudS) {
		this.ridSolicitudS = (HashMap<String, Object>) ridSolicitudS;
	}

	public Double getSaldoRubroAux() {
		return saldoRubroAux;
	}

	public void setSaldoRubroAux(Double saldoRubroAux) {
		this.saldoRubroAux = saldoRubroAux;
	}

	public Double getSvalorSolicitado() {
		return svalorSolicitado;
	}

	public void setSvalorSolicitado(Double svalorSolicitado) {
		this.svalorSolicitado = svalorSolicitado;
	}

	public boolean isBloqueaImprimir() {
		return bloqueaImprimir;
	}

	public void setBloqueaImprimir(boolean bloqueaImprimir) {
		this.bloqueaImprimir = bloqueaImprimir;
	}

	public String getProgramaProyecto() {
		return programaProyecto;
	}

	public void setProgramaProyecto(String programaProyecto) {
		this.programaProyecto = programaProyecto;
	}

	public String getProgramaProyectoAux() {
		return programaProyectoAux;
	}

	public void setProgramaProyectoAux(String programaProyectoAux) {
		this.programaProyectoAux = programaProyectoAux;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public boolean isBloqueaProyecto() {
		return bloqueaProyecto;
	}

	public void setBloqueaProyecto(boolean bloqueaProyecto) {
		this.bloqueaProyecto = bloqueaProyecto;
	}

	public boolean isBloqueaComponente() {
		return bloqueaComponente;
	}

	public void setBloqueaComponente(boolean bloqueaComponente) {
		this.bloqueaComponente = bloqueaComponente;
	}

	public boolean isBloqueaMetaProducto() {
		return bloqueaMetaProducto;
	}

	public void setBloqueaMetaProducto(boolean bloqueaMetaProducto) {
		this.bloqueaMetaProducto = bloqueaMetaProducto;
	}

	public boolean isBloqueaActividad() {
		return bloqueaActividad;
	}

	public void setBloqueaActividad(boolean bloqueaActividad) {
		this.bloqueaActividad = bloqueaActividad;
	}

	public boolean isActualizadoEnPlan() {
		return actualizadoEnPlan;
	}

	public void setActualizadoEnPlan(boolean actualizadoEnPlan) {
		this.actualizadoEnPlan = actualizadoEnPlan;
	}

	public boolean isBloqueaProyectoContinuo() {
		return bloqueaProyectoContinuo;
	}

	public void setBloqueaProyectoContinuo(boolean bloqueaProyectoContinuo) {
		this.bloqueaProyectoContinuo = bloqueaProyectoContinuo;
	}

	public boolean isBloqueaComponenteContinuo() {
		return bloqueaComponenteContinuo;
	}

	public void setBloqueaComponenteContinuo(boolean bloqueaComponenteContinuo) {
		this.bloqueaComponenteContinuo = bloqueaComponenteContinuo;
	}

	public boolean isBloqueaMetaProductoContinuo() {
		return bloqueaMetaProductoContinuo;
	}

	public void setBloqueaMetaProductoContinuo(boolean bloqueaMetaProductoContinuo) {
		this.bloqueaMetaProductoContinuo = bloqueaMetaProductoContinuo;
	}

	public boolean isBloqueaActividadContinuo() {
		return bloqueaActividadContinuo;
	}

	public void setBloqueaActividadContinuo(boolean bloqueaActividadContinuo) {
		this.bloqueaActividadContinuo = bloqueaActividadContinuo;
	}

	public String getConsecutivoNovedad() {
		return consecutivoNovedad;
	}

	public void setConsecutivoNovedad(String consecutivoNovedad) {
		this.consecutivoNovedad = consecutivoNovedad;
	}

	public String getIdMetaProducto() {
		return idMetaProducto;
	}

	// CODIGO DESARROLLADO
	// CODIGO DESARROLLADO
	public void setIdMetaProducto(String idMetaProducto) {
		this.idMetaProducto = idMetaProducto;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getAuxTotales() {
		return auxTotales;
	}

	public void setAuxTotales(String auxTotales) {
		this.auxTotales = auxTotales;
	}

	public List<Registro> getListaTotales() {
		return listaTotales;
	}

	public void setListaTotales(List<Registro> listaTotales) {
		this.listaTotales = listaTotales;
	}

	public String getNombreProyecto() {
		return nombreProyecto;
	}

	public void setNombreProyecto(String nombreProyecto) {
		this.nombreProyecto = nombreProyecto;
	}

	public String getInfoDetalles() {
		return infoDetalles;
	}

	public void setInfoDetalles(String infoDetalles) {
		this.infoDetalles = infoDetalles;
	}

	public String getInfoDetallesPlan() {
		return infoDetallesPlan;
	}

	public void setInfoDetallesPlan(String infoDetallesPlan) {
		this.infoDetallesPlan = infoDetallesPlan;
	}

	public String getCiudadL() {
		return ciudadL;
	}

	public void setCiudadL(String ciudadL) {
		this.ciudadL = ciudadL;
	}

	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}

	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}

	public RegistroDataModelImpl getListaFuente() {
		return listaFuente;
	}

	public void setListaFuente(RegistroDataModelImpl listaFuente) {
		this.listaFuente = listaFuente;
	}

	public String getDepartamentoL() {
		return departamentoL;
	}

	public void setDepartamentoL(String departamentoL) {
		this.departamentoL = departamentoL;
	}

	public String getPaisL() {
		return paisL;
	}

	public void setPaisL(String paisL) {
		this.paisL = paisL;
	}

	public List<Registro> getListaINDICADOR() {
		return listaINDICADOR;
	}

	public void setListaINDICADOR(List<Registro> listaINDICADOR) {
		this.listaINDICADOR = listaINDICADOR;
	}

	public RegistroDataModel getListaINDICADORE() {
		return listaINDICADORE;
	}

	public void setListaINDICADORE(RegistroDataModel listaINDICADORE) {
		this.listaINDICADORE = listaINDICADORE;
	}

	public RegistroDataModelImpl getListaMetaProducto() {
		return listaMetaProducto;
	}

	public void setListaMetaProducto(RegistroDataModelImpl listaMetaProducto) {
		this.listaMetaProducto = listaMetaProducto;
	}

	public RegistroDataModelImpl getListaMetaProductoE() {
		return listaMetaProductoE;
	}

	public void setListaMetaProductoE(RegistroDataModelImpl listaMetaProductoE) {
		this.listaMetaProductoE = listaMetaProductoE;
	}

	public RegistroDataModelImpl getListaProyecto() {
		return listaProyecto;
	}

	public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
		this.listaProyecto = listaProyecto;
	}

	public RegistroDataModelImpl getListaProyectoE() {
		return listaProyectoE;
	}

	public void setListaProyectoE(RegistroDataModelImpl listaProyectoE) {
		this.listaProyectoE = listaProyectoE;
	}

	public RegistroDataModelImpl getListaComponente() {
		return listaComponente;
	}

	public void setListaComponente(RegistroDataModelImpl listaComponente) {
		this.listaComponente = listaComponente;
	}

	public RegistroDataModelImpl getListaComponenteE() {
		return listaComponenteE;
	}

	public void setListaComponenteE(RegistroDataModelImpl listaComponenteE) {
		this.listaComponenteE = listaComponenteE;
	}

	public RegistroDataModelImpl getListaActividad() {
		return listaActividad;
	}

	public void setListaActividad(RegistroDataModelImpl listaActividad) {
		this.listaActividad = listaActividad;
	}

	public RegistroDataModelImpl getListaActividadE() {
		return listaActividadE;
	}

	public void setListaActividadE(RegistroDataModelImpl listaActividadE) {
		this.listaActividadE = listaActividadE;
	}

	public RegistroDataModelImpl getListaRubroPresupuestal() {
		return listaRubroPresupuestal;
	}

	public void setListaRubroPresupuestal(RegistroDataModelImpl listaRubroPresupuestal) {
		this.listaRubroPresupuestal = listaRubroPresupuestal;
	}

	public RegistroDataModelImpl getListaRubroPresupuestalE() {
		return listaRubroPresupuestalE;
	}

	public void setListaRubroPresupuestalE(RegistroDataModelImpl listaRubroPresupuestalE) {
		this.listaRubroPresupuestalE = listaRubroPresupuestalE;
	}

	public RegistroDataModelImpl getListaBarrioL() {
		return listaBarrioL;
	}

	public void setListaBarrioL(RegistroDataModelImpl listaBarrioL) {
		this.listaBarrioL = listaBarrioL;
	}

	public RegistroDataModelImpl getListaBarrioLE() {
		return listaBarrioLE;
	}

	public void setListaBarrioLE(RegistroDataModelImpl listaBarrioLE) {
		this.listaBarrioLE = listaBarrioLE;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public String getTipoT() {
		return tipoT;
	}

	public void setTipoT(String tipoT) {
		this.tipoT = tipoT;
	}

	public String getClaseT() {
		return claseT;
	}

	public void setClaseT(String claseT) {
		this.claseT = claseT;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDependencia() {
		return dependencia;
	}

	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	public String getVigencia() {
		return vigencia;
	}

	public void setVigencia(String vigencia) {
		this.vigencia = vigencia;
	}

	public String getValorSolicitado1() {
		return valorSolicitado1;
	}

	public void setValorSolicitado1(String valorSolicitado1) {
		this.valorSolicitado1 = valorSolicitado1;
	}

	public String getValorAprobado1() {
		return valorAprobado1;
	}

	public void setValorAprobado1(String valorAprobado1) {
		this.valorAprobado1 = valorAprobado1;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public String getComponente() {
		return componente;
	}

	public void setComponente(String componente) {
		this.componente = componente;
	}

	public String getTipoComponente() {
		return tipoComponente;
	}

	public void setTipoComponente(String tipoComponente) {
		this.tipoComponente = tipoComponente;
	}

	public String getFuenteRecursos() {
		return fuenteRecursos;
	}

	public void setFuenteRecursos(String fuenteRecursos) {
		this.fuenteRecursos = fuenteRecursos;
	}

	public String getIndicador() {
		return indicador;
	}

	public void setIndicador(String indicador) {
		this.indicador = indicador;
	}

	public String getNombreMetaProducto() {
		return nombreMetaProducto;
	}

	public void setNombreMetaProducto(String nombreMetaProducto) {
		this.nombreMetaProducto = nombreMetaProducto;
	}

	public String getTipoMetaProducto() {
		return tipoMetaProducto;
	}

	public void setTipoMetaProducto(String tipoMetaProducto) {
		this.tipoMetaProducto = tipoMetaProducto;
	}

	public String getNaturalezaPlanPptal() {
		return naturalezaPlanPptal;
	}

	public void setNaturalezaPlanPptal(String naturalezaPlanPptal) {
		this.naturalezaPlanPptal = naturalezaPlanPptal;
	}

	public double getCantidadProgramadaMeta() {
		return cantidadProgramadaMeta;
	}

	public void setCantidadProgramadaMeta(double cantidadProgramadaMeta) {
		this.cantidadProgramadaMeta = cantidadProgramadaMeta;
	}

	public double getCantidadEjecutadaMeta() {
		return cantidadEjecutadaMeta;
	}

	public void setCantidadEjecutadaMeta(double cantidadEjecutadaMeta) {
		this.cantidadEjecutadaMeta = cantidadEjecutadaMeta;
	}

	public double getComponenteValorTotal() {
		return componenteValorTotal;
	}

	public void setComponenteValorTotal(double componenteValorTotal) {
		this.componenteValorTotal = componenteValorTotal;
	}

	public double getComponenteValorSolicitado() {
		return componenteValorSolicitado;
	}

	public void setComponenteValorSolicitado(double componenteValorSolicitado) {
		this.componenteValorSolicitado = componenteValorSolicitado;
	}

	public double getValorProgramadoMeta() {
		return valorProgramadoMeta;
	}

	public void setValorProgramadoMeta(double valorProgramadoMeta) {
		this.valorProgramadoMeta = valorProgramadoMeta;
	}

	public double getValorProgramadoMetaOtros() {
		return valorProgramadoMetaOtros;
	}

	public void setValorProgramadoMetaOtros(double valorProgramadoMetaOtros) {
		this.valorProgramadoMetaOtros = valorProgramadoMetaOtros;
	}

	public double getValorEjecutadoMeta() {
		return valorEjecutadoMeta;
	}

	public void setValorEjecutadoMeta(double valorEjecutadoMeta) {
		this.valorEjecutadoMeta = valorEjecutadoMeta;
	}

	public String getVigenciaComponente() {
		return vigenciaComponente;
	}

	public void setVigenciaComponente(String vigenciaComponente) {
		this.vigenciaComponente = vigenciaComponente;
	}

	public double getValorProgramadocomponente() {
		return valorProgramadocomponente;
	}

	public void setValorProgramadocomponente(double valorProgramadocomponente) {
		this.valorProgramadocomponente = valorProgramadocomponente;
	}

	public double getValorEjecutadoComponente() {
		return valorEjecutadoComponente;
	}

	public void setValorEjecutadoComponente(double valorEjecutadoComponente) {
		this.valorEjecutadoComponente = valorEjecutadoComponente;
	}

	public double getSaldoRubro() {
		return saldoRubro;
	}

	public void setSaldoRubro(double saldoRubro) {
		this.saldoRubro = saldoRubro;
	}

	public double getCantidadActividad() {
		return cantidadActividad;
	}

	public void setCantidadActividad(double cantidadActividad) {
		this.cantidadActividad = cantidadActividad;
	}

	public double getCantidadEjeActividad() {
		return cantidadEjeActividad;
	}

	public void setCantidadEjeActividad(double cantidadEjeActividad) {
		this.cantidadEjeActividad = cantidadEjeActividad;
	}

	public Double getValorSolicitado() {
		return valorSolicitado;
	}

	public void setValorSolicitado(Double valorSolicitado) {
		this.valorSolicitado = valorSolicitado;
	}

	public double getSaldoComponente() {
		return saldoComponente;
	}

	public void setSaldoComponente(double saldoComponente) {
		this.saldoComponente = saldoComponente;
	}

	public double getCostoTotalActividad() {
		return costoTotalActividad;
	}

	public void setCostoTotalActividad(double costoTotalActividad) {
		this.costoTotalActividad = costoTotalActividad;
	}

	public String getIdPlanMeta() {
		return idPlanMeta;
	}

	public void setIdPlanMeta(String idPlanMeta) {
		this.idPlanMeta = idPlanMeta;
	}

	public String getNombreComponente() {
		return nombreComponente;
	}

	public void setNombreComponente(String nombreComponente) {
		this.nombreComponente = nombreComponente;
	}

	public String getConsulta() {
		return consulta;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getCargoResponsable() {
		return cargoResponsable;
	}

	public void setCargoResponsable(String cargoResponsable) {
		this.cargoResponsable = cargoResponsable;
	}

	public int getVigenciaMetaP() {
		return vigenciaMetaP;
	}

	public void setVigenciaMetaP(int vigenciaMetaP) {
		this.vigenciaMetaP = vigenciaMetaP;
	}

	public double getCantidadPlan() {
		return cantidadPlan;
	}

	public void setCantidadPlan(double cantidadPlan) {
		this.cantidadPlan = cantidadPlan;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public boolean isSoloComponente() {
		return soloComponente;
	}

	public void setSoloComponente(boolean soloComponente) {
		this.soloComponente = soloComponente;
	}

	public Double getValComponente() {
		return valComponente;
	}

	public void setValComponente(Double valComponente) {
		this.valComponente = valComponente;
	}

	public String getEspecificacion() {
		return especificacion;
	}

	public void setEspecificacion(String especificacion) {
		this.especificacion = especificacion;
	}

	public String getObtejoComponente() {
		return obtejoComponente;
	}

	public void setObtejoComponente(String obtejoComponente) {
		this.obtejoComponente = obtejoComponente;
	}

	public int getVigenciaPlanIndicativo() {
		return vigenciaPlanIndicativo;
	}

	public void setVigenciaPlanIndicativo(int vigenciaPlanIndicativo) {
		this.vigenciaPlanIndicativo = vigenciaPlanIndicativo;
	}

	public String getPeriodicidadProyecto() {
		return periodicidadProyecto;
	}

	public void setPeriodicidadProyecto(String periodicidadProyecto) {
		this.periodicidadProyecto = periodicidadProyecto;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getRubroPresupuestal() {
		return rubroPresupuestal;
	}

	public void setRubroPresupuestal(String rubroPresupuestal) {
		this.rubroPresupuestal = rubroPresupuestal;
	}

	public boolean isEditarFormulario() {
		return editarFormulario;
	}

	public void setEditarFormulario(boolean editarFormulario) {
		this.editarFormulario = editarFormulario;
	}

	public boolean isEliminarFormulario() {
		return eliminarFormulario;
	}

	public void setEliminarFormulario(boolean eliminarFormulario) {
		this.eliminarFormulario = eliminarFormulario;
	}

	public boolean isInsertarFormulario() {
		return insertarFormulario;
	}

	public void setInsertarFormulario(boolean insertarFormulario) {
		this.insertarFormulario = insertarFormulario;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the filtraPrimeroMetaProd
	 */
	public String getFiltraPrimeroMetaProd() {
		return filtraPrimeroMetaProd;
	}

	/**
	 * @param filtraPrimeroMetaProd the filtraPrimeroMetaProd to set
	 */
	public void setFiltraPrimeroMetaProd(String filtraPrimeroMetaProd) {
		this.filtraPrimeroMetaProd = filtraPrimeroMetaProd;
	}

	/**
	 * @return the codigoMetaProducto
	 */
	public String getCodigoMetaProducto() {
		return codigoMetaProducto;
	}

	/**
	 * @param codigoMetaProducto the codigoMetaProducto to set
	 */
	public void setCodigoMetaProducto(String codigoMetaProducto) {
		this.codigoMetaProducto = codigoMetaProducto;
	}

	/**
	 * @return the actividadMeta
	 */
	public String getActividadMeta() {
		return actividadMeta;
	}

	/**
	 * @param actividadMeta the actividadMeta to set
	 */
	public void setActividadMeta(String actividadMeta) {
		this.actividadMeta = actividadMeta;
	}

	/**
	 * @return the componenteMeta
	 */
	public String getComponenteMeta() {
		return componenteMeta;
	}

	/**
	 * @param componenteMeta the componenteMeta to set
	 */
	public void setComponenteMeta(String componenteMeta) {
		this.componenteMeta = componenteMeta;
	}

	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}

	/**
	 * @param sector the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}

	/**
	 * @return the listaFuenteE
	 */
	public RegistroDataModelImpl getListaFuenteE() {
		return listaFuenteE;
	}

	/**
	 * @param listaFuenteE the listaFuenteE to set
	 */
	public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
		this.listaFuenteE = listaFuenteE;
	}

	public String getPieComponente() {
		return pieComponente;
	}

	public void setPieComponente(String pieComponente) {
		this.pieComponente = pieComponente;
	}

	public String getPieActividad() {
		return pieActividad;
	}

	public void setPieActividad(String pieActividad) {
		this.pieActividad = pieActividad;
	}

	public String getPieMetaProd() {
		return pieMetaProd;
	}

	public void setPieMetaProd(String pieMetaProd) {
		this.pieMetaProd = pieMetaProd;
	}

	public String getPieLineaBase() {
		return pieLineaBase;
	}

	public void setPieLineaBase(String pieLineaBase) {
		this.pieLineaBase = pieLineaBase;
	}

	public String getPieMetaTotal() {
		return pieMetaTotal;
	}

	public void setPieMetaTotal(String pieMetaTotal) {
		this.pieMetaTotal = pieMetaTotal;
	}

	public String getPieMetaVigencia() {
		return pieMetaVigencia;
	}

	public void setPieMetaVigencia(String pieMetaVigencia) {
		this.pieMetaVigencia = pieMetaVigencia;
	}

	public String getPieMetaEjecutada() {
		return pieMetaEjecutada;
	}

	public void setPieMetaEjecutada(String pieMetaEjecutada) {
		this.pieMetaEjecutada = pieMetaEjecutada;
	}

	public String getPieSector() {
		return pieSector;
	}

	public void setPieSector(String pieSector) {
		this.pieSector = pieSector;
	}

	public String getAuxiliarGeneral() {
		return auxiliarGeneral;
	}

	public void setAuxiliarGeneral(String auxiliarGeneral) {
		this.auxiliarGeneral = auxiliarGeneral;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}

	public boolean isCampobloqueado() {
		return campobloqueado;
	}

	public void setCampobloqueado(boolean campobloqueado) {
		this.campobloqueado = campobloqueado;
	}

	/**
	 * @return the listaActividad1
	 */
	public RegistroDataModelImpl getListaActividad1() {
		return listaActividad1;
	}

	/**
	 * @param listaActividad1 the listaActividad1 to set
	 */
	public void setListaActividad1(RegistroDataModelImpl listaActividad1) {
		this.listaActividad1 = listaActividad1;
	}

	/**
	 * @return the listaActividad1E
	 */
	public RegistroDataModelImpl getListaActividad1E() {
		return listaActividad1E;
	}

	/**
	 * @param listaActividad1E the listaActividad1E to set
	 */
	public void setListaActividad1E(RegistroDataModelImpl listaActividad1E) {
		this.listaActividad1E = listaActividad1E;
	}

	public List<Registro> getListaAnosClasificacion() {
		return listaAnosClasificacion;
	}

	public void setListaAnosClasificacion(List<Registro> listaAnosClasificacion) {
		this.listaAnosClasificacion = listaAnosClasificacion;
	}

	public String getComponenteE() {
		return componenteE;
	}

	public void setComponenteE(String componenteE) {
		this.componenteE = componenteE;
	}

	public String getActividadE() {
		return actividadE;
	}

	public void setActividadE(String actividadE) {
		this.actividadE = actividadE;
	}

	public boolean isSectorComboMostar() {
		return sectorComboMostar;
	}

	public void setSectorComboMostar(boolean sectorComboMostar) {
		this.sectorComboMostar = sectorComboMostar;
	}

	public boolean isCampobloqueadosector() {
		return campobloqueadosector;
	}

	public void setCampobloqueadosector(boolean campobloqueadosector) {
		this.campobloqueadosector = campobloqueadosector;
	}

	public boolean isCampobloqueadoprograma() {
		return campobloqueadoprograma;
	}

	public void setCampobloqueadoprograma(boolean campobloqueadoprograma) {
		this.campobloqueadoprograma = campobloqueadoprograma;
	}

	public boolean isCampobloqueadosubprograma() {
		return campobloqueadosubprograma;
	}

	public void setCampobloqueadosubprograma(boolean campobloqueadosubprograma) {
		this.campobloqueadosubprograma = campobloqueadosubprograma;
	}

	public boolean isCampobloqueadocodproducto() {
		return campobloqueadocodproducto;
	}

	public void setCampobloqueadocodproducto(boolean campobloqueadocodproducto) {
		this.campobloqueadocodproducto = campobloqueadocodproducto;
	}

	public boolean isCampobloqueadocodbpin() {
		return campobloqueadocodbpin;
	}

	public void setCampobloqueadocodbpin(boolean campobloqueadocodbpin) {
		this.campobloqueadocodbpin = campobloqueadocodbpin;
	}

	public boolean isCampobloqueadocodCCPET() {
		return campobloqueadocodCCPET;
	}

	public void setCampobloqueadocodCCPET(boolean campobloqueadocodCCPET) {
		this.campobloqueadocodCCPET = campobloqueadocodCCPET;
	}

	public boolean isCampobloqueadocodCPCDANE() {
		return campobloqueadocodCPCDANE;
	}

	public void setCampobloqueadocodCPCDANE(boolean campobloqueadocodCPCDANE) {
		this.campobloqueadocodCPCDANE = campobloqueadocodCPCDANE;
	}

	public boolean isCampobloqueadocodunidadejecutora() {
		return campobloqueadocodunidadejecutora;
	}

	public void setCampobloqueadocodunidadejecutora(boolean campobloqueadocodunidadejecutora) {
		this.campobloqueadocodunidadejecutora = campobloqueadocodunidadejecutora;
	}

	public boolean isCampobloqueadocodfuente() {
		return campobloqueadocodfuente;
	}

	public void setCampobloqueadocodfuente(boolean campobloqueadocodfuente) {
		this.campobloqueadocodfuente = campobloqueadocodfuente;
	}

	public boolean isCampobloqueadocodCCPETregalias() {
		return campobloqueadocodCCPETregalias;
	}

	public void setCampobloqueadocodCCPETregalias(boolean campobloqueadocodCCPETregalias) {
		this.campobloqueadocodCCPETregalias = campobloqueadocodCCPETregalias;
	}
	
	public boolean isCampobloqueadocoddetalleSectorial() {
		return campobloqueadocoddetalleSectorial;
	}

	public void setCampobloqueadocoddetalleSectorial(boolean campobloqueadocoddetalleSectorial) {
		this.campobloqueadocoddetalleSectorial = campobloqueadocoddetalleSectorial;
	}

	public boolean isProgramaMostar() {
		return programaMostar;
	}

	public void setProgramaMostar(boolean programaMostar) {
		this.programaMostar = programaMostar;
	}

	public boolean isSubprogramaMostar() {
		return subprogramaMostar;
	}

	public void setSubprogramaMostar(boolean subprogramaMostar) {
		this.subprogramaMostar = subprogramaMostar;
	}

	public boolean isCodigoProductoMostar() {
		return codigoProductoMostar;
	}

	public void setCodigoProductoMostar(boolean codigoProductoMostar) {
		this.codigoProductoMostar = codigoProductoMostar;
	}

	public boolean isCodigoBpinMostar() {
		return codigoBpinMostar;
	}

	public void setCodigoBpinMostar(boolean codigoBpinMostar) {
		this.codigoBpinMostar = codigoBpinMostar;
	}

	public boolean isCodigoCCPETMostar() {
		return codigoCCPETMostar;
	}

	public void setCodigoCCPETMostar(boolean codigoCCPETMostar) {
		this.codigoCCPETMostar = codigoCCPETMostar;
	}

	public boolean isCodigoCPCDANEMostar() {
		return codigoCPCDANEMostar;
	}

	public void setCodigoCPCDANEMostar(boolean codigoCPCDANEMostar) {
		this.codigoCPCDANEMostar = codigoCPCDANEMostar;
	}

	public boolean isCodigoUnidadEjecutoraMostar() {
		return codigoUnidadEjecutoraMostar;
	}

	public void setCodigoUnidadEjecutoraMostar(boolean codigoUnidadEjecutoraMostar) {
		this.codigoUnidadEjecutoraMostar = codigoUnidadEjecutoraMostar;
	}

	public boolean isCodigoFuenteMostar() {
		return codigoFuenteMostar;
	}

	public void setCodigoFuenteMostar(boolean codigoFuenteMostar) {
		this.codigoFuenteMostar = codigoFuenteMostar;
	}

	public boolean isCodigoCCPETRegaliasMostar() {
		return codigoCCPETRegaliasMostar;
	}

	public void setCodigoCCPETRegaliasMostar(boolean codigoCCPETRegaliasMostar) {
		this.codigoCCPETRegaliasMostar = codigoCCPETRegaliasMostar;
	}
	
	public boolean isCodigodetalleSectorialMostar() {
		return codigodetalleSectorialMostar;
	}

	public void setCodigodetalleSectorialMostar(boolean codigodetalleSectorialMostar) {
		this.codigodetalleSectorialMostar = codigodetalleSectorialMostar;
	}

	public boolean isCodigoCPCMostar() {
		return codigoCPCMostar;
	}

	public void setCodigoCPCMostar(boolean codigoCPCMostar) {
		this.codigoCPCMostar = codigoCPCMostar;
	}

	public boolean isFuenteCuipoMostar() {
		return fuenteCuipoMostar;
	}

	public void setFuenteCuipoMostar(boolean fuenteCuipoMostar) {
		this.fuenteCuipoMostar = fuenteCuipoMostar;
	}

	public boolean isProductoCuipoMostar() {
		return productoCuipoMostar;
	}

	public void setProductoCuipoMostar(boolean productoCuipoMostar) {
		this.productoCuipoMostar = productoCuipoMostar;
	}

	public boolean isCodigoCCPETCuipoMostar() {
		return codigoCCPETCuipoMostar;
	}

	public void setCodigoCCPETCuipoMostar(boolean codigoCCPETCuipoMostar) {
		this.codigoCCPETCuipoMostar = codigoCCPETCuipoMostar;
	}

	public boolean isCodigoBpinCampoMostrar() {
		return codigoBpinCampoMostrar;
	}

	public void setCodigoBpinCampoMostrar(boolean codigoBpinCampoMostrar) {
		this.codigoBpinCampoMostrar = codigoBpinCampoMostrar;
	}

	public boolean isCampobloqueadocodigoCPC() {
		return campobloqueadocodigoCPC;
	}

	public void setCampobloqueadocodigoCPC(boolean campobloqueadocodigoCPC) {
		this.campobloqueadocodigoCPC = campobloqueadocodigoCPC;
	}

	public boolean isCampobloqueadoFuenteCuipo() {
		return campobloqueadoFuenteCuipo;
	}

	public void setCampobloqueadoFuenteCuipo(boolean campobloqueadoFuenteCuipo) {
		this.campobloqueadoFuenteCuipo = campobloqueadoFuenteCuipo;
	}

	public boolean isCampobloqueadoProductoCuipo() {
		return campobloqueadoProductoCuipo;
	}

	public void setCampobloqueadoProductoCuipo(boolean campobloqueadoProductoCuipo) {
		this.campobloqueadoProductoCuipo = campobloqueadoProductoCuipo;
	}

	public boolean isCampobloqueadoCodigoCCPETCuipo() {
		return campobloqueadoCodigoCCPETCuipo;
	}

	public void setCampobloqueadoCodigoCCPETCuipo(boolean campobloqueadoCodigoCCPETCuipo) {
		this.campobloqueadoCodigoCCPETCuipo = campobloqueadoCodigoCCPETCuipo;
	}

	public boolean isCampobloqueadoCodigoBPINCampo() {
		return campobloqueadoCodigoBPINCampo;
	}

	public void setCampobloqueadoCodigoBPINCampo(boolean campobloqueadoCodigoBPINCampo) {
		this.campobloqueadoCodigoBPINCampo = campobloqueadoCodigoBPINCampo;
	}

	public RegistroDataModelImpl getListacodigoCPCDANE() {
		return listacodigoCPCDANE;
	}

	public void setListacodigoCPCDANE(RegistroDataModelImpl listacodigoCPCDANE) {
		this.listacodigoCPCDANE = listacodigoCPCDANE;
	}

	public RegistroDataModelImpl getListacodigoUnidadEjecutora() {
		return listacodigoUnidadEjecutora;
	}

	public void setListacodigoUnidadEjecutora(RegistroDataModelImpl listacodigoUnidadEjecutora) {
		this.listacodigoUnidadEjecutora = listacodigoUnidadEjecutora;
	}

	public RegistroDataModelImpl getListacodigoFuente() {
		return listacodigoFuente;
	}

	public void setListacodigoFuente(RegistroDataModelImpl listacodigoFuente) {
		this.listacodigoFuente = listacodigoFuente;
	}

	public RegistroDataModelImpl getListacodigoCCPETRegalias() {
		return listacodigoCCPETRegalias;
	}

	public void setListacodigoCCPETRegalias(RegistroDataModelImpl listacodigoCCPETRegalias) {
		this.listacodigoCCPETRegalias = listacodigoCCPETRegalias;
	}
	
	public RegistroDataModelImpl getListadetalleSectorial() {
		return listadetalleSectorial;
	}

	public void setListadetalleSectorial(RegistroDataModelImpl listadetalleSectorial) {
		this.listadetalleSectorial = listadetalleSectorial;
	}

	public RegistroDataModelImpl getListaCodigoCPC() {
		return listaCodigoCPC;
	}

	public void setListaCodigoCPC(RegistroDataModelImpl listaCodigoCPC) {
		this.listaCodigoCPC = listaCodigoCPC;
	}

	public RegistroDataModelImpl getListaCodigoCPCE() {
		return listaCodigoCPCE;
	}

	public void setListaCodigoCPCE(RegistroDataModelImpl listaCodigoCPCE) {
		this.listaCodigoCPCE = listaCodigoCPCE;
	}

	public RegistroDataModelImpl getListafuenteCuipo() {
		return listafuenteCuipo;
	}

	public void setListafuenteCuipo(RegistroDataModelImpl listafuenteCuipo) {
		this.listafuenteCuipo = listafuenteCuipo;
	}

	public RegistroDataModelImpl getListaproductoCuipo() {
		return listaproductoCuipo;
	}

	public void setListaproductoCuipo(RegistroDataModelImpl listaproductoCuipo) {
		this.listaproductoCuipo = listaproductoCuipo;
	}

	public RegistroDataModelImpl getListacodigoCCPETCuipo() {
		return listacodigoCCPETCuipo;
	}

	public void setListacodigoCCPETCuipo(RegistroDataModelImpl listacodigoCCPETCuipo) {
		this.listacodigoCCPETCuipo = listacodigoCCPETCuipo;
	}

	public RegistroDataModelImpl getListacodigoCCPET() {
		return listacodigoCCPET;
	}

	public void setListacodigoCCPET(RegistroDataModelImpl listacodigoCCPET) {
		this.listacodigoCCPET = listacodigoCCPET;
	}

	public RegistroDataModelImpl getListacodigoBpin() {
		return listacodigoBpin;
	}

	public void setListacodigoBpin(RegistroDataModelImpl listacodigoBpin) {
		this.listacodigoBpin = listacodigoBpin;
	}

	public RegistroDataModelImpl getListacodigoProducto() {
		return listacodigoProducto;
	}

	public void setListacodigoProducto(RegistroDataModelImpl listacodigoProducto) {
		this.listacodigoProducto = listacodigoProducto;
	}

	public RegistroDataModelImpl getListasubPrograma() {
		return listasubPrograma;
	}

	public void setListasubPrograma(RegistroDataModelImpl listasubPrograma) {
		this.listasubPrograma = listasubPrograma;
	}

	public RegistroDataModelImpl getListaprograma() {
		return listaprograma;
	}

	public void setListaprograma(RegistroDataModelImpl listaprograma) {
		this.listaprograma = listaprograma;
	}

	public RegistroDataModelImpl getListasectorCombo() {
		return listasectorCombo;
	}

	public void setListasectorCombo(RegistroDataModelImpl listasectorCombo) {
		this.listasectorCombo = listasectorCombo;
	}

	public RegistroDataModelImpl getListafuenteCuipoE() {
		return listafuenteCuipoE;
	}

	public void setListafuenteCuipoE(RegistroDataModelImpl listafuenteCuipoE) {
		this.listafuenteCuipoE = listafuenteCuipoE;
	}

	public RegistroDataModelImpl getListaproductoCuipoE() {
		return listaproductoCuipoE;
	}

	public void setListaproductoCuipoE(RegistroDataModelImpl listaproductoCuipoE) {
		this.listaproductoCuipoE = listaproductoCuipoE;
	}

	public RegistroDataModelImpl getListacodigoCCPETCuipoE() {
		return listacodigoCCPETCuipoE;
	}

	public void setListacodigoCCPETCuipoE(RegistroDataModelImpl listacodigoCCPETCuipoE) {
		this.listacodigoCCPETCuipoE = listacodigoCCPETCuipoE;
	}

	public RegistroDataModelImpl getListasectorComboE() {
		return listasectorComboE;
	}

	public void setListasectorComboE(RegistroDataModelImpl listasectorComboE) {
		this.listasectorComboE = listasectorComboE;
	}

	public RegistroDataModelImpl getListacodigoCPCDANEE() {
		return listacodigoCPCDANEE;
	}

	public void setListacodigoCPCDANEE(RegistroDataModelImpl listacodigoCPCDANEE) {
		this.listacodigoCPCDANEE = listacodigoCPCDANEE;
	}

	public RegistroDataModelImpl getListacodigoUnidadEjecutoraE() {
		return listacodigoUnidadEjecutoraE;
	}

	public void setListacodigoUnidadEjecutoraE(RegistroDataModelImpl listacodigoUnidadEjecutoraE) {
		this.listacodigoUnidadEjecutoraE = listacodigoUnidadEjecutoraE;
	}

	public RegistroDataModelImpl getListacodigoFuenteE() {
		return listacodigoFuenteE;
	}

	public void setListacodigoFuenteE(RegistroDataModelImpl listacodigoFuenteE) {
		this.listacodigoFuenteE = listacodigoFuenteE;
	}

	public RegistroDataModelImpl getListacodigoCCPETRegaliasE() {
		return listacodigoCCPETRegaliasE;
	}

	public void setListacodigoCCPETRegaliasE(RegistroDataModelImpl listacodigoCCPETRegaliasE) {
		this.listacodigoCCPETRegaliasE = listacodigoCCPETRegaliasE;
	}
	
	public RegistroDataModelImpl getListadetalleSectorialE() {
		return listadetalleSectorialE;
	}

	public void setListadetalleSectorialE(RegistroDataModelImpl listadetalleSectorialE) {
		this.listadetalleSectorialE = listadetalleSectorialE;
	}

	public RegistroDataModelImpl getListacodigoCCPETE() {
		return listacodigoCCPETE;
	}

	public void setListacodigoCCPETE(RegistroDataModelImpl listacodigoCCPETE) {
		this.listacodigoCCPETE = listacodigoCCPETE;
	}

	public RegistroDataModelImpl getListacodigoBpinE() {
		return listacodigoBpinE;
	}

	public void setListacodigoBpinE(RegistroDataModelImpl listacodigoBpinE) {
		this.listacodigoBpinE = listacodigoBpinE;
	}

	public RegistroDataModelImpl getListacodigoProductoE() {
		return listacodigoProductoE;
	}

	public void setListacodigoProductoE(RegistroDataModelImpl listacodigoProductoE) {
		this.listacodigoProductoE = listacodigoProductoE;
	}

	public RegistroDataModelImpl getListasubProgramaE() {
		return listasubProgramaE;
	}

	public void setListasubProgramaE(RegistroDataModelImpl listasubProgramaE) {
		this.listasubProgramaE = listasubProgramaE;
	}

	public RegistroDataModelImpl getListaprogramaE() {
		return listaprogramaE;
	}

	public void setListaprogramaE(RegistroDataModelImpl listaprogramaE) {
		this.listaprogramaE = listaprogramaE;
	}

	public String getCodigoCPC() {
		return codigoCPC;
	}

	public void setCodigoCPC(String codigoCPC) {
		this.codigoCPC = codigoCPC;
	}

	public String getCodigoCPCE() {
		return codigoCPCE;
	}

	public void setCodigoCPCE(String codigoCPCE) {
		this.codigoCPCE = codigoCPCE;
	}
	
	public String getObligaCampos1() {
		return obligaCampos1;
	}

	public void setObligaCampos1(String obligaCampos1) {
		this.obligaCampos1 = obligaCampos1;
	}
	
	public String getObligaCampos2() {
		return obligaCampos2;
	}

	public void setObligaCampos2(String obligaCampos2) {
		this.obligaCampos2 = obligaCampos2;
	}
	
	public String getObligaCampos3() {
		return obligaCampos3;
	}

	public void setObligaCampos3(String obligaCampos3) {
		this.obligaCampos3 = obligaCampos3;
	}
	
	public String getObligaCampos4() {
		return obligaCampos4;
	}

	public void setObligaCampos4(String obligaCampos4) {
		this.obligaCampos4 = obligaCampos4;
	}
	
	public String getObligaCampos5() {
		return obligaCampos5;
	}

	public void setObligaCampos5(String obligaCampos5) {
		this.obligaCampos5 = obligaCampos5;
	}
	
	public String getObligaCampos6() {
		return obligaCampos6;
	}

	public void setObligaCampos6(String obligaCampos6) {
		this.obligaCampos6 = obligaCampos6;
	}
	
	public String getObligaCampos7() {
		return obligaCampos7;
	}

	public void setObligaCampos7(String obligaCampos7) {
		this.obligaCampos7 = obligaCampos7;
	}
	
	public String getObligaCampos8() {
		return obligaCampos8;
	}

	public void setObligaCampos8(String obligaCampos8) {
		this.obligaCampos8 = obligaCampos8;
	}
	
	public String getObligaCampos9() {
		return obligaCampos9;
	}

	public void setObligaCampos9(String obligaCampos9) {
		this.obligaCampos9 = obligaCampos9;
	}
	
	public String getObligaCampos10() {
		return obligaCampos10;
	}

	public void setObligaCampos10(String obligaCampos10) {
		this.obligaCampos10 = obligaCampos10;
	}
	
	public String getObligaCampos11() {
		return obligaCampos11;
	}

	public void setObligaCampos11(String obligaCampos11) {
		this.obligaCampos11 = obligaCampos11;
	}

	public boolean isValidacionExitosa() {
		return validacionExitosa;
	}

	public void setValidacionExitosa(boolean validacionExitosa) {
		this.validacionExitosa = validacionExitosa;
	}
}
