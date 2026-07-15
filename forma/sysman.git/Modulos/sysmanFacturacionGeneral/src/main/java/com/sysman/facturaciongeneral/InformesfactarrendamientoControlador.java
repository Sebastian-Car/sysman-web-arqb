/*-
 * InformesfactarrendamientoControlador.java
 *
 * 1.0
 * 
 * 19/11/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.facturaciongeneral.enums.InformesfactarrendamientoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/11/2024
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class InformesfactarrendamientoControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	private String companiaLabel;
//<DECLARAR_ATRIBUTOS>
	/**
	 * variable del informe seleccionado
	 */
	private String informeSeleccionado;
	/**
	 * variable del tercero inicial seleccionado
	 */
	private String terceroInicial;
	/**
	 * variable para el tercero final seleccionado
	 */
	private String terceroFinal;
	/**
	 * variable para el codigoinmueble inicial seleccionado
	 */
	private String codigoInmuebleInicial;
	/**
	 * variable para el codigoinmueble final seleccionado
	 */
	private String codigoInmuebleFinal;
	/**
	 * variable edificio inicial seleccionado
	 */
	private String edificioInicial;
	/**
	 * variable edificio final seleccionado
	 */
	private String edificioFinal;
	/**
	 * variable de la fecha corte seleccionada
	 */
	private Date fechaCorte;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * variable lista tercero inicial
	 */
	private RegistroDataModelImpl listaterceroInicial;
	/**
	 * variable lista terceo final
	 */
	private RegistroDataModelImpl listaterceroFinal;
	/**
	 * variable lista codigoinmueble inicial
	 */
	private RegistroDataModelImpl listacodigoInmuebleInicial;
	/**
	 * variable lista codigoinmueble Final
	 */
	private RegistroDataModelImpl listacodigoInmuebleFinal;
	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    
    private boolean visibleInformesTercero;
    
    private boolean visibleInformesInmueble;
    
    private boolean visibleInformesEdificio;
    
    private boolean visibleInformesFechaCorte;
    
    private boolean visibleInformesFechasGeneral;
    
    private String nombreTerceroFinal;
    
    private String nombreTerceroInicial;
    
    private Date fechaInicial;
    
    private Date fechaFinal;
    
    private final String tipoCobro;
    
    private static String PRFECHACORTELETRAS = "PR_FECHACORTE_LETRAS";
    
    private static String CODIGOINMUEBLE = "CODIGOINMUEBLE";
    
    private static String rp_compania = "compania";
    
    private static String rp_terceroInicial = "terceroInicial";
    
    private static String rp_terceroFinal = "terceroFinal";
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InformesfactarrendamientoControlador
	 */
	public InformesfactarrendamientoControlador() {
		super();
		compania = SessionUtil.getCompania();
		companiaLabel = SessionUtil.getCompaniaIngreso().getNombre();
		tipoCobro = SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()).toString();
		fechaCorte = new Date();
		fechaInicial =  new Date();
		fechaFinal = new Date();
		try {
			//2493
			numFormulario = GeneralCodigoFormaEnum.INFORMESFACTURACIONARRIENDOS.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} 
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaterceroInicial();
		cargarListacodigoInmuebleInicial();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaterceroInicial
	 *
	 * 
	 */
	public void cargarListaterceroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesfactarrendamientoControladorUrlEnum.URL3639.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), "0");

		listaterceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,     true,
                "NIT");
	}

	/**
	 * 
	 * Carga la lista listaterceroFinal
	 *
	 * 
	 */
	public void cargarListaterceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesfactarrendamientoControladorUrlEnum.URL3642.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), SysmanFunciones.nvlStr(terceroInicial, "0").trim());

		listaterceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,     true,
                "NIT");
	}

	/**
	 * 
	 * Carga la lista listacodigoInmuebleInicial
	 *
	 * 
	 */
	public void cargarListacodigoInmuebleInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesfactarrendamientoControladorUrlEnum.URL3641.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacodigoInmuebleInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,     true,
                CODIGOINMUEBLE);
	}

	/**
	 * 
	 * Carga la lista listacodigoInmuebleFinal
	 *
	 * 
	 */
	public void cargarListacodigoInmuebleFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesfactarrendamientoControladorUrlEnum.URL3640.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINMUEBLEINCIAL", codigoInmuebleInicial);
		
		listacodigoInmuebleFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,     true,
                CODIGOINMUEBLE);
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		setArchivoDescarga(null);
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	
	public void oprimirExcel() {
		setArchivoDescarga(null);
		generarInforme(FORMATOS.EXCEL97);
	}
	
	@SuppressWarnings("deprecation")
	private void generarInforme(FORMATOS formato) {

		try {
			String nombreReporte = ""; 
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			if(informeSeleccionado.equals("3") || informeSeleccionado.equals("4") || informeSeleccionado.equals("5")) {
				nombreReporte = "002655FacturacionInmueblesComercializables";				
				reemplazar.put("codigoinmuebleincial", 	codigoInmuebleInicial.equals("") || informeSeleccionado.equals("4") || informeSeleccionado.equals("5")?"0":codigoInmuebleInicial);
				reemplazar.put("codigoinmueblefinal", codigoInmuebleFinal.equals("") || informeSeleccionado.equals("4") || informeSeleccionado.equals("5")?"ZZZZZZZZZ":codigoInmuebleFinal);
				reemplazar.put("edificioinicial", edificioInicial==null || informeSeleccionado.equals("4") || informeSeleccionado.equals("5")?"0":edificioInicial);
				reemplazar.put("edificiofinal", edificioFinal==null || informeSeleccionado.equals("4") || informeSeleccionado.equals("5")?"9":edificioFinal);
				reemplazar.put("condicional", informeSeleccionado.equals("3")?" IMF.COMERCIALIZABLE NOT IN 0 ":informeSeleccionado.equals("4")?" IMF.DISPONIBLE NOT IN 0 ":informeSeleccionado.equals("5")?" IMF.ARRENDADO NOT IN 0 ":"");
				reemplazar.put(rp_compania, compania);
				reemplazar.put("tipoCobro", tipoCobro);
				parametros.put("PR_NOMBRECOMPANIA",
	                    SessionUtil.getCompaniaIngreso().getNombre());
				parametros.put("PR_FECHACORTE", fechaCorte);
				parametros.put(PRFECHACORTELETRAS,new SimpleDateFormat("dd MMMMM yyyy", new Locale("es","ES")).format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				parametros.put("PR_NOMBRE_INFORME",informeSeleccionado.equals("3")?" INMUEBLES COMERCIALIZABLES ":informeSeleccionado.equals("4")?" INMUEBLES DISPONIBLES ":informeSeleccionado.equals("5")?" INMUEBLES ARRENDADOS":"");
			}else if(informeSeleccionado.equals("2")) {
				nombreReporte = "002657IndiceInmueblesFacturacion";
				parametros.put("PR_FECHACORTE_LETRAS",new SimpleDateFormat(" MMMMM yyyy", new Locale("es","ES")).format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				parametros.put("PR_NOMBRE_FIRMA", obtenerParametro("NOMBRE COORDINADOR BIENES INMUEBLES","Ingrese nombre del coordinador de imuebles"));
				parametros.put("PR_CARGO_FIRMA", obtenerParametro("CARGO COORDINADOR BIENES INMUEBLES","Ingrese cargo del coordinador de imuebles"));
				reemplazar.put("codigoinmuebleincial", 	codigoInmuebleInicial.equals("")?"0":codigoInmuebleInicial);
				reemplazar.put("codigoinmueblefinal", codigoInmuebleFinal.equals("")?"ZZZZZZZZZ":codigoInmuebleFinal);
				reemplazar.put("edificioinicial", edificioInicial==null?"0":edificioInicial);
				reemplazar.put("edificiofinal", edificioFinal==null?"9":edificioFinal);
				reemplazar.put("compania", compania);
			}else if(informeSeleccionado.equals("1")) {
				nombreReporte = "002658AnticiposClientesInmueblesFacturacion";	
				parametros.put("PR_NOMBRECOMPANIA",
	                    SessionUtil.getCompaniaIngreso().getNombre());
				parametros.put(PRFECHACORTELETRAS,new SimpleDateFormat("dd MMMMM yyyy", new Locale("es","ES")).format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				parametros.put("PR_FECHACORTE", fechaCorte);
				parametros.put("PR_NOMBRE_INFORME"," ANÁLISIS ANTICIPOS RECIBIDOS CLIENTES ");
				reemplazar.put(rp_terceroInicial, terceroInicial.equals("")?"0":terceroInicial);
				reemplazar.put(rp_terceroFinal, terceroFinal.equals("")?"999999999999999999":terceroFinal);
				reemplazar.put("fechaCorte", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				reemplazar.put(rp_compania, compania);
			}else if(informeSeleccionado.equals("6") || informeSeleccionado.equals("7") || informeSeleccionado.equals("8") || informeSeleccionado.equals("9")) {
				nombreReporte = "002659CotizacionesFacturacionArrendamiento";	
				parametros.put("PR_NOMBRE_PERSONALIZDO_REOPRTE", informeSeleccionado.equals("6")?"COTIZACIONES":informeSeleccionado.equals("7")?"COTIZACIONES CREADAS Y EN PROCESO":informeSeleccionado.equals("8")?" COTIZACIONES APROBADAS ":informeSeleccionado.equals("9")?" COTIZACIONES RECHAZADAS Y CANCELADAS ":"");
				reemplazar.put(rp_terceroInicial, terceroInicial.equals("")?"0":terceroInicial);
				reemplazar.put(rp_terceroFinal, terceroFinal.equals("")?"999999999999999999":terceroFinal);
				reemplazar.put("fechaInicial", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaInicial != null?fechaInicial.getTime():new Date("01/01/2024").getTime())));
				reemplazar.put("fechaFinal", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaFinal != null?fechaFinal.getTime():new Date().getTime())));
				reemplazar.put(rp_compania, compania);
				reemplazar.put("tipoCobro", tipoCobro);
				reemplazar.put("estadoFiltro", informeSeleccionado.equals("6")?"":informeSeleccionado.equals("7")?" AND SF_COTIZACION.ESTADO IN (1,2) ":informeSeleccionado.equals("8")?" AND SF_COTIZACION.ESTADO IN (3) ":informeSeleccionado.equals("9")?" AND SF_COTIZACION.ESTADO IN (4,5) ":"");
			}else if(informeSeleccionado.equals("10")) {
				nombreReporte = "002661ContratosFacturacionArredamientos";	
				reemplazar.put(rp_terceroInicial, terceroInicial.equals("")?"0":terceroInicial);
				reemplazar.put(rp_terceroFinal, terceroFinal.equals("")?"999999999999999999":terceroFinal);
				reemplazar.put("fechaInicial", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaInicial != null?fechaInicial.getTime():new Date("01/01/2024").getTime())));
				reemplazar.put("fechaFinal", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaFinal != null?fechaFinal.getTime():new Date().getTime())));
				reemplazar.put(rp_compania, compania);				
			}else if( informeSeleccionado.equals("11") ) {
				nombreReporte = "002662ContratosArrendamientosActivos";	
				reemplazar.put("fechaCorte", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				reemplazar.put(rp_compania, compania);
			}else if (informeSeleccionado.equals("12")){
				nombreReporte = "002663ContratosVencimientos";	
				reemplazar.put("fechaCorte", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				if(fechaCorte.getMonth() > 3) {
					fechaCorte = new Date((fechaCorte.getMonth()-2)+"/01/"+fechaCorte.getYear());
				}else {
					fechaCorte = new Date("01/01/"+fechaCorte.getYear());
				}
				reemplazar.put("fechaPrevia", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime()))); 
				reemplazar.put(rp_compania, compania);
			}else if(informeSeleccionado.equals("13") || informeSeleccionado.equals("14")) {
				nombreReporte = "002664ContratosEstadosVarios";	
				parametros.put("PR_NOMBREREPORTE", informeSeleccionado.equals("13")?"CONTRATOS ACTIVOS":informeSeleccionado.equals("14")?"CONTRATOS SUSPENDIDOS Y CANCELADOS":"");
				reemplazar.put("fechaCorte", new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(fechaCorte != null?fechaCorte.getTime():new Date().getTime())));
				reemplazar.put(rp_compania, compania);
				reemplazar.put("estadoFiltro", informeSeleccionado.equals("13")?" AND SF_CONTRATOS.ESTADO IN (2) ":informeSeleccionado.equals("14")?" AND SF_CONTRATOS.ESTADO IN (3, 5) ":"");
			}
			
			Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar,
					parametros);

			setArchivoDescarga(JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato));
			
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	public void cambiarinformes() {
		if(informeSeleccionado.equals("1")) {
			visibleInformesTercero = true;
			visibleInformesFechaCorte = true;
			visibleInformesEdificio = false;
			visibleInformesInmueble = false;
			visibleInformesFechasGeneral = false;
		}else if(informeSeleccionado.equals("2")) {
			visibleInformesTercero = false;
			visibleInformesFechaCorte = true;
			visibleInformesEdificio = true;
			visibleInformesInmueble = true;
			visibleInformesFechasGeneral = false;
		}else if(informeSeleccionado.equals("3")) {
			visibleInformesTercero = false;
			visibleInformesFechaCorte = false;
			visibleInformesEdificio = true;
			visibleInformesInmueble = true;
			visibleInformesFechasGeneral = false;
		}else if(informeSeleccionado.equals("6") || informeSeleccionado.equals("7") || informeSeleccionado.equals("8") || informeSeleccionado.equals("9")||informeSeleccionado.equals("10")){
			visibleInformesFechasGeneral = true;
			visibleInformesTercero = true;
			visibleInformesFechaCorte = false;
			visibleInformesEdificio = false;
			visibleInformesInmueble = false;
		}else if( informeSeleccionado.equals("11") || informeSeleccionado.equals("12") || informeSeleccionado.equals("13") || informeSeleccionado.equals("14")){
			fechaCorte = new Date();
			visibleInformesFechasGeneral = false;
			visibleInformesTercero = false;
			visibleInformesFechaCorte = true;
			visibleInformesEdificio = false;
			visibleInformesInmueble = false;
		}else {
			visibleInformesFechasGeneral = false;
			visibleInformesTercero = false;
			visibleInformesFechaCorte = false;
			visibleInformesEdificio = false;
			visibleInformesInmueble = false;
		}
	}
	
	/**
	 * metodo cambiar del combo edifico
	 */
	public void cambiaredificioInicial() {
		
	}
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaterceroInicial
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaterceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if(!registroAux.getCampos().isEmpty()) {
			terceroInicial = registroAux.getCampos().get("NIT").toString();
			nombreTerceroInicial = registroAux.getCampos().get("NOMBRE").toString();
		}else {
			terceroInicial = "";
			nombreTerceroInicial = "";
		}
		cargarListaterceroFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaterceroFinal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaterceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if(!registroAux.getCampos().isEmpty()) {
			terceroFinal = registroAux.getCampos().get("NIT").toString();
			nombreTerceroFinal = registroAux.getCampos().get("NOMBRE").toString();
		}else {
			terceroFinal = "";
			nombreTerceroFinal = "";
		}
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodigoInmuebleInicial
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoInmuebleInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if(!registroAux.getCampos().isEmpty()) {
			codigoInmuebleInicial = registroAux.getCampos().get(CODIGOINMUEBLE).toString();
		}else {
			codigoInmuebleInicial = "";
		}
		cargarListacodigoInmuebleFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacodigoInmuebleFinal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoInmuebleFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if(!registroAux.getCampos().isEmpty()) {
			codigoInmuebleFinal = registroAux.getCampos().get(CODIGOINMUEBLE).toString();
		}
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
	/**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
    		String valorDefault)
    {
    	String parametro = null;
    	try {
    		parametro = ejbSysmanUtil.consultarParametro(compania,
    				nombreParametro, SessionUtil.getModulo(),
    				new Date(), true);
    	} catch (com.sysman.exception.SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    	return parametro != null ? parametro : valorDefault;
    }
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable informeSeleccionado
	 * 
	 * @return informeSeleccionado
	 */
	public String getInformeSeleccionado() {
		return informeSeleccionado;
	}

	/**
	 * Asigna la variable informeSeleccionado
	 * 
	 * @param informeSeleccionado Variable a asignar en informeSeleccionado
	 */
	public void setInformeSeleccionado(String informeSeleccionado) {
		this.informeSeleccionado = informeSeleccionado;
	}

	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * Asigna la variable terceroInicial
	 * 
	 * @param terceroInicial Variable a asignar en terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * Asigna la variable terceroFinal
	 * 
	 * @param terceroFinal Variable a asignar en terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * Retorna la variable codigoInmuebleInicial
	 * 
	 * @return codigoInmuebleInicial
	 */
	public String getCodigoInmuebleInicial() {
		return codigoInmuebleInicial;
	}

	/**
	 * Asigna la variable codigoInmuebleInicial
	 * 
	 * @param codigoInmuebleInicial Variable a asignar en codigoInmuebleInicial
	 */
	public void setCodigoInmuebleInicial(String codigoInmuebleInicial) {
		this.codigoInmuebleInicial = codigoInmuebleInicial;
	}

	/**
	 * Retorna la variable codigoInmuebleFinal
	 * 
	 * @return codigoInmuebleFinal
	 */
	public String getCodigoInmuebleFinal() {
		return codigoInmuebleFinal;
	}

	/**
	 * Asigna la variable codigoInmuebleFinal
	 * 
	 * @param codigoInmuebleFinal Variable a asignar en codigoInmuebleFinal
	 */
	public void setCodigoInmuebleFinal(String codigoInmuebleFinal) {
		this.codigoInmuebleFinal = codigoInmuebleFinal;
	}

	/**
	 * Retorna la variable edificoInicial
	 * 
	 * @return edificoInicial
	 */
	public String getEdificioInicial() {
		return edificioInicial;
	}

	/**
	 * Asigna la variable edificoInicial
	 * 
	 * @param edificoInicial Variable a asignar en edificoInicial
	 */
	public void setEdificioInicial(String edificioInicial) {
		this.edificioInicial = edificioInicial;
	}

	/**
	 * Retorna la variable edificoFinal
	 * 
	 * @return edificoFinal
	 */
	public String getEdificioFinal() {
		return edificioFinal;
	}

	/**
	 * Asigna la variable edificoFinal
	 * 
	 * @param edificoFinal Variable a asignar en edificoFinal
	 */
	public void setEdificioFinal(String edificioFinal) {
		this.edificioFinal = edificioFinal;
	}

	/**
	 * Retorna la variable fechaCorte
	 * 
	 * @return fechaCorte
	 */
	public Date getFechaCorte() {
		return fechaCorte;
	}

	/**
	 * Asigna la variable fechaCorte
	 * 
	 * @param fechaCorte Variable a asignar en fechaCorte
	 */
	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaterceroInicial
	 * 
	 * @return listaterceroInicial
	 */
	public RegistroDataModelImpl getListaterceroInicial() {
		return listaterceroInicial;
	}

	/**
	 * Asigna la lista listaterceroInicial
	 * 
	 * @param listaterceroInicial Variable a asignar en listaterceroInicial
	 */
	public void setListaterceroInicial(RegistroDataModelImpl listaterceroInicial) {
		this.listaterceroInicial = listaterceroInicial;
	}

	/**
	 * Retorna la lista listaterceroFinal
	 * 
	 * @return listaterceroFinal
	 */
	public RegistroDataModelImpl getListaterceroFinal() {
		return listaterceroFinal;
	}

	/**
	 * Asigna la lista listaterceroFinal
	 * 
	 * @param listaterceroFinal Variable a asignar en listaterceroFinal
	 */
	public void setListaterceroFinal(RegistroDataModelImpl listaterceroFinal) {
		this.listaterceroFinal = listaterceroFinal;
	}

	/**
	 * Retorna la lista listacodigoInmuebleInicial
	 * 
	 * @return listacodigoInmuebleInicial
	 */
	public RegistroDataModelImpl getListacodigoInmuebleInicial() {
		return listacodigoInmuebleInicial;
	}

	/**
	 * Asigna la lista listacodigoInmuebleInicial
	 * 
	 * @param listacodigoInmuebleInicial Variable a asignar en
	 *                                   listacodigoInmuebleInicial
	 */
	public void setListacodigoInmuebleInicial(RegistroDataModelImpl listacodigoInmuebleInicial) {
		this.listacodigoInmuebleInicial = listacodigoInmuebleInicial;
	}

	/**
	 * Retorna la lista listacodigoInmuebleFinal
	 * 
	 * @return listacodigoInmuebleFinal
	 */
	public RegistroDataModelImpl getListacodigoInmuebleFinal() {
		return listacodigoInmuebleFinal;
	}

	/**
	 * Asigna la lista listacodigoInmuebleFinal
	 * 
	 * @param listacodigoInmuebleFinal Variable a asignar en
	 *                                 listacodigoInmuebleFinal
	 */
	public void setListacodigoInmuebleFinal(RegistroDataModelImpl listacodigoInmuebleFinal) {
		this.listacodigoInmuebleFinal = listacodigoInmuebleFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	public String getCompaniaLabel() {
		return companiaLabel;
	}

	public void setCompaniaLabel(String companiaLabel) {
		this.companiaLabel = companiaLabel;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public boolean isVisibleInformesTercero() {
		return visibleInformesTercero;
	}

	public void setVisibleInformesTercero(boolean visibleInformesTercero) {
		this.visibleInformesTercero = visibleInformesTercero;
	}

	public boolean isVisibleInformesInmueble() {
		return visibleInformesInmueble;
	}

	public void setVisibleInformesInmueble(boolean visibleInformesInmueble) {
		this.visibleInformesInmueble = visibleInformesInmueble;
	}

	public boolean isVisibleInformesEdificio() {
		return visibleInformesEdificio;
	}

	public void setVisibleInformesEdificio(boolean visibleInformesEdificio) {
		this.visibleInformesEdificio = visibleInformesEdificio;
	}

	public boolean isVisibleInformesFechaCorte() {
		return visibleInformesFechaCorte;
	}

	public void setVisibleInformesFechaCorte(boolean visibleInformesFechaCorte) {
		this.visibleInformesFechaCorte = visibleInformesFechaCorte;
	}

	public String getNombreTerceroFinal() {
		return nombreTerceroFinal;
	}

	public void setNombreTerceroFinal(String nombreTerceroFinal) {
		this.nombreTerceroFinal = nombreTerceroFinal;
	}

	public String getNombreTerceroInicial() {
		return nombreTerceroInicial;
	}

	public void setNombreTerceroInicial(String nombreTerceroInicial) {
		this.nombreTerceroInicial = nombreTerceroInicial;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public boolean isVisibleInformesFechasGeneral() {
		return visibleInformesFechasGeneral;
	}

	public void setVisibleInformesFechasGeneral(boolean visibleInformesFechasGeneral) {
		this.visibleInformesFechasGeneral = visibleInformesFechasGeneral;
	}
}
