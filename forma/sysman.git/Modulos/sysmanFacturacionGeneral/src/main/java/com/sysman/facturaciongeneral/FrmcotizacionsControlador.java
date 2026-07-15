/*-
 * FrmcotizacionsControlador.java
 *
 * 1.0
 * 
 * 17/03/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import org.primefaces.model.StreamedContent;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorEnum;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmListadoFacturacionControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmcontratosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmcotizacionsControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmcotizacionsControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.accessibility.AccessibleIcon;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 17/03/2023
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmcotizacionsControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	// <DECLARAR_ATRIBUTOS>
	private String compania;
	private final String modulo;
	private String anocontrato;
	private String tipo;
	private final String tipoCobro;
	private String desTercero;
	private String desCentroCosto;
	private String desAuxiliar1;
	private String numero;
	private String tipoDelReg;
	private String usuario;
	private String sucursal;
	private String informe;
	private String logo;
	private String marcaagua;
	private String firma;
	private boolean verImprimirCotizacion;
	private int anio;
	
	private String valorUnidad;
	private String valor;
	private String iva;
	private String reteFuente;
	private String descuento;
	private String ica;
	private String total;
	/**
	     * Variable que almacena el valor de la tarifa seleccionada en el
	     * detalle de cada concepto
	     */
	    private double vlrTarifa;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 */
	private RegistroDataModelImpl listaTarifa;
	private RegistroDataModelImpl listaTarifaE;
	private RegistroDataModelImpl listaTercero;
	private RegistroDataModelImpl listaTerceroE;
	private RegistroDataModelImpl listaCentroCosto;
	private RegistroDataModelImpl listaCentroCostoE;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaAuxiliarE;
	private RegistroDataModelImpl listaConcepto;
	private RegistroDataModelImpl listaConceptoE;
	private RegistroDataModelImpl listaInmueble;
    private RegistroDataModelImpl listaInmuebleE;
	
	private StreamedContent archivoDescarga;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	private RegistroDataModelImpl listaTercero1;
	private RegistroDataModelImpl listaCentroCosto1;
	private RegistroDataModelImpl listaAuxiliar1;
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
	/**
	 */
	private RegistroDataModelImpl listaSub_conceptoscontrato;
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario
	 */
	private Registro registroSub;
	private Registro regTemp;
	
	/**
         * Variable para determinar si se cargan las tarifas base
         */
        private boolean manejaTarifaBase;

//</DECLARAR_ADICIONALES>
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
private boolean visibleCremil;
private String cuentas;
private double valorIva;
private Boolean aplicaIva;
	private String inmuebleSelecionadoDetalle = "";
	
	private static String SF_MANEJA_TARIFA_POR_TERCERO = "SF MANEJA TARIFA POR TERCERO";
	/**
	 * Crea una nueva instancia de FrmcotizacionsControlador
	 */
	public FrmcotizacionsControlador() {
		super();
		compania = SessionUtil.getCompania();
		tipoCobro = (String) SessionUtil.getSessionVar("tipoCobro");
		usuario = SessionUtil.getUser().getCodigo();
		anocontrato = (String) SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.ANIO.getValue());
		modulo = SessionUtil.getModulo();
		anio = SysmanFunciones.ano(new Date());
		//tipo = (String) SessionUtil.getSessionVar("tipo");
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_COTIZACIONES_CONTROLADOR.getCodigo();
			validarPermisos();
			registroSub = new Registro(new HashMap<String, Object>());
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
		cargarListaTarifa();
		cargarListaTarifaE();
		cargarListaTercero();
		cargarListaTerceroE();
		cargarListaCentroCosto();
		cargarListaCentroCostoE();
		cargarListaAuxiliar();
		cargarListaAuxiliarE();
		cargarListaConcepto();
		cargarListaConceptoE();
		cargarListaTercero1();
		cargarListaCentroCosto1();
		cargarListaAuxiliar1();
		cargarListaInmueble(); 
		cargarListaInmuebleE();
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
//		desTercero = (String) registro.getCampos().get("NOMBRE_TERCERO");
//		desAuxiliar1 = (String) registro.getCampos().get("NOMBRE_AUX");
//		desCentroCosto = (String) registro.getCampos().get("NOMBRE_CENTRO");		
		cargarListaSub_conceptoscontrato();
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
		listaSub_conceptoscontrato = null;
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

		enumBase = GenericUrlEnum.SF_COTIZACION;
		buscarLlave();
		asignarOrigenDatos();
		abrirFormulario();
