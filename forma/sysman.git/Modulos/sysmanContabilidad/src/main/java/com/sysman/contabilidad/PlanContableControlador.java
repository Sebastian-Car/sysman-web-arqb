package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.PlanContableControladorEnum;
import com.sysman.contabilidad.enums.PlanContableControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SfconceptosControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author cmanrique
 * @version 2, 19/04/2017, mzanguna, Refactorización y ajustes sonar.
 * @version 3, 20/04/2017, mzanguna, Cambio EJB.
 * @version 3.1, 01/06/2017, mzanguna Adición condición para cuentas padre con
 * algun indicador.
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class PlanContableControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private Registro registroSub;

    private List<Registro> listaClaseCuenta;
    private List<Registro> listabanco;
    private List<Registro> listaCodFlujoCaja;
    private List<Registro> listaCcBalance;
    private List<Registro> listaCmbCodEquivalente;

    private List<Registro> listaSubplanpptalcuentacnt;

    private RegistroDataModelImpl listaRUBRO;
    private RegistroDataModelImpl listaRUBROE;
    private String auxiliar;
    private RegistroDataModelImpl listaConceptoEx;
    private RegistroDataModelImpl listaEquivprDebito;
    private RegistroDataModelImpl listaEquivprCredito;
    private RegistroDataModelImpl listaDeterioroDebCausDet;
    private RegistroDataModelImpl listaDeterioroCreCausDet;
    private RegistroDataModelImpl listaDeterioroDebRecDet;
    private RegistroDataModelImpl listaDeterioroCreRecDet;
    private RegistroDataModelImpl listaDeterioroDebRecoDet;
    private RegistroDataModelImpl listaDeterioroCreRecoDet;

    private RegistroDataModelImpl listaReconocimientoDebito;

    private RegistroDataModelImpl listaReconocimientoCredito;

    private RegistroDataModelImpl listaCausacionDebito;

    private RegistroDataModelImpl listaCausacionCredito;

    private RegistroDataModelImpl listaRecaudoDebito;

    private RegistroDataModelImpl listaRecaudoCredito;

    private RegistroDataModelImpl listaDebitoVigenciaActual;

    private RegistroDataModelImpl listaCreditoVigenciaActual;

    private RegistroDataModelImpl listaDebitoVigenciaAnterior;

    private RegistroDataModelImpl listaCreditoVigenciaAnterior;

    private RegistroDataModelImpl listaCodigoEquivalenteCartera;
    
    private RegistroDataModelImpl listaAuxiliar;

    private RegistroDataModelImpl listaAuxiliarE;
    
    private RegistroDataModelImpl listaFuente;
    
    private RegistroDataModelImpl listaFuenteE;
    
    private RegistroDataModelImpl listaCentro;
    
    private RegistroDataModelImpl listaCentroE;
    
    private RegistroDataModelImpl listaReferencia;
    
    private RegistroDataModelImpl listaReferenciaE;
    
    private RegistroDataModelImpl listaEquivalenteDevolucion;
    
    private RegistroDataModelImpl listaCodReconocimiento;
    
    
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    private String anio;
    private String id;
    private String indicadorDialog;
    private Direccionador direccionador;

    private boolean bloqueoMovimiento;
    private boolean bloqueoIndicadores;

    private boolean manDolares;
    private boolean permiteEleTamLetra;
    private boolean manFormEgresoBanco;
    private boolean manRefCuentas;
    private boolean manDistrCont;
    private boolean manDistrCtoGast;
    private boolean manBalCtoDist;
    private boolean manChequera;
    private boolean afectarFactArren;
    private boolean cargaParametros;
    private boolean manRestriccionesCC;
    private StreamedContent archivoDescarga;
    private boolean seleccionarCuentaVisible;

    private boolean habilitarDeterioro;
    private boolean bloquearCkDeterioro;

    private boolean muestraEliminar = true;
    private boolean muestraNuevo = true;
    private boolean muestraActualiza = true;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
	private boolean manejaEquivalente;

    @SuppressWarnings("unchecked")
    public PlanContableControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        direccionador = new Direccionador();

        try {
            numFormulario = GeneralCodigoFormaEnum.PLAN_CONTABLE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                anio = (String) parametros.get("anio");
                rid = (Map<String, Object>) parametros.get("rid");
                Map<String, Object> parametrosAno = new HashMap<>();
                parametrosAno.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametrosAno.put(GeneralParameterEnum.ANO.getName(), anio);

                HashMap<String, Object> rsAno = new HashMap<>();
                UrlBean urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PlanContableControladorUrlEnum.URL15053
                                                                .getValue());
                rsAno = (HashMap<String, Object>) requestManager
                                .get(urlReg.getUrl(), parametrosAno)
                                .getFields();

                if (!"A".equals(SysmanFunciones.nvl(rsAno
                                .get(GeneralParameterEnum.ESTADO.getName()), "")
                                .toString())) {
                    muestraEliminar = false;
                    muestraNuevo = false;
                    muestraActualiza = false;
                }

            }
        }
        catch (Exception ex) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PLANCONTABLE;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("ANIOF", anio);
    }

    @Override
    public void iniciarListas() {
        cargarListaRUBRO();
        cargarListaRUBROE();
        cargarListaConceptoEx();
        cargarListaEquivprDebito();
        cargarListaEquivprCredito();
        cargarListaClaseCuenta();
        cargarListabanco();
        cargarListaCodFlujoCaja();
        cargarListaCcBalance();
        cargarListaCmbCodEquivalente();
        cargarListaReconocimientoDebito();
        cargarListaReconocimientoCredito();
        cargarListaCausacionDebito();
        cargarListaCausacionCredito();
        cargarListaRecaudoDebito();
        cargarListaRecaudoCredito();
        cargarListaDebitoVigenciaActual();
        cargarListaCreditoVigenciaActual();
        cargarListaDebitoVigenciaAnterior();
        cargarListaCreditoVigenciaAnterior();
        cargarListaCodigoEquivalenteCartera();
        cargarListaAuxiliar(); cargarListaAuxiliarE();
        cargarListaFuente(); cargarListaFuenteE();
        cargarListaCentro(); cargarListaCentroE();
        cargarListaReferencia(); cargarListaReferenciaE();	
        cargarListaEquivalenteDevolucion();
        cargarListaDeterioroDebRecDet(); cargarListaDeterioroCreRecDet(); cargarListaDeterioroDebRecoDet(); cargarListaDeterioroCreRecoDet(); cargarListaDebitoVigenciaActual(); cargarListaCreditoVigenciaActual(); cargarListaDebitoVigenciaAnterior(); cargarListaCreditoVigenciaAnterior(); cargarListaCodigoEquivalenteCartera(); cargarListaEquivalenteDevolucion(); cargarListaCodReconocimiento();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubplanpptalcuentacnt();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubplanpptalcuentacnt = null;
    }

    public void iniciarListasDeterioro() {
        cargarListaDeterioroDebCausDet();
        cargarListaDeterioroCreCausDet();
        cargarListaDeterioroDebRecDet();
        cargarListaDeterioroCreRecDet();
        cargarListaDeterioroDebRecoDet();
        cargarListaDeterioroCreRecoDet();

    }

    public void cargarListaRUBRO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL5885
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaRUBRO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaRUBROE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL6551
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaRUBROE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    
    public void cargarListaAuxiliar(){
    	 Map<String, Object> param = new TreeMap<>();
	     param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	     param.put(PlanContableControladorEnum.ANO.getValue(), anio);

	     UrlBean urlBean = UrlServiceUtil.getInstance()
	                     .getUrlServiceByUrlByEnumID(
	                    		 PlanContableControladorUrlEnum.URL23006
	                                                     .getValue());
	     
	     listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
	                     urlBean.getUrlConteo().getUrl(), param,
	                     true, GeneralParameterEnum.CODIGO.getName()); 
    	}
    	    /**
    	     * 
    	     * Carga la lista listaAuxiliar
    	     *
    	     */
    	public void  cargarListaAuxiliarE(){
  
    	 Map<String, Object> param = new TreeMap<>();
	     param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	     param.put(PlanContableControladorEnum.ANO.getValue(), anio);

	     UrlBean urlBean = UrlServiceUtil.getInstance()
	                     .getUrlServiceByUrlByEnumID(
	                    		 PlanContableControladorUrlEnum.URL23006
	                                                     .getValue());
	     
	     listaAuxiliarE = new RegistroDataModelImpl(urlBean.getUrl(),
	                     urlBean.getUrlConteo().getUrl(), param,
	                     true, GeneralParameterEnum.CODIGO.getName());
    	}
    	    /**
    	     * 
    	     * Carga la lista listaFuente
    	     *
    	     */
    	public void cargarListaFuente(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(PlanContableControladorUrlEnum.URL34043.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANIO.getName(),anio);

    		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
    				GeneralParameterEnum.CODIGO.getName()); 
    	}
    	    /**
    	     * 
    	     * Carga la lista listaFuente
    	     *
    	     */
    	public void  cargarListaFuenteE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(PlanContableControladorUrlEnum.URL34043.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANIO.getName(), anio);

    		listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
    				GeneralParameterEnum.CODIGO.getName()); 
    	}
    	    /**
    	     * 
    	     * Carga la lista listaCentro
    	     *
    	     */
    	public void cargarListaCentro(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						PlanContableControladorUrlEnum.URL20017
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

    		listaCentro = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param,
    				true, GeneralParameterEnum.CODIGO.getName()); 
    	}
    	    /**
    	     * 
    	     * Carga la lista listaCentro
    	     *
    	     */
    	public void  cargarListaCentroE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						PlanContableControladorUrlEnum.URL20017
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

    		listaCentroE = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param,
    				true, GeneralParameterEnum.CODIGO.getName()); 
    	}
    	/**
    	 * 
    	 * Carga la lista listaReferencia
    	 *
    	 */
    	public void cargarListaReferencia(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						PlanContableControladorUrlEnum.URL13028
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param,
    				true, GeneralParameterEnum.CODIGO.getName());
    	}
    	/**
    	 * 
    	 * Carga la lista listaReferencia
    	 *
    	 */
    	public void  cargarListaReferenciaE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						PlanContableControladorUrlEnum.URL13028
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		listaReferenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param,
    				true, GeneralParameterEnum.CODIGO.getName());
    	}

    public void cargarListaClaseCuenta() {

        try {
            listaClaseCuenta = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlanContableControladorUrlEnum.URL6985
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void cargarListabanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listabanco = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlanContableControladorUrlEnum.URL6236
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListaCodFlujoCaja() {

        try {
            listaCodFlujoCaja = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlanContableControladorUrlEnum.URL7555
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void cargarListaCcBalance() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANOACT.getValue(), anio);

        try {
            listaCcBalance = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlanContableControladorUrlEnum.URL7769
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    /**
     *
     * Carga la lista listaCmbCodEquivalente
     *
     * Lista las cuentas equivalentes
     */
    public void cargarListaCmbCodEquivalente() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        try {
            listaCmbCodEquivalente = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlanContableControladorUrlEnum.URL22459
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListaConceptoEx() {

        Map<String, Object> param = new TreeMap<>();
        param.put(PlanContableControladorEnum.FRMATO.getValue(), "1001");
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL8604
                                                        .getValue());
        listaConceptoEx = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaEquivprDebito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL8964
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaEquivprDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaEquivprCredito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL9410
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaEquivprCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaSubplanpptalcuentacnt() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.COMPANIA.getName()));
        param.put(PlanContableControladorEnum.ANO.getValue(), registro
                        .getCampos().get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        try {
            listaSubplanpptalcuentacnt = RegistroConverter
                            .toListRegistro(requestManager
                                            .getList(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            GenericUrlEnum.PLANPPTALCUENTACNT
                                                                                            .getGridKey())
                                                            .getUrl(), param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            "PLAN_PPTAL_CUENTACNT"));
        }
        catch (SystemException | SysmanException e) {

            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void cargarListaDeterioroDebCausDet() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL11012
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaDeterioroDebCausDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDeterioroCreCausDet() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL11924
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaDeterioroCreCausDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDeterioroDebRecDet() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL12835
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaDeterioroDebRecDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaDeterioroCreRecDet() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL13745
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaDeterioroCreRecDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDeterioroDebRecoDet() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL14656
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaDeterioroDebRecoDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaDeterioroCreRecoDet() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL15568
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanContableControladorEnum.ANO.getValue(), anio);

        listaDeterioroCreRecoDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReconocimientoDebito
     *
     */
    public void cargarListaReconocimientoDebito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReconocimientoDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaReconocimientoCredito
     *
     */
    public void cargarListaReconocimientoCredito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReconocimientoCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCausacionDebito
     *
     */
    public void cargarListaCausacionDebito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCausacionDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCausacionCredito
     *
     */
    public void cargarListaCausacionCredito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCausacionCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaRecaudoDebito
     *
     */
    public void cargarListaRecaudoDebito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaRecaudoDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaRecaudoCredito
     *
     */
    public void cargarListaRecaudoCredito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaRecaudoCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaDebitoVigenciaActual
     *
     */
    public void cargarListaDebitoVigenciaActual() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaDebitoVigenciaActual = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCreditoVigenciaActual
     *
     */
    public void cargarListaCreditoVigenciaActual() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCreditoVigenciaActual = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaDebitoVigenciaAnterior
     *
     */
    public void cargarListaDebitoVigenciaAnterior() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaDebitoVigenciaAnterior = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCreditoVigenciaAnterior
     *
     */
    public void cargarListaCreditoVigenciaAnterior() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL29127
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCreditoVigenciaAnterior = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCodigoEquivalenteCartera
     *
     * 
     */
    public void cargarListaCodigoEquivalenteCartera() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL16052
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("CLASECONTABLE", "C");

        listaCodigoEquivalenteCartera = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    
    /**
     * 
     * Carga la lista listaEquivalenteDevolucion
     *
     */
    public void cargarListaEquivalenteDevolucion(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					PlanContableControladorUrlEnum.URL8964
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.ANO.getName(), anio);

    	listaEquivalenteDevolucion = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param,
    			true, GeneralParameterEnum.CODIGO.getName());
    }
    
    /**
     * 
     * Carga la lista listaCodReconocimiento
     *
     */
    public void cargarListaCodReconocimiento(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					PlanContableControladorUrlEnum.URL8964
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.ANO.getName(), anio);

    	listaCodReconocimiento = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param,
    			true, GeneralParameterEnum.CODIGO.getName());
    }

    public void agregarRegistroSubSubplanpptalcuentacnt() {
    	
    	String referencia = "";
        String fuente = "";
        String auxiliar = "";
        String centroCosto = "";

        registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        registro.getCampos().get(GeneralParameterEnum.COMPANIA
                                        .getName()));
        registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(), registro
                        .getCampos().get(GeneralParameterEnum.ANO.getName()));
        registroSub.getCampos().put("CUENTA_CONTABLE",
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registroSub.getCampos().put("CREATED_BY",
                        SessionUtil.getUser().getCodigo());
        registroSub.getCampos().put("DATE_CREATED", new Date());
        
        if(manejaEquivalente) {
	        referencia = registroSub.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()).toString();
	        fuente = registroSub.getCampos().get(GeneralParameterEnum.FUENTE_RECURSO.getName()).toString();
	        auxiliar = registroSub.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()).toString();
	        centroCosto = registroSub.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString();
        }
        
        registroSub.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
        							SysmanFunciones.nvlStr(referencia,SysmanConstantes.CONS_REFERENCIA));
        registroSub.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
        							SysmanFunciones.nvlStr(fuente,SysmanConstantes.CONS_FUENTE));
        registroSub.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
        							SysmanFunciones.nvlStr(auxiliar,SysmanConstantes.CONS_AUXILIAR));
        registroSub.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
        							SysmanFunciones.nvlStr(centroCosto,SysmanConstantes.CONS_CENTRO));
        
        UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.PLANPPTALCUENTACNT
                                                        .getCreateKey());
        try {
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
        }
        catch (SystemException e) {

            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        cargarListaSubplanpptalcuentacnt();
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_REGISTRO_INGRESADO"));

    }

    public void editarRegSubSubplanpptalcuentacnt(RowEditEvent event) {

        Registro reg = (Registro) event.getObject();

        reg.getCampos().put("MODIFIED_BY",
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put("DATE_MODIFIED",
                        new Date());

        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        reg.getCampos().remove(GeneralParameterEnum.ANO.getName());

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PLANPPTALCUENTACNT
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }

        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaSubplanpptalcuentacnt();
        }
    }

    public void eliminarRegSubSubplanpptalcuentacnt(Registro reg) {

        FacesContext context = FacesContext.getCurrentInstance();
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PLANPPTALCUENTACNT
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                            idioma.getString("TB_TB8"),
                                            idioma.getString(
                                                            "MSM_REGISTRO_ELIMINADO")));
            cargarListaSubplanpptalcuentacnt();
        }

        catch (SystemException e) {

            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                            idioma.getString(
                                                            "MSM_TRANS_INTERRUMPIDA"),
                                            e.getMessage()));

        }
    }

    public void cambiarid() {
        // <CODIGO_DESARROLLADO>
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            String idC = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()),
                                            "")
                            .toString();

            if (idC.length() == 1) {
                if ((idC.compareTo("2") >= 0) && (idC.compareTo("4") <= 0)) {
                    registro.getCampos().put(
                                    GeneralParameterEnum.NATURALEZA.getName(),
                                    "C");
                }
                else {
                    registro.getCampos().put(
                                    GeneralParameterEnum.NATURALEZA.getName(),
                                    "D");
                }
            }
            else {
                HashMap<String, Object> indicadores = new HashMap<>();
                buscarPredecesorContable(registro, indicadores);
                bloqueoIndicadores = (boolean) (indicadores
                                .get(PlanContableControladorEnum.BLOCIND
                                                .getValue()) != null
                                                    ? indicadores.get(
                                                                    PlanContableControladorEnum.BLOCIND
                                                                                    .getValue())
                                                    : false);
                bloqueoMovimiento = (boolean) (indicadores
                                .get(PlanContableControladorEnum.BLOCIND
                                                .getValue()) != null
                                                    ? indicadores.get(
                                                                    PlanContableControladorEnum.BLOCIND
                                                                                    .getValue())
                                                    : false);

            }
        }
        catch (SysmanException ex) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            null);
            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                            idioma.getString("TB_TB1"),
                                            ex.getMessage()));
        }
        // </CODIGO_DESARROLLADO>

    }

    public void oprimirComando202() {
        String[] campos = { GeneralParameterEnum.ANO.getName(),
                            GeneralParameterEnum.CUENTA.getName(),
                            GeneralParameterEnum.NOMBRE.getName(),
                            GeneralParameterEnum.NATURALEZA.getName(),
                            "BANCO" };
        Object[] valores = { anio,
                             SysmanFunciones.nvl(
                                             registro.getCampos()
                                                             .get(GeneralParameterEnum.CODIGO
                                                                             .getName()),
                                             "").toString(),
                             SysmanFunciones.nvl(
                                             registro.getCampos()
                                                             .get(GeneralParameterEnum.NOMBRE
                                                                             .getName()),
                                             "").toString(),
                             SysmanFunciones.nvl(
                                             registro.getCampos()
                                                             .get(GeneralParameterEnum.NATURALEZA
                                                                             .getName()),
                                             "").toString(),
                             SysmanFunciones.nvl(
                                             registro.getCampos()
                                                             .get(PlanContableControladorEnum.BANCO
                                                                             .getValue()),
                                             "").toString() };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.DISTRIBUCIONCTAUXILIARES_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos, valores);
    }

    public void oprimirConRestricciones() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCuentaBancos() {

        if ((registro.getCampos().get(
                        PlanContableControladorEnum.BANCO.getValue()) == null)
            || ("").equals(registro.getCampos().get(
                            PlanContableControladorEnum.BANCO.getValue()))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3"));
            return;
        }
        else {
            String[] campos = { GeneralParameterEnum.ANO.getName(),
                                GeneralParameterEnum.CUENTA.getName(),
                                GeneralParameterEnum.NOMBRE.getName(),
                                GeneralParameterEnum.NATURALEZA.getName(),
                                PlanContableControladorEnum.BANCO.getValue() };
            Object[] valores = { anio,
                                 registro.getCampos()
                                                 .get(GeneralParameterEnum.CODIGO
                                                                 .getName())
                                                 .toString(),
                                 registro.getCampos()
                                                 .get(GeneralParameterEnum.NOMBRE
                                                                 .getName())
                                                 .toString(),
                                 registro.getCampos()
                                                 .get(GeneralParameterEnum.NATURALEZA
                                                                 .getName())
                                                 .toString(),
                                 registro.getCampos()
                                                 .get(PlanContableControladorEnum.BANCO
                                                                 .getValue())
                                                 .toString() };
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.CUENTABANCOS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos,
                            valores);

        }

    }

    public void oprimirChequera() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.ANO.getName(), anio);
        parametros.put(GeneralParameterEnum.CUENTA.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName())
                                        .toString());
        parametros.put("RID", css);

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.CHEQUERAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirGrafica() {
        indicadorDialog = "grafica";
        archivoDescarga = null;
        if (validarRubroSinAuxiliares()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRE_CUENTA",
                            registro.getCampos().get(
                                            GeneralParameterEnum.NOMBRE
                                                            .getName()));
            parametros.put("PR_CUENTA", GeneralParameterEnum.CODIGO.getName());
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("id", registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));
            reemplazar.put("anio", anio);
            Reporteador.resuelveConsulta("001152graficaSaldosCT",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            try {
                archivoDescarga = JsfUtil.exportarStreamed(
                                "001152graficaSaldosCT",
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {

            redireccionarSeleccionar(direccionador);
        }
    }

    public void oprimirMovimiento() {
        // <CODIGO_DESARROLLADO>
        indicadorDialog = "movimientos";

        Map<String, Object> parametros;

        parametros = new HashMap<>();
        parametros.put("rid", css);
        parametros.put("anio", anio);
        parametros.put("cuenta", registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString());

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.MOVIMIENTOS_CUENTAS_CONTROLADOR
                                        .getCodigo()));

        direccionador.setRuta("/subformmovimientos.sysman");
        direccionador.setParametros(parametros);

        if (validarRubroSinAuxiliares()) {
            SessionUtil.redireccionarForma(direccionador,
                            modulo);
        }
        else {

            redireccionarSeleccionar(direccionador);
        }
    }

    public void oprimirSaldos() {
        indicadorDialog = "saldos";

        Map<String, Object> parametros;

        parametros = new HashMap<>();
        parametros.put("rid", rid);
        parametros.put("anio", anio);
        parametros.put(GeneralParameterEnum.CUENTA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName())
                                        .toString());
        direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SALDO_PLAN_CONTABLE_CONTROLADOR
                                        .getCodigo()));
        direccionador.setRuta("/subsaldoplancontable.sysman");
        direccionador.setParametros(parametros);

        if (validarRubroSinAuxiliares()) {
            SessionUtil.redireccionarForma(direccionador,
                            modulo);
        }
        else {

            redireccionarSeleccionar(direccionador);
        }
    }

    private void redireccionarSeleccionar(Direccionador direccionador) {

        String[] campos = { GeneralParameterEnum.NOMBRE.getName(),
                            "codigo",
                            "ano", "indicador", "direccionador" };
        Object[] valores = { registro.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.CODIGO
                                                             .getName())
                                             .toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.ANO
                                                             .getName())
                                             .toString(),
                             indicadorDialog, direccionador };

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SELECCION_CUENTA_CONTABLE
                                                        .getCodigo()),
                        modulo, campos, valores);

    }

    public void retornarFormularioMovimiento(SelectEvent event) {
        if (event.getObject() != null) {
            SessionUtil.redireccionarForma((Direccionador) event.getObject(),
                            modulo);
        }
    }

    public void retornarFormularioSaldos(SelectEvent event) {
        if (event.getObject() != null) {
            SessionUtil.redireccionarForma((Direccionador) event.getObject(),
                            modulo);
        }
    }

    /**
     * Valida si la cuenta tiene auxiliares relacionados
     * 
     * @return
     */
    private boolean validarRubroSinAuxiliares() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        int total = 0;

        try {
            Registro numeroAuxiliares = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlanContableControladorUrlEnum.URL6565
                                                                                            .getValue())

                                                            .getUrl(),
                                            param));

            total = (int) numeroAuxiliares.getCampos().get("TOTAL");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return total <= 1;

    }

    public void cambiarMov() {
        // <CODIGO_DESARROLLADO>
        bloqueoIndicadores = (boolean) registro.getCampos()
                        .get(GeneralParameterEnum.MOVIMIENTO.getName());
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCenCos() {
        boolean condicion;
        boolean manCenCto = (boolean) (registro.getCampos()
                        .get("MAN_CEN_CTO") != null
                            ? registro.getCampos().get("MAN_CEN_CTO")
                            : false);
        boolean manAuxTer = (boolean) (registro.getCampos()
                        .get("MAN_AUX_TER") != null
                            ? registro.getCampos().get("MAN_AUX_TER")
                            : false);
        boolean manAuxGen = (boolean) (registro.getCampos()
                        .get("MAN_AUX_GEN") != null
                            ? registro.getCampos().get("MAN_AUX_GEN")
                            : false);
        boolean manAuxFue = (boolean) (registro.getCampos()
                        .get("MAN_AUX_FUE") != null
                            ? registro.getCampos().get("MAN_AUX_FUE")
                            : false);
        boolean manAuxRef = (boolean) (registro.getCampos()
                        .get("MAN_AUX_REF") != null
                            ? registro.getCampos().get("MAN_AUX_REF")
                            : false);

        condicion = manCenCto || manAuxTer;
        bloqueoMovimiento = condicion || manAuxGen || manAuxFue
            || manAuxRef;
    }

    public void cambiarAuxTer() {
        // <CODIGO_DESARROLLADO>
        boolean condicionBloq;
        boolean manCenCto = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXCTO
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXCTO
                                                                            .getValue())
                                            : false);
        boolean manAuxTer = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXTER
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXTER
                                                                            .getValue())
                                            : false);
        boolean manAuxGen = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXGEN
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXGEN
                                                                            .getValue())
                                            : false);
        boolean manAuxFue = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXFUE
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXFUE
                                                                            .getValue())
                                            : false);
        boolean manAuxRef = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXREF
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXREF
                                                                            .getValue())
                                            : false);

        condicionBloq = manCenCto || manAuxTer;
        bloqueoMovimiento = condicionBloq || manAuxGen || manAuxFue
            || manAuxRef;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAuxGen() {
        // <CODIGO_DESARROLLADO>
        boolean condicionMovBlo;
        boolean manCenCto = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXCTO
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXCTO
                                                                            .getValue())
                                            : false);
        boolean manAuxTer = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXTER
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXTER
                                                                            .getValue())
                                            : false);
        boolean manAuxGen = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXGEN
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXGEN
                                                                            .getValue())
                                            : false);
        boolean manAuxFue = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXFUE
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXFUE
                                                                            .getValue())
                                            : false);
        boolean manAuxRef = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXREF
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXREF
                                                                            .getValue())
                                            : false);
        condicionMovBlo = manCenCto || manAuxTer;
        bloqueoMovimiento = condicionMovBlo || manAuxGen || manAuxFue
            || manAuxRef;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarManejaFuenteRecursos() {
        // <CODIGO_DESARROLLADO>
        boolean condicionBloq;
        boolean manCenCto = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXCTO
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXCTO
                                                                            .getValue())
                                            : false);
        boolean manAuxTer = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXTER
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXTER
                                                                            .getValue())
                                            : false);
        boolean manAuxGen = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXGEN
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXGEN
                                                                            .getValue())
                                            : false);
        boolean manAuxFue = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXFUE
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXFUE
                                                                            .getValue())
                                            : false);
        boolean manAuxRef = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXREF
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXREF
                                                                            .getValue())
                                            : false);

        condicionBloq = manCenCto || manAuxTer;
        bloqueoMovimiento = condicionBloq || manAuxGen || manAuxFue
            || manAuxRef;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarManejaReferencia() {
        // <CODIGO_DESARROLLADO>
        boolean condicion;
        boolean manCenCto = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXCTO
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXCTO
                                                                            .getValue())
                                            : false);
        boolean manAuxTer = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXTER
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXTER
                                                                            .getValue())
                                            : false);
        boolean manAuxGen = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXGEN
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXGEN
                                                                            .getValue())
                                            : false);
        boolean manAuxFue = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXFUE
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXFUE
                                                                            .getValue())
                                            : false);
        boolean manAuxRef = (boolean) (registro.getCampos()
                        .get(PlanContableControladorEnum.MANAUXREF
                                        .getValue()) != null
                                            ? registro.getCampos()
                                                            .get(PlanContableControladorEnum.MANAUXREF
                                                                            .getValue())
                                            : false);

        condicion = manCenCto || manAuxTer;

        bloqueoMovimiento = condicion || manAuxGen || manAuxFue
            || manAuxRef;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCkAplicaDeterioro() {
        // <CODIGO_DESARROLLADO>

        habilitarDeterioro = (boolean) registro.getCampos()
                        .get("APLICA_DETERIORO");
        if (habilitarDeterioro) {
            iniciarListasDeterioro();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaRUBRO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("RUBRO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaRUBROE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }
    public void seleccionarFilaAuxiliar(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registroSub.getCampos().put("AUXILIAR", registroAux.getCampos().get("CODIGO"));
    }

    public void seleccionarFilaAuxiliarE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaFuente(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registroSub.getCampos().put("FUENTE_RECURSO", registroAux.getCampos().get("CODIGO"));
    }

    public void seleccionarFilaFuenteE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar = registroAux.getCampos()
    			.get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaCentro(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registroSub.getCampos().put("CENTRO_COSTO", registroAux.getCampos().get("CODIGO"));
    }

    public void seleccionarFilaCentroE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaReferencia(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registroSub.getCampos().put("REFERENCIA", registroAux.getCampos().get("CODIGO"));
    }

    public void seleccionarFilaReferenciaE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaConceptoEx(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTOEX",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaEquivprDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EQUIVPR_DEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaEquivprCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EQUIVPR_CREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDeterioroDebCausDet(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEB_CAUS_DET",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDeterioroCreCausDet(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CRE_CAUS_DET",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDeterioroDebRecDet(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEB_REC_DET",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDeterioroCreRecDet(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CRE_REC_DET",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDeterioroDebRecoDet(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEB_RECO_DET",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDeterioroCreRecoDet(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CRE_RECO_DET",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReconocimientoDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReconocimientoDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_DEBITO_RECONOCIMIENTO",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReconocimientoCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReconocimientoCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_CREDITO_RECONOCIMIENTO",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCausacionDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCausacionDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_DEBITO_CAUSACION",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCausacionCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCausacionCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_CREDITO_CAUSACION",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaRecaudoDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRecaudoDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_DEBITO_RECAUDO",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaRecaudoCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRecaudoCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_CREDITO_RECAUDO",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoVigenciaActual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoVigenciaActual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_REVERSION_DET_ACTUAL",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoVigenciaActual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoVigenciaActual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_REVERSION_DET_ACTUAL",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoVigenciaAnterior
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoVigenciaAnterior(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_REVERSION_DET_ANTERIOR",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoVigenciaAnterior
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoVigenciaAnterior(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_REVERSION_DET_ANTERIOR",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilacuentaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        id = registroAux.getCampos().get("ID").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoEquivalenteCartera
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoEquivalenteCartera(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_EQUI_CARTERA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEquivalenteDevolucion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEquivalenteDevolucion(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("EQUIVALENTE_DEVOLUCION", registroAux.getCampos().get("CODIGO"));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodReconocimiento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodReconocimiento(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("COD_RECONOCIMIENTO", registroAux.getCampos().get("CODIGO"));
    }

    @Override
    public void abrirFormulario() {
    	// <CODIGO_DESARROLLADO>
    	try {
    		manejaEquivalente = SysmanFunciones.nvl(
    				ejbSysmanUtil.consultarParametro(compania, "MANEJA EQUIVALENTE PRESUPUESTAL FIJO AUTOMATICO", modulo, new Date(), true),
    				"NO").equals("SI");
    		
    	} catch (SystemException e) {
    		e.printStackTrace();
    	}	
    	

        
        if (!cargaParametros) {
            FacesContext context = FacesContext.getCurrentInstance();
            try {
                manDolares = "SI".equalsIgnoreCase((String) SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MANEJA DOLARES", modulo,
                                                new Date(), false), "NO"));

                permiteEleTamLetra = "SI"
                                .equalsIgnoreCase((String) SysmanFunciones.nvl(
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                parametros.getString(
                                                                                "PR_TAMANOCHEQUE"),
                                                                modulo,
                                                                new Date(),
                                                                false),
                                                "NO"));

                manFormEgresoBanco = "SI"
                                .equalsIgnoreCase((String) SysmanFunciones.nvl(
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "MANEJA FORMATO EGRESO POR BANCO",
                                                                modulo,
                                                                new Date(),
                                                                false),
                                                "NO"));

                manDistrCont = "SI"
                                .equalsIgnoreCase((String) SysmanFunciones.nvl(
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "MANEJA DISTRIBUCION CONTABLE",
                                                                modulo,
                                                                new Date(),
                                                                false),
                                                "NO"));

                manDistrCtoGast = "SI".equalsIgnoreCase((String) SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MANEJA DISTRIBUCION POR CENTROS DE COSTO EN GASTOS",
                                                modulo, new Date(),
                                                false), "NO"))
                    && "SI".equalsIgnoreCase((String) SysmanFunciones.nvl(
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "MANEJA DISTRIBUCION DE BANCOS POR CENTRO DE COSTO EN EGRESO",
                                                    modulo, new Date(),
                                                    false),
                                    "NO"));

                manBalCtoDist = "SI".equalsIgnoreCase(
                                (String) SysmanFunciones
                                                .nvl(ejbSysmanUtil
                                                                .consultarParametro(
                                                                                compania,
                                                                                "MANEJA BALANCE POR CENTRO DE COSTO DISTRIBUIDO",
                                                                                modulo,
                                                                                new Date(),
                                                                                false),
                                                                "NO"));

                manChequera = "SI".equalsIgnoreCase((String) SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MANEJA CONTROL DE CHEQUERAS",
                                                modulo, new Date(), false),
                                                "NO"));

                afectarFactArren = "SI"
                                .equalsIgnoreCase((String) SysmanFunciones.nvl(
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "AFECTAR FACTURAS DE ARRENDAMIENTO",
                                                                modulo,
                                                                new Date(),
                                                                false),
                                                "NO"));

                manRestriccionesCC = "SI"
                                .equalsIgnoreCase((String) SysmanFunciones.nvl(
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "MANEJA RESTRICCIONES EN CENTRO DE COSTO",
                                                                modulo,
                                                                new Date(),
                                                                false),
                                                "NO"));

                // </CODIGO_DESARROLLADO>
            }
            catch (SystemException ex) {
                Logger.getLogger(PlanContableControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                context.addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                idioma.getString("TB_TB4"),
                                                ex.getMessage()));
            }
        }
        cargaParametros = true;
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>

        precargarRegistro();
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        if ("m".equals(accion)) {
            boolean condicionCampos;
            bloqueoIndicadores = (boolean) registro.getCampos()
                            .get(GeneralParameterEnum.MOVIMIENTO.getName());
            boolean manCenCto = (boolean) (registro.getCampos()
                            .get(PlanContableControladorEnum.MANAUXCTO
                                            .getValue()) != null
                                                ? registro.getCampos()
                                                                .get(PlanContableControladorEnum.MANAUXCTO
                                                                                .getValue())
                                                : false);
            boolean manAuxTer = (boolean) (registro.getCampos()
                            .get(PlanContableControladorEnum.MANAUXTER
                                            .getValue()) != null
                                                ? registro.getCampos()
                                                                .get(PlanContableControladorEnum.MANAUXTER
                                                                                .getValue())
                                                : false);
            boolean manAuxGen = (boolean) (registro.getCampos()
                            .get(PlanContableControladorEnum.MANAUXGEN
                                            .getValue()) != null
                                                ? registro.getCampos()
                                                                .get(PlanContableControladorEnum.MANAUXGEN
                                                                                .getValue())
                                                : false);
            boolean manAuxFue = (boolean) (registro.getCampos()
                            .get(PlanContableControladorEnum.MANAUXFUE
                                            .getValue()) != null
                                                ? registro.getCampos()
                                                                .get(PlanContableControladorEnum.MANAUXFUE
                                                                                .getValue())
                                                : false);
            boolean manAuxRef = (boolean) (registro.getCampos()
                            .get(PlanContableControladorEnum.MANAUXREF
                                            .getValue()) != null
                                                ? registro.getCampos()
                                                                .get(PlanContableControladorEnum.MANAUXREF
                                                                                .getValue())
                                                : false);

            condicionCampos = manCenCto || manAuxTer;

            bloqueoMovimiento = condicionCampos || manAuxGen || manAuxFue
                || manAuxRef;

            condicionCampos = (registro.getCampos().get("DEB_CAUS_DET") != null)
                || (registro.getCampos().get("CRE_CAUS_DET") != null)
                || (registro.getCampos().get("DEB_REC_DET") != null)
                    ? true
                    : false;

            if (condicionCampos
                || (registro.getCampos().get("CRE_REC_DET") != null)
                || (registro.getCampos().get("DEB_RECO_DET") != null)
                || (registro.getCampos().get("CRE_RECO_DET") != null)) {
                bloquearCkDeterioro = true;
            }
            else {
                bloquearCkDeterioro = false;
            }

        }
        else {
            bloqueoIndicadores = true;
            bloqueoMovimiento = true;
            registro.getCampos().put("BLOQUEACUENTA", "NO");
        }

        if (css != null) {
            habilitarDeterioro = "true"
                            .equals(registro.getCampos().get("APLICA_DETERIORO")
                                            .toString());
            if (habilitarDeterioro) {
                iniciarListasDeterioro();
            }
        }
    }

    public void cancelarEdicionSubplanpptalcuentacnt() {
        cargarListaSubplanpptalcuentacnt();
    }

    public void buscarPredecesorContable(Registro registro,
        Map<String, Object> indicadores)
                    throws SysmanException {

        String idPre = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(PlanContableControladorEnum.ANO.getValue(), anio);
        parametros.put(GeneralParameterEnum.CODIGO.getName(), idPre);
        parametros.put(PlanContableControladorEnum.CANTCOD.getValue(),
                        idPre.length());

        UrlBean urlReg = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanContableControladorUrlEnum.URL16196
                                                        .getValue());

        Registro rs = null;

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(urlReg.getUrl(), parametros));

        }
        catch (SystemException e) {
            Logger.getLogger(PlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null) {
            boolean condicionIndicadores = ((boolean) rs.getCampos()
                            .get(GeneralParameterEnum.MOVIMIENTO.getName()))
                || ((boolean) rs.getCampos()
                                .get(PlanContableControladorEnum.MANAUXCTO
                                                .getValue()))
                || ((boolean) rs.getCampos()
                                .get(PlanContableControladorEnum.MANAUXTER
                                                .getValue()));

            if (rs.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                            .toString().equals(idPre)) {
                registro.getCampos().put(
                                GeneralParameterEnum.NATURALEZA.getName(),
                                null);
                registro.getCampos().put(PlanContableControladorEnum.CLASCUENTA
                                .getValue(), null);

                throw new SysmanException(idioma.getString("TB_TB11")); // MZ
                                                                        // YA
                                                                        // EXISTE
                                                                        // EL
                                                                        // REGISTRO
            }
            else
                if (condicionIndicadores
                    || ((boolean) rs.getCampos()
                                    .get(PlanContableControladorEnum.MANAUXGEN
                                                    .getValue()))
                    || ((boolean) rs.getCampos()
                                    .get(PlanContableControladorEnum.MANAUXFUE
                                                    .getValue()))
                    || ((boolean) rs.getCampos()
                                    .get(PlanContableControladorEnum.MANAUXREF
                                                    .getValue()))) {
                                                        registro.getCampos()
                                                                        .put(
                                                                                        GeneralParameterEnum.NATURALEZA
                                                                                                        .getName(),
                                                                                        null);
                                                        registro.getCampos()
                                                                        .put(PlanContableControladorEnum.CLASCUENTA
                                                                                        .getValue(),
                                                                                        null);

                                                        throw new SysmanException(
                                                                        idioma.getString(
                                                                                        "TB_TB14"));
                                                    }

            registro.getCampos().put(GeneralParameterEnum.NATURALEZA.getName(),
                            rs.getCampos().get(GeneralParameterEnum.NATURALEZA
                                            .getName()).toString());

            registro.getCampos().put(
                            PlanContableControladorEnum.CLASCUENTA.getValue(),
                            rs.getCampos().get(
                                            PlanContableControladorEnum.CLASCUENTA
                                                            .getValue())
                                            .toString());

            parametros.clear();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(PlanContableControladorEnum.ANO.getValue(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));
            parametros.put(GeneralParameterEnum.CODIGO.getName(), idPre);
            parametros.put(PlanContableControladorEnum.LONGCOD.getValue(),
                            idPre.length());

            HashMap<String, Object> rst = new HashMap<>();

            UrlBean urlRegis = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlanContableControladorUrlEnum.URL9891
                                                            .getValue());
            try {
                rst = (HashMap<String, Object>) requestManager
                                .get(urlRegis.getUrl(), parametros).getFields();
            }
            catch (SystemException e) {
                Logger.getLogger(PlanContableControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }

            if ((rst != null) && ((int) rst.get("HIJOS") > 0)) {
                registro.getCampos().put("MOVIMIENTO", false);
                registro.getCampos().put(PlanContableControladorEnum.MANAUXCTO
                                .getValue(), false);
                registro.getCampos().put(PlanContableControladorEnum.MANAUXTER
                                .getValue(), false);
                registro.getCampos().put(PlanContableControladorEnum.MANAUXGEN
                                .getValue(), false);
                registro.getCampos().put(PlanContableControladorEnum.MANAUXFUE
                                .getValue(), false);
                registro.getCampos().put(PlanContableControladorEnum.MANAUXREF
                                .getValue(), false);
                indicadores.put(PlanContableControladorEnum.BLOCIND.getValue(),
                                true);
            }

        }
        else {
            registro.getCampos().put(GeneralParameterEnum.NATURALEZA.getName(),
                            null);
            registro.getCampos().put(
                            PlanContableControladorEnum.CLASCUENTA.getValue(),
                            null);
        }

    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        String idAc = SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        if (idAc.length() == 1) {
            if ((idAc.compareTo("2") >= 0) && (idAc.compareTo("4") <= 0)) {
                registro.getCampos().put(
                                GeneralParameterEnum.NATURALEZA.getName(), "C");
            }
            else {
                registro.getCampos().put(
                                GeneralParameterEnum.NATURALEZA.getName(), "D");
            }
        }

        if ("m".equals(accion)) {
            boolean condicion = (boolean) registro.getCampos()
                            .get(PlanContableControladorEnum.MOV.getValue())
                ||
                (boolean) registro.getCampos()
                                .get(PlanContableControladorEnum.MANAUXCTO
                                                .getValue())
                ||
                (boolean) registro.getCampos()
                                .get(PlanContableControladorEnum.MANAUXTER
                                                .getValue())
                ||
                (boolean) registro.getCampos()
                                .get(PlanContableControladorEnum.MANAUXGEN
                                                .getValue());

            if ((registro.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                            .toString().length() < 6)
                &&
                (condicion || (boolean) registro.getCampos()
                                .get(PlanContableControladorEnum.MANAUXREF
                                                .getValue())
                    ||
                    (boolean) registro.getCampos()
                                    .get(PlanContableControladorEnum.MANAUXFUE
                                                    .getValue()))

            ) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3184"));
                return false;
            }

            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

        }

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
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
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaClaseCuenta() {
        return listaClaseCuenta;
    }

    public void setListaClaseCuenta(List<Registro> listaClaseCuenta) {
        this.listaClaseCuenta = listaClaseCuenta;
    }

    public List<Registro> getListabanco() {
        return listabanco;
    }

    public void setListabanco(List<Registro> listabanco) {
        this.listabanco = listabanco;
    }

    public List<Registro> getListaCcBalance() {
        return listaCcBalance;
    }

    public List<Registro> getListaCmbCodEquivalente() {
        return listaCmbCodEquivalente;
    }

    public void setListaCmbCodEquivalente(
        List<Registro> listaCmbCodEquivalente) {
        this.listaCmbCodEquivalente = listaCmbCodEquivalente;
    }

    public List<Registro> getListaCodFlujoCaja() {
        return listaCodFlujoCaja;
    }

    public void setListaCodFlujoCaja(List<Registro> listaCodFlujoCaja) {
        this.listaCodFlujoCaja = listaCodFlujoCaja;
    }

    public void setListaCcBalance(List<Registro> listaCcBalance) {
        this.listaCcBalance = listaCcBalance;
    }

    public RegistroDataModelImpl getListaEquivprDebito() {
        return listaEquivprDebito;
    }

    public void setListaEquivprDebito(
        RegistroDataModelImpl listaEquivprDebito) {
        this.listaEquivprDebito = listaEquivprDebito;
    }

    public RegistroDataModelImpl getListaEquivprCredito() {
        return listaEquivprCredito;
    }

    public void setListaEquivprCredito(
        RegistroDataModelImpl listaEquivprCredito) {
        this.listaEquivprCredito = listaEquivprCredito;
    }

    public List<Registro> getListaSubplanpptalcuentacnt() {
        return listaSubplanpptalcuentacnt;
    }

    public void setListaSubplanpptalcuentacnt(
        List<Registro> listaSubplanpptalcuentacnt) {
        this.listaSubplanpptalcuentacnt = listaSubplanpptalcuentacnt;
    }

    public RegistroDataModelImpl getListaRUBRO() {
        return listaRUBRO;
    }

    public void setListaRUBRO(RegistroDataModelImpl listaRUBRO) {
        this.listaRUBRO = listaRUBRO;
    }

    public RegistroDataModelImpl getListaRUBROE() {
        return listaRUBROE;
    }

    public void setListaRUBROE(RegistroDataModelImpl listaRUBROE) {
        this.listaRUBROE = listaRUBROE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaConceptoEx() {
        return listaConceptoEx;
    }

    public void setListaConceptoEx(RegistroDataModelImpl listaConceptoEx) {
        this.listaConceptoEx = listaConceptoEx;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    // <CODIGO_DESARROLLADO>
    public boolean isBloqueoMovimiento() {
        return bloqueoMovimiento;
    }

    public void setBloqueoMovimiento(boolean bloqueoMovimiento) {
        this.bloqueoMovimiento = bloqueoMovimiento;
    }

    public boolean isBloqueoIndicadores() {
        return bloqueoIndicadores;
    }

    public void setBloqueoIndicadores(boolean bloqueoIndicadores) {
        this.bloqueoIndicadores = bloqueoIndicadores;
    }

    public boolean isManDolares() {
        return manDolares;
    }

    public void setManDolares(boolean manDolares) {
        this.manDolares = manDolares;
    }

    public boolean isPermiteEleTamLetra() {
        return permiteEleTamLetra;
    }

    public void setPermiteEleTamLetra(boolean permiteEleTamLetra) {
        this.permiteEleTamLetra = permiteEleTamLetra;
    }

    public boolean isManFormEgresoBanco() {
        return manFormEgresoBanco;
    }

    public void setManFormEgresoBanco(boolean manFormEgresoBanco) {
        this.manFormEgresoBanco = manFormEgresoBanco;
    }

    public boolean isManRefCuentas() {
        return manRefCuentas;
    }

    public void setManRefCuentas(boolean manRefCuentas) {
        this.manRefCuentas = manRefCuentas;
    }

    public boolean isManDistrCont() {
        return manDistrCont;
    }

    public void setManDistrCont(boolean manDistrCont) {
        this.manDistrCont = manDistrCont;
    }

    public boolean isManDistrCtoGast() {
        return manDistrCtoGast;
    }

    public void setManDistrCtoGast(boolean manDistrCtoGast) {
        this.manDistrCtoGast = manDistrCtoGast;
    }

    public boolean isManBalCtoDist() {
        return manBalCtoDist;
    }

    public void setManBalCtoDist(boolean manBalCtoDist) {
        this.manBalCtoDist = manBalCtoDist;
    }

    public boolean isManChequera() {
        return manChequera;
    }

    public void setManChequera(boolean manChequera) {
        this.manChequera = manChequera;
    }

    public boolean isAfectarFactArren() {
        return afectarFactArren;
    }

    public void setAfectarFactArren(boolean afectarFactArren) {
        this.afectarFactArren = afectarFactArren;
    }

    public boolean isManRestriccionesCC() {
        return manRestriccionesCC;
    }

    public void setManRestriccionesCC(boolean manRestriccionesCC) {
        this.manRestriccionesCC = manRestriccionesCC;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaDeterioroDebCausDet() {
        return listaDeterioroDebCausDet;
    }

    public void setListaDeterioroDebCausDet(
        RegistroDataModelImpl listaDeterioroDebCausDet) {
        this.listaDeterioroDebCausDet = listaDeterioroDebCausDet;
    }

    public RegistroDataModelImpl getListaDeterioroCreCausDet() {
        return listaDeterioroCreCausDet;
    }

    public void setListaDeterioroCreCausDet(
        RegistroDataModelImpl listaDeterioroCreCausDet) {
        this.listaDeterioroCreCausDet = listaDeterioroCreCausDet;
    }

    public RegistroDataModelImpl getListaDeterioroDebRecDet() {
        return listaDeterioroDebRecDet;
    }

    public void setListaDeterioroDebRecDet(
        RegistroDataModelImpl listaDeterioroDebRecDet) {
        this.listaDeterioroDebRecDet = listaDeterioroDebRecDet;
    }

    public RegistroDataModelImpl getListaDeterioroCreRecDet() {
        return listaDeterioroCreRecDet;
    }

    public void setListaDeterioroCreRecDet(
        RegistroDataModelImpl listaDeterioroCreRecDet) {
        this.listaDeterioroCreRecDet = listaDeterioroCreRecDet;
    }

    public RegistroDataModelImpl getListaDeterioroDebRecoDet() {
        return listaDeterioroDebRecoDet;
    }

    public void setListaDeterioroDebRecoDet(
        RegistroDataModelImpl listaDeterioroDebRecoDet) {
        this.listaDeterioroDebRecoDet = listaDeterioroDebRecoDet;
    }

    /**
     * Retorna la lista listaReconocimientoDebito
     * 
     * @return listaReconocimientoDebito
     */
    public RegistroDataModelImpl getListaReconocimientoDebito() {
        return listaReconocimientoDebito;
    }

    /**
     * Asigna la lista listaReconocimientoDebito
     * 
     * @param listaReconocimientoDebito
     * Variable a asignar en listaReconocimientoDebito
     */
    public void setListaReconocimientoDebito(
        RegistroDataModelImpl listaReconocimientoDebito) {
        this.listaReconocimientoDebito = listaReconocimientoDebito;
    }

    /**
     * Retorna la lista listaReconocimientoCredito
     * 
     * @return listaReconocimientoCredito
     */
    public RegistroDataModelImpl getListaReconocimientoCredito() {
        return listaReconocimientoCredito;
    }

    /**
     * Asigna la lista listaReconocimientoCredito
     * 
     * @param listaReconocimientoCredito
     * Variable a asignar en listaReconocimientoCredito
     */
    public void setListaReconocimientoCredito(
        RegistroDataModelImpl listaReconocimientoCredito) {
        this.listaReconocimientoCredito = listaReconocimientoCredito;
    }

    /**
     * Retorna la lista listaCausacionDebito
     * 
     * @return listaCausacionDebito
     */
    public RegistroDataModelImpl getListaCausacionDebito() {
        return listaCausacionDebito;
    }

    /**
     * Asigna la lista listaCausacionDebito
     * 
     * @param listaCausacionDebito
     * Variable a asignar en listaCausacionDebito
     */
    public void setListaCausacionDebito(
        RegistroDataModelImpl listaCausacionDebito) {
        this.listaCausacionDebito = listaCausacionDebito;
    }

    /**
     * Retorna la lista listaCausacionCredito
     * 
     * @return listaCausacionCredito
     */
    public RegistroDataModelImpl getListaCausacionCredito() {
        return listaCausacionCredito;
    }

    /**
     * Asigna la lista listaCausacionCredito
     * 
     * @param listaCausacionCredito
     * Variable a asignar en listaCausacionCredito
     */
    public void setListaCausacionCredito(
        RegistroDataModelImpl listaCausacionCredito) {
        this.listaCausacionCredito = listaCausacionCredito;
    }

    /**
     * Retorna la lista listaRecaudoDebito
     * 
     * @return listaRecaudoDebito
     */
    public RegistroDataModelImpl getListaRecaudoDebito() {
        return listaRecaudoDebito;
    }

    /**
     * Asigna la lista listaRecaudoDebito
     * 
     * @param listaRecaudoDebito
     * Variable a asignar en listaRecaudoDebito
     */
    public void setListaRecaudoDebito(
        RegistroDataModelImpl listaRecaudoDebito) {
        this.listaRecaudoDebito = listaRecaudoDebito;
    }

    /**
     * Retorna la lista listaRecaudoCredito
     * 
     * @return listaRecaudoCredito
     */
    public RegistroDataModelImpl getListaRecaudoCredito() {
        return listaRecaudoCredito;
    }

    /**
     * Asigna la lista listaRecaudoCredito
     * 
     * @param listaRecaudoCredito
     * Variable a asignar en listaRecaudoCredito
     */
    public void setListaRecaudoCredito(
        RegistroDataModelImpl listaRecaudoCredito) {
        this.listaRecaudoCredito = listaRecaudoCredito;
    }

    /**
     * Retorna la lista listaDebitoVigenciaActual
     * 
     * @return listaDebitoVigenciaActual
     */
    public RegistroDataModelImpl getListaDebitoVigenciaActual() {
        return listaDebitoVigenciaActual;
    }

    /**
     * Asigna la lista listaDebitoVigenciaActual
     * 
     * @param listaDebitoVigenciaActual
     * Variable a asignar en listaDebitoVigenciaActual
     */
    public void setListaDebitoVigenciaActual(
        RegistroDataModelImpl listaDebitoVigenciaActual) {
        this.listaDebitoVigenciaActual = listaDebitoVigenciaActual;
    }

    /**
     * Retorna la lista listaCreditoVigenciaActual
     * 
     * @return listaCreditoVigenciaActual
     */
    public RegistroDataModelImpl getListaCreditoVigenciaActual() {
        return listaCreditoVigenciaActual;
    }

    /**
     * Asigna la lista listaCreditoVigenciaActual
     * 
     * @param listaCreditoVigenciaActual
     * Variable a asignar en listaCreditoVigenciaActual
     */
    public void setListaCreditoVigenciaActual(
        RegistroDataModelImpl listaCreditoVigenciaActual) {
        this.listaCreditoVigenciaActual = listaCreditoVigenciaActual;
    }

    /**
     * Retorna la lista listaDebitoVigenciaAnterior
     * 
     * @return listaDebitoVigenciaAnterior
     */
    public RegistroDataModelImpl getListaDebitoVigenciaAnterior() {
        return listaDebitoVigenciaAnterior;
    }

    /**
     * Asigna la lista listaDebitoVigenciaAnterior
     * 
     * @param listaDebitoVigenciaAnterior
     * Variable a asignar en listaDebitoVigenciaAnterior
     */
    public void setListaDebitoVigenciaAnterior(
        RegistroDataModelImpl listaDebitoVigenciaAnterior) {
        this.listaDebitoVigenciaAnterior = listaDebitoVigenciaAnterior;
    }

    /**
     * Retorna la lista listaCreditoVigenciaAnterior
     * 
     * @return listaCreditoVigenciaAnterior
     */
    public RegistroDataModelImpl getListaCreditoVigenciaAnterior() {
        return listaCreditoVigenciaAnterior;
    }

    /**
     * Asigna la lista listaCreditoVigenciaAnterior
     * 
     * @param listaCreditoVigenciaAnterior
     * Variable a asignar en listaCreditoVigenciaAnterior
     */
    public void setListaCreditoVigenciaAnterior(
        RegistroDataModelImpl listaCreditoVigenciaAnterior) {
        this.listaCreditoVigenciaAnterior = listaCreditoVigenciaAnterior;
    }

    /**
     * Retorna la lista listaCodigoEquivalenteCartera
     * 
     * @return listaCodigoEquivalenteCartera
     */
    public RegistroDataModelImpl getListaCodigoEquivalenteCartera() {
        return listaCodigoEquivalenteCartera;
    }

    /**
     * Asigna la lista listaCodigoEquivalenteCartera
     * 
     * @param listaCodigoEquivalenteCartera
     * Variable a asignar en listaCodigoEquivalenteCartera
     */
    public void setListaCodigoEquivalenteCartera(
        RegistroDataModelImpl listaCodigoEquivalenteCartera) {
        this.listaCodigoEquivalenteCartera = listaCodigoEquivalenteCartera;
    }

    public RegistroDataModelImpl getListaDeterioroCreRecoDet() {
        return listaDeterioroCreRecoDet;
    }

    public void setListaDeterioroCreRecoDet(
        RegistroDataModelImpl listaDeterioroCreRecoDet) {
        this.listaDeterioroCreRecoDet = listaDeterioroCreRecoDet;
    }

    public boolean isHabilitarDeterioro() {
        return habilitarDeterioro;
    }

    public void setHabilitarDeterioro(boolean habilitarDeterioro) {
        this.habilitarDeterioro = habilitarDeterioro;
    }

    public boolean isBloquearCkDeterioro() {
        return bloquearCkDeterioro;
    }

    public void setBloquearCkDeterioro(boolean bloquearCkDeterioro) {
        this.bloquearCkDeterioro = bloquearCkDeterioro;
    }

    public String getId() {
        return id;
    }

    public boolean isMuestraEliminar() {
        return muestraEliminar;
    }

    public void setMuestraEliminar(boolean muestraEliminar) {
        this.muestraEliminar = muestraEliminar;
    }

    public boolean isMuestraNuevo() {
        return muestraNuevo;
    }

    public void setMuestraNuevo(boolean muestraNuevo) {
        this.muestraNuevo = muestraNuevo;
    }

    public boolean isMuestraActualiza() {
        return muestraActualiza;
    }

    public void setMuestraActualiza(boolean muestraActualiza) {
        this.muestraActualiza = muestraActualiza;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSeleccionarCuentaVisible() {
        return seleccionarCuentaVisible;
    }

    public void setSeleccionarCuentaVisible(boolean seleccionarCuentaVisible) {
        this.seleccionarCuentaVisible = seleccionarCuentaVisible;
    }

    public String getIndicadorDialog() {
        return indicadorDialog;
    }

    public void setIndicadorDialog(String indicadorDialog) {
        this.indicadorDialog = indicadorDialog;
    }

	/**
	 * @return the listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	/**
	 * @param listaAuxiliar the listaAuxiliar to set
	 */
	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	/**
	 * @return the listaAuxiliarE
	 */
	public RegistroDataModelImpl getListaAuxiliarE() {
		return listaAuxiliarE;
	}

	/**
	 * @param listaAuxiliarE the listaAuxiliarE to set
	 */
	public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
		this.listaAuxiliarE = listaAuxiliarE;
	}

	/**
	 * @return the listaFuente
	 */
	public RegistroDataModelImpl getListaFuente() {
		return listaFuente;
	}

	/**
	 * @param listaFuente the listaFuente to set
	 */
	public void setListaFuente(RegistroDataModelImpl listaFuente) {
		this.listaFuente = listaFuente;
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

	/**
	 * @return the listaCentro
	 */
	public RegistroDataModelImpl getListaCentro() {
		return listaCentro;
	}

	/**
	 * @param listaCentro the listaCentro to set
	 */
	public void setListaCentro(RegistroDataModelImpl listaCentro) {
		this.listaCentro = listaCentro;
	}

	/**
	 * @return the listaCentroE
	 */
	public RegistroDataModelImpl getListaCentroE() {
		return listaCentroE;
	}

	/**
	 * @param listaCentroE the listaCentroE to set
	 */
	public void setListaCentroE(RegistroDataModelImpl listaCentroE) {
		this.listaCentroE = listaCentroE;
	}

	/**
	 * @return the listaReferencia
	 */
	public RegistroDataModelImpl getListaReferencia() {
		return listaReferencia;
	}

	/**
	 * @param listaReferencia the listaReferencia to set
	 */
	public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
		this.listaReferencia = listaReferencia;
	}

	/**
	 * @return the listaReferenciaE
	 */
	public RegistroDataModelImpl getListaReferenciaE() {
		return listaReferenciaE;
	}

	/**
	 * @param listaReferenciaE the listaReferenciaE to set
	 */
	public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
		this.listaReferenciaE = listaReferenciaE;
	}

	/**
	 * @return the manejaEquivalente
	 */
	public boolean isManejaEquivalente() {
		return manejaEquivalente;
	}

	/**
	 * @param manejaEquivalente the manejaEquivalente to set
	 */
	public void setManejaEquivalente(boolean manejaEquivalente) {
		this.manejaEquivalente = manejaEquivalente;
	}

	/**
	 * @return the listaEquivalenteDevolucion
	 */
	public RegistroDataModelImpl getListaEquivalenteDevolucion() {
		return listaEquivalenteDevolucion;
	}

	/**
	 * @param listaEquivalenteDevolucion the listaEquivalenteDevolucion to set
	 */
	public void setListaEquivalenteDevolucion(RegistroDataModelImpl listaEquivalenteDevolucion) {
		this.listaEquivalenteDevolucion = listaEquivalenteDevolucion;
	}

	/**
	 * @return the listaCodReconocimiento
	 */
	public RegistroDataModelImpl getListaCodReconocimiento() {
		return listaCodReconocimiento;
	}

	/**
	 * @param listaCodReconocimiento the listaCodReconocimiento to set
	 */
	public void setListaCodReconocimiento(RegistroDataModelImpl listaCodReconocimiento) {
		this.listaCodReconocimiento = listaCodReconocimiento;
	}

}
