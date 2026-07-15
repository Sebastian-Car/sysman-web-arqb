/*-
 * FrmCausacionAutomaticaControlador.java
 *
 * 1.0
 * 
 * 07/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.FrmCausacionAutomaticaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
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

/**
 * CAUSACION AUTOMATICA
 *
 * @version 1.0, 07/05/2024
 * @author User
 */
@ManagedBean
@ViewScoped
public class  FrmCausacionAutomaticaControlador  extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private Registro registroSub;
	boolean retorno = false;
	int varCausar;			   
	private final String compania;
	private final String modulo;
	private String auxiliar;
	private String ano;
	private String tipoRetencion;
	private String concepto;
	private String proveedor;
	private String clase;
	
	private StreamedContent archivoDescarga;

	private List<Registro> listaretencion;

	private List<Registro> listaretencionE;

	private RegistroDataModelImpl listacodigo;

	private RegistroDataModelImpl listacodigoE;

	private List<Registro> listaSubretenciones;

	private RegistroDataModelImpl listaConcepto;

	private RegistroDataModelImpl listaProveedor;
	
	private RegistroDataModelImpl listaReferenciado;
	
	private RegistroDataModelImpl listaCentroCosto;
	
	private RegistroDataModelImpl listaAnticipo;

	private List<Registro> listaTipo;

	private String tipoComp;

	private String numeroComp;

	private String tercero;

	private boolean visibleAceptaCausacion = false;

	/**
	 * Campo TIPORETENCION.
	 */
	private static final String CAMPO_TIPORETENCION = "TIPORETENCION";
	/**
	 * Campo VALORBASE.
	 */
	private static final String CAMPO_VALORBASE = "VALORBASE";
	/**
	 * Campo CODIGORETENCION.
	 */
	private static final String CAMPO_CODIGORETENCION = "CODIGORETENCION";

	@EJB
	private EjbContabilidadTresRemote ejbContabilidadTres;
	
    @EJB	
	EjbSysmanUtilRemote ejbSysmanUtil;

	private Map<String, Object> ridComprobante;

	private String mes;

	private String nombreComprobante;

	private double vlrBaseIva;

	private double vlrBase;

	private String opcionMenu;

	private String sucursal;

	private boolean manejaTercero;

	private int cantidad;
	
	private boolean referenciadoVisible;
	
	private boolean manejaCausacion = false;
	
	private String centroCostoAnt;
	private String referenciadoAnt;
	private String proveedorAnt;
	private boolean mostrarInsert;
	private boolean mostrarUpdate;
	private boolean mostrarDelete;

	/**
	 * Crea una nueva instancia de FrmCausacionAutomaticaControlador
	 */
	public FrmCausacionAutomaticaControlador() {
		super();
		SessionUtil.setSessionVar("modulo", "1");
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null)
			{
				ridComprobante = (Map<String, Object>) parametrosEntrada.get("rid");
				mes = extraerString(parametrosEntrada.get("mes"));
				ano = extraerString(parametrosEntrada.get("ano"));
				tipoComp = extraerString(parametrosEntrada.get("tipoComp"));
				numeroComp = extraerString(parametrosEntrada.get("numeroComp"));
				tercero = extraerString(parametrosEntrada.get("tercero"));
				nombreComprobante = extraerString(parametrosEntrada.get("nombreComprobante"));
				String vlrBaseIvaE = parametrosEntrada.get("vlrBaseIva").toString();
				vlrBaseIva = Double.parseDouble(vlrBaseIvaE);
				String vlrBaseE = parametrosEntrada.get("vlrBase").toString();
				vlrBase = Double.parseDouble(vlrBaseE);
				opcionMenu = extraerString(parametrosEntrada.get("opcionMenu"));
				sucursal = extraerString(parametrosEntrada.get("sucursal"));
				clase = extraerString(parametrosEntrada.get("claseComprobante"));
			}
			else{
				SessionUtil.redireccionarMenuPermisos();
			} 
			numFormulario = GeneralCodigoFormaEnum.FRM_CAUSACION_AUTOMATICA.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			mostrarDelete = true;
			mostrarInsert = true;
			mostrarUpdate = true;
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		try {
			enumBase=GenericUrlEnum.CAUSACION_AUTOMATICA;
			buscarLlave();
			asignarOrigenDatos();
			validarDetallesContables();

			manejaTercero = ejbSysmanUtil.consultarParametro(compania, "MANEJA RETENCIONES POR TERCERO", modulo, new Date(), true).equals("SI");			
			referenciadoVisible = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA REFERENCIADO EN CAUSACION AUTOMATICA", modulo, new Date(), true), "NO").equals("SI") ? true
							: false;
			
			manejaCausacion = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CAUSACION AUTOMATICA", modulo, new Date(), true),
					"NO").toString().equals("SI")?true:false;
			
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}	
	
	@Override
	public void cargarRegistro() {
		precargarRegistro();
		cargarListaReferenciado(); 
		referenciadoAnt=SysmanFunciones.toString(registro.getCampos().get("REFERENCIADO"));
		centroCostoAnt=SysmanFunciones.toString(registro.getCampos().get("CENTROCOSTO"));
		proveedorAnt=SysmanFunciones.toString(registro.getCampos().get("PROVEEDOR"));
		String valorBase = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.VALOR.getName()), "0").toString();
		if (valorBase.equals("0")) {
			DecimalFormat df = new DecimalFormat("#.##");
			String valor = df.format(vlrBase+vlrBaseIva);
			registro.getCampos().put(GeneralParameterEnum.VALOR.getName(), valor);
		}
				
	}
	@Override
	public void iniciarListasSubNulo() {
		listaSubretenciones = null;		
	}
	@Override
	public void iniciarListasSub() {
		cargarListaSubretenciones();		
	}
	@Override
	public void iniciarListas() {
		cargarListaretencion(); cargarListaretencionE();
		cargarListacodigo(); cargarListacodigoE();
		cargarListaConcepto(); cargarListaProveedor();
		cargarListaCentroCosto();
		cargarListaAnticipo();

	}
	private void cargarListaProveedor() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL1931003.getValue());
		listaProveedor = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());		
	}
	private void cargarListaConcepto() {

		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CLASE.getName(), clase);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL1930004.getValue());
		listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListaReferenciado() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CONCEPTO.getName(),registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL1936001.getValue());
		
		listaReferenciado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListaCentroCosto(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmCausacionAutomaticaControladorUrlEnum.URL20001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
	
	public void cargarListaAnticipo(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmCausacionAutomaticaControladorUrlEnum.URL1933004
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		param.put(GeneralParameterEnum.ANO.getName(),ano);

		listaAnticipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
		parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
		parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
		
	}


	public void cargarListaSubretenciones(){
		try {

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69008.getValue());

			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
			param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
			param.put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			param.put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			param.put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
			param.put("CONCEPTO", registro.getCampos().get("CONCEPTO"));
			
			listaSubretenciones = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), param),
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.COMPROBANTE_CNTRETENCION.getTable()));			
		} catch (SystemException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

	}

	public void cargarListaTipo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaTipo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmCausacionAutomaticaControladorUrlEnum.URL15019
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaretencion(){

		try {
			listaretencion = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmCausacionAutomaticaControladorUrlEnum.URL8005
									.getValue())
							.getUrl(), null));
			
			Boolean RetencionXRegimen = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CONFIGURACION DE RETENCIONES POR REGIMEN", SessionUtil.getModulo(), new Date(), true),
					"NO").toString().equals("SI")?true:false;
        	if(RetencionXRegimen) {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.NIT.getName(), tercero);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
        		
                listaretencion = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		FrmCausacionAutomaticaControladorUrlEnum.URL8014
                                                                        .getValue())
                                        .getUrl(), param));
        	}

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}


	public void  cargarListaretencionE(){
		try {
			listaretencionE = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmCausacionAutomaticaControladorUrlEnum.URL8005
									.getValue())
							.getUrl(), null));
			
			Boolean RetencionXRegimen = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CONFIGURACION DE RETENCIONES POR REGIMEN", SessionUtil.getModulo(), new Date(), true),
					"NO").toString().equals("SI")?true:false;
        	if(RetencionXRegimen) {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.NIT.getName(), tercero);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
        		
                listaretencionE = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		FrmCausacionAutomaticaControladorUrlEnum.URL8014
                                                                        .getValue())
                                        .getUrl(), param));
        	}
        	
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacodigo
	 *
	 */
	public void cargarListacodigo(){

		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoRetencion);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL12005.getValue());
		listacodigo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	/**
	 * 
	 * Carga la lista listacodigo
	 *
	 */
	public void  cargarListacodigoE(){
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoRetencion);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL12005.getValue());
		listacodigoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void agregarRegistroSubSubretenciones() {
		try {

			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
			registroSub.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipoComp);
			registroSub.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
			registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(), registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
			registroSub.getCampos().put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			registroSub.getCampos().put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			registroSub.getCampos().put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
			
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.COMPROBANTE_CNTRETENCION.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_INGRESADO"));
			cargarListaSubretenciones();
			calcularIbc();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(),ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}finally{
			registroSub = new Registro(new HashMap<String, Object>());
		} 
	}

	public void cambiarcodigoC(int rowNum) {
		//		listaInicial.getDatasource().get(rowNum % 10).getCampos();  
	}

	public void cambiarretencionC(int rowNum) {		
		boolean noAportPension = false;
		tipoRetencion = SysmanFunciones.toString(listaSubretenciones.get(rowNum).getCampos().get(CAMPO_TIPORETENCION));  
		if (tipoRetencion.equals("ICA")) {
			
			Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		    param.put(GeneralParameterEnum.ANO.getName(), ano);
			List<Registro> regS= null;
	    	try {
	    		
	    		regS = RegistroConverter.toListRegistro(
	    	            requestManager.getList(
	    	                UrlServiceUtil.getInstance()
	    	                    .getUrlServiceByUrlByEnumID(
	    	                        FrmCausacionAutomaticaControladorUrlEnum.URL4080.getValue()
	    	                    ).getUrl(),
	    	                param
	    	            )
	    	        );
	    		
	    	} catch (SystemException e) {
		        logger.error(e.getMessage(), e);
		        JsfUtil.agregarMensajeError(e.getMessage());
		    }
	    	
	    	double salarioMinimo = Double.parseDouble(regS.get(0).getCampos().get("SALARIOMINIMO").toString());
	    	double porcentajeSalud = Double.parseDouble(regS.get(0).getCampos().get("PORCENTAJESALUD").toString());
	    	double porcentajePension = Double.parseDouble(regS.get(0).getCampos().get("PORCENTAJEPENSION").toString());
	    	double base = Double.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("VALOR"), "0").toString());
	        double baseIca = SysmanFunciones.redondear((base * 0.4), 0);
	    	
	    	if(salarioMinimo > baseIca) {
	    		baseIca = salarioMinimo;
	    	}
	    	
	    	double baseSalud = SysmanFunciones.redondear((baseIca * (porcentajeSalud / 100)),0);
	        double basePension = SysmanFunciones.redondear((baseIca * (porcentajePension / 100)),0);

	        //CC_934 (24/04/2025 JCROJAS)
	        Map<String, Object> params = new TreeMap<>();
	        List<Registro> regRet = null;
	    	try {
	    	    params.put(GeneralParameterEnum.COMPANIA.getName(),compania);
	    	    params.put(GeneralParameterEnum.NIT.getName(),tercero);
	    	    params.put(GeneralParameterEnum.SUCURSAL.getName(),sucursal);
	    	    
	    		regRet = RegistroConverter.toListRegistro(
	    	              requestManager.getList(
	    	            	  UrlServiceUtil.getInstance()
	    	                      .getUrlServiceByUrlByEnumID(
	    	                          FrmCausacionAutomaticaControladorUrlEnum.URL14209.getValue()).getUrl(),
	    	                  params));
	    		
	    		if (regRet != null && !regRet.isEmpty()) {
		            noAportPension = (boolean) SysmanFunciones.nvl(regRet.get(0).getCampos().get("NOAPORTAPENSION"),false);
		        }
	    	} catch(SystemException e) {
		        logger.error(e.getMessage(), e);
		        JsfUtil.agregarMensajeError(e.getMessage());
		    }
	        
	    	if(noAportPension) {
	    		base = SysmanFunciones.redondear((base - baseSalud),0);
	    	} else {
	    		base = SysmanFunciones.redondear((base - baseSalud - basePension),0);
	    	}

			registroSub.getCampos().put("VALORBASE", base);

		} else {
			registroSub.getCampos().put("VALORBASE", vlrBase);
		}
		registroSub.getCampos().put("VALOR", "0");

		cargarListacodigoE();      
	}



	public void cambiarretencion() {
		
		boolean aplicaTer1819 = false;
	    boolean aplicaRen1819 = false;
	    boolean noAportPension = false;

	    tipoRetencion = SysmanFunciones.nvl(registroSub.getCampos().get(CAMPO_TIPORETENCION), "").toString();

	    Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), ano);
	    param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
	    param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
	    param.put("CONCEPTO", registro.getCampos().get("CONCEPTO"));
	    param.put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
	    param.put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
	    param.put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));

	    List<Registro> reg = null;
	    try {
	        reg = RegistroConverter.toListRegistro(
	            requestManager.getList(
	                UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                        FrmCausacionAutomaticaControladorUrlEnum.URL1933007.getValue()
	                    ).getUrl(),
	                param
	            )
	        );
	        if (reg != null && !reg.isEmpty()) {
	            aplicaTer1819 = (boolean) SysmanFunciones.nvl(reg.get(0).getCampos().get("LEY1819"), false);
	            aplicaRen1819 = (boolean) SysmanFunciones.nvl(reg.get(0).getCampos().get("ALEY1819"), false);
	        }
	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }

	    if ("ICA".equals(tipoRetencion) && aplicaTer1819 && aplicaRen1819) {
	    	
	    	List<Registro> regS= null;
	    	try {
	    		
	    		regS = RegistroConverter.toListRegistro(
	    	            requestManager.getList(
	    	                UrlServiceUtil.getInstance()
	    	                    .getUrlServiceByUrlByEnumID(
	    	                        FrmCausacionAutomaticaControladorUrlEnum.URL4080.getValue()
	    	                    ).getUrl(),
	    	                param
	    	            )
	    	        );
	    		
	    	} catch (SystemException e) {
		        logger.error(e.getMessage(), e);
		        JsfUtil.agregarMensajeError(e.getMessage());
		    }
	    	
	    	double salarioMinimo = Double.parseDouble(regS.get(0).getCampos().get("SALARIOMINIMO").toString());
	    	double porcentajeSalud = Double.parseDouble(regS.get(0).getCampos().get("PORCENTAJESALUD").toString());
	    	double porcentajePension = Double.parseDouble(regS.get(0).getCampos().get("PORCENTAJEPENSION").toString());
	    	double base = Double.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("VALOR"), "0").toString());
	    	double baseIca = SysmanFunciones.redondear((base * 0.4), 0);
	    	
	    	if(salarioMinimo > baseIca) {
	    		baseIca = salarioMinimo;
	    	}
	    	
	        double baseSalud = SysmanFunciones.redondear((baseIca * (porcentajeSalud / 100)),0);
	        double basePension = SysmanFunciones.redondear((baseIca * (porcentajePension / 100)),0);
	        
	        //CC_934 (24/04/2025 JCROJAS)
	        Map<String, Object> params = new TreeMap<>();
	        List<Registro> regRet = null;
	    	try {
	    	    params.put(GeneralParameterEnum.COMPANIA.getName(),compania);
	    	    params.put(GeneralParameterEnum.NIT.getName(),tercero);
	    	    params.put(GeneralParameterEnum.SUCURSAL.getName(),sucursal);
	    	    
	    		regRet = RegistroConverter.toListRegistro(
	    	              requestManager.getList(
	    	            	  UrlServiceUtil.getInstance()
	    	                      .getUrlServiceByUrlByEnumID(
	    	                          FrmCausacionAutomaticaControladorUrlEnum.URL14209.getValue()).getUrl(),
	    	                  params));
	    		
	    		if (regRet != null && !regRet.isEmpty()) {
		            noAportPension = (boolean) SysmanFunciones.nvl(regRet.get(0).getCampos().get("NOAPORTAPENSION"),false);
		        }
	    	} catch(SystemException e) {
		        logger.error(e.getMessage(), e);
		        JsfUtil.agregarMensajeError(e.getMessage());
		    }
	        
	    	if(noAportPension) {
	    		base = SysmanFunciones.redondear((base - baseSalud),0);
	    	} else {
	    		base = SysmanFunciones.redondear((base - baseSalud - basePension),0);
	    	}
	        registroSub.getCampos().put("VALORBASE", base);
	        
	    } else if ("IVA".equals(tipoRetencion)){
	    	
	    	registroSub.getCampos().put("VALORBASE", vlrBaseIva);
	    	
	    }else {
	        registroSub.getCampos().put("VALORBASE", vlrBase);
	    }
	    registroSub.getCampos().put("VALOR", "0");
	    cargarListacodigo();
	}
	
	/**
	 * Metodo ejecutado al cambiar el control cpDeducibleViv
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws SystemException 
	 * 
	 */
	public void cambiarcpDeducibleViv() throws SystemException {
		
		try {
			int limiteDeducibleViv = Integer.parseInt(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "LIMITE MENSUAL UVT INTERESES VIVIENDA", modulo, new Date(), true), "100").toString());
			
			Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		    param.put(GeneralParameterEnum.ANO.getName(), ano);
		    
		    List<Registro> reg = null;
		    reg = RegistroConverter.toListRegistro(requestManager.getList(
		                UrlServiceUtil.getInstance()
		                    .getUrlServiceByUrlByEnumID(
		                        FrmCausacionAutomaticaControladorUrlEnum.URL4070.getValue()
		                    ).getUrl(),
		                param
		            )
		        );
		    
		    double uvt = 0;
		    
		    if (reg != null && !reg.isEmpty()) {
	            uvt = Double.parseDouble(SysmanFunciones.nvl(reg.get(0).getCampos().get("VALORUVT"), 0).toString());
	        }
			
			double deducibleViv = Double.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("DEDUCIBLE_VIVIENDA"), "0").toString());
			
			if (deducibleViv > (limiteDeducibleViv*uvt) ) {
				
				registro.getCampos().put("DEDUCIBLE_VIVIENDA", 0);
				DecimalFormat df = new DecimalFormat("###,###,###.##");
				String valor = df.format(limiteDeducibleViv*uvt);

				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4459")
                        .replace("s$limite$s",
                        		String.valueOf(valor)));
								
			}
			
		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}
	/**
	 * Metodo ejecutado al cambiar el control cpMedPrepag
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws SystemException 
	 * 
	 */
	public void cambiarcpMedPrepag() throws SystemException {
		try {
			int limiteMedPrepag = Integer.parseInt((String) SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "LIMITE MENSUAL UVT MEDICINA PREPAGADA", modulo, new Date(), true), "16"));
			
			Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		    param.put(GeneralParameterEnum.ANO.getName(), ano);
		    
		    List<Registro> reg = null;
		    reg = RegistroConverter.toListRegistro(requestManager.getList(
		                UrlServiceUtil.getInstance()
		                    .getUrlServiceByUrlByEnumID(
		                        FrmCausacionAutomaticaControladorUrlEnum.URL4070.getValue()
		                    ).getUrl(),
		                param
		            )
		        );
		    
		    double uvt = 0;
		    
		    if (reg != null && !reg.isEmpty()) {
	            uvt = Double.parseDouble(SysmanFunciones.nvl(reg.get(0).getCampos().get("VALORUVT"), 0).toString());
	        }
			
			double medPrepagada = Double.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("MEDPREPAGADA"), "0").toString());
			
			if (medPrepagada > (limiteMedPrepag*uvt) ) {
				
				registro.getCampos().put("MEDPREPAGADA", 0);
				DecimalFormat df = new DecimalFormat("###,###,###.##");
				String valor = df.format(limiteMedPrepag*uvt);

				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4459")
                        .replace("s$limite$s",
                        		String.valueOf(valor)));
								
			}
			
		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo de edicion del formulario Subretenciones
	 * 
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSubretenciones(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove("NOMBRE");
			reg.getCampos().remove("COMPANIA");
			reg.getCampos().remove("ANO");
			reg.getCampos().remove("PORCIVA");
			reg.getCampos().remove("NOMBRERETENCIONES");
			reg.getCampos().remove("TIPO");
			reg.getCampos().remove("NUMERO");
			reg.getLlave().remove("KEY_CONCEPTO");
			reg.getLlave().remove("KEY_REFERENCIADO");
			reg.getLlave().remove("KEY_CENTROCOSTO");
			reg.getLlave().remove("KEY_PROVEEDOR");
			
			reg.getCampos().put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
			reg.getCampos().put("DATE_MODIFIED", new Date());
			reg.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(), registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
			reg.getCampos().put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			reg.getCampos().put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			reg.getCampos().put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));			
			reg.getLlave().put("KEY_CONCEPTO", registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
			reg.getLlave().put("KEY_REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			reg.getLlave().put("KEY_CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			reg.getLlave().put("KEY_PROVEEDOR", registro.getCampos().get("PROVEEDOR"));

			UrlBean urlUpdateC = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69009.getValue());

			requestManager.update(urlUpdateC.getUrl(), urlUpdateC.getMetodo(), reg.getCampos(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
			
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSubretenciones();
		}
	}


	/**
	 * Metodo de eliminacion del formulario Subretenciones
	 * 
	 * 
	 * @param reg
	 * registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSubretenciones(Registro reg) {
		try {
			reg.getCampos().remove("PORCIVA");
			reg.getCampos().remove("VALORBASE");
			reg.getCampos().remove("NOMBRERETENCIONES");
			reg.getCampos().remove("VALOR");
			reg.getCampos().remove("NOMBRE");
			
			reg.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(), registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
			reg.getCampos().put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			reg.getCampos().put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			reg.getCampos().put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
			
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69007.getValue());

			requestManager.delete(urlDelete.getUrl(), reg.getCampos());
			cargarListaSubretenciones();
			calcularIbc();
		} catch ( SystemException ex) {
			logger.error(ex.getMessage(),ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}


	public void cancelarEdicionSubretenciones(){
		cargarListaSubretenciones();
	}

	
	public void ejecutarrcCerrar(){
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", ridComprobante);
		parametros.put("ano", ano);
		parametros.put("mes", mes);
		parametros.put("tipoMov", tipoComp);
		parametros.put("opcionMenu", opcionMenu);

	        Direccionador direccionador = new Direccionador();
	        direccionador.setParametros(parametros);
	        direccionador.setNumForm(Integer
	                        .toString(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
	                                        .getCodigo()));
	        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}
	
	public void ejecutarrcVolver(){
		 ejecutarrcCerrar();
   }
	
	private void actualizaDataCntRete() {
		try {

			UrlBean urlUpdate;
			Map<String, Object> param = new TreeMap<>();
			param.put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			param.put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			param.put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
			param.put("DATE_MODIFIED", registro.getCampos().get("DATE_MODIFIED"));
			param.put("MODIFIED_BY", registro.getCampos().get("MODIFIED_BY"));
			
			param.put("KEY_COMPANIA", registro.getCampos().get("COMPANIA"));	
			param.put("KEY_ANO", registro.getCampos().get("ANO"));
			param.put("KEY_TIPO", registro.getCampos().get("TIPO_COMPROBANTE"));
			param.put("KEY_NUMERO", registro.getCampos().get("NUMERO_COMPROBANTE"));
			param.put("KEY_CONCEPTO", registro.getCampos().get("CONCEPTO"));
			param.put("KEY_PROVEEDOR", proveedorAnt);
			param.put("KEY_REFERENCIADO", referenciadoAnt);
			param.put("KEY_CENTROCOSTO", centroCostoAnt);		
			
			Parameter parameter = new Parameter();
            parameter.setFields(param);
			
			urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69010.getValue());
			
			int rta = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);


		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}
	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>

	public void seleccionarFilaConcepto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		eliminarDetalles();
		registro.getCampos().put("CONCEPTO", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRE_CONCEPTO", registroAux.getCampos().get("NOMBRE"));
		cargarListaReferenciado(); 
	}

	private void insertarDataCntRete() {
		try {

			UrlBean urlCreate;
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
			param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
		    param.put("VALORBASE", vlrBase);
		    param.put("VALORBASEIVA", vlrBaseIva);
			param.put("CREATED_BY", SessionUtil.getUser().getCodigo());
			param.put("DATE_CREATED", SysmanFunciones.convertirAFechaCadena(new Date()));
			param.put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			param.put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			param.put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
			param.put("CONCEPTO", registro.getCampos().get("CONCEPTO"));			
			
			if(manejaTercero) {

				param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
				param.put(GeneralParameterEnum.TERCERO.getName(), tercero);

				urlCreate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69006.getValue());
			}else {

				param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("CONCEPTO"));

				urlCreate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69005.getValue());

			}
			
			Boolean RetencionXRegimen = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CONFIGURACION DE RETENCIONES POR REGIMEN", SessionUtil.getModulo(), new Date(), true),
					"NO").toString().equals("SI")?true:false;
        	if(RetencionXRegimen) {
        		
        		param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("CONCEPTO"));
                param.put(GeneralParameterEnum.NIT.getName(), tercero);

				urlCreate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69012.getValue());
				
        	}
			int rta = requestManager.saveCount(urlCreate.getUrl(), urlCreate.getMetodo(), param);


		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}	
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProveedor
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProveedor(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("PROVEEDOR", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRE_PROVEEDOR", registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodigo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(CAMPO_CODIGORETENCION, registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodigo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciado
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("REFERENCIADO", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("CUENTACONTABLE", registroAux.getCampos().get("CUENTACONTABLE"));	
		//actualizaDataCntRete();
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
		registro.getCampos().put("CENTROCOSTO", registroAux.getCampos().get("CODIGO"));

	}
	
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAnticipo
     *
     * @param event objeto que encapsula la accion proveniente de la vista
     */
	
	public void seleccionarFilaAnticipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		        registro.getCampos().put("CUENTA_ANTICIPO", registroAux.getCampos().get("CODIGO"));
		}
	
	//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarAceptaCausacion() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", ano);
		registro.getCampos().put("TIPO_COMPROBANTE", tipoComp);
		registro.getCampos().put("NUMERO_COMPROBANTE", numeroComp);

		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
			eliminarDetalles();
			insertarDataCntRete();
			validarIndAnticipo();
			calcularIbc();
			cargarListaSubretenciones();
			
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().remove("NOMBRE_CONCEPTO");
		registro.getCampos().remove("NOMBRE_PROVEEDOR");
		actualizaDataCntRete();	
		cargarListaSubretenciones();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		validarIndAnticipo();
		calcularIbc();
		//</CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		eliminarDetalles();
		//</CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		validarIndAnticipo();
		//</CODIGO_DESARROLLADO>
		return true;
	}

	public void eliminarDetalles() {

		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
			param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
			param.put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
			param.put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));
			param.put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
			param.put("CONCEPTO", registro.getCampos().get("CONCEPTO"));
			
			
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL69011.getValue());

			requestManager.delete(urlUpdate.getUrl(), param);

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

	}
	
	private void validarIndAnticipo() {
		
		String indicador = "0";
		String cuentaAnticipo = SysmanFunciones.nvl(registro.getCampos().get("CUENTA_ANTICIPO"), "").toString();
		String valorAnticipo = SysmanFunciones.nvl(registro.getCampos().get("VALOR_ANTICIPO"), "").toString();
		
		if (Objects.nonNull(cuentaAnticipo) && !cuentaAnticipo.isEmpty() && Objects.nonNull(valorAnticipo) && !valorAnticipo.isEmpty() && !valorAnticipo.equals("0")) {
			indicador = "-1";
		}
		
		UrlBean urlUpdate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmCausacionAutomaticaControladorUrlEnum.URL1933006.getValue());
		
		Map<String, Object> fields = new TreeMap<>();
		fields.put("IND_ANTICIPO", indicador);
		
		fields.put(GeneralParameterEnum.KEY_COMPANIA.getName(),
				compania);
		fields.put(GeneralParameterEnum.KEY_ANO.getName(),
				registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		fields.put(GeneralParameterEnum.KEY_TIPO.getName(),
				registro.getCampos().get("TIPO_COMPROBANTE"));
		fields.put(GeneralParameterEnum.KEY_NUMERO.getName(),
				registro.getCampos().get("NUMERO_COMPROBANTE"));
		
		Parameter parameter = new Parameter();
		parameter.setFields(fields);

		try {
			requestManager.update(urlUpdate.getUrl(),
					urlUpdate.getMetodo(),
					parameter);
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
		
	}
	
	private void calcularIbc() {

		double valorIbc = 0;

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
		param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
		param.put("CONCEPTO", registro.getCampos().get("CONCEPTO"));
		param.put("PROVEEDOR", registro.getCampos().get("PROVEEDOR"));
		param.put("REFERENCIADO", registro.getCampos().get("REFERENCIADO"));
		param.put("CENTROCOSTO", registro.getCampos().get("CENTROCOSTO"));

		List<Registro> reg = null;
		try {
			reg = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCausacionAutomaticaControladorUrlEnum.URL1933007.getValue())
									.getUrl(),
									param));
			if (!reg.isEmpty()) {

				valorIbc = Double.parseDouble(reg.get(0).getCampos().get(CAMPO_VALORBASE).toString()) * 0.4;

			}

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmCausacionAutomaticaControladorUrlEnum.URL1933008.getValue());

			Map<String, Object> fields = new TreeMap<>();
			fields.put("IBC", valorIbc);
			fields.put(GeneralParameterEnum.KEY_COMPANIA.getName(),
					compania);
			fields.put(GeneralParameterEnum.KEY_ANO.getName(),
					registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
			fields.put(GeneralParameterEnum.KEY_TIPO.getName(),
					registro.getCampos().get("TIPO_COMPROBANTE"));
			fields.put(GeneralParameterEnum.KEY_NUMERO.getName(),
					registro.getCampos().get("NUMERO_COMPROBANTE"));
			fields.put("KEY_CONCEPTO",registro.getCampos().get("CONCEPTO"));
			fields.put("KEY_PROVEEDOR",registro.getCampos().get("PROVEEDOR"));
			fields.put("KEY_REFERENCIADO",registro.getCampos().get("REFERENCIADO"));
			fields.put("KEY_CENTROCOSTO",registro.getCampos().get("CENTROCOSTO"));

			Parameter parameter = new Parameter();
			parameter.setFields(fields);

			try {
				requestManager.update(urlUpdate.getUrl(),
						urlUpdate.getMetodo(),
						parameter);
			}
			catch (SystemException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
				logger.error(e.getMessage(), e);

			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		registro.getCampos().put("IBC", valorIbc);

	}
	
	private void validarDetallesContables() {
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausacionAutomaticaControladorUrlEnum.URL39049.getValue());
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put("STRTIPOCOMPROBANTE", tipoComp);
		param.put(GeneralParameterEnum.NUMERO.getName(),numeroComp);

		int conteo;
		try {
			conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
					.get("CONTEO").toString());
			
			if (conteo > 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4458"));
				mostrarInsert = false;
				mostrarUpdate = false;
				mostrarDelete = false;
			}
		
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		
	}
	
	
	/**

	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaretencion
	 * 
	 * @return listaretencion
	 */
	public List<Registro> getListaretencion() {
		return listaretencion;
	}
	/**
	 * Asigna la lista listaretencion
	 * 
	 * @param listaretencion
	 * Variable a asignar en  listaretencion
	 */
	public void setListaretencion(List<Registro> listaretencion) {
		this.listaretencion = listaretencion;
	}
	/**
	 * Retorna la lista listaretencion
	 * 
	 * @return listaretencion
	 */
	public List<Registro> getListaretencionE() {
		return listaretencionE;
	}
	/**
	 * Asigna la lista listaretencion
	 * 
	 * @param listaretencion
	 * Variable a asignar en  listaretencion
	 */
	public void setListaretencionE(List<Registro> listaretencionE) {
		this.listaretencionE = listaretencionE;
	}
	/**
	 * Retorna la lista listacodigo
	 * 
	 * @return listacodigo
	 */
	public RegistroDataModelImpl getListacodigo() {
		return listacodigo;
	}
	/**
	 * Asigna la lista listacodigo
	 * 
	 * @param listacodigo
	 * Variable a asignar en  listacodigo
	 */
	public void setListacodigo(RegistroDataModelImpl listacodigo) {
		this.listacodigo = listacodigo;
	}
	/**
	 * Retorna la lista listacodigo
	 * 
	 * @return listacodigo
	 */
	public RegistroDataModelImpl getListacodigoE() {
		return listacodigoE;
	}
	/**
	 * Asigna la lista listacodigo
	 * 
	 * @param listacodigo
	 * Variable a asignar en  listacodigo
	 */
	public void setListacodigoE(RegistroDataModelImpl listacodigoE) {
		this.listacodigoE = listacodigoE;
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
	 * @return the registroSub
	 */
	public Registro getRegistroSub() {
		return registroSub;
	}
	/**
	 * @param registroSub the registroSub to set
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}
	/**
	 * @return the listaSubretenciones
	 */
	public List<Registro> getListaSubretenciones() {
		return listaSubretenciones;
	}
	/**
	 * @param listaSubretenciones the listaSubretenciones to set
	 */
	public void setListaSubretenciones(List<Registro> listaSubretenciones) {
		this.listaSubretenciones = listaSubretenciones;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the tipoRetencion
	 */
	public String getTipoRetencion() {
		return tipoRetencion;
	}
	/**
	 * @param tipoRetencion the tipoRetencion to set
	 */
	public void setTipoRetencion(String tipoRetencion) {
		this.tipoRetencion = tipoRetencion;
	}
	/**
	 * @return the concepto
	 */
	public String getConcepto() {
		return concepto;
	}
	/**
	 * @param concepto the concepto to set
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	/**
	 * @return the proveedor
	 */
	public String getProveedor() {
		return proveedor;
	}
	/**
	 * @param proveedor the proveedor to set
	 */
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	/**
	 * @return the listaConcepto
	 */
	public RegistroDataModelImpl getListaConcepto() {
		return listaConcepto;
	}
	/**
	 * @param listaConcepto the listaConcepto to set
	 */
	public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
		this.listaConcepto = listaConcepto;
	}
	/**
	 * @return the listaProveedor
	 */
	public RegistroDataModelImpl getListaProveedor() {
		return listaProveedor;
	}
	/**
	 * @param listaProveedor the listaProveedor to set
	 */
	public void setListaProveedor(RegistroDataModelImpl listaProveedor) {
		this.listaProveedor = listaProveedor;
	}
	/**
	 * @return the ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * @param ano the ano to set
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}
	/**
	 * @return the listaTipo
	 */
	public List<Registro> getListaTipo() {
		return listaTipo;
	}
	/**
	 * @param listaTipo the listaTipo to set
	 */
	public void setListaTipo(List<Registro> listaTipo) {
		this.listaTipo = listaTipo;
	}
	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}
	
	public boolean isMostrarInsert() {
		return mostrarInsert;
	}

	public void setMostrarInsert(boolean mostrarInsert) {
		this.mostrarInsert = mostrarInsert;
	}

	public boolean isMostrarUpdate() {
		return mostrarUpdate;
	}

	public void setMostrarUpdate(boolean mostrarUpdate) {
		this.mostrarUpdate = mostrarUpdate;
	}

	public boolean isMostrarDelete() {
		return mostrarDelete;
	}

	public void setMostrarDelete(boolean mostrarDelete) {
		this.mostrarDelete = mostrarDelete;
	}

	private String extraerString(Object object)
	{
		return object != null ? object.toString() : null;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	/**
	 * @return the visibleAceptaCausacion
	 */
	public boolean isVisibleAceptaCausacion() {
		return visibleAceptaCausacion;
	}
	/**
	 * @param visibleAceptaCausacion the visibleAceptaCausacion to set
	 */
	public void setVisibleAceptaCausacion(boolean visibleAceptaCausacion) {
		this.visibleAceptaCausacion = visibleAceptaCausacion;
	}
	public RegistroDataModelImpl getListaReferenciado() {
		return listaReferenciado;
	}
	public void setListaReferenciado(RegistroDataModelImpl listaReferenciado) {
		this.listaReferenciado = listaReferenciado;
	}
	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}
	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}
	public RegistroDataModelImpl getListaAnticipo() {
        return listaAnticipo;
    }
	public void setListaAnticipo(RegistroDataModelImpl listaAnticipo) {
        this.listaAnticipo = listaAnticipo;
    }
	public boolean isReferenciadoVisible() {
		return referenciadoVisible;
	}
	public void setReferenciadoVisible(boolean referenciadoVisible) {
		this.referenciadoVisible = referenciadoVisible;
	}
	public boolean isManejaCausacion() {
		return manejaCausacion;
	}
	public void setManejaCausacion(boolean manejaCausacion) {
		this.manejaCausacion = manejaCausacion;
	}
	
	
	
}