//		TRY {
//			CARGARLISTA();
//		} CATCH (IOEXCEPTION E) {
//			LOGGER.ERROR(E.GETMESSAGE(), E);
//			JSFUTIL.AGREGARMENSAJEERROR(E.GETMESSAGE());
//		}
		
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
		parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anocontrato);
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {		
	
	    try {
                manejaTarifaBase = "SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania, "SF MANEJA TARIFAS BASE PARA FACTURAR",
                                                SessionUtil.getModulo(), new Date(), true), "NO"));
                
                visibleCremil = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
    					"PERMITIR FACTURACION ARRENDAMIENTO",
    					modulo, new Date(), false));
                
        } catch (SystemException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
	    
	    try {
			//Consultar parametros 
			informe = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,"FORMATO COTIZACION", modulo, new Date(), false),"NO");        
			logo = ejbSysmanUtil.consultarParametro(compania,"FORMATO COTIZACION LOGO 2", modulo, new Date(), false);
			marcaagua = ejbSysmanUtil.consultarParametro(compania,"FORMATO COTIZACION MARCA DE AGUA", modulo, new Date(), false);
			firma = ejbSysmanUtil.consultarParametro(compania,"FORMATO COTIZACION FIRMA", modulo, new Date(), true);
			
			if (informe == null || "NO".equals(informe) ) {
				verImprimirCotizacion = false;
			} else {
				verImprimirCotizacion = true;
			}
			
			logo = "NO".equals(logo)?null:logo;
			marcaagua = "NO".equals(marcaagua)?null:marcaagua;
			firma = "NO".equals(firma)?null:firma;	
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		totalesHeader();
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
		String inicial = "";
		tipo = "COT";
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put("ANOCONTRATO", anocontrato);
		registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
		registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);
		try {
			long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial("SF_COTIZACION",
					"COMPANIA = ''" + compania + "''" + "AND TIPO = ''" + tipo + "''" + " AND EXTRACT(YEAR FROM FECHA_SOLICITUD) = " + anocontrato , "NUMERO", inicial);
			if (consecutivo == 1) {
				inicial = anocontrato + "000001";
				consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial("SF_COTIZACION",
						"COMPANIA = ''" + compania + "''" + "AND TIPO = ''" + tipo + "''" + " AND EXTRACT(YEAR FROM FECHA_SOLICITUD) = " + anocontrato, "NUMERO", inicial);
			}
			registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), consecutivo);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

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
		registro.getCampos().remove("NOMBRE_TERCERO");
		registro.getCampos().remove("NOMBRE_AUX");
		registro.getCampos().remove("NOMBRE_CENTRO");
		registro.getCampos().remove("VALOR_DESCUENTO");
		registro.getCampos().remove("VALOR_ICA");
		registro.getCampos().remove("VALOR_IVA");
		registro.getCampos().remove("SUCURSAL");
		registro.getCampos().remove("VALOR_RETE");
		registro.getCampos().remove("VALOR_TOTAL");
		registro.getCampos().remove("COMPANIA");
		registro.getCampos().remove("TIPO");
		registro.getCampos().remove("VALOR_BASE");
		registro.getCampos().remove("NUMERO");
		registro.getCampos().remove("ANOCONTRATO");
		registro.getCampos().remove("TIPOCONTRATO");
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * 
	 * Carga la lista listaSub_conceptoscontrato
	 *
	 */
	public void cargarListaSub_conceptoscontrato() {
		numero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
		tipoDelReg = registro.getCampos().get("TIPO").toString();

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		//param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));		
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);
		try {

			String urlEnumId = GenericUrlEnum.SF_DETALLE_COTIZACION.getGridKey();
            UrlBean urlSelectSub = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);


		            String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
		                            GenericUrlEnum.SF_DETALLE_COTIZACION.getTable());
		            listaSub_conceptoscontrato = new RegistroDataModelImpl(
		                            urlSelectSub.getUrl(),
		                            urlSelectSub.getUrlConteo().getUrl(),
		                            param, rowKey);

		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaTarifa
	 *
	 */
	public void cargarListaTarifa() {
	    
	    if(manejaTarifaBase) {
	        
	        UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL007.getValue());

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));
                param.put(FrmcotizacionsControladorEnum.TIPOCOBRO.getValue(), String.valueOf(tipoCobro));
                param.put(GeneralParameterEnum.ANO.getName(), anocontrato);

                listaTarifa = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                                GeneralParameterEnum.CODIGO.getName());
	        
	    } else {	    

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL007.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));
		param.put(FrmcotizacionsControladorEnum.TIPO.getValue(), String.valueOf(tipo));
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);

		listaTarifa = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	    }

	}

	/**
	 * 
	 * Carga la lista listaTarifa
	 *
	 */
	public void cargarListaTarifaE() {

		listaTarifaE = listaTarifa;
	}

	/**
	 * 
	 * Carga la lista listaTercero
	 *
	 */
	public void cargarListaTercero() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL002.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmcotizacionsControladorEnum.NIT.getValue());
	}

	/**
	 * 
	 * Carga la lista listaTercero
	 *
	 */
	public void cargarListaTerceroE() {

		listaTerceroE = listaTercero;
	}

	/**
	 * 
	 * Carga la lista listaCentroCosto
	 *
	 */
	public void cargarListaCentroCosto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL001.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);

		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCentroCosto
	 *
	 */
	public void cargarListaCentroCostoE() {

		listaCentroCostoE = listaCentroCosto;
	}

	/**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 */
	public void cargarListaAuxiliar() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL003.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);

		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmcotizacionsControladorEnum.CODIGO.getValue());
	}

	/**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 */
	public void cargarListaAuxiliarE() {

		listaAuxiliarE = listaAuxiliar;
	}

	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 */
	public void cargarListaConcepto() {
        
		UrlBean urlBean = null;
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);
		param.put(FrmcotizacionsControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
		
		if(visibleCremil) {
			
			param.put("CODIGOINMUEBLE", inmuebleSelecionadoDetalle);
			
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL010.getValue());
			
		}else {
			
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL004.getValue());
			
		}

		listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmcotizacionsControladorEnum.CODIGO.getValue());

	}

	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 */
	public void cargarListaConceptoE() {

		listaConceptoE = listaConcepto;
	}

	/**
	 * 
	 * Carga la lista listaTercero1
	 *
	 */
	public void cargarListaTercero1() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL002.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));

		listaTercero1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmcotizacionsControladorEnum.NIT.getValue());
	}

	/**
	 * 
	 * Carga la lista listaCentroCosto1
	 *
	 */
	public void cargarListaCentroCosto1() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL001.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);

		listaCentroCosto1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaAuxiliar1
	 *
	 */
	public void cargarListaAuxiliar1() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL003.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anocontrato);

		listaAuxiliar1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmcotizacionsControladorEnum.CODIGO.getValue());
	}
	
	/**
	 * 
	 * Carga la lista listaInmueble
	 *
	 */
	public void cargarListaInmueble(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL009.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaInmueble = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOINMUEBLE");
	}
	/**
	 * 
	 * Carga la lista listaInmueble
	 *
	 */
	public void  cargarListaInmuebleE(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcotizacionsControladorUrlEnum.URL009.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaInmuebleE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOINMUEBLE");
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>
	
	    /**
	     * Metodo ejecutado al cambiar el control Cantidad
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     */
	public void cambiarCantidad() {
	     
	    double cantidad = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            FrmcotizacionsControladorEnum.CANTIDAD.getValue()), "0")
                                            .toString());
	    
	    double valorUnitario = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            FrmcotizacionsControladorEnum.VALOR_UNITARIO.getValue()), "0")
                                            .toString());
	    
	    double valorNeto = cantidad*valorUnitario;
	    
	    registroSub.getCampos().put(
                            FrmcotizacionsControladorEnum.CANTIDAD.getValue(),cantidad);
	    
	    registroSub.getCampos().put(
                            FrmcotizacionsControladorEnum.VALOR_NETO.getValue(),valorNeto);
	    
	    }
	
	    /**
	     * Metodo ejecutado al cambiar el control ValorUnitario
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     */
	public void cambiarValorUnitario() {
	         //<CODIGO_DESARROLLADO>
	    
	    double cantidad = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            FrmcotizacionsControladorEnum.CANTIDAD.getValue()), "0")
                                            .toString());
            
            double valorUnitario = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            FrmcotizacionsControladorEnum.VALOR_UNITARIO.getValue()), "0")
                                            .toString());
            
            double valorNeto = cantidad*valorUnitario;
            
            registroSub.getCampos().put(
                            FrmcotizacionsControladorEnum.VALOR_BASE.getValue(),valorNeto);
            
            registroSub.getCampos().put(
                            FrmcotizacionsControladorEnum.VALOR_NETO.getValue(),valorNeto);
            
            if(aplicaIva) {
                double vlrIva = (valorNeto * valorIva) / 100; 
                double vlrTotal = vlrIva + valorNeto;
                
                registroSub.getCampos().put(
                        FrmcotizacionsControladorEnum.VALOR_IVA.getValue(), vlrIva);
                
                registroSub.getCampos().put(
                        FrmcotizacionsControladorEnum.VALOR_NETO.getValue(), vlrTotal);
                
            }
	    
	        //</CODIGO_DESARROLLADO>
	    }	

	
	 /**
	     * Metodo ejecutado al cambiar el control Tarifa en la fila
	     * seleccionada dentro de la grilla
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     * @param rowNum
	     * indice de la fila seleccionada
	     */
	public void cambiarTarifaC(int rowNum) {
	         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
	         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
	         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
	         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
	         //<CODIGO_DESARROLLADO>
	        //</CODIGO_DESARROLLADO>
	    }
	    /**
	     * Metodo ejecutado al cambiar el control Cantidad en la fila
	     * seleccionada dentro de la grilla
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     * @param rowNum
	     * indice de la fila seleccionada
	     */
	public void cambiarCantidadC(int rowNum) {
	         
	    calcularTotales(listaSub_conceptoscontrato.getDatasource().get(rowNum % 10));
	    
	    }
	
	/**
	     * Metodo ejecutado al cambiar el control ValorUnitario en la fila
	     * seleccionada dentro de la grilla
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     * @param rowNum
	     * indice de la fila seleccionada
	     */
	public void cambiarValorUnitarioC(int rowNum) {
	         
	    calcularTotales(listaSub_conceptoscontrato.getDatasource().get(rowNum % 10));
	    
	    }
	
	public void cambiarTiempoCotizado() {
		
	}
	
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTarifa
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTarifa(SelectEvent event) {
	    Registro registroAux = (Registro) event.getObject();

	        registroSub.getCampos().put(
	                        FrmcotizacionsControladorEnum.TARIFA.getValue(),
	                        registroAux.getCampos().get(
	                                        GeneralParameterEnum.CODIGO.getName())
	                                        .toString());
	        
	        logicaSeleccionarTarifa(registroAux,
	                        registroSub.getCampos()
	                                        .get(GeneralParameterEnum.CONCEPTO
	                                                        .getName()));
	    

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTarifa
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTarifaE(SelectEvent event) {
	    
	    Registro registroAux = (Registro) event.getObject();
	    
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTercero
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTercero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTercero
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("NIT");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCosto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CENTRO_COSTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCosto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("AUXILIAR", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaConcepto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 * @throws SystemException 
	 */
	public void seleccionarFilaConcepto(SelectEvent event) throws SystemException {
		Registro registroAux = (Registro) event.getObject();

		
		// cambioConcepto = true;
		registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

		registroSub.getCampos().put(FrmcotizacionsControladorEnum.NOMBRECONCEPTO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        if(!visibleCremil) {
		registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(),
				SysmanFunciones.nvl(registro.getCampos().get("CANTIDAD_GRUPO"), "1"));
        }
		registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));

		registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		
		String param = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, SF_MANEJA_TARIFA_POR_TERCERO, "69", new Date(), false), "NO");
		if(param.equals(GeneralParameterEnum.SI.getName())) {
			valorTarifaConceptosPorTercero(registro.getCampos().get(GeneralParameterEnum.NUMEROLISTA.getName()).toString(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
		}
		if(visibleCremil) {
			registroSub.getCampos().put("VALOR_UNITARIO",
					registroAux.getCampos().get("VALOR_BASE"));
			aplicaIva = Boolean.valueOf(registroAux.getCampos().get("APLICAIVA") != null ? registroAux.getCampos().get("APLICAIVA").toString():"false");
			valorIva = Double.parseDouble(SysmanFunciones.toString(SysmanFunciones.nvl(registroAux.getCampos().get("PORCENTAJEIVA"),0)));
			cambiarValorUnitario();
		}
//        extraerValorTarifa(registroSub.getCampos()
//                        .get(FrmcotizacionsControladorEnum.TARIFA.getValue()));
//
//        obtenerRegistroConceptos(registroAux.getCampos()
//                        .get(GeneralParameterEnum.CODIGO.getName()));
//
//        cambioConcepto = false;
	}
	
	public void valorTarifaConceptosPorTercero(String numeroLista, String concepto) throws SystemException {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("NUMEROLISTA", SysmanFunciones.nvl(numeroLista, ""));
		param.put("CONCEPTO", SysmanFunciones.nvl(concepto, ""));

		Registro reg = RegistroConverter
				.toRegistro(
						requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmcotizacionsControladorUrlEnum.URL008.getValue())
										.getUrl(),
								param));

		registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_UNITARIO.getValue(),
				reg.getCampos().get(FacturacionconceptosControladorEnum.VALOR.getValue()));
		
		registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_BASE.getValue(),
				new BigDecimal(reg.getCampos().get(FacturacionconceptosControladorEnum.VALOR.getValue()).toString()).multiply(new BigDecimal(registroSub.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString())));
		
		registroSub.getCampos().put(GeneralParameterEnum.NUMEROLISTA.getName(),
				new BigDecimal(numeroLista));
		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaConcepto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoE(SelectEvent event) {
		
	}

	/**	
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTercero1
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTercero1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
		registro.getCampos().put("NOMBRE_TERCERO", registroAux.getCampos().get("NOMBRE"));
		sucursal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "")
				.toString();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCosto1
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCosto1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CENTRO_COSTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put("NOMBRE_CENTRO", registroAux.getCampos().get("NOMBRE"));

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar1
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("AUXILIAR", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put("NOMBRE_AUX", registroAux.getCampos().get("NOMBRE"));
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaInmueble
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaInmueble(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		inmuebleSelecionadoDetalle = registroAux.getCampos().get("CODIGOINMUEBLE").toString();
		registroSub.getCampos().put("DENOMINACION", SysmanFunciones.nvl(SysmanFunciones.toString(registroAux.getCampos().get("DENOMINACION")),""));
		registroSub.getCampos().put("INMUEBLE", registroAux.getCampos().get("CODIGOINMUEBLE"));
		registroSub.getCampos().put("TIPO_INMUEBLE", registroAux.getCampos().get("TIPO"));
		registroSub.getCampos().put("CANTIDAD", registroAux.getCampos().get("AREA"));

		String concepto1 = SysmanFunciones.toString(registroAux.getCampos().get("CONCEPTO1"));
	    String concepto2 = SysmanFunciones.toString(registroAux.getCampos().get("CONCEPTO2"));
	    String concepto3 = SysmanFunciones.toString(registroAux.getCampos().get("CONCEPTO3"));
	    String concepto4 = SysmanFunciones.toString(registroAux.getCampos().get("CONCEPTO4"));
	    
	    StringBuilder concatenacion = new StringBuilder();
	    if (concepto1 != null && !concepto1.isEmpty()) {
	        concatenacion.append(concepto1);
	    }
	    if (concepto2 != null && !concepto2.isEmpty()) {
	        if (concatenacion.length() > 0) concatenacion.append(", ");
	        concatenacion.append(concepto2);
	    }
	    if (concepto3 != null && !concepto3.isEmpty()) {
	        if (concatenacion.length() > 0) concatenacion.append(", ");
	        concatenacion.append(concepto3);
	    }
	    if (concepto4 != null && !concepto4.isEmpty()) {
	        if (concatenacion.length() > 0) concatenacion.append(", ");
	        concatenacion.append(concepto4);
	    }
	    
	    cuentas = SysmanFunciones.toString(concatenacion);
	    registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(), null);
	    cargarListaConcepto();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaInmueble
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaInmuebleE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGOINMUEBLE");
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
	 *
	 *
	 */
	public void oprimirCmdImprimir() {
		archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
	}
	
	private void generaInforme(FORMATOS formato) {

	        try {
	            Map<String, Object> parametros = new HashMap<>();
	            HashMap<String, Object> reemplazar = new HashMap<>();
	            
	            numero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
	    		tipo = registro.getCampos().get("TIPO").toString();
	    		
	    		if (numero!=null) {
			            reemplazar.put("compania", compania);
			            reemplazar.put("tipo", tipo);
			            reemplazar.put("anio", anocontrato);
			            reemplazar.put("numero", numero);
			            reemplazar.put("tipocobro", tipoCobro);
			           
			            parametros.put("PR_TIPOCOBRO", "nombreTipoCobro");
			            parametros.put("PR_FECHAINICIAL", "SysmanFunciones.convertirAFechaCadena(fechaIncial)");
			            parametros.put("PR_FECHAFINAL", "SysmanFunciones.convertirAFechaCadena(fechaFinal)");
			            parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
						parametros.put("PR_NITCOMPANIA",SessionUtil.getCompaniaIngreso().getNit());
						parametros.put("PR_LOGO2",logo);
						parametros.put("PR_MARCAAGUA",marcaagua); 
						parametros.put("PR_FIRMA",firma); 
						
						String usuario = SysmanFunciones.concatenar(
                                SessionUtil.getUser().getNombre1(), " ",
                                SessionUtil.getUser().getApellido1(), " ",
                                SessionUtil.getUser().getApellido2());
						String nombreCoordinador = ejbSysmanUtil.consultarParametro(compania,"NOMBRE COORDINADOR AUTORIZACION COTIZACION", modulo, new Date(), false);
						String cargoCoordinador = ejbSysmanUtil.consultarParametro(compania,"CARGO COORDINADOR AUTORIZACION COTIZACION", modulo, new Date(), false);
						String proyecto = ejbSysmanUtil.consultarParametro(compania,"PROYECTO COTIZACION", modulo, new Date(), false);
						parametros.put("PR_NOMBRE_COORDINADOR_AUTORIZACION", nombreCoordinador); 
						parametros.put("PR_CARGO_COORDINADOR_AUTORIZACION", cargoCoordinador); 
						parametros.put("PR_PROYECTO_COTIZACION", proyecto);
						parametros.put("PR_USER", usuario);
						
						String resolucionCotizacion = ejbSysmanUtil.consultarParametro(compania,"RESOLUCION COTIZACION", modulo, new Date(), false);
						parametros.put("PR_RESOLUCION_COTIZACION", resolucionCotizacion);
						
						Reporteador.resuelveConsulta(informe,Integer.parseInt(modulo),reemplazar, parametros);
			            archivoDescarga = JsfUtil.exportarStreamed(
			            				informe,
			                            parametros,
			                            ConectorPool.ESQUEMA_SYSMAN,
			                            formato);
	    		}
	        }
	        catch (FileNotFoundException ex) {
	            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
	                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
	                            ex.getMessage(), " ",informe));
	            Logger.getLogger(FrmlistadoRecaudoDifControlador.class.getName())
	                            .log(Level.SEVERE, null, ex);
	        }
	        catch ( JRException | IOException | SysmanException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        } catch (SystemException e) {
	        	logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
			}

	}

