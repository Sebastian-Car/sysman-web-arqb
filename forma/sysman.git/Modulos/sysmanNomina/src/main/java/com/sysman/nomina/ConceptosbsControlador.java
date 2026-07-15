package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.EstampillasControlador;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.impl.EjbNominaSiete;
import com.sysman.nomina.enums.ConceptosbsControladorEnum;
import com.sysman.nomina.enums.ConceptosbsControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author cmanrique
 * @version 1.1, 24/03/2017, pespitia <br>
 * - Se paso la consulta del metodo oprimirREVISAINTERFACE al esquema
 * SYSMANIRIS, con el nombre de 800101CONCEPTOSBS. <br>
 * - Buenas practicas SonarLint.
 *
 * @author eamaya
 * @version 2.0, 22/08/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs ,cambio de numero de formulario por enum y cambio de
 * redireccionamiento
 *
 * @version 3.0, 19/02/2018, <strong>pespitia</strong> <br>
 * Ajustes para abrir el formulario desde la opcion de menu: 60506.
 *
 */
@ManagedBean
@ViewScoped
public class ConceptosbsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private String anoNomina;
    private String modulo;
    private String anio;
    private String anioPreparar;
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Atributo que almacena el nombre del rubro credito
     */
    private String nombreRubroCredito;
    /**
     * Constante a nivel de clase que aloja el codigo del menu desde
     * el cual el usuario inicio sesion.
     */
    private final String menuActual = SessionUtil.getMenuActual();

    /**
     * Constante a nivel de clase que aloja el codigo del menu: 60506.
     */
    private final String m60506 = ConceptosbsControladorEnum.M_60506.getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * {@code ID_DE_CONCEPTO}.
     */
    private final String cIdConcepto = GeneralParameterEnum.ID_DE_CONCEPTO
                    .getName();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code NOMCONCEPTORETRO}
     */
    private final String cNomConcepto = ConceptosbsControladorEnum.NOMCONCEPTORETRO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code NOMCONCEPTORELA}.
     */
    private final String cNombreConceptoRela = ConceptosbsControladorEnum.NOMCONCEPTORELA
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code TERCEROINTERFAZ}.
     */
    private final String cTerceroInterfaz = ConceptosbsControladorEnum.TERCEROINTERFAZ
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_SUELDOS_EMB}.
     */
    private final String cFactorSueldosEmb = ConceptosbsControladorEnum.FACTOR_SUELDOS_EMB
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_SUELDOS}
     */
    private final String cFactorSueldos = ConceptosbsControladorEnum.FACTOR_SUELDOS
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_SALARIAL_EMB}.
     */
    private final String cFactorSalarialEmb = ConceptosbsControladorEnum.FACTOR_SALARIAL_EMB
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_PRESTACIONAL_EMB}.
     */
    private final String cFactorPrestacionalEmb = ConceptosbsControladorEnum.FACTOR_PRESTACIONAL_EMB
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_SALARIAL}.
     */
    private final String cFactorSalarial = ConceptosbsControladorEnum.FACTOR_SALARIAL
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_LEGALES}.
     */
    private final String cFactorLegales = ConceptosbsControladorEnum.FACTOR_LEGALES
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_EXTRALEGALES}.
     */
    private final String cFactorExtralegales = ConceptosbsControladorEnum.FACTOR_EXTRALEGALES
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_PRESTACIONAL}.
     */
    private final String cFactorPrestacional = ConceptosbsControladorEnum.FACTOR_PRESTACIONAL
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_OTROSPAGOS}.
     */
    private final String cFactorOtros = ConceptosbsControladorEnum.FACTOR_OTROSPAGOS
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code FACTOR_RETEFUENTE}.
     */
    private final String cFactorRetefuente = ConceptosbsControladorEnum.FACTOR_RETEFUENTE
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code Cta Debito }.
     */
    private final String cCtaDebito = ConceptosbsControladorEnum.C_CTA_DEBITO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code Cta Credito }.
     */
    private final String cCtaCredito = ConceptosbsControladorEnum.C_CTA_CREDITO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja el valor {@code C_RETRO }.
     */
    private final String cCReto = ConceptosbsControladorEnum.C_RETRO.getValue();

    private String nomConceptoRetro;
    private String grupoAdmon;
    private String grupoAdmonC;
    private String valParametro;
    private String grupoVentasD;
    private String grupoVentasC;
    private String grupoProdD;
    private String grupoProdC;
    private String grupoOtroD;
    private String grupoOtroC;
    private String grupoSuperD;
    private String grupoSuperC;
    private Registro registroSub;
    private RegistroDataModelImpl listaRelacionado;
    private List<Registro> listaCBanco;
    private List<Registro> listaTipoConceptoAGR;
    private List<Registro> listaTipoDeFondo;
    private List<Registro> listaTipoPagoSia;
    private List<Registro> listaAnioActual;
    private List<Registro> listaAnioPreparar;

    private RegistroDataModelImpl listaIdCentrodeCosto;
	private RegistroDataModelImpl listaIdCentrodeCostoE;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaAuxiliarE;
	private RegistroDataModelImpl listaSubconceptocentrocosto;
    private RegistroDataModelImpl listaCRetro;
    private RegistroDataModelImpl listaTercero;
    private StreamedContent archivoDescarga;
    
    private RegistroDataModelImpl listaCtaDebito;
	private RegistroDataModelImpl listaCtaDebitoE;
	private RegistroDataModelImpl listaCtaCredito;
	private RegistroDataModelImpl listaCtaCreditoE;
	private RegistroDataModelImpl listaCtaDebito1;
	private RegistroDataModelImpl listaCtaDebito1E;
	private RegistroDataModelImpl listaCtaCredito1;
	private RegistroDataModelImpl listaCtaCredito1E;

    private boolean visibleUggpp;
    private boolean duplicarDescuentos;

    /**
     * Indicador que controla los componentes que deben mostrar al
     * abrir el formulario desde la opcion de menu: 60506.
     */
    private boolean verNominaSiif;
    
    private boolean verPes14 = true;
    private boolean verPes265 = true;
    private boolean verPes12 = true;
    private boolean verPes15 = true;
    
    private boolean verPes11c = true;

    private String mesNomina;
    private String periodoNomina;
    private String procesoNomina;

	/**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
  private String auxiliar;

    /**
     * Indicador que controla bloqueo de combo Clase
     */
    private boolean bloquearComponente;

    /**
     * Lista que contiene los detalles del combo Tipo Doc. SIIF
     * (CB5722).
     */
    private RegistroDataModelImpl listaTipoDocSiif;

    /**
     * Lista que almacena los cuentas debito administracion
     */
    private RegistroDataModelImpl listaCtaDbtAdministracion;
    /**
     * Lista que almacena los cuentas credito administracion
     */
    private RegistroDataModelImpl listaCtaCrdAdministracion;
    /**
     * Lista que almacena los cuentas credito supernum
     */
    private RegistroDataModelImpl listaCtaCrdSupernum;
    /**
     * Lista que almacena los cuentas debito supernum
     */
    private RegistroDataModelImpl listaCtaDbtSupernum;
    /**
     * Lista que almacena los cuentas credito otros
     */
    private RegistroDataModelImpl listaCtaCrdOtro;
    /**
     * Lista que almacena los cuentas debito otros
     */
    private RegistroDataModelImpl listaCtaDbtOtro;
    /**
     * Lista que almacena los cuentas credito produccion
     */
    private RegistroDataModelImpl listaCtaCrddeProduccion;
    /**
     * Lista que almacena los cuentas debito produccion
     */
    private RegistroDataModelImpl listaCtaDbtProduccion;
    /**
     * Lista que almacena los cuentas credito ventas
     */
    private RegistroDataModelImpl listaCtaCrdVentas;
    /**
     * Lista que almacena los cuentas debito ventas
     */
    private RegistroDataModelImpl listaCtaDbtVentas;

    /**
     * Lista que almacena los rubros credito presupuestales
     */
    private RegistroDataModelImpl listaCtaCrePptal;

    /**
     * Lista de registros de la tabla CGR_CONCEPTO_PAGO
     */
    private RegistroDataModelImpl listaConceptoCgr;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    @EJB
    private EjbNominaSiete ejbNominaSiete;
    
    @EJB
	private EjbPrepararAnoRemote ejbPrepararAno;
    
    private boolean verCuentas;
	private Object nombreCC;
	private Object nombreAux;

    public ConceptosbsControlador()
    {
        super();

        compania = SessionUtil.getCompania();
        anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
        anio = anoNomina;

        mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
        periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");

        modulo = SessionUtil.getModulo();
        
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOSBS_CONTROLADOR
                            .getCodigo();

            registroSub = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ConceptosbsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CONCEPTOS;

        buscarLlave();
        asignarOrigenDatos();
        
               
        if (m60506.equals(menuActual))
        {
            verNominaSiif = true;
        }
        
        //Llamado del formulario desde el modulo de CONTABILIZAR
        if ("960103".equals(menuActual)) {
        	setVerPes14(false);
            setVerPes265(false);
            setVerPes12(false);
            setVerPes15(false);
            setVerPes11c(false);
             
             //[INSERTAR,BORRAR,MODIFICAR,CONSULTAR,EXPORTAR]/
     		permisos[0] = false;
     		permisos[1] = false; 
     		permisos[2] = true; 
     		permisos[4] = true;
     		
     		modulo = "6";
     		
     		if (anoNomina == null) {
     			Calendar fecha = new GregorianCalendar();
     			int anio = fecha.get(Calendar.YEAR);
     			anoNomina = String.valueOf(anio);  			
     		}
     		
        }
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    @Override
    public void abrirFormulario() {
        // </CODIGO_DESARROLLADO>
        validarBotoresUgpp();        

        try
        {
            valParametro = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE INTERFAZ NOMINA GRUPO ADMON", modulo,
                            new Date(), false);

            if (valParametro == null)
            {
                grupoAdmon = idioma.getString("TB_TB3007");
                grupoAdmonC = idioma.getString("TB_TB3008");
            }
            else
            {
                grupoAdmon = SysmanFunciones.concatenar(cCtaDebito,
                                valParametro.toLowerCase(), ":");

                grupoAdmonC = SysmanFunciones.concatenar(cCtaCredito,
                                valParametro.toLowerCase(), ":");
            }
            valParametro = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE INTERFAZ NOMINA GRUPO VENTAS", modulo,
                            new Date(), false);

            if (valParametro == null)
            {
                grupoVentasD = idioma.getString("TB_TB3009");
                grupoVentasC = idioma.getString("TB_TB3010");
            }
            else
            {
                grupoVentasD = SysmanFunciones.concatenar(cCtaDebito,
                                valParametro.toLowerCase(), ":");

                grupoVentasC = SysmanFunciones.concatenar(cCtaCredito,
                                valParametro.toLowerCase(), ":");
            }

            valParametro = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE INTERFAZ NOMINA GRUPO PRODUCCION", modulo,
                            new Date(), false);

            if (valParametro == null)
            {
                grupoProdD = idioma.getString("TB_TB3011");
                grupoProdC = idioma.getString("TB_TB3012");
            }
            else
            {
                grupoProdD = SysmanFunciones.concatenar(cCtaDebito,
                                valParametro.toLowerCase(), ":");

                grupoProdC = SysmanFunciones.concatenar(cCtaCredito,
                                valParametro.toLowerCase(), ":");
            }

            valParametro = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE INTERFAZ NOMINA GRUPO OTROS", modulo,
                            new Date(), false);

            if (valParametro == null)
            {
                grupoOtroD = idioma.getString("TB_TB3013");
                grupoOtroC = idioma.getString("TB_TB3014");
            }
            else
            {
                grupoOtroD = SysmanFunciones.concatenar(cCtaDebito,
                                valParametro.toLowerCase(), ":");

                grupoOtroC = SysmanFunciones.concatenar(cCtaCredito,
                                valParametro.toLowerCase(), ":");
            }

            valParametro = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE INTERFAZ NOMINA GRUPO SUPERNUM", modulo,
                            new Date(), false);

            if (valParametro == null)
            {
                grupoSuperD = idioma.getString("TB_TB3013");
                grupoSuperC = idioma.getString("TB_TB3014");
            }
            else
            {
                grupoSuperD = SysmanFunciones.concatenar(cCtaDebito,
                                valParametro.toLowerCase(), ":");

                grupoSuperC = SysmanFunciones.concatenar(cCtaCredito,
                                valParametro.toLowerCase(), ":");
            }
            
            duplicarDescuentos = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
    				"ACTIVAR PERMITIR DUPLICAR DESCUENTOS",
    				modulo, new Date(), false)) ? true : false;
            
			verCuentas = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
					"PERMITIR VER CUENTAS SECUNDARIAS EN CONCEPTOS DE NOMINA",
					modulo, new Date(), false));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        nombreRubroCredito = "";
        try
        {
            if (!accion.equals(ACCION_INSERTAR))
            {

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get("CTA_CRE_PPTAL"));

                nombreRubroCredito = SysmanFunciones
                                .nvl(listaCtaCrePptal.getRegistroUnico(param)
                                                .getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName()),
                                                "")
                                .toString();

            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListas() {
        cargarListaCRetro();
        cargarListaTercero();
        cargarListaRelacionado();
        cargarListaCBanco();
        cargarListaTipoConceptoAGR();
        cargarListaTipoDeFondo();
        cargarListaTipoPagoSia();

        cargarListaIdCentrodeCosto();
        cargarListaIdCentrodeCostoE();
        cargarListaTipoDocSiif();

        cargarListaCtaDbtAdministracion();
        cargarListaCtaCrdAdministracion();
        cargarListaCtaCrdSupernum();
        cargarListaCtaDbtSupernum();
        cargarListaCtaCrdOtro();
        cargarListaCtaDbtOtro();
        cargarListaCtaCrddeProduccion();
        cargarListaCtaDbtProduccion();
        cargarListaCtaCrdVentas();
        cargarListaCtaDbtVentas();
        cargarListaCtaCrePptal();
        cargarListaConceptoCgr();
        cargarListaAuxiliar(); 
		cargarListaAuxiliarE();
		cargarListaCtaDebito(); 
		cargarListaCtaDebitoE();
		cargarListaCtaCredito(); 
		cargarListaCtaCreditoE();
		cargarListaCtaDebito1(); 
		cargarListaCtaDebito1E();
		cargarListaCtaCredito1(); 
		cargarListaCtaCredito1E();
		
		cargarListaAnioActual();
        cargarListaAnioPreparar();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubconceptocentrocosto();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubconceptocentrocosto = null;
    }

    public void cargarListaCRetro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL8686
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCRetro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdConcepto);
    }

    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL9410
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     *
     * Carga la lista listaRelacionado
     *
     */
    public void cargarListaRelacionado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL10156
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaRelacionado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdConcepto);
    }

    public void cargarListaCBanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try
        {
            listaCBanco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosbsControladorUrlEnum.URL10015
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoConceptoAGR() {
        try
        {
            listaTipoConceptoAGR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosbsControladorUrlEnum.URL10548
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoDeFondo() {
        try
        {
            listaTipoDeFondo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosbsControladorUrlEnum.URL11086
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoPagoSia() {
        try
        {
            listaTipoPagoSia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosbsControladorUrlEnum.URL1755
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaIdCentrodeCosto() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anoNomina);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConceptosbsControladorUrlEnum.URL20001
						.getValue());


		listaIdCentrodeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaIdCentrodeCosto
	 *
	 */
	public void  cargarListaIdCentrodeCostoE(){
		listaIdCentrodeCostoE = listaIdCentrodeCosto;
	}

	public void cargarListaSubconceptocentrocosto() {
		try
		{

			Map<String, Object> param = new TreeMap<>();
			param.put(cCompania, compania);
			param.put(GeneralParameterEnum.ANIO.getName(),
                    anoNomina);
			param.put(GeneralParameterEnum.CONCEPTO.getName(),
					registro.getCampos().get(cIdConcepto));

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CONCEPTO_CENTROCOSTO.getGridKey());
			
			String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
					GenericUrlEnum.CONCEPTO_CENTROCOSTO.getTable());

			listaSubconceptocentrocosto = new RegistroDataModelImpl(
					urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(),
					param, rowKey);

		}
		catch (SysmanException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

    /**
     * Carga la lista: listaTipoDocSiif asociada al combo Tipo Doc.
     * SIIF (CB5722).
     */
    public void cargarListaTipoDocSiif() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaTipoDocSiif = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "DCTO_IDENTIDAD");
    }

    /**
     *
     * Carga la lista listaCtaDbtAdministracion
     *
     */
    public void cargarListaCtaDbtAdministracion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL6060
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anoNomina);

        listaCtaDbtAdministracion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaCtaCrdAdministracion
     *
     */
    public void cargarListaCtaCrdAdministracion() {
        listaCtaCrdAdministracion = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaCrdSupernum
     *
     */
    public void cargarListaCtaCrdSupernum() {
        listaCtaCrdSupernum = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaDbtSupernum
     *
     */
    public void cargarListaCtaDbtSupernum() {
        listaCtaDbtSupernum = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaCrdOtro
     *
     */
    public void cargarListaCtaCrdOtro() {
        listaCtaCrdOtro = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaDbtOtro
     *
     */
    public void cargarListaCtaDbtOtro() {
        listaCtaDbtOtro = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaCrddeProduccion
     *
     */
    public void cargarListaCtaCrddeProduccion() {
        listaCtaCrddeProduccion = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaDbtProduccion
     *
     */
    public void cargarListaCtaDbtProduccion() {
        listaCtaDbtProduccion = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaCrdVentas
     *
     */
    public void cargarListaCtaCrdVentas() {
        listaCtaCrdVentas = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaDbtVentas
     *
     */
    public void cargarListaCtaDbtVentas() {
        listaCtaDbtVentas = listaCtaDbtAdministracion;
    }

    /**
     *
     * Carga la lista listaCtaCrePptal
     *
     */
    public void cargarListaCtaCrePptal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL4545
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anoNomina);

        listaCtaCrePptal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaConceptoCgr
     *
     */
    public void cargarListaConceptoCgr() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosbsControladorUrlEnum.URL875
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(ConceptosbsControladorEnum.TIPO_ENTIDAD.getValue(),
                        SessionUtil.getCompaniaIngreso().getTipoEntidad());

        listaConceptoCgr = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }
    /**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 */
	public void cargarListaAuxiliar(){
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(GeneralParameterEnum.ANIO.getName(),
				anoNomina);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConceptosbsControladorUrlEnum.URL23006
						.getValue());


		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 */
	public void  cargarListaAuxiliarE(){
		listaAuxiliarE = listaAuxiliar;
	}
	/**
	 * 
	 * Carga la lista listaCtaDebito
	 *
	 */
	public void cargarListaCtaDebito(){
		listaCtaDebito = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaDebito
	 *
	 */
	public void  cargarListaCtaDebitoE(){
		listaCtaDebitoE = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaCredito
	 *
	 */
	public void cargarListaCtaCredito(){
		listaCtaCredito = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaCredito
	 *
	 */
	public void  cargarListaCtaCreditoE(){
		listaCtaCreditoE = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaDebito1
	 *
	 */
	public void cargarListaCtaDebito1(){
		listaCtaDebito1 = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaDebito1
	 *
	 */
	public void  cargarListaCtaDebito1E(){
		listaCtaDebito1E = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaCredito1
	 *
	 */
	public void cargarListaCtaCredito1(){
		listaCtaCredito1 = listaCtaDbtAdministracion;
	}
	/**
	 * 
	 * Carga la lista listaCtaCredito1
	 *
	 */
	public void  cargarListaCtaCredito1E(){
		listaCtaCredito1E = listaCtaDbtAdministracion;
	}    
	
	public void cargarListaAnioActual() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnioActual = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConceptosbsControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	public void cargarListaAnioPreparar() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaAnioPreparar = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConceptosbsControladorUrlEnum.URL4016
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

    public void agregarRegistroSubSubconceptocentrocosto() {
        try
        {
            registroSub.getCampos().put(cCompania, compania);
            registroSub.getCampos().put(cIdConcepto,
                            registro.getCampos().get(cIdConcepto));
            registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            anoNomina);
            registroSub.getCampos().remove("NOMCENTROCOSTO");
            registroSub.getCampos().remove("NOMBRE_AUX");
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONCEPTO_CENTROCOSTO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubconceptocentrocosto();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(ConceptosbsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubconceptocentrocosto(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try
        {
            reg.getCampos().remove(cCompania);
            reg.getCampos().remove("NOMCENTROCOSTO");
            reg.getCampos().remove("NOMBRE_AUX");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONCEPTO_CENTROCOSTO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(ConceptosbsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSubconceptocentrocosto();
        }
    }

    public void eliminarRegSubSubconceptocentrocosto(Registro reg) {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONCEPTO_CENTROCOSTO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubconceptocentrocosto();
            cargarListaSubconceptocentrocosto();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(ConceptosbsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubconceptocentrocosto() {
        cargarListaSubconceptocentrocosto();

    }

    public void oprimircmdConceptoAgr() {

        Map<String, Object> param = new TreeMap<>();

        param.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.CONCEPTOSAGRS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirREVISAINTERFACE() {
        // <CODIGO_DESARROLLADO>

        generarReporte(FORMATOS.EXCEL97, "800101CONCEPTOSBS");
    }

    /**
     * Genera un reporte con un determinado formato.
     *
     * @param formato
     * Tipo de documento a generar.
     * @author pespitia
     */
    private void generarReporte(FORMATOS formato, String reporte) {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", anoNomina);

            String consulta = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, formato, reporte);

        }
        catch (SQLException | OutOfMemoryError | JRException
                        | IOException | DRException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirListaFactores() {
        generarInforme(ReportesBean.FORMATOS.PDF,
                        "000002ListadoFactoresPrestaciones");
    }

    public void generarInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazos = new HashMap<>();

        reemplazos.put("compania", compania);
        String nombreEmpresa;
        try
        {
            nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazos, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | JRException | IOException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton AdicionarConceptos en la
     * vista
     *
     */
    public void oprimirAdicionarConceptos() {
        // <CODIGO_DESARROLLADO>
        try
        {
            ejbNominaSiete.actualiarConcepUgpp(compania,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton RevisarConceptos en la
     * vista
     *
     *
     */
    public void oprimirRevisarConceptos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97, "900009conceptoss_cn_ugpp_ano");
        // </CODIGO_DESARROLLADO>
    }
    
    /**
	 * 
	 * Metodo ejecutado al oprimir el boton Preparar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		if ("".equals(anio) || (anio == null)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2680"));
			return;
		}
		if ("".equals(anioPreparar) || (anioPreparar == null)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2652"));
			return;
		}
		try {

			ejbPrepararAno.copiarConceptoCentroCosto(compania,
					Integer.parseInt(anioPreparar),
					Integer.parseInt(anio), 
					Integer.parseInt(registro.getCampos().get(cIdConcepto).toString()),
					compania);
			JsfUtil.agregarMensajeInformativo(
                    idioma.getString("MSM_PROCESO_EJECUTADO"));
			anio = null;
			anioPreparar = null;

		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

    /**
     *
     * Metodo ejecutado al oprimir el boton ActualizarConceptos en la
     * vista
     *
     *
     */
    public void oprimirActualizarConceptos() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton RevisarConcSinConf en la
     * vista
     *
     *
     */
    public void oprimirRevisarConcSinConf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97, "900010revision_conceptos_nulos_ugpp");

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton revisarCuentasContables en
     * la vista
     *
     *
     */
    public void oprimirrevisarCuentasContables() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try
        {
            ejbNominaSiete.generarErroresCuentasContables(compania,
                            Integer.parseInt(anoNomina),
                            Integer.parseInt(mesNomina), periodoNomina,
                            procesoNomina, SessionUtil.getUser().getCodigo());
            generarInforme(FORMATOS.PDF, "000070ReporteLiquidacion");
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConceptoSinConfig
	 * en la vista
	 *
	 *
	 */
	public void oprimirConceptoSinConfig() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme("800663ConceptosSinConfigurar");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton RevisarDif
	 * en la vista
	 *
	 *
	 */
	public void oprimirRevisarDif() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generarInforme("800664RevisarDiferenciasConfig");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton RevisarCn
	 * en la vista
	 *
	 *
	 */
	public void oprimirRevisarCn() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;      
		generarInforme("800665RevisarConceptosCCyAux");
		//</CODIGO_DESARROLLADO>
	}

    public void cambiarCRetro() {
        // heredadode del bean base
    }

    public void cambiarDevRetencion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTercero() {
        // heredadode del bean base
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRelacionado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRelacionado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RELACIONADO",
                        registroAux.getCampos().get(cIdConcepto));
        registro.getCampos().put(cNombreConceptoRela,
                        registroAux.getCampos().get("NOMBRE_CONCEPTO"));
    }

    public void seleccionarFilaCRetro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCReto,
                        registroAux.getCampos().get(cIdConcepto));
        registro.getCampos().put(cNomConcepto,
                        registroAux.getCampos().get("NOMBRE_CONCEPTO"));

        if (!"3".equals(registro.getCampos().get("CLASE").toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2537"));
            registro.getCampos().put(cCReto, null);
            registro.getCampos().put(cNomConcepto, null);
        }

    }

    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));
        registro.getCampos().put(cTerceroInterfaz,
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * listaTipoDocSiif, asociada al combo Tipo Doc. SIIF (CB5722).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDocSiif(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("DOC_SIIF_CONCEPTO",
                        registroAux.getCampos().get("DCTO_IDENTIDAD"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaDbtAdministracion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaDbtAdministracion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_DBT_ADMINISTRACION",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCrdAdministracion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCrdAdministracion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_CRD_ADMINISTRACION",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCrdSupernum
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCrdSupernum(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_CRD_SUPERNUM",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaDbtSupernum
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaDbtSupernum(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_DBT_SUPERNUM",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCrdOtro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCrdOtro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_CRD_OTRO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaDbtOtro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaDbtOtro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_DBT_OTRO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCrddeProduccion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCrddeProduccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_CRD_PRODUCCION",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaDbtProduccion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaDbtProduccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_DBT_PRODUCCION",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCrdVentas
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCrdVentas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_CRD_VENTAS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaDbtVentas
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaDbtVentas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_DBT_VENTAS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCrePptal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCrePptal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTA_CRE_PPTAL",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        nombreRubroCredito = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoCgr
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoCgr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CGR_CONCEPTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaIdCentrodeCosto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaIdCentrodeCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("ID_CENTRO_DE_COSTO", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("NOMCENTROCOSTO", registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaIdCentrodeCosto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaIdCentrodeCostoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nombreCC = registroAux.getCampos().get("NOMBRE");
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliar
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CODAUXILIAR", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("NOMBRE_AUX", registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliar
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nombreAux = registroAux.getCampos().get("NOMBRE");
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaDebito
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaDebito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CTA_DEBITO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaDebito
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaDebitoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaCredito
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaCredito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CTA_CREDITO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaCredito
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaCreditoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaDebito1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaDebito1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CTA_DEBITO1", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaDebito1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaDebito1E(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaCredito1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaCredito1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CTA_CREDITO1", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCtaCredito1
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCtaCredito1E(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnioActual() {
		// <CODIGO_DESARROLLADO>
		anioPreparar = null;
		cargarListaAnioPreparar();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnioPreparar() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control IdCentrodeCosto en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarIdCentrodeCostoC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaSubconceptocentrocosto.getDatasource().get(rowNum % 10).getCampos().put("NOMCENTROCOSTO", nombreCC);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Auxiliar en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarAuxiliarC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaSubconceptocentrocosto.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_AUX", nombreAux);
		//</CODIGO_DESARROLLADO>
	}

    public void cambiarClase() {
        if (!"3".equals(SysmanFunciones
                        .nvl(registro.getCampos().get("CLASE"), "0")
                        .toString()))
        {

            registro.getCampos().put(cCReto, null);
            registro.getCampos().put(cNomConcepto, null);
        }
        
        if ( !"5".equals(SysmanFunciones
                .nvl(registro.getCampos().get("CLASE"), "0")
                .toString())) 
        {
        	registro.getCampos().put("PERMITE_DUPLICAR","0");
        }

    }
    
    public void cambiarDuplicar() {
    	if ( !"5".equals(SysmanFunciones
                .nvl(registro.getCampos().get("CLASE"), "0")
                .toString())) 
        {
    		 if (!"0".equals(registro.getCampos().get("PERMITE_DUPLICAR").toString()))
    	     {
    			 registro.getCampos().put("PERMITE_DUPLICAR","0");
    	     }
        	
        }
    }

    public void cambiarReporteEps() {
        if ("174".equals(registro.getCampos().get(cIdConcepto).toString()))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2538"));
            registro.getCampos().put("REPORTEEPS", false);
        }

    }

    private boolean cambiarRetencionAux() {
        if (!"124".equals(registro.getCampos().get(cIdConcepto).toString())
            && !"127".equals(
                            registro.getCampos().get(cIdConcepto).toString())
            && !"122".equals(registro.getCampos().get(cIdConcepto)
                            .toString()))
        {
            return true;
        }
        return false;
    }

    public void cambiarDedRetencion() {
        if (!"131".equals(registro.getCampos().get(cIdConcepto).toString())
            && !"132".equals(
                            registro.getCampos().get(cIdConcepto).toString())
            && !"120".equals(
                            registro.getCampos().get(cIdConcepto).toString())
            && cambiarRetencionAux())
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2539"));
            registro.getCampos().put("DEDRETENCION", false);
        }
    }

    public void cambiarFactorRenta() {
        String conceptoAct = registro.getCampos().get(cIdConcepto).toString();
        String[] conceptos = "370,371,372,373,374,375,376,377,378,379"
                        .split(",");
        boolean esConceptoInc = false;
        for (int i = 0; i < conceptos.length; i++)
        {
            if (conceptoAct.equals(conceptos[i]))
            {
                esConceptoInc = true;
                break;
            }

        }

        if (esConceptoInc)
        {
            HashMap<String, Object> rsConsulta = new HashMap();
            Map<String, Object> parametrosConsulta = new HashMap<>();
            parametrosConsulta.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            try
            {
                UrlBean urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ConceptosbsControladorUrlEnum.URL15135
                                                                .getValue());
                rsConsulta = (HashMap<String, Object>) requestManager
                                .get(urlReg.getUrl(), parametrosConsulta)
                                .getFields();
            }
            catch (SystemException e)
            {
                Logger.getLogger(EstampillasControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
            if (!rsConsulta.isEmpty())
            {

                if ((!"379".equals(conceptoAct)
                    && (Integer.parseInt(rsConsulta.get(
                                    ConceptosbsControladorEnum.INDRENTACN379
                                                    .getValue())
                                    .toString()) > 0))
                    || ("379".equals(conceptoAct)
                        && (Integer.parseInt(rsConsulta.get(
                                        ConceptosbsControladorEnum.INDRETATOTALINC
                                                        .getValue())
                                        .toString()) > 0)))

                {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB4277"));
                    registro.getCampos().put("IND_RENTA_EXENTA", false);
                }

            }
        }
    }

    public void cambiarFactorSueldoEmb() {
        if ((Boolean) registro.getCampos().get(cFactorSueldosEmb))
        {
            registro.getCampos().put(cFactorSalarialEmb, false);
            registro.getCampos().put(cFactorPrestacionalEmb, false);
        }
        else
        {
            registro.getCampos().put(cFactorSalarialEmb, true);
            registro.getCampos().put(cFactorPrestacionalEmb, false);
        }
    }

    public void cambiarckOtrosPagosLab() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfactorSalarialEmb() {
        if ((Boolean) registro.getCampos().get(cFactorSalarialEmb))
        {
            registro.getCampos().put(cFactorSueldosEmb, false);
            registro.getCampos().put(cFactorPrestacionalEmb, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldosEmb, false);
            registro.getCampos().put(cFactorPrestacionalEmb, true);
        }
    }

    public void cambiarfactorPrestacionalEmb() {
        if ((Boolean) registro.getCampos().get(cFactorPrestacionalEmb))
        {
            registro.getCampos().put(cFactorSueldosEmb, false);
            registro.getCampos().put(cFactorSalarialEmb, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldosEmb, true);
            registro.getCampos().put(cFactorSalarialEmb, false);
        }
    }

    public void cambiarfactorSueldo() {
        if ((Boolean) registro.getCampos().get(cFactorSueldos))
        {
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorOtros, false);
        }
        else
        {
            registro.getCampos().put(cFactorSalarial, true);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorOtros, false);
        }

    }

    public void cambiarfactorSalarial() {
        if ((Boolean) registro.getCampos().get(cFactorSalarial))
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorOtros, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorLegales, true);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorOtros, false);
        }
    }

    public void cambiarfactorLegales() {
        if ((Boolean) registro.getCampos().get(cFactorLegales))
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorOtros, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorExtralegales, true);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorOtros, false);
        }
    }

    public void cambiarFactorExtraLegales() {
        if ((Boolean) registro.getCampos().get("FACTOR_EXTRALEGALES"))
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorPrestacional, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorOtros, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorPrestacional, true);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorOtros, false);
        }
    }

    public void cambiarFactorPrestacional() {
        if ((Boolean) registro.getCampos().get("FACTOR_PRESTACIONAL"))
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorOtros, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorOtros, true);
        }
    }

    public void cambiarFactorOtrosPagos() {
        if ((Boolean) registro.getCampos().get(cFactorOtros))
        {
            registro.getCampos().put(cFactorSueldos, false);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
        }
        else
        {
            registro.getCampos().put(cFactorSueldos, true);
            registro.getCampos().put(cFactorSalarial, false);
            registro.getCampos().put(cFactorExtralegales, false);
            registro.getCampos().put(cFactorLegales, false);
            registro.getCampos().put(cFactorPrestacional, false);
        }
    }

    public void cambiarFactorParafiscal() {
       /* FacesContext context = FacesContext.getCurrentInstance();
        if ("382".equals(registro.getCampos().get(cIdConcepto).toString())
            || "383".equals(registro.getCampos().get(cIdConcepto).toString())
            || "384".equals(registro.getCampos().get(cIdConcepto)
                            .toString()))
        {
            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                            idioma.getString("TB_TB2540"),
                                            idioma.getString("TB_TB2541")));
            registro.getCampos().put("FACTOR_PARAFISCAL", true);
        }*/
    }

    public void cambiarFactorReteFuente() {
        FacesContext context = FacesContext.getCurrentInstance();
        if ("158".equals(registro.getCampos().get(cIdConcepto).toString()))
        {
            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                            idioma.getString("TB_TB2540"),
                                            idioma.getString("TB_TB2542")));
            registro.getCampos().put(cFactorRetefuente, false);
        }
        if ("826".equals(registro.getCampos().get(cIdConcepto).toString()))
        {
            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                            idioma.getString("TB_TB2540"),
                                            idioma.getString("TB_TB4410")));
            registro.getCampos().put(cFactorRetefuente, false);
        }
        if ((Boolean) registro.getCampos().get(cFactorRetefuente))
        {
            registro.getCampos().put("DEVRETENCION", true);
        }
        else
        {
            registro.getCampos().put("DEVRETENCION", false);
        }
        try {
			String entidad = ejbSysmanUtil.consultarParametro(compania, "ENTIDAD PUBLICA O PRIVADA",
					modulo, new Date(), false);
	        if ("160".equals(registro.getCampos().get(cIdConcepto).toString()) && "PRIVADA".equals(entidad)) 
	        {
	            context.addMessage(null,
	                            new FacesMessage(FacesMessage.SEVERITY_INFO,
	                                            idioma.getString("TB_TB2540"),
	                                            idioma.getString("TB_TB4365")));
	            registro.getCampos().put(cFactorRetefuente, false);
	        }
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    /**
     * Metodo ejecutado al cambiar el control IdDeConcepto
     * 
     * TAR1000094833 Al crear un concepto comprendido entre el rango
     * 1600 al 1699 el sistema lo tome como clase descuento y no
     * permita configurar ninguna otra clase de concepto
     * 
     */
    public void cambiarIdDeConcepto() {
        int concepto = Integer.parseInt(
                        registro.getCampos().get(cIdConcepto).toString());
        if (concepto >= 1600 && concepto <= 1698)
        {
            bloquearComponente = true; // Bloquea campo clase
            registro.getCampos().put("CLASE", "5");
        }
        else
        {
            if (bloquearComponente)
            {
                bloquearComponente = false;
                registro.getCampos().put("CLASE", null);
            }
        }
    }
    
    public void generarInforme(String informe) {
		try {

			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			HashMap<String, Object> reemplazos = new HashMap<>();
			reemplazos.put("anio", anoNomina);


			// MANEJO DE PARAMETROS DEL REPORTE
			Map<String, Object> parametros = new HashMap<>();

			String sql= Reporteador.resuelveConsulta(informe,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, informe);

		}
		catch (JRException | IOException | NumberFormatException  | DRException | SQLException  | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	
	}

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove(cNomConcepto);
        registro.getCampos().remove(cTerceroInterfaz);
        registro.getCampos().remove(cNombreConceptoRela);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(cNomConcepto);
        registro.getCampos().remove(cTerceroInterfaz);
        registro.getCampos().remove(cNombreConceptoRela);

        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos().remove(cCompania);
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
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

    public RegistroDataModelImpl getListaRelacionado() {
        return listaRelacionado;
    }

    public void setListaRelacionado(RegistroDataModelImpl listaRelacionado) {
        this.listaRelacionado = listaRelacionado;
    }

    public List<Registro> getListaCBanco() {
        return listaCBanco;
    }

    public void setListaCBanco(List<Registro> listaCBanco) {
        this.listaCBanco = listaCBanco;
    }

    public List<Registro> getListaTipoConceptoAGR() {
        return listaTipoConceptoAGR;
    }

    public void setListaTipoConceptoAGR(List<Registro> listaTipoConceptoAGR) {
        this.listaTipoConceptoAGR = listaTipoConceptoAGR;
    }

    public List<Registro> getListaTipoDeFondo() {
        return listaTipoDeFondo;
    }

    public void setListaTipoDeFondo(List<Registro> listaTipoDeFondo) {
        this.listaTipoDeFondo = listaTipoDeFondo;
    }

    public List<Registro> getListaTipoPagoSia() {
        return listaTipoPagoSia;
    }

    public void setTipoPagoSia(List<Registro> listaTipoPagoSia) {
        this.listaTipoPagoSia = listaTipoPagoSia;
    }

    public RegistroDataModelImpl getListaIdCentrodeCosto() {
		return listaIdCentrodeCosto;
	}

	public void setListaIdCentrodeCosto(RegistroDataModelImpl listaIdCentrodeCosto) {
		this.listaIdCentrodeCosto = listaIdCentrodeCosto;
	}

	public RegistroDataModelImpl getListaSubconceptocentrocosto() {
		return listaSubconceptocentrocosto;
	}

	public void setListaSubconceptocentrocosto(
			RegistroDataModelImpl listaSubconceptocentrocosto) {
		this.listaSubconceptocentrocosto = listaSubconceptocentrocosto;
	}

    public RegistroDataModelImpl getListaCRetro() {
        return listaCRetro;
    }

    public void setListaCRetro(RegistroDataModelImpl listaCRetro) {
        this.listaCRetro = listaCRetro;
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getNomConceptoRetro() {
        return nomConceptoRetro;
    }

    public void setNomConceptoRetro(String nomConceptoRetro) {
        this.nomConceptoRetro = nomConceptoRetro;
    }

    public String getGrupoAdmon() {
        return grupoAdmon;
    }

    public void setGrupoAdmon(String grupoAdmon) {
        this.grupoAdmon = grupoAdmon;
    }

    public String getGrupoAdmonC() {
        return grupoAdmonC;
    }

    public void setGrupoAdmonC(String grupoAdmonC) {
        this.grupoAdmonC = grupoAdmonC;
    }

    public String getValParametro() {
        return valParametro;
    }

    public void setValParametro(String valParametro) {
        this.valParametro = valParametro;
    }

    public String getGrupoVentasD() {
        return grupoVentasD;
    }

    public void setGrupoVentasD(String grupoVentasD) {
        this.grupoVentasD = grupoVentasD;
    }

    public String getGrupoVentasC() {
        return grupoVentasC;
    }

    public void setGrupoVentasC(String grupoVentasC) {
        this.grupoVentasC = grupoVentasC;
    }

    public String getGrupoProdD() {
        return grupoProdD;
    }

    public void setGrupoProdD(String grupoProdD) {
        this.grupoProdD = grupoProdD;
    }

    public String getGrupoProdC() {
        return grupoProdC;
    }

    public void setGrupoProdC(String grupoProdC) {
        this.grupoProdC = grupoProdC;
    }

    public String getGrupoOtroD() {
        return grupoOtroD;
    }

    public void setGrupoOtroD(String grupoOtroD) {
        this.grupoOtroD = grupoOtroD;
    }

    public String getGrupoOtroC() {
        return grupoOtroC;
    }

    public void setGrupoOtroC(String grupoOtroC) {
        this.grupoOtroC = grupoOtroC;
    }

    public String getGrupoSuperD() {
        return grupoSuperD;
    }

    public void setGrupoSuperD(String grupoSuperD) {
        this.grupoSuperD = grupoSuperD;
    }

    public String getGrupoSuperC() {
        return grupoSuperC;
    }

    public void setGrupoSuperC(String grupoSuperC) {
        this.grupoSuperC = grupoSuperC;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getAnoNomina() {
        return anoNomina;
    }

    public boolean isVisibleUggpp() {
        return visibleUggpp;
    }

    public void setVisibleUggpp(boolean visibleUggpp) {
        this.visibleUggpp = visibleUggpp;
    }
    
    public boolean getDuplicarDescuentos() {
    	return duplicarDescuentos;
    }
    
    public void setDuplicarDescuentos (boolean dd) {
    	this.duplicarDescuentos = dd;
    }

    private void validarBotoresUgpp() {
        try
        {
            boolean parametro = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "NOMBRE INTERFAZ NOMINA GRUPO ADMON",
                                            modulo,
                                            new Date(), false), "NO"));

            if (parametro)
            {
                visibleUggpp = true;
            }
            else
            {
                visibleUggpp = false;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario.
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        /*- Redireccionar al formulario asociado a la opcion de menu 60506*/
        if (m60506.equals(menuActual))
        {
            SessionUtil.redireccionarMenuFormulario(m60506);
        }
        else
        {
            SessionUtil.redireccionarMenu();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Retorna la variable nombreRubroCredito
     *
     * @return nombreRubroCredito
     */
    public String getNombreRubroCredito() {
        return nombreRubroCredito;
    }

    /**
     * Asigna la variable nombreRubroCredito
     *
     * @param nombreRubroCredito
     * Variable a asignar en nombreRubroCredito
     */
    public void setNombreRubroCredito(String nombreRubroCredito) {
        this.nombreRubroCredito = nombreRubroCredito;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario.
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * @return the verNominaSiif
     */
    public boolean isVerNominaSiif() {
        return verNominaSiif;
    }

    /**
     * @param verNominaSiif
     * the verNominaSiif to set
     */
    public void setVerNominaSiif(boolean verNominaSiif) {
        this.verNominaSiif = verNominaSiif;
    }

    public boolean isBloquearComponente() {
        return bloquearComponente;
    }

    public void setBloquearComponente(boolean bloquearComponente) {
        this.bloquearComponente = bloquearComponente;
    }

    /**
     * @return the listaTipoDocSiif
     */
    public RegistroDataModelImpl getListaTipoDocSiif() {
        return listaTipoDocSiif;
    }

    /**
     * @param listaTipoDocSiif
     * the listaTipoDocSiif to set
     */
    public void setListaTipoDocSiif(RegistroDataModelImpl listaTipoDocSiif) {
        this.listaTipoDocSiif = listaTipoDocSiif;
    }

    /**
     * Retorna la lista listaCtaDbtAdministracion
     *
     * @return listaCtaDbtAdministracion
     */
    public RegistroDataModelImpl getListaCtaDbtAdministracion() {
        return listaCtaDbtAdministracion;
    }

    /**
     * Asigna la lista listaCtaDbtAdministracion
     *
     * @param listaCtaDbtAdministracion
     * Variable a asignar en listaCtaDbtAdministracion
     */
    public void setListaCtaDbtAdministracion(
        RegistroDataModelImpl listaCtaDbtAdministracion) {
        this.listaCtaDbtAdministracion = listaCtaDbtAdministracion;
    }

    /**
     * Retorna la lista listaCtaCrdAdministracion
     *
     * @return listaCtaCrdAdministracion
     */
    public RegistroDataModelImpl getListaCtaCrdAdministracion() {
        return listaCtaCrdAdministracion;
    }

    /**
     * Asigna la lista listaCtaCrdAdministracion
     *
     * @param listaCtaCrdAdministracion
     * Variable a asignar en listaCtaCrdAdministracion
     */
    public void setListaCtaCrdAdministracion(
        RegistroDataModelImpl listaCtaCrdAdministracion) {
        this.listaCtaCrdAdministracion = listaCtaCrdAdministracion;
    }

    /**
     * Retorna la lista listaCtaCrdSupernum
     *
     * @return listaCtaCrdSupernum
     */
    public RegistroDataModelImpl getListaCtaCrdSupernum() {
        return listaCtaCrdSupernum;
    }

    /**
     * Asigna la lista listaCtaCrdSupernum
     *
     * @param listaCtaCrdSupernum
     * Variable a asignar en listaCtaCrdSupernum
     */
    public void setListaCtaCrdSupernum(
        RegistroDataModelImpl listaCtaCrdSupernum) {
        this.listaCtaCrdSupernum = listaCtaCrdSupernum;
    }

    /**
     * Retorna la lista listaCtaDbtSupernum
     *
     * @return listaCtaDbtSupernum
     */
    public RegistroDataModelImpl getListaCtaDbtSupernum() {
        return listaCtaDbtSupernum;
    }

    /**
     * Asigna la lista listaCtaDbtSupernum
     *
     * @param listaCtaDbtSupernum
     * Variable a asignar en listaCtaDbtSupernum
     */
    public void setListaCtaDbtSupernum(
        RegistroDataModelImpl listaCtaDbtSupernum) {
        this.listaCtaDbtSupernum = listaCtaDbtSupernum;
    }

    /**
     * Retorna la lista listaCtaCrdOtro
     *
     * @return listaCtaCrdOtro
     */
    public RegistroDataModelImpl getListaCtaCrdOtro() {
        return listaCtaCrdOtro;
    }

    /**
     * Asigna la lista listaCtaCrdOtro
     *
     * @param listaCtaCrdOtro
     * Variable a asignar en listaCtaCrdOtro
     */
    public void setListaCtaCrdOtro(RegistroDataModelImpl listaCtaCrdOtro) {
        this.listaCtaCrdOtro = listaCtaCrdOtro;
    }

    /**
     * Retorna la lista listaCtaDbtOtro
     *
     * @return listaCtaDbtOtro
     */
    public RegistroDataModelImpl getListaCtaDbtOtro() {
        return listaCtaDbtOtro;
    }

    /**
     * Asigna la lista listaCtaDbtOtro
     *
     * @param listaCtaDbtOtro
     * Variable a asignar en listaCtaDbtOtro
     */
    public void setListaCtaDbtOtro(RegistroDataModelImpl listaCtaDbtOtro) {
        this.listaCtaDbtOtro = listaCtaDbtOtro;
    }

    /**
     * Retorna la lista listaCtaCrddeProduccion
     *
     * @return listaCtaCrddeProduccion
     */
    public RegistroDataModelImpl getListaCtaCrddeProduccion() {
        return listaCtaCrddeProduccion;
    }

    /**
     * Asigna la lista listaCtaCrddeProduccion
     *
     * @param listaCtaCrddeProduccion
     * Variable a asignar en listaCtaCrddeProduccion
     */
    public void setListaCtaCrddeProduccion(
        RegistroDataModelImpl listaCtaCrddeProduccion) {
        this.listaCtaCrddeProduccion = listaCtaCrddeProduccion;
    }

    /**
     * Retorna la lista listaCtaDbtProduccion
     *
     * @return listaCtaDbtProduccion
     */
    public RegistroDataModelImpl getListaCtaDbtProduccion() {
        return listaCtaDbtProduccion;
    }

    /**
     * Asigna la lista listaCtaDbtProduccion
     *
     * @param listaCtaDbtProduccion
     * Variable a asignar en listaCtaDbtProduccion
     */
    public void setListaCtaDbtProduccion(
        RegistroDataModelImpl listaCtaDbtProduccion) {
        this.listaCtaDbtProduccion = listaCtaDbtProduccion;
    }

    /**
     * Retorna la lista listaCtaCrdVentas
     *
     * @return listaCtaCrdVentas
     */
    public RegistroDataModelImpl getListaCtaCrdVentas() {
        return listaCtaCrdVentas;
    }

    /**
     * Asigna la lista listaCtaCrdVentas
     *
     * @param listaCtaCrdVentas
     * Variable a asignar en listaCtaCrdVentas
     */
    public void setListaCtaCrdVentas(RegistroDataModelImpl listaCtaCrdVentas) {
        this.listaCtaCrdVentas = listaCtaCrdVentas;
    }

    /**
     * Retorna la lista listaCtaDbtVentas
     *
     * @return listaCtaDbtVentas
     */
    public RegistroDataModelImpl getListaCtaDbtVentas() {
        return listaCtaDbtVentas;
    }

    /**
     * Asigna la lista listaCtaDbtVentas
     *
     * @param listaCtaDbtVentas
     * Variable a asignar en listaCtaDbtVentas
     */
    public void setListaCtaDbtVentas(RegistroDataModelImpl listaCtaDbtVentas) {
        this.listaCtaDbtVentas = listaCtaDbtVentas;
    }

    /**
     * Retorna la lista listaCtaCrePptal
     *
     * @return listaCtaCrePptal
     */
    public RegistroDataModelImpl getListaCtaCrePptal() {
        return listaCtaCrePptal;
    }

    /**
     * Asigna la lista listaCtaCrePptal
     *
     * @param listaCtaCrePptal
     * Variable a asignar en listaCtaCrePptal
     */
    public void setListaCtaCrePptal(RegistroDataModelImpl listaCtaCrePptal) {
        this.listaCtaCrePptal = listaCtaCrePptal;
    }

    /**
     * Retorna la lista listaConceptoCgr
     * 
     * @return listaConceptoCgr
     */
    public RegistroDataModelImpl getListaConceptoCgr() {
        return listaConceptoCgr;
    }

    /**
     * Asigna la lista listaConceptoCgr
     * 
     * @param listaConceptoCgr
     * Variable a asignar en listaConceptoCgr
     */
    public void setListaConceptoCgr(RegistroDataModelImpl listaConceptoCgr) {
        this.listaConceptoCgr = listaConceptoCgr;
    }

	public boolean isVerPes14() {
		return verPes14;
	}

	public void setVerPes14(boolean verPes14) {
		this.verPes14 = verPes14;
	}

	public boolean isVerPes265() {
		return verPes265;
	}

	public void setVerPes265(boolean verPes265) {
		this.verPes265 = verPes265;
	}

	public boolean isVerPes12() {
		return verPes12;
	}

	public void setVerPes12(boolean verPes12) {
		this.verPes12 = verPes12;
	}

	public boolean isVerPes15() {
		return verPes15;
	}

	public void setVerPes15(boolean verPes15) {
		this.verPes15 = verPes15;
	}

	public boolean isVerPes11c() {
		return verPes11c;
	}

	public void setVerPes11c(boolean verPes11c) {
		this.verPes11c = verPes11c;
	}
	/**
	 * @return the listaIdCentrodeCostoE
	 */
	public RegistroDataModelImpl getListaIdCentrodeCostoE() {
		return listaIdCentrodeCostoE;
	}

	/**
	 * @param listaIdCentrodeCostoE the listaIdCentrodeCostoE to set
	 */
	public void setListaIdCentrodeCostoE(RegistroDataModelImpl listaIdCentrodeCostoE) {
		this.listaIdCentrodeCostoE = listaIdCentrodeCostoE;
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
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * @return the verCuentas
	 */
	public boolean isVerCuentas() {
		return verCuentas;
	}

	/**
	 * @param verCuentas the verCuentas to set
	 */
	public void setVerCuentas(boolean verCuentas) {
		this.verCuentas = verCuentas;
	}

	/**
	 * @return the listaCtaDebito
	 */
	public RegistroDataModelImpl getListaCtaDebito() {
		return listaCtaDebito;
	}

	/**
	 * @param listaCtaDebito the listaCtaDebito to set
	 */
	public void setListaCtaDebito(RegistroDataModelImpl listaCtaDebito) {
		this.listaCtaDebito = listaCtaDebito;
	}

	/**
	 * @return the listaCtaDebitoE
	 */
	public RegistroDataModelImpl getListaCtaDebitoE() {
		return listaCtaDebitoE;
	}

	/**
	 * @param listaCtaDebitoE the listaCtaDebitoE to set
	 */
	public void setListaCtaDebitoE(RegistroDataModelImpl listaCtaDebitoE) {
		this.listaCtaDebitoE = listaCtaDebitoE;
	}

	/**
	 * @return the listaCtaCredito
	 */
	public RegistroDataModelImpl getListaCtaCredito() {
		return listaCtaCredito;
	}

	/**
	 * @param listaCtaCredito the listaCtaCredito to set
	 */
	public void setListaCtaCredito(RegistroDataModelImpl listaCtaCredito) {
		this.listaCtaCredito = listaCtaCredito;
	}

	/**
	 * @return the listaCtaCreditoE
	 */
	public RegistroDataModelImpl getListaCtaCreditoE() {
		return listaCtaCreditoE;
	}

	/**
	 * @param listaCtaCreditoE the listaCtaCreditoE to set
	 */
	public void setListaCtaCreditoE(RegistroDataModelImpl listaCtaCreditoE) {
		this.listaCtaCreditoE = listaCtaCreditoE;
	}

	/**
	 * @return the listaCtaDebito1
	 */
	public RegistroDataModelImpl getListaCtaDebito1() {
		return listaCtaDebito1;
	}

	/**
	 * @param listaCtaDebito1 the listaCtaDebito1 to set
	 */
	public void setListaCtaDebito1(RegistroDataModelImpl listaCtaDebito1) {
		this.listaCtaDebito1 = listaCtaDebito1;
	}

	/**
	 * @return the listaCtaDebito1E
	 */
	public RegistroDataModelImpl getListaCtaDebito1E() {
		return listaCtaDebito1E;
	}

	/**
	 * @param listaCtaDebito1E the listaCtaDebito1E to set
	 */
	public void setListaCtaDebito1E(RegistroDataModelImpl listaCtaDebito1E) {
		this.listaCtaDebito1E = listaCtaDebito1E;
	}

	/**
	 * @return the listaCtaCredito1
	 */
	public RegistroDataModelImpl getListaCtaCredito1() {
		return listaCtaCredito1;
	}

	/**
	 * @param listaCtaCredito1 the listaCtaCredito1 to set
	 */
	public void setListaCtaCredito1(RegistroDataModelImpl listaCtaCredito1) {
		this.listaCtaCredito1 = listaCtaCredito1;
	}

	/**
	 * @return the listaCtaCredito1E
	 */
	public RegistroDataModelImpl getListaCtaCredito1E() {
		return listaCtaCredito1E;
	}

	/**
	 * @param listaCtaCredito1E the listaCtaCredito1E to set
	 */
	public void setListaCtaCredito1E(RegistroDataModelImpl listaCtaCredito1E) {
		this.listaCtaCredito1E = listaCtaCredito1E;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public List<Registro> getListaAnioActual() {
		return listaAnioActual;
	}

	public void setListaAnioActual(List<Registro> listaAnioActual) {
		this.listaAnioActual = listaAnioActual;
	}

	public List<Registro> getListaAnioPreparar() {
		return listaAnioPreparar;
	}

	public void setListaAnioPreparar(List<Registro> listaAnioPreparar) {
		this.listaAnioPreparar = listaAnioPreparar;
	}

	public String getAnioPreparar() {
		return anioPreparar;
	}

	public void setAnioPreparar(String anioPreparar) {
		this.anioPreparar = anioPreparar;
	}

}