//</METODOS_BOTONES>	
	public void totalesHeader() {
		try {
				numero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName())!= null?registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString():"0";
				tipo = registro.getCampos().get("TIPO")!=null?registro.getCampos().get("TIPO").toString():"";
				double meses = registro.getCampos().get("TIEMPO_COTIZADO")!=null?Double.parseDouble(SysmanFunciones.toString(registro.getCampos().get("TIEMPO_COTIZADO"))):0;
				
		
		        Map<String, Object> paramDisminuido = new HashMap<>();
		        paramDisminuido.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		        paramDisminuido.put(GeneralParameterEnum.NUMERO.getName(), numero);
		        paramDisminuido.put(GeneralParameterEnum.TIPO.getName(), tipo);
		
		        List<Registro> rs;
				
					rs = RegistroConverter
					                .toListRegistro(requestManager
			                                .getList(UrlServiceUtil
	                                                .getInstance()
	                                                .getUrlServiceByUrlByEnumID(
	                                                		FrmcotizacionsControladorUrlEnum.URL011.getValue())
	                                                .getUrl(), paramDisminuido));
				
		        if (!rs.isEmpty())
		        {		        	
		        	double valorNeto = 0;
		        	double cantidad = 0;	        	
		        	for(Registro reg: rs) {
		        		valorNeto = valorNeto + Double.parseDouble(SysmanFunciones.toString(reg.getCampos().get("VALOR_NETO")));
		        		cantidad = cantidad + Double.parseDouble(SysmanFunciones.toString(reg.getCampos().get("CANTIDAD")));
		        	}
		        	double total = meses * valorNeto;
		        	registro.getCampos().put("TOTAL_PERIODO", valorNeto);
		        	registro.getCampos().put("TOTAL_CONTRATO", total);
		        	registro.getCampos().put("TOTAL_AREA", cantidad);
		        }else {
		        	registro.getCampos().put("TOTAL_PERIODO",0);
		        	registro.getCampos().put("TOTAL_CONTRATO",0);
		        	registro.getCampos().put("TOTAL_AREA",0);
		        }
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
			}
	}
//<METODOS_SUBFORM>	
	/**
	 * Metodo de insercion del formulario Sub_conceptoscontrato
	 * 
	 */
	public void agregarRegistroSubSub_conceptoscontrato() {

		try {

			registroSub.getCampos().remove(FrmcotizacionsControladorEnum.VALOR_UNIDAD.getValue());
			registroSub.getCampos().remove(GeneralParameterEnum.ANO.getName());

			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(GeneralParameterEnum.TIPO.getName(),
					registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
			registroSub.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			registroSub.getCampos().remove(FrmcotizacionsControladorEnum.NOMBRECONCEPTO.getValue());
			registroSub.getCampos().remove(GeneralParameterEnum.NUMEROLISTA.getName());
			registroSub.getCampos().remove("DENOMINACION");
			
			// se valida los conceptos existentes
			if(!visibleCremil) {
				Map<String, Object> param = new TreeMap<>();
			
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.NUMERO.getName(), SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.NUMERO.getName()), ""));
				param.put(GeneralParameterEnum.CONCEPTO.getName(), SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), ""));
	
				Registro reg = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmcotizacionsControladorUrlEnum.URL012.getValue())
												.getUrl(),
										param));
				if(!reg.getCampos().isEmpty()) {
					if(Integer.parseInt(reg.getCampos().get("EXISTE").toString()) > 0) {
						JsfUtil.agregarMensajeError("El concepto ya se encuentra asociado a un detalle.");
						return;
					}else {
						registroSub.getCampos().put("INMUEBLE",(int) SysmanFunciones.nvl(reg.getCampos().get("CONSEC"), 0) + 1);
					}
				}
			}
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.SF_DETALLE_COTIZACION.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
//            cargarListaSubconceptoscontrato();
//
//            cargarTotales();
			totalesHeader();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	/**
	 * Metodo de edicion del formulario Sub_conceptoscontrato
	 * 
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSub_conceptoscontrato(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {

			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(FrmcotizacionsControladorEnum.NOMBRECONCEPTO.getValue());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			reg.getCampos().remove(GeneralParameterEnum.NUMEROLISTA.getName());
			reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
			reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
			reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
			reg.getCampos().remove(GeneralParameterEnum.TIPO.getName());
			reg.getCampos().remove("DENOMINACION");

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.SF_DETALLE_COTIZACION.getUpdateKey());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

//	            cargarTotales();
			totalesHeader();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
//            cargarListaSubconceptoscontrato();

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo de eliminacion del formulario Sub_conceptoscontrato
	 * 
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSub_conceptoscontrato(Registro reg) {
		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.SF_DETALLE_COTIZACION.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			totalesHeader();
//            cargarTotales();
//            cargarListaSubconceptoscontrato();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Sub_conceptoscontrato
	 *
	 */
	public void cancelarEdicionSub_conceptoscontrato() {
	}

//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
//</METODOS_ADICIONALES>

	private void logicaSeleccionarTarifa(Registro reg, Object concepto) {

	        if(manejaTarifaBase) {
	            
	            vlrTarifa = retornarDouble(reg,
	                            GeneralParameterEnum.PORCENTAJE.getName());
	            
	            registroSub.getCampos().put(
	                            FrmcotizacionsControladorEnum.VALOR_UNITARIO.getValue(),vlrTarifa); 
	            
	            registroSub.getCampos().put(
	                            FrmcotizacionsControladorEnum.VALOR_BASE.getValue(),vlrTarifa);
	            
	            registroSub.getCampos().put(
	                            FrmcotizacionsControladorEnum.CANTIDAD.getValue(),1);
	            
	            registroSub.getCampos().put(
	                            FrmcotizacionsControladorEnum.VALOR_NETO.getValue(),vlrTarifa);
	            
	                       
	        }
	        
	}
	
	private double retornarDouble(Registro reg, String campo) {
	        return Double.parseDouble(SysmanFunciones
	                        .nvl(reg.getCampos().get(campo), "0").toString());
	    }
	
	
	public void calcularTotales(Registro auxRegistro) {
	    
	    double cantidad = Double
	                        .parseDouble(SysmanFunciones
	                                        .nvl(auxRegistro.getCampos().get(
	                                                        FrmcotizacionsControladorEnum.CANTIDAD.getValue()), "0.0")
	                                        .toString());
	    double valorUnitario = Double
	                        .parseDouble(SysmanFunciones
	                                        .nvl(auxRegistro.getCampos().get(
	                                                        FrmcotizacionsControladorEnum.VALOR_UNITARIO.getValue()), "0.0")
	                                        .toString());
	    
	    double valorNeto = cantidad*valorUnitario;
	    
	    auxRegistro.getCampos().put(FrmcotizacionsControladorEnum.VALOR_NETO.getValue(),
                            valorNeto);
	    
	    auxRegistro.getCampos().put(FrmcotizacionsControladorEnum.VALOR_BASE.getValue(),
                            valorNeto);
	    
	}
	

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		regTemp =  null;
		regTemp=registro;	
		  if (accion.equals(ACCION_MODIFICAR)) {
				
				registro.getCampos().remove("NUMCONTRATO");
				registro.getCampos().remove("NOMBRE_TERCERO");
				registro.getCampos().remove("NOMBRE_AUX");
				registro.getCampos().remove("NOMBRE_CENTRO");
				registro.getCampos().remove("VALOR_DESCUENTO");
				registro.getCampos().remove("VALOR_ICA");
				registro.getCampos().remove("VALOR_IVA");
				registro.getCampos().remove("SUCURSAL");
				registro.getCampos().remove("VALOR_RETE");
				registro.getCampos().remove("VALOR_TOTAL");
			    registro.getCampos().remove("COMPANIA");
				registro.getCampos().remove("TIPO");
				registro.getCampos().remove("VALOR_BASE");
				registro.getCampos().remove("NUMERO");
				registro.getCampos().remove("ANOCONTRATO");
				registro.getCampos().remove("TIPOCONTRATO");
		    }

		registro.getCampos().remove("NUMCONTRATO");
		registro.getCampos().remove("NOMBRE_TERCERO");
		registro.getCampos().remove("NOMBRE_AUX");
		registro.getCampos().remove("NOMBRE_CENTRO");
		registro.getCampos().remove("VALOR_DESCUENTO");
		registro.getCampos().remove("VALOR_ICA");
		registro.getCampos().remove("VALOR_IVA");
		registro.getCampos().remove("VALOR_RETE");
		registro.getCampos().remove("VALOR_TOTAL");
		registro.getCampos().remove("VALOR_BASE");
		registro.getCampos().remove("TIPOCONTRATO");
		registro.getCampos().remove("NUMEROLISTA");
	
		// </CODIGO_DESARROLLADO>
		return true;
	}
	
	

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		registro = regTemp;
		
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
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
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
//		try {
//			cargarLista();
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//			JsfUtil.agregarMensajeError(e.getMessage());
//		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable desTercero
	 * 
	 * @return desTercero
	 */
	public String getDesTercero() {
		return desTercero;
	}

	/**
	 * Asigna la variable desTercero
	 * 
	 * @param desTercero Variable a asignar en desTercero
	 */
	public void setDesTercero(String desTercero) {
		this.desTercero = desTercero;
	}

	/**
	 * Retorna la variable desCentroCosto
	 * 
	 * @return desCentroCosto
	 */
	public String getDesCentroCosto() {
		return desCentroCosto;
	}

	/**
	 * Asigna la variable desCentroCosto
	 * 
	 * @param desCentroCosto Variable a asignar en desCentroCosto
	 */
	public void setDesCentroCosto(String desCentroCosto) {
		this.desCentroCosto = desCentroCosto;
	}

	/**
	 * Retorna la variable desAuxiliar1
	 * 
	 * @return desAuxiliar1
	 */
	public String getDesAuxiliar1() {
		return desAuxiliar1;
	}

	/**
	 * Asigna la variable desAuxiliar1
	 * 
	 * @param desAuxiliar1 Variable a asignar en desAuxiliar1
	 */
	public void setDesAuxiliar1(String desAuxiliar1) {
		this.desAuxiliar1 = desAuxiliar1;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTarifa
	 * 
	 * @return listaTarifa
	 */
	public RegistroDataModelImpl getListaTarifa() {
		return listaTarifa;
	}

	/**
	 * Asigna la lista listaTarifa
	 * 
	 * @param listaTarifa Variable a asignar en listaTarifa
	 */
	public void setListaTarifa(RegistroDataModelImpl listaTarifa) {
		this.listaTarifa = listaTarifa;
	}

	/**
	 * Retorna la lista listaTarifa
	 * 
	 * @return listaTarifa
	 */
	public RegistroDataModelImpl getListaTarifaE() {
		return listaTarifaE;
	}

	/**
	 * Asigna la lista listaTarifa
	 * 
	 * @param listaTarifa Variable a asignar en listaTarifa
	 */
	public void setListaTarifaE(RegistroDataModelImpl listaTarifaE) {
		this.listaTarifaE = listaTarifaE;
	}

	/**
	 * Retorna la lista listaTercero
	 * 
	 * @return listaTercero
	 */
	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	/**
	 * Asigna la lista listaTercero
	 * 
	 * @param listaTercero Variable a asignar en listaTercero
	 */
	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	/**
	 * Retorna la lista listaTercero
	 * 
	 * @return listaTercero
	 */
	public RegistroDataModelImpl getListaTerceroE() {
		return listaTerceroE;
	}

	/**
	 * Asigna la lista listaTercero
	 * 
	 * @param listaTercero Variable a asignar en listaTercero
	 */
	public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
		this.listaTerceroE = listaTerceroE;
	}

	/**
	 * Retorna la lista listaCentroCosto
	 * 
	 * @return listaCentroCosto
	 */
	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}

	/**
	 * Asigna la lista listaCentroCosto
	 * 
	 * @param listaCentroCosto Variable a asignar en listaCentroCosto
	 */
	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}

	/**
	 * Retorna la lista listaCentroCosto
	 * 
	 * @return listaCentroCosto
	 */
	public RegistroDataModelImpl getListaCentroCostoE() {
		return listaCentroCostoE;
	}

	/**
	 * Asigna la lista listaCentroCosto
	 * 
	 * @param listaCentroCosto Variable a asignar en listaCentroCosto
	 */
	public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
		this.listaCentroCostoE = listaCentroCostoE;
	}

	/**
	 * Retorna la lista listaAuxiliar
	 * 
	 * @return listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	/**
	 * Asigna la lista listaAuxiliar
	 * 
	 * @param listaAuxiliar Variable a asignar en listaAuxiliar
	 */
	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	/**
	 * Retorna la lista listaAuxiliar
	 * 
	 * @return listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliarE() {
		return listaAuxiliarE;
	}

	/**
	 * Asigna la lista listaAuxiliar
	 * 
	 * @param listaAuxiliar Variable a asignar en listaAuxiliar
	 */
	public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
		this.listaAuxiliarE = listaAuxiliarE;
	}

	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public RegistroDataModelImpl getListaConcepto() {
		return listaConcepto;
	}

	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto Variable a asignar en listaConcepto
	 */
	public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
		this.listaConcepto = listaConcepto;
	}

	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public RegistroDataModelImpl getListaConceptoE() {
		return listaConceptoE;
	}

	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto Variable a asignar en listaConcepto
	 */
	public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
		this.listaConceptoE = listaConceptoE;
	}

	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	
	 public String getSucursal() {
	        return sucursal;
	    }

	    public void setSucursal(String sucursal) {
	        this.sucursal = sucursal;
	    }
	
	/**
	 * Retorna la lista listaTercero1
	 * 
	 * @return listaTercero1
	 */
	public RegistroDataModelImpl getListaTercero1() {
		return listaTercero1;
	}

	/**
	 * Asigna la lista listaTercero1
	 * 
	 * @param listaTercero1 Variable a asignar en listaTercero1
	 */
	public void setListaTercero1(RegistroDataModelImpl listaTercero1) {
		this.listaTercero1 = listaTercero1;
	}

	/**
	 * Retorna la lista listaCentroCosto1
	 * 
	 * @return listaCentroCosto1
	 */
	public RegistroDataModelImpl getListaCentroCosto1() {
		return listaCentroCosto1;
	}

	/**
	 * Asigna la lista listaCentroCosto1
	 * 
	 * @param listaCentroCosto1 Variable a asignar en listaCentroCosto1
	 */
	public void setListaCentroCosto1(RegistroDataModelImpl listaCentroCosto1) {
		this.listaCentroCosto1 = listaCentroCosto1;
	}

	/**
	 * Retorna la lista listaAuxiliar1
	 * 
	 * @return listaAuxiliar1
	 */
	public RegistroDataModelImpl getListaAuxiliar1() {
		return listaAuxiliar1;
	}

	/**
	 * Asigna la lista listaAuxiliar1
	 * 
	 * @param listaAuxiliar1 Variable a asignar en listaAuxiliar1
	 */
	public void setListaAuxiliar1(RegistroDataModelImpl listaAuxiliar1) {
		this.listaAuxiliar1 = listaAuxiliar1;
	}

	/**
	 * Retorna la lista listaSub_conceptoscontrato
	 * 
	 * @return listaSub_conceptoscontrato
	 */
	public RegistroDataModelImpl getListaSub_conceptoscontrato() {
		return listaSub_conceptoscontrato;
	}

	/**
	 * Asigna la lista listaSub_conceptoscontrato
	 * 
	 * @param listaSub_conceptoscontrato Variable a asignar en
	 *                                   listaSub_conceptoscontrato
	 */
	public void setListaSub_conceptoscontrato(RegistroDataModelImpl listaSub_conceptoscontrato) {
		this.listaSub_conceptoscontrato = listaSub_conceptoscontrato;
	}

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

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getInforme() {
		return informe;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMarcaagua() {
		return marcaagua;
	}

	public void setMarcaagua(String marcaagua) {
		this.marcaagua = marcaagua;
	}

	public boolean isVerImprimirCotizacion() {
		return verImprimirCotizacion;
	}

	public void setVerImprimirCotizacion(boolean verImprimirCotizacion) {
		this.verImprimirCotizacion = verImprimirCotizacion;
	}

	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}

	/**
	 * @return the visibleCremil
	 */
	public boolean isVisibleCremil() {
		return visibleCremil;
	}

	/**
	 * @param visibleCremil the visibleCremil to set
	 */
	public void setVisibleCremil(boolean visibleCremil) {
		this.visibleCremil = visibleCremil;
	}

	
	
	
	//Problemas con la forma 
	/**
	 * @return the valorUnidad
	 */
	public String getValorUnidad() {
		return valorUnidad;
	}

	/**
	 * @param valorUnidad the valorUnidad to set
	 */
	public void setValorUnidad(String valorUnidad) {
		this.valorUnidad = valorUnidad;
	}

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * @return the iva
	 */
	public String getIva() {
		return iva;
	}

	/**
	 * @param iva the iva to set
	 */
	public void setIva(String iva) {
		this.iva = iva;
	}

	/**
	 * @return the reteFuente
	 */
	public String getReteFuente() {
		return reteFuente;
	}

	/**
	 * @param reteFuente the reteFuente to set
	 */
	public void setReteFuente(String reteFuente) {
		this.reteFuente = reteFuente;
	}

	/**
	 * @return the ica
	 */
	public String getIca() {
		return ica;
	}

	/**
	 * @param ica the ica to set
	 */
	public void setIca(String ica) {
		this.ica = ica;
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * @return the descuento
	 */
	public String getDescuento() {
		return descuento;
	}

	/**
	 * @param descuento the descuento to set
	 */
	public void setDescuento(String descuento) {
		this.descuento = descuento;
	}

	/**
	 * @return the listaInmueble
	 */
	public RegistroDataModelImpl getListaInmueble() {
		return listaInmueble;
	}

	/**
	 * @param listaInmueble the listaInmueble to set
	 */
	public void setListaInmueble(RegistroDataModelImpl listaInmueble) {
		this.listaInmueble = listaInmueble;
	}

	/**
	 * @return the listaInmuebleE
	 */
	public RegistroDataModelImpl getListaInmuebleE() {
		return listaInmuebleE;
	}

	/**
	 * @param listaInmuebleE the listaInmuebleE to set
	 */
	public void setListaInmuebleE(RegistroDataModelImpl listaInmuebleE) {
		this.listaInmuebleE = listaInmuebleE;
	}

}