package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoDosRemote;
import com.sysman.bancoproyectos.ejb.impl.EjbBancoProyectoDos;
import com.sysman.bancoproyectos.enums.FrmSolicitudCdpControladorEnum;
import com.sysman.bancoproyectos.enums.FrmSolicitudCdpControladorUrlEnum;
import com.sysman.bancoproyectos.enums.FrmproyectosControladorEnum;
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
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
 * @author acaceres
 * @version 1, 28/08/2015
 * @modified jguerrero
 * @version 2. 26/09/2017 Se realizo el refactory de las consultas sql en el controlador. Adem�s se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class FrmSolicitudCdpControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private final String session;
    private String nit;
    private Registro registroSub;
    private List<Registro> listaClasetAfectar;
    private List<Registro> listaComponenteNom;
    private List<Registro> listaActividadNom;
    private List<Registro> listaIdMetaProductoNom;
    private List<Registro> listaNovedaestecnicas;
    private List<Registro> listaSubpreguntascdp;
    private List<Registro> listaPregunta;
    private RegistroDataModelImpl listaDependencia;
    private String auxiliar;
    private RegistroDataModelImpl listaTipotAfectar;
    private RegistroDataModelImpl listaDocumentoAfectar;
    private RegistroDataModelImpl listaResponsable;
    private RegistroDataModelImpl listaResponsableRevis;
    private RegistroDataModelImpl listaDependenciaSec;
    private RegistroDataModelImpl listaResponsableSec;
    private RegistroDataModelImpl listaModalidadSeleccion;
    private String ano;
    private String tipoNovedad;
    private String createdBy;
    private String argNovedades;
    private String proyectoM;
    private String tipoTFiltrarM;
    private String estadoFiltrarM;
    private String cmbDependenciaM;
    private Boolean impreso;
    // variable que almacena el valor del check de modificacion
    private Boolean modificacion = false;
    private String tipoNovedadAux;
    private String tituloFormulario;
    private boolean bloqCodigo;
    private boolean cargarInfoAdicional;
    private boolean cargarAdicional;
    // variable que almacena en valor del check sin segunda firma
    private boolean sinSegundaFir;

    private String eCodigo;
    private String fechaInicialM;
    private String fechaFinalM;
    private Boolean voBo;
    private String vigencia;
    private String vigenciaPeriodo;
    private String nombreproyectoM;
    private String nombreTM;
    private String reporte1;
    private String reporte2;
    private Boolean bloqueaVoBo;
    private Boolean bloqueaVoBoBP;
    private boolean bloqueaContDepBppim;
    private boolean visibleAfectar = false;
    private Boolean bloqueaProcesoFinanciero;
    private Boolean bloqueaConComprobantePPTAL;
    private Boolean bloqueaAfectado;
    private boolean bloqueaFecha;
    private Boolean bloqueaImprimir;
    private Boolean bloqueaFechaVoBo;
    private Boolean bloqueaHoraVoBo;
    private Boolean activarVoBo;
    private Boolean bloqueaNecesidadN;
    private Boolean comando189Visible = false;
    private Boolean novedadesTecnicasVisible = false;
    private boolean varVolver;
    /*
     * variable que se le envia por pararametro al reporte para visualizar firmas secundarias en CDPTOCAN
     */
    private boolean camposSegResp;
    private StreamedContent archivoDescarga;
    private String documentoAfectarAnt;
    private String eje;
    private String programa;
    private String sector;
    private String metaResul;
    private String consultaReportInscripBp;
    private String nombreDependenciaSec;
    private String nombreResponsableSec;
    private String nombreModalidad;
    private boolean visibleDependencia;
    private boolean visibleModalidad;
    private String tipoTmodal;
    private String consConsecutivo;
    private String cedula;
    private String sucursal;
    private String nombreRes;
    private final String tipoTCons;
    private String tipotconst;
    private final String claseTCons;
    private String dependenciaTipo;
    private String cargo;
    private String imagenLema;
    private String titulo;
    private String Fecha;
    private Registro rsDisminuido;
    private Double valorDisminucion;

    /**
     * Atributo que almacena el valor del parametro clase, del registro seleccionado en el formulario Todas las novedades
     */
    private String clase;
    private final String vigenciaCons;
    private final String codigoCons;
    private final String nombreDependenciaCons;
    private final String nombreResponRevisCons;
    private final String cargoResponRevisCons;
    private final String nombreCargoResponsableCons;

    private final String dependenciaCons;
    private boolean esBotonVer = false;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbBancoProyectoCincoRemote ejbBanProyCinco;
    @EJB
    private EjbBancoProyectoDosRemote ejbBanProyDos;

    @EJB
    private EjbSysmanUtilRemote ejbSymanUtl;

    @EJB
    private EjbBancoProyectoDos ejbBancoProDos;
    private String modificaConsecutivo;
    private boolean modificaCons;
    private String meta;
    private String subprograma;
    private Map<String,Object> parametroswf;
	private boolean verCerrar = true;

    @SuppressWarnings("unchecked")
    public FrmSolicitudCdpControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        SessionUtil.setSessionVar("modulo", "52");
        modulo = SessionUtil.getModulo();
        session = SessionUtil.getUser().toString();
        nit = SessionUtil.getCompaniaIngreso().getNit();
        tipoTCons = FrmSolicitudCdpControladorEnum.TIPOT.getValue();
        claseTCons = FrmSolicitudCdpControladorEnum.CLASET.getValue();
        vigenciaCons = GeneralParameterEnum.VIGENCIA.getName();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        consConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        dependenciaCons = GeneralParameterEnum.DEPENDENCIA.getName();
        dependenciaTipo = "DEPENDENCIA_AFECTAR";
        nombreDependenciaCons = "NOMBREDEPENDECIA";
        nombreResponRevisCons = "NOMBRE_RESPON_REVIS";
        cargoResponRevisCons = "CARGO_RESPONS_REVIS";
        nombreCargoResponsableCons = "NOMBRECARGORESPONSABLE";
        tipotconst = "";

 

        try
        {

            numFormulario = GeneralCodigoFormaEnum.FRM_SOLICITUD_CDP_CONTROLADOR.getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            Map<String, Object> parametros = SessionUtil.getFlash();
            registroSub = new Registro(new HashMap<String, Object>());

            if (parametros != null)
            {
            	parametroswf = (Map<String,Object>) parametros.get("parametroswf");
    			if(parametroswf != null) {
    				varVolver = false;
    				verCerrar = false;
    				SessionUtil.setSessionVar("menuActual", 
    						SysmanFunciones.nvl(parametroswf.get("menu"),"").toString());
    				setTitulo("Solicitud de Certificado de disponibilidad presupuestal");
    			}
                if (FrmSolicitudCdpControladorEnum.C52020301.getValue().equals(SessionUtil.getMenuActual())
                                || "52020303".equals(SessionUtil.getMenuActual())
                                || FrmSolicitudCdpControladorEnum.C52020302.getValue().equals(SessionUtil.getMenuActual()))
                {
                    varVolver = false;
                    vigenciaPeriodo = (String) parametros
                                    .get(FrmSolicitudCdpControladorEnum.VIGENCIAPERIODO_LOWER.getValue());
                }

                if ("520206".equals(SessionUtil.getMenuActual()))
                {

                    vigenciaPeriodo = (String) parametros
                                    .get(FrmSolicitudCdpControladorEnum.VIGENCIAPERIODO_LOWER.getValue());

                    tipoNovedad = (String) parametros.get("tipo");

                    clase = (String) parametros.get("clase");

                    tipoTmodal = parametros.get("TipoTmodal").toString();

                }

                if (SessionUtil.getMenuActual().equals(FrmSolicitudCdpControladorEnum.C52020401.getValue()))
                {
                    ano = (String) parametros.get(FrmSolicitudCdpControladorEnum.ANO_LOWER.getValue());
                    argNovedades = (String) parametros
                                    .get(FrmSolicitudCdpControladorEnum.ARGNOVEDADES_LOWER.getValue());

                    proyectoM = (String) parametros.get(FrmSolicitudCdpControladorEnum.TIPONOVEDAD_LOWER.getValue());
                    estadoFiltrarM = (String) parametros
                                    .get(FrmSolicitudCdpControladorEnum.ESTADOFILTRARM_LOWER.getValue());
                    cmbDependenciaM = (String) parametros.get("cmbDependenciaM");
                    fechaInicialM = (String) parametros
                                    .get(FrmSolicitudCdpControladorEnum.FECHAINICIALM_LOWER.getValue());
                    fechaFinalM = (String) parametros.get(FrmSolicitudCdpControladorEnum.FECHAFINALM_LOWER.getValue());
                    nombreproyectoM = (String) parametros
                                    .get(FrmSolicitudCdpControladorEnum.NOMBREPROYECTO_LOWER.getValue());
                    nombreTM = (String) parametros.get(FrmSolicitudCdpControladorEnum.NOMBRETM_LOWER.getValue());
                    vigencia = (String) parametros.get(FrmSolicitudCdpControladorEnum.ANO_LOWER.getValue());
                    vigenciaPeriodo = vigencia;
                    varVolver = true;
                }

                rid = (Map<String, Object>) parametros.get(FrmSolicitudCdpControladorEnum.RID_LOWER.getValue());
            }

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
			SessionUtil.cleanFlash();
		}
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();

        if ("520206".equals(SessionUtil.getMenuActual()))
        {

            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametrosListado.put(tipoTCons, tipoNovedad);
            parametrosListado.put(claseTCons, SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B"));
            parametrosListado.put("VIGENCIASOLICITUD", vigenciaPeriodo);
        }
        else
        {

            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametrosListado.put(tipoTCons, SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "SCD"));
            parametrosListado.put(claseTCons, SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B"));
            parametrosListado.put("VIGENCIASOLICITUD", vigenciaPeriodo);
        }

        if (!SessionUtil.getGrupo(modulo).isEsAdministrador())
        {

            parametrosListado.put(GeneralParameterEnum.USUARIO.getName(), session);

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL00003.getValue());

        }

    }

    @PostConstruct
    public void inicializar()
    {
    	
        if (("52020301").equals(SessionUtil.getMenuActual()))
        {
            setTitulo("Solicitud de Certificado de disponibilidad presupuestal");
        }
        else if (("52020303").equals(SessionUtil.getMenuActual()))
        {

        	String tituloForm = "Solicitud de pertinencia";

				try {
					tituloForm = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					        "TITULO PARA FORMULARIO CERTIFICADO DE VIABILIDAD", "52", new Date(), false),
					        "Solicitud de pertinencia");
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					tituloForm = "Solicitud de pertinencia";
				}

        	
            setTitulo(tituloForm);
        }
        else
        {
            setTitulo("Solicitud de Certificado de Banco de proyectos");
        }
        
    	if(parametroswf != null) {
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
			
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");
		}
        enumBase = GenericUrlEnum.BPNOVEDADPROYECTO;
        buscarLlave();
        asignarOrigenDatos();
        registro.getCampos();
        bloqueaContDepBppim = false;

        cargarInfoAdicional = "899999465".equals(nit) ? true : false;

    }

    @Override
    public void iniciarListas()
    {
        cargarListaTipotAfectar();
        cargarListaPregunta();
        cargarListaDependencia();
        cargarListaResponsable();
        cargarListaDependenciaSec();
        cargarListaResponsableSec();
        cargarListaModalidadSeleccion();
    }

    @Override
    public void iniciarListasSub()
    {
        // cargarListaDocumentoAfectar();
        // cargarlistaClasetAfectar();
        cargarListaResponsableRevis();
        cargarListaSubpreguntascdp();
    }

    @Override
    public void iniciarListasSubNulo()
    {
        listaNovedaestecnicas = null;
        registro = new Registro(new HashMap<String, Object>());
        bloqueaAfectado = false;
        registro.getCampos().put(vigenciaCons, vigenciaPeriodo);
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), new Date());
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "V");
        bloqueaContDepBppim = false;
        

    }

    public void ejecutarrcVolver()
    {
        // <CODIGO_DESARROLLADO>

        if (SessionUtil.getMenuActual().equals(FrmSolicitudCdpControladorEnum.C52020401.getValue()))
        {
            Map<String, Object> parametros = SessionUtil.getFlash();
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.MONITOR_NOVEDADES_PROYECTOS_CONTROLADOR.getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);

        }

        // </CODIGO_DESARROLLADO>
    }

    public void cargarlistaClasetAfectar()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoTCons, registro.getCampos().get(tipoTCons));

        try
        {
            listaClasetAfectar = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL26841
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 130019 COMPANIA TIPOT
    }

    public void cargarListaDependencia()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL27776.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        codigoCons);

        // 62060 COMPANIA
    }

    public void cargarListaTipotAfectar()
    {

        if ("CDP".equals(tipoTmodal))
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL34239.getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.FECHA.getName(), Fecha);

            try
            {
                listaTipotAfectar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                                true, CacheUtil.getLlaveServicio(urlConexionCache, "BPNOVEDADPROYECTO"));
            }
            catch (SysmanException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        else
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL0007.getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.FECHA.getName(), Fecha);

            try
            {
                listaTipotAfectar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                                true, CacheUtil.getLlaveServicio(urlConexionCache, "BPNOVEDADPROYECTO"));
            }
            catch (SysmanException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 130022 TIPOT
        }

    }

    /**
     * 
     * Carga la lista listaDimension
     */
    public void cargarListaPregunta()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaPregunta = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL0009
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // Cesar Ochoa - Se comentó el cuerpo de la función por error de negocio -
    // Doble intención con la función afectarDocumento
    // public void modificar() {
    // String tipoTAfectar;
    // tipoTAfectar = registro.getCampos().get("TIPOT_AFECTAR").toString();
    // // modificacion = false;
    // modificacion = (Boolean)
    // registro.getCampos().get(FrmSolicitudCdpControladorEnum.MODIFICACION.getValue());

    // if ("MOD".equals(tipoTAfectar) && (modificacion == false)) {
    // registro.getCampos().put(FrmSolicitudCdpControladorEnum.MODIFICACION.getValue(),
    // true);
    // }

    // }

    public void cargarListaDocumentoAfectar()
    {
        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();

        if ("MOD".equals(tipoTmodal))
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL00001.getValue());
        }

        else
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL0004.getValue());

        }

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(dependenciaCons, registro.getCampos().get(dependenciaCons));
        // param.put(tipoTCons,
        // registro.getCampos()
        // .get(FrmSolicitudCdpControladorEnum.TIPOT_AFECTAR
        // .getValue()));
        param.put(claseTCons, registro.getCampos().get("CLASET"));
        param.put("FECHAS", Fecha);

        listaDocumentoAfectar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 130024 COMPANIA DEPENDENCIA TIPOT CLASET
    }

    public void cargarListaResponsable()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL34235.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(dependenciaCons, registro.getCampos().get(dependenciaCons));
        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.RESPONSABLE.getName());

        // 71023 DEPENDENCIA
    }

    public void cargarListaResponsableRevis()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL35595.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(dependenciaCons, registro.getCampos().get(dependenciaCons));

        listaResponsableRevis = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.RESPONSABLE.getName());

        // 71025 DEPENDENCIA
    }

    /**
     * Carga la lista listaDependenciaSec
     */
    public void cargarListaDependenciaSec()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL27776.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaSec = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        codigoCons);
    }

    /**
     * Carga la lista listaResponsableSec
     */
    public void cargarListaResponsableSec()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL49759.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(dependenciaCons, registro.getCampos().get("DEPENDENCIA_SEC"));
        listaResponsableSec = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.RESPONSABLE.getName());
    }

    /**
     * Carga la lista listaModalidadSeleccion
     */
    public void cargarListaModalidadSeleccion()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL29968.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaModalidadSeleccion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        // jose caceres
        sinSegundaFirma();
        impreso = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.IMPRESO.getValue());
        if (impreso)
        {
            registro.getCampos().put(FrmSolicitudCdpControladorEnum.IMPRESO.getValue(), impreso);
            String mensaje = idioma.getString("TB_TB2150");
            mensaje = mensaje.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
            JsfUtil.agregarMensajeInformativo(mensaje);

        }
        else
        {
            isnotImpreso();
        }

    }

    private void isnotImpreso()
    {
        tipoNovedadAux = (String) registro.getCampos().get(tipoTCons);

        getReporteSolicitudCdpPtg(FORMATOS.PDF);
    }

    public void cambiarImpreso()
    {
        bloqueVOBO();
    }

    /**
     * Metodo ejecutado al cambiar el control Pregunta
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarPregunta()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Codigo
     * 
     */
    public void cambiarCodigo()
    {
        // <CODIGO_DESARROLLADO>

        if (!modificaCons)
        {
            if (registro.getCampos().get("CODIGO").toString().length() != 10)
            {
                JsfUtil.agregarMensajeAlerta("El c�digo debe tener 10 digitos. Por favor ingreselo nuevamente.");
                registro.getCampos().put(codigoCons, null);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioNovedadesProyecto(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
    }

    public void cambiarEstado()
    {
        registro.getCampos().put("ESTADO_MODIFICADOPOR", SessionUtil.getUser().getCodigo());
        registro.getCampos().put("ESTADO_HORAMODIFICADO", new Date());
        registro.getCampos().put("ESTADO_FECHAMODIFICADO", new Date());
    }

    public void cambiarClasetAfectar()
    {
        registro.getCampos().put(FrmSolicitudCdpControladorEnum.DOCUMENTO_AFECTAR.getValue(), null);
        cargarListaDocumentoAfectar();
    }

    public void cambiarInfoDetalles()
    {
        // </CODIGO_DESARROLLADO>
    }

    public void bloqueVOBO()
    {

        voBo = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.VOBOBP.getValue());
        impreso = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.IMPRESO.getValue());
        boolean afectado = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.AFECTADO.getValue());

        // Al nivel 9 se le da permiso de quitar el visto bueno y de
        // imprimir
        if ("m".equals(accion))
        {
            if (voBo || impreso || afectado)
            {
                bloqueaContDepBppim = true;
                bloqueaNecesidadN = false;
                if ((SessionUtil.getNivelUsuario(modulo) == 9) && !afectado)
                { // &&
                    bloqueaVoBoBP = false;
                    bloqueaImprimir = false;
                }
                else if (afectado)
                {

                    bloqueaImprimir = false;
                }

            }
            else
            {

                bloqueaFechaVoBo = true;
                bloqueaHoraVoBo = true;
                bloqueaVoBoBP = false;
                bloqueaImprimir = false;
                bloqueaContDepBppim = false;
                bloqueaProcesoFinanciero = true;
                bloqueaConComprobantePPTAL = true;
                bloqueaAfectado = false;
                bloqueaNecesidadN = true;
            }
        }
    }

    public void cambiarVoboBp()
    {
        
        if (SessionUtil.getNivelUsuario(modulo) != 9)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2403"));
            registro.getCampos().put(FrmSolicitudCdpControladorEnum.VOBOBP.getValue(), false);
            return;
        }
        
        else {

        try
        {

            String rta = ejbBanProyCinco.validarVoBo(compania, new BigInteger(registro.getCampos().get(codigoCons).toString()),
                            registro.getCampos().get(tipoTCons).toString(), registro.getCampos().get(claseTCons).toString(),
                            (boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.VOBOBP.getValue()));

            if (!rta.equals("false"))
            {
                registro.getCampos().put(FrmSolicitudCdpControladorEnum.VOBOBP.getValue(), false);
                JsfUtil.agregarMensajeAlerta(rta);

            }
            else
            {

                boolean afectado = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.AFECTADO.getValue());

                if (!afectado)
                {
                	agregarRegistroNuevo(false);
            	
                    ejbBancoProDos.validarVOBO(compania, registro.getCampos().get("TIPOT").toString(),
                                    registro.getCampos().get("CLASET").toString(), registro.getCampos().get("CODIGO").toString(),
                                    registro.getCampos().get("DEPENDENCIA").toString(), SessionUtil.getUser().getCodigo());

                    JsfUtil.agregarMensajeInformativo("Proceso VoBo ejecutado correctamente");

                }
                else
                {

                    registro.getCampos().put(FrmSolicitudCdpControladorEnum.VOBOBP.getValue(), true);

                }

                

                registro.getCampos().put(FrmSolicitudCdpControladorEnum.USUARIO_VOBO.getValue(),
                                SessionUtil.getUser().getCodigo().toUpperCase());
                if (registro.getCampos().get(FrmSolicitudCdpControladorEnum.FECHA_VOBO.getValue()) == null)
                {
                    registro.getCampos().put(FrmSolicitudCdpControladorEnum.FECHA_VOBO.getValue(), new Date());
                    registro.getCampos().put(FrmSolicitudCdpControladorEnum.HORA_VOBO.getValue(), new Date());
                }
                bloqueVOBO();

            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        }
    }

    public void cambiarAfectado()
    {
        bloqueVOBO();
    }

    public void cambiarVigencia()
    {
        if (conDetalles())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2404"));
            return;
        }
    }

    // cambio jose caceres
    public boolean sinSegundaFirma()
    {

        sinSegundaFir = (boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.SIN_SEGUNDA_FIRMA.getValue());

        if ((sinSegundaFir == false) && !"".equals(nombreDependenciaSec))
        {
            camposSegResp = true;

        }
        else
        {
            camposSegResp = false;
        }
        return camposSegResp;

    }

    public void getReporteSolicitudCdpPtg(FORMATOS formatos)
    {

        archivoDescarga = null;

        int vigenciaActual = SysmanFunciones.ano(new Date());
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("eje", eje);
        reemplazar.put("sector", sector);
        reemplazar.put("programa", programa);
        reemplazar.put("meta", meta);
        reemplazar.put("subprograma", subprograma);
        reemplazar.put("metaresultado", metaResul);
        reemplazar.put("digitoprog", programa);
        reemplazar.put("digitodim", "2");
        reemplazar.put("tipot", registro.getCampos().get(tipoTCons));
        reemplazar.put("claset", registro.getCampos().get(claseTCons));
        reemplazar.put("codigo", registro.getCampos().get(codigoCons));
        reemplazar.put("argNovedades", registro.getCampos().get(codigoCons));
        reemplazar.put("dependencia", registro.getCampos().get(dependenciaCons));
        reemplazar.put("vigencia", retornarString(registro, vigenciaCons));
        reemplazar.put("vigenciaActual", vigenciaActual);
        reemplazar.put("consecutivo", registro.getCampos().get(codigoCons));
        try
        {

            createdBy = SysmanFunciones.nvl(registro.getCampos().get("CREATED_BY"), "").toString();

            Map<String, Object> paramUsuario = new TreeMap<>();

            paramUsuario.put(GeneralParameterEnum.CODIGO.getName(), createdBy);

            Registro rsr = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL0002
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            paramUsuario));

            if (rsr == null)
            {
                JsfUtil.agregarMensajeAlerta("El usuario no tiene c�dula asociada.");
            }
            else
            {

                cedula = SysmanFunciones.nvl(rsr.getCampos().get("CEDULA"), "").toString();

                nombreRes = SysmanFunciones.nvl(rsr.getCampos().get("NOMBRECOMPLETO"), "").toString();
                sucursal = SysmanFunciones.nvl(rsr.getCampos().get("SUCURSAL"), "").toString();

                Map<String, Object> paramCargo = new TreeMap<>();

                paramCargo.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                paramCargo.put(GeneralParameterEnum.CEDULA.getName(), cedula);

                paramCargo.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

                Registro rsc = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL0003

                                                .getValue())
                                .getUrl(), paramCargo));
                if (rsc == null)
                {
                    JsfUtil.agregarMensajeAlerta("El usuario no tiene un cargo asociado.");
                }
                else
                {
                    cargo = SysmanFunciones.nvl(rsc.getCampos().get("CARGO"), "").toString();
                }
            }

            Map<String, Object> parametros = new HashMap<>();
            String faxCompania = SysmanFunciones.nvlStr(SessionUtil.getCompaniaIngreso().getFax(), " ");

            String nombreRevisa = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN REVISA",
                            SessionUtil.getModulo(), new Date(), true);

            String cargoAutorizado = ejbSysmanUtil.consultarParametro(compania, "CARGO AUTORIZADO",
                            SessionUtil.getModulo(), new Date(), true);

            String jefeBancoProyectos = ejbSysmanUtil.consultarParametro(compania, "JEFE DE BANCO PROYECTOS",
                            SessionUtil.getModulo(), new Date(), true);

            String cargoJefeBancoProyectos = ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE BANCO PROYECTOS",
                            SessionUtil.getModulo(), new Date(), true);

            String profesionJefeBancoProyectos = ejbSysmanUtil.consultarParametro(compania,
                            "PROFESION JEFE DE BANCO PROYECTOS", SessionUtil.getModulo(), new Date(), true);

            String responsableSec = nombreResponsableSec;

            String cargoResponsableSec = nombreDependenciaSec;

            // String responsableSec = ejbSysmanUtil
            // .consultarParametro(
            // compania,
            // "RESPONSABLE SEC",
            // SessionUtil.getModulo(),
            // new Date(), true);
            //
            // String cargoResponsableSec = ejbSysmanUtil
            // .consultarParametro(
            // compania,
            // "CARGO RESPONSABLE SEC",
            // SessionUtil.getModulo(),
            // new Date(), true);

            String firmaJefeAsesoriaPlaneacion = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA JEFE ASESORIA DE PLANEACION", SessionUtil.getModulo(), new Date(), true);
            String cargoJefeAsesoriaPLaneacion = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE ASESORIA DE PLANEACION", SessionUtil.getModulo(), new Date(), true);
            String firmaJefeSecretariaGobierno = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA JEFE SECRETARIA DE GOBIERNO", SessionUtil.getModulo(), new Date(), true);
            String cargoJefeSecretariaGobierno = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE SECRETARIA DE GOBIERNO", SessionUtil.getModulo(), new Date(), true);
            String vigenciaGubernamentalActual = ejbSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", SessionUtil.getModulo(), new Date(), true);

            String formatopertinencia = ejbSysmanUtil.consultarParametro(compania, "IMAGEN FORMATO PERTINENCIA",
                            SessionUtil.getModulo(), new Date(), false);
            String cargoJefeBancoDeProyectos = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE BANCO PROYECTOS", SessionUtil.getModulo(), new Date(), true);
            String profesionJefeBancoDeProyectos = ejbSysmanUtil.consultarParametro(compania,
                            "PROFESION JEFE BANCO PROYECTOS", SessionUtil.getModulo(), new Date(), true);

            int vigenciaGFinal = Integer.parseInt(vigenciaGubernamentalActual) + 3;

            String vigenciaGubernamentalFinal = String.valueOf(vigenciaGFinal);

            String tituloPlanDesarrollo = ejbSysmanUtil.consultarParametro(compania, "TITULO PLAN DE DESARROLLO",
                            SessionUtil.getModulo(), new Date(), true);

            String ciudadCompania = SysmanFunciones.nvlStr(SessionUtil.getCompaniaIngreso().getCiudad(), " ");

            String departmentoCompania = SysmanFunciones.nvlStr(SessionUtil.getCompaniaIngreso().getDepartamento(),
                            " ");

            String siglaCompania = SysmanFunciones.nvlStr(SessionUtil.getCompaniaIngreso().getNombre(), " ");

            String direccionCompania = SysmanFunciones.nvlStr(SessionUtil.getCompaniaIngreso().getDireccion(), " ");

            String telefonoCompania = SysmanFunciones.nvlStr(SessionUtil.getCompaniaIngreso().getTelefono(), " ");

            String planDesarrollo = ejbSysmanUtil.consultarParametro(compania, "PLAN DE DESARROLLO",
                            SessionUtil.getModulo(), new Date(), true);

            String tituloCDP = ejbSysmanUtil.consultarParametro(compania, "TITULO SOLICITUD CDP",
                            SessionUtil.getModulo(), new Date(), true);

            String tituloCDPDOS;

            tituloCDPDOS = ejbSysmanUtil.consultarParametro(compania, "TITULO DOS SOLICITUD CDP",
                            SessionUtil.getModulo(), new Date(), true);

            // cambio jose caceres

            parametros.put("PR_MOSTAR", camposSegResp);
            parametros.put("PR_NOMBRE_REVISA", nombreRevisa);
            parametros.put("PR_NUMSOLICITUD",registro.getCampos().get(codigoCons));
            parametros.put("PR_CARGO AUTORIZADO", cargoAutorizado);
            parametros.put("PR_RESPONSABLE_DEPENDENCIA", registro.getCampos().get("NOMBRERESPONSABLE"));
            parametros.put("PR_CARGO_RESPONSABLE_DEPENDENCIA", registro.getCampos().get("CARGORESPONSABLE"));
            parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
            //
            parametros.put("PR_FAXCOMPANIA", faxCompania);
            parametros.put("PR_JEFE_BANCO_PROYECTOS", jefeBancoProyectos);
            parametros.put("PR_CARGO_JEFE_BANCO_PROYECTOS", profesionJefeBancoProyectos);
            parametros.put("PR_PROFESION_JEFE_BANCO_PROYECTOS", cargoJefeBancoProyectos);
            parametros.put("PR_FIRMA_JEFE_ASESORIA_DE_PLANEACION", firmaJefeAsesoriaPlaneacion);
            parametros.put("PR_CARGO_JEFE_ASESORIA_DE_PLANEACION", cargoJefeAsesoriaPLaneacion);
            parametros.put("PR_FIRMA_JEFE_DE_SECRETARIA_DE_GOBIERNO", firmaJefeSecretariaGobierno);
            parametros.put("PR_CARGO_JEFE_DE_SECRETARIA_DE_GOBIERNO", cargoJefeSecretariaGobierno);
            parametros.put("PR_VIGENCIA_GUBERNAMENTAL_ACTUAL", vigenciaGubernamentalActual);
            parametros.put("PR_VIGENCIA_GUBERNAMENTAL_FINAL", vigenciaGubernamentalFinal);
            parametros.put("PR_TITULO_PLAN_DE_DESARROLLO", tituloPlanDesarrollo);
            parametros.put("PR_CIUDADCOMPANIA", ciudadCompania);
            parametros.put("PR_DEPARTAMENTOCOMPANIA", departmentoCompania);
            parametros.put("PR_SIGLA_COMPANIA", siglaCompania);
            parametros.put("PR_DIRECCION_COMPANIA", direccionCompania);
            parametros.put("PR_TELEFONO_COMPANIA", telefonoCompania);
            parametros.put("PR_PLAN_DE_DESARROLLO", planDesarrollo);
            parametros.put("PR_TITULO_SOLICITUD", tituloCDP);
            parametros.put("PR_TITULO_SOLICITUDDOS", tituloCDPDOS);
            parametros.put("PR_CARGO_JEFE_BANCO_DE_PROYECTOS", cargoJefeBancoDeProyectos);
            parametros.put("PR_PROFESION_JEFE_BANCO_DE_PROYECTOS", profesionJefeBancoDeProyectos);

            //
            parametros.put("PR_RESPONSABLE_SEC", responsableSec);
            parametros.put("PR_CARGO_RESPONSABLE_SEC", cargoResponsableSec);

            // parametros.put("PR_RESPONSABLE_SEC", responsableSec);
            // parametros.put("PR_CARGO_RESPONSABLE_SEC",
            // cargoResponsableSec);
            parametros.put("PR_RESPONSABLE_ASOCIADO", nombreRes);
            parametros.put("PR_IMAGEN_FORMATO_PERTINENCIA", formatopertinencia);

            parametros.put("PR_CARGO_RESPONSABLE_ASOCIADO", cargo);
            parametros.put("PR_NOMBRELARGO", session);
            parametros.put("PR_CARGO_JEFE_DE_PRESUPUESTO",
                            ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE PRESUPUESTO", modulo, new Date(), true));
            parametros.put("PR_CARGO_JEFE_DE_PRESUPUESTO",
                            ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE PRESUPUESTO", modulo, new Date(), true));

            parametros.put("PR_JEFE_BANCO_PROYECTOS", ejbSysmanUtil.consultarParametro(compania,
                            "JEFE BANCO PROYECTOS", modulo, new Date(), true));

            parametros.put("PR_IMAGEN_LEMA", imagenLema);
            
            // INICIO 7711610 mperez 05/05/2022
            parametros.put("PR_IMAGEN_LEMA",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "IMAGEN LEMA CASTILLA",
                                    SessionUtil.getModulo(), new Date(),
                                    false));
            parametros.put("PR_IMAGEN_LEMA_CASTILLA",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "IMAGEN LEMA2 CASTILLA",
                                    SessionUtil.getModulo(), new Date(),
                                    false));
            parametros.put("PR_IMAGEN_REDES",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "IMAGEN REDES CASTILLA",
                                    SessionUtil.getModulo(), new Date(),
                                    false));
            // FIN 7711610 mperez 05/05/2022

            Map<String, Object> parametros1 = new HashMap<>();
            parametros1.put("PR_NOMBRELARGO", session);
            parametros1.put("PR_CARGO_JEFE_DE_PRESUPUESTO",
                            ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE PRESUPUESTO", modulo, new Date(), true));
            parametros1.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", modulo, new Date(), true));
            String jefeBancoProyectos1 = ejbSysmanUtil.consultarParametro(compania, "JEFE BANCO PROYECTOS",
                            SessionUtil.getModulo(), new Date(), true);

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("CODIGO", registro.getCampos().get(codigoCons));
            param.put("TIPOT", registro.getCampos().get(tipoTCons));
            param.put("CLASET", registro.getCampos().get(claseTCons));
            param.put("PR_CARGO_JEFE_DE_PRESUPUESTO", ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE DE PRESUPUESTO", modulo, new Date(), true));
            param.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", modulo, new Date(), true));
            param.put("PR_IMAGEN_LEMA", imagenLema);

            Registro existe = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL2473
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(tipoTCons, registro.getCampos().get("TIPOT"));
            param.put(claseTCons, registro.getCampos().get("CLASET"));
            parametros.put("PR_CARGO_DE_SECRETARIA_DE_HACIENDAS",
                    ejbSysmanUtil.consultarParametro(compania, "CARGO DE SECRETARIA DE HACIENDAS", modulo, new Date(), true));
            
            parametros.put("PR_IMAGEN_LOGO_FORMATO_CDP",
                    ejbSysmanUtil.consultarParametro(compania, "IMAGEN LOGO FORMATO CDP", modulo, new Date(), true));

            parametros.put("PR_IMAGEN_ESCUDO_FORMATO_CDP",
                    ejbSysmanUtil.consultarParametro(compania, "IMAGEN ESCUDO FORMATO CDP", modulo, new Date(), true));
            


            Registro rs = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL0001

                                            .getValue())
                            .getUrl(), param));

            String formato = (String) rs.getCampos().get("TIPOFORMATO");

            if (!"0".equals(existe.getCampos().get("EXISTE").toString()))
            {

                /*- Se solicito que el nombre del informe lo tomara de campo TIPOFORMATO de la tabla 
                 *  BPTIPONOVEDAD el cual se ingresa en el formulario tipos de novedades proyectos 
                 *  por la ruta Banco de proyectos/ archivos/ tipos de novedades protectos. a solicitud de manuel cifuentes
                 */

                if ("520206".equals(SessionUtil.getMenuActual()))
                {

                    if ("CDP".equals(tipoTmodal))
                    {
                    	if(formato.equals("002365CertificadoCDPCastilla") || formato.equals("002628CertificadoCastilla")) 
                        {
                        	
                        	//reemplazar.clear();
                        	//parametros.clear();
                        	reemplazar.put("compania", compania);
                        	reemplazar.put("codigo",
                                    registro.getCampos().get(codigoCons));
                        	
                        	parametros.put("PR_PLAN_DE_DESARROLLO",
                        			ejbSysmanUtil.consultarParametro(compania,
                                            "PLAN DE DESARROLLO", SessionUtil.getModulo(), new Date(), true));
                        	
                        	parametros.put("PR_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "JEFE BANCO PROYECTOS",
                                                    SessionUtil.getModulo(), new Date(),
                                                    true));
        		            parametros.put("PR_CARGO_JEFE_BP",
        		                            ejbSysmanUtil.consultarParametro(compania,
        		                                            "CARGO JEFE BANCO PROYECTOS",
        		                                            SessionUtil.getModulo(), new Date(),
        		                                            true));
        		            parametros.put("PR_IMAGEN",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "IMAGEN LOGO CASTILLA",
                                                    SessionUtil.getModulo(), new Date(),
                                                    false));
        		            parametros.put("PR_IMAGEN_LEMA_CASTILLA",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "IMAGEN LEMA2 CASTILLA",
                                                    SessionUtil.getModulo(), new Date(),
                                                    false));
        		            
        		            parametros.put("PR_IMAGEN_REDES",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "IMAGEN REDES CASTILLA",
                                                    SessionUtil.getModulo(), new Date(),
                                                    false));
                        	
                        	Reporteador.resuelveConsulta(formato,
                                    Integer.parseInt(SessionUtil.getModulo()), reemplazar,
                                    parametros); 

    		                archivoDescarga = JsfUtil.exportarStreamed(formato,
    		                                parametros,
    		                                ConectorPool.ESQUEMA_SYSMAN, formatos);
                        }
                    	else 
                    	{
                        String nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "FORMATO CERTIFICADO SOLICITUD CDP", SessionUtil.getModulo(), new Date(), false),
                                        "001776MODTOCAN");
                        
                        // 001776MODTOCAN
                        Reporteador.resuelveConsulta(formato, Integer.parseInt(modulo), reemplazar, parametros);

                        archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros,
                                        ConectorPool.ESQUEMA_SYSMAN, formatos);
                    	}

                    }
                    else
                    {
                        // 001771CDPTOCAN
                        Reporteador.resuelveConsulta(formato, Integer.parseInt(modulo), reemplazar, parametros);

                        archivoDescarga = JsfUtil.exportarStreamed(formato, parametros,
                                        ConectorPool.ESQUEMA_SYSMAN, formatos);

                    }

                }
                else
                {

                    if (formato.equals("001885SolicitudCDP_GC"))
                    {

                        Reporteador.resuelveConsulta("001885solicitudCDP_CAQ", Integer.parseInt(modulo), reemplazar,
                                        parametros);
                        // 001885SolicitudCDP_GC
                        archivoDescarga = JsfUtil.exportarStreamed(formato, parametros, ConectorPool.ESQUEMA_SYSMAN,
                                        formatos);

                    }
                 // INICIO 7711610 mperez 10/05/2022
                    else if(formato.equals("002364SolicitudCDPCastilla")) 
                    {
                    	
                    	reemplazar.clear();
                    	parametros.clear();
                    	reemplazar.put("compania", compania);
                    	reemplazar.put("codigo",
                                registro.getCampos().get(codigoCons));                    	
                    	
                    	parametros.put("PR_PLAN_DE_DESARROLLO",
                    			ejbSysmanUtil.consultarParametro(compania,
                                        "PLAN DE DESARROLLO", SessionUtil.getModulo(), new Date(), true));
                    	
                    	parametros.put("PR_IMAGEN_LEMA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "IMAGEN LEMA CASTILLA",
                                                SessionUtil.getModulo(), new Date(),
                                                false));
                    	
                    	Reporteador.resuelveConsulta(formato,
                                Integer.parseInt(SessionUtil.getModulo()), reemplazar,
                                parametros);

		                archivoDescarga = JsfUtil.exportarStreamed(formato,
		                                parametros,
		                                ConectorPool.ESQUEMA_SYSMAN, formatos);
                    }
                    else if(formato.equals("002365CertificadoCDPCastilla")) 
                    {
                    	
                    	reemplazar.clear();
                    	parametros.clear();
                    	reemplazar.put("compania", compania);
                    	reemplazar.put("codigo",
                                registro.getCampos().get(codigoCons));
                    	
                    	parametros.put("PR_PLAN_DE_DESARROLLO",
                    			ejbSysmanUtil.consultarParametro(compania,
                                        "PLAN DE DESARROLLO", SessionUtil.getModulo(), new Date(), true));
                    	
                    	parametros.put("PR_JEFE_BANCO_PROYECTOS",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "JEFE BANCO PROYECTOS",
                                                SessionUtil.getModulo(), new Date(),
                                                true));
    		            parametros.put("PR_CARGO_JEFE_BP",
    		                            ejbSysmanUtil.consultarParametro(compania,
    		                                            "CARGO JEFE BANCO PROYECTOS",
    		                                            SessionUtil.getModulo(), new Date(),
    		                                            true));
    		            parametros.put("PR_IMAGEN",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "IMAGEN LOGO CASTILLA",
                                                SessionUtil.getModulo(), new Date(),
                                                false));
    		            parametros.put("PR_IMAGEN_LEMA_CASTILLA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "IMAGEN LEMA2 CASTILLA",
                                                SessionUtil.getModulo(), new Date(),
                                                false));
    		            
    		            parametros.put("PR_IMAGEN_REDES",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "IMAGEN REDES CASTILLA",
                                                SessionUtil.getModulo(), new Date(),
                                                false));
                    	
                    	Reporteador.resuelveConsulta(formato,
                                Integer.parseInt(SessionUtil.getModulo()), reemplazar,
                                parametros); 

		                archivoDescarga = JsfUtil.exportarStreamed(formato,
		                                parametros,
		                                ConectorPool.ESQUEMA_SYSMAN, formatos);
                    }
                 // FIN 7711610 mperez 10/05/2022
                    else
                    {

                        Reporteador.resuelveConsulta(formato, Integer.parseInt(modulo), reemplazar, parametros);

                        archivoDescarga = JsfUtil.exportarStreamed(formato, parametros, ConectorPool.ESQUEMA_SYSMAN,
                                        formatos);

                    }
                }
            }
            else
            {

                String nombreReport = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                "FORMATO SOLICITUD PERTINENCIA", SessionUtil.getModulo(), new Date(), false),
                                "000233SolicitudCDPPTG");

                Reporteador.resuelveConsulta(nombreReport, Integer.parseInt(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(nombreReport, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);
            }
            if (!esBotonVer)
            {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL48820.getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put(GeneralParameterEnum.IMPRESO.getName(), "-1");
                fields.put(tipoTCons, registro.getCampos().get(tipoTCons));
                fields.put(claseTCons, registro.getCampos().get(claseTCons));
                fields.put(dependenciaCons, registro.getCampos().get(dependenciaCons));
                fields.put(codigoCons, registro.getCampos().get(codigoCons));
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
                registro.getCampos().put(FrmSolicitudCdpControladorEnum.IMPRESO.getValue(), true);

            }

            esBotonVer = false;

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1766"));
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(FrmSolicitudCdpControladorEnum.MSM_TRANS_INTERRUMPIDA.getValue()),
                            ex.getMessage()));
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirVistaPrevia()
    {
        esBotonVer = true;
        archivoDescarga = null;
        oprimirImprimir();
    }

    public Boolean conDetalles()
    {
        Registro rs = null;

        Boolean conDetalles = false;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoTCons, SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "SCD"));
        param.put(claseTCons, SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B").toString());
        param.put(codigoCons, registro.getCampos().get(codigoCons));

        try
        {
            rs = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL35863
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null)
        {
            conDetalles = true;
        }
        return conDetalles;

    }

    public void configurarNovedad()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoTCons, registro.getCampos().get(tipoTCons));
        param.put(claseTCons, registro.getCampos().get(claseTCons));

        try
        {
            Registro reg = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL34236
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            boolean documentoAfectarTN = Boolean
                            .parseBoolean(SysmanFunciones.nvl(reg.getCampos().get("AFECTADOCUMENTO"), "false").toString());

            if (documentoAfectarTN)
            {
                visibleAfectar = true;

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirComando189()
    {
        archivoDescarga = null;
        obtenerInscripcionBP(ReportesBean.FORMATOS.PDF);
    }

    public void obtenerInscripcionBP(FORMATOS formato)
    {

        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("vigencia", retornarString(registro, vigenciaCons));
            reemplazar.put("tipot", SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "MOD"));
            reemplazar.put("claset", SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B"));
            reemplazar.put("novedad", registro.getCampos().get(codigoCons));
            reemplazar.put(FrmSolicitudCdpControladorEnum.DEPENDENCIA_LOWER.getValue(),
                            registro.getCampos().get(dependenciaCons));
            reemplazar.put("eje", eje);
            reemplazar.put("sector", sector);
            reemplazar.put("meta", meta);
            reemplazar.put("subprograma", subprograma);
            reemplazar.put("programa", programa);
            reemplazar.put("metaResul", metaResul);
            String withConsulta = Reporteador.resuelveConsulta(consultaReportInscripBp, Integer.parseInt(modulo),
                            reemplazar);
            reemplazar.put("with", withConsulta);
            String strSql = Reporteador.resuelveConsulta("000376INSCRIPCIONBP", Integer.parseInt(modulo), reemplazar);
            Map<String, Object> parametros = new HashMap<>();
            String jefeBancoProyectos = ejbSysmanUtil.consultarParametro(compania, "JEFE DE BANCO DE PROYECTOS",
                            SessionUtil.getModulo(), new Date(), true);

            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_JEFE_BANCO_PROYECTOS", jefeBancoProyectos);
            parametros.put("PR_COMPANIA", compania);

            archivoDescarga = JsfUtil.exportarStreamed("000376INSCRIPCIONBP", parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirNovedadesProyecto()
    {
        agregarRegistroNuevo(false);
        tipoTFiltrarM = retornarString(registro, tipoTCons);
        String claseTS = retornarString(registro, claseTCons);
        ano = retornarString(registro, vigenciaCons);
        String voBoBp = String.valueOf(registro.getCampos().get(FrmSolicitudCdpControladorEnum.VOBOBP.getValue()));
        String dependenciaSec = String.valueOf(registro.getCampos().get("DEPENDENCIA_SEC"));
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tipoTFiltrarM", tipoTFiltrarM);
        parametros.put("claseTS", claseTS);
        parametros.put(FrmSolicitudCdpControladorEnum.PROYECTOM_LOWER.getValue(), proyectoM);
        parametros.put("cmbDependenciaM", cmbDependenciaM);
        parametros.put("ano", ano);
        parametros.put("ridSolicitud", css);
        parametros.put("fechaInicialM", fechaInicialM);
        parametros.put("fechaFinalM", fechaFinalM);
        parametros.put(FrmSolicitudCdpControladorEnum.ARGNOVEDADES_LOWER.getValue(), argNovedades);
        parametros.put(FrmSolicitudCdpControladorEnum.CODIGO_LOWER.getValue(), retornarString(registro, codigoCons));
        parametros.put(FrmSolicitudCdpControladorEnum.DEPENDENCIA_LOWER.getValue(),
                        retornarString(registro, dependenciaCons));
        parametros.put("voBo", voBoBp);
        parametros.put("dependenciaSe", dependenciaSec);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador
                        .setNumForm(String.valueOf(GeneralCodigoFormaEnum.FRMSUBDNOVEDADESPROYECTOS_CONTROLADOR.getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirObjetosInversion()
    {
        tipoTFiltrarM = (String) registro.getCampos().get(tipoTCons);
        String claseTS = (String) registro.getCampos().get(claseTCons);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tipoTFiltrarM", tipoTFiltrarM);
        parametros.put("claseTS", claseTS);
        parametros.put(FrmSolicitudCdpControladorEnum.PROYECTOM_LOWER.getValue(), proyectoM);
        parametros.put(FrmSolicitudCdpControladorEnum.CMBDEPENDENCIAM_LOWER.getValue(), cmbDependenciaM);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);
        parametros.put("ridSolicitud", css);
        parametros.put("fechaInicialM", fechaInicialM);
        parametros.put("fechaFinalM", fechaFinalM);
        parametros.put("codigo", retornarString(registro, codigoCons));
        parametros.put(FrmSolicitudCdpControladorEnum.DEPENDENCIA_LOWER.getValue(),
                        retornarString(registro, dependenciaCons));

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.SUBOBJNOVEDADS_CONTROLADOR.getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Disminucion en la vista
     *
     */
    public void oprimirDisminucion()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try
        {
            String reporte = "001747rptdisminucionessolicitud";
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("comprobante", registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
            reemplazar.put("tipo", registro.getCampos().get("TIPOT").toString());

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_DEPENDENCIA", registro.getCampos().get("NOMBREDEPENDECIA").toString());

            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_SOLICITUDCDP",
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nombreDependenciaCons, null);
        registro.getCampos().put("CARGORESPONSABLE", null);
        registro.getCampos().put("RESPONSABLE_REVIS", "999999999999999999");
        registro.getCampos().put("SUCURSAL_REVIS", "999");

        registro.getCampos().put(nombreResponRevisCons, null);
        registro.getCampos().put(cargoResponRevisCons, null);
        registro.getCampos().put("NOMBRERESPONSABLE", null);

        registro.getCampos().put(dependenciaCons, registroAux.getCampos().get(codigoCons));
        registro.getCampos().put(nombreDependenciaCons,
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(), null);

        cargarListaResponsable();
        cargarListaResponsableRevis();

    }

    public void ejecutarCambiarCheck()
    {
        // <CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipotAfectar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("TIPOT_AFECTAR", registroAux.getCampos().get(tipoTCons));
        registro.getCampos().put("CLASET_AFECTAR", registroAux.getCampos().get(claseTCons));

        // registro.getCampos().put("DOCUMENTO_AFECTAR", "0");

        registro.getCampos().put("ESTADO", "A");

        cargarlistaClasetAfectar();
        cargarListaDocumentoAfectar();
    }

    public void seleccionarFilaDocumentoAfectar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        documentoAfectarAnt = SysmanFunciones
                        .nvl(registroAux.getCampos().get(FrmSolicitudCdpControladorEnum.CODIGO.getValue()), "0").toString();

        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ESTADO.getName()));
        // registro.getCampos().put(vigenciaCons,
        // registroAux.getCampos()
        // 7 .get(GeneralParameterEnum.VIGENCIA
        // .getName()));
        registro.getCampos().put("TIEMPOEJECUCION", registroAux.getCampos().get("TIEMPOEJECUCION"));
        registro.getCampos().put(FrmSolicitudCdpControladorEnum.OBJETO.getValue(),
                        registroAux.getCampos().get(FrmSolicitudCdpControladorEnum.OBJETO.getValue()));
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.RESPONSABLE.getName()));
        registro.getCampos().put("OBSERVACIONES", registroAux.getCampos().get("OBSERVACIONES"));

        registro.getCampos().put("VALOR_DISMINUIDO", registroAux.getCampos().get("VALOR_DISMINUIDO"));

        registro.getCampos().put("VALORSOLICITADO", registroAux.getCampos().get("VALORSOLICITADO"));

        registro.getCampos().put("VALORAPROBADO", registroAux.getCampos().get("VALORAPROBADO"));

        registro.getCampos().put("VALORTOTAL", registroAux.getCampos().get("VALORTOTAL"));

        registro.getCampos().put("DEPENDENCIA_AFECTAR", registroAux.getCampos().get("DEPENDENCIA"));
        registro.getCampos().put(FrmSolicitudCdpControladorEnum.DOCUMENTO_AFECTAR.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

        agregarRegistroNuevo(false);

        try
        {
            // Cesar Ochoa - Se comenta método por error de lógica de negocio

            ejbBanProyCinco.heredarSolicitudScd(compania, registro.getCampos().get("TIPOT_AFECTAR").toString(),
                            registro.getCampos().get("CLASET_AFECTAR").toString(),
                            Long.parseLong(registro.getCampos().get("DOCUMENTO_AFECTAR").toString()),
                            registro.getCampos().get("DEPENDENCIA").toString(), registro.getCampos().get("TIPOT").toString(),
                            registro.getCampos().get("CLASET").toString(),
                            Long.parseLong(registro.getCampos().get("CODIGO").toString()),
                            registro.getCampos().get("DEPENDENCIA").toString(),

                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException e)
        {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SystemException e)
        {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaResponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.RESPONSABLE.getName()));

        registro.getCampos().put(FrmSolicitudCdpControladorEnum.NOMBRERESPONSABLE.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));

        registro.getCampos().put(FrmSolicitudCdpControladorEnum.CARGORESPONSABLE.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CARGO.getName()));
    }

    public void seleccionarFilaResponsableRevis(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(nombreResponRevisCons, null);
        registro.getCampos().put(cargoResponRevisCons, null);

        registro.getCampos().put("RESPONSABLE_REVIS",
                        registroAux.getCampos().get(GeneralParameterEnum.RESPONSABLE.getName()));

        registro.getCampos().put("DEPENDENCIA_RESPONS_REVIS", registroAux.getCampos().get(dependenciaCons));

        registro.getCampos().put("SUCURSAL_REVIS",
                        registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

        registro.getCampos().put(nombreResponRevisCons,
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(cargoResponRevisCons,
                        registroAux.getCampos().get(GeneralParameterEnum.CARGO.getName()));

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pertinencia en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirPertinencia()
    {
        // <CODIGO_DESARROLLADO>

        String[] campos = { FrmSolicitudCdpControladorEnum.TIPOT.getValue(),
                        FrmSolicitudCdpControladorEnum.CLASET.getValue(), GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.DEPENDENCIA.getName(), "rowIdPertinencia" };
        Object[] valores = { registro.getCampos().get(FrmSolicitudCdpControladorEnum.TIPOT.getValue()).toString(),
                        registro.getCampos().get(FrmSolicitudCdpControladorEnum.CLASET.getValue()).toString(),
                        registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString(),
                        registro.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()).toString(), css };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.PERTINENCIA_NOVEDAD_CONTROLADOR.getCodigo()), modulo, campos,
                        valores); // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaDependenciaSec
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaSec(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA_SEC", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        nombreDependenciaSec = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        cargarListaResponsableSec();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaResponsableSec
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableSec(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESPONSABLE_SEC", registroAux.getCampos().get("RESPONSABLE"));
        registro.getCampos().put("SUCURSAL_SEC", registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
        nombreResponsableSec = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaModalidadSeleccion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadSeleccion(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MODALIDAD_SELECC",
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        nombreModalidad = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "").toString();
    }

    private void validarParametroControlar()
    {

        try
        {
            String controlar = ejbSysmanUtil.consultarParametro(compania, "CONTROLAR DEPENDENCIA EN BPPIM",
                            SessionUtil.getModulo(), new Date(), true);

            if ("SI".equals(controlar) && (SessionUtil.getNivelUsuario(modulo) != 9))
            {

                bloqueaContDepBppim = true;
            }
            else
            {

                bloqueaContDepBppim = false;

            }

        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void validarComando189Visible()
    {
        try
        {
            comando189Visible = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                            "IMPRIME CERTIFICADO DE INSCRIPCION EN EL BANCO DE PROYECTOS", SessionUtil.getModulo(), new Date(),
                            true), "NO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void asignarValor(String auxParametro)
    {
        String valor = auxParametro.substring(0, 3);

        switch (valor)
        {
        case "ECD":
            eCodigo = "Registro:";
            break;
        case "RES":
            eCodigo = "Registro:";
            bloqueaImprimir = false;
            break;
        case "SVP":
        case "SCD":
            eCodigo = FrmSolicitudCdpControladorEnum.SOLICITUD_LOWER.getValue();
            bloqueaAfectado = false;
            break;

        default:
            eCodigo = "Consecutivo:";

            bloqueaImprimir = false;
            break;
        }
    }

    private void validarNovedadesTecnicas()
    {
        try
        {
            novedadesTecnicasVisible = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                            "CONFIGURAR OBJETOS DE LA SOLICITUD CDP", SessionUtil.getModulo(), new Date(), true), "NO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo heredado del bean base
        validarParametroControlar();
        validarComando189Visible();
        validarNovedadesTecnicas();
        validarNiveles();
        eCodigo = FrmSolicitudCdpControladorEnum.SOLICITUD_LOWER.getValue();
        visibleModalidad = "800091594-4".equals(SessionUtil.getCompaniaIngreso().getNit());

        try
        {

            imagenLema = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "RUTA IMAGEN LEMA CAQUETA", modulo, new Date(), false),
                            " ");

            visibleDependencia = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE RELACIONAR DEPENDENCIA SECUNDARIA EN SOLICITUD DE CDP", modulo, new Date(), true));
            String controlarFecha = ejbSysmanUtil.consultarParametro(compania, "BLOQUEA FECHA DE SOLICITUD CDP",
                            SessionUtil.getModulo(), new Date(), true);

            if (controlarFecha.equals("SI"))
            {

                bloqueaFecha = true;

            }
            else
            {

                bloqueaFecha = false;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cargarRegistro()
    {
        precargarRegistro();
        if (accion.equals(ACCION_INSERTAR))
        {
            nombreResponsableSec = nombreDependenciaSec = nombreModalidad = null;
            if (FrmSolicitudCdpControladorEnum.C52020301.getValue().equals(SessionUtil.getMenuActual())
                            || "52020303".equals(SessionUtil.getMenuActual())
                            || FrmSolicitudCdpControladorEnum.C52020302.getValue().equals(SessionUtil.getMenuActual()))
            {
                registro.getCampos().put(tipoTCons, "SCD");
            }

        }
        else
        {
            nombreResponsableSec = SysmanFunciones.nvl(registro.getCampos().get("NOMBRE_RESPON_SEC"), "").toString();
            nombreDependenciaSec = SysmanFunciones.nvl(registro.getCampos().get("NOMBREDEPENDECIASEC"), "").toString();
            nombreModalidad = SysmanFunciones.nvl(registro.getCampos().get("DESCRIPCION"), "").toString();

        }
        if (css != null)
        {

            validadAbrirFormulario();
            bloquearControlesTipoP();
            bloqueVOBO();
            voBo = (boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.VOBOBP.getValue());
            impreso = (boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.IMPRESO.getValue());

            List<Registro> listaTotales = listarTotales();

            if ((listaTotales == null) || listaTotales.isEmpty())
            {
                registro.getCampos().put(FrmSolicitudCdpControladorEnum.VALORSOLICITADO.getValue(), "0");
                registro.getCampos().put(FrmSolicitudCdpControladorEnum.VALORAPROBADO.getValue(), "0");
                registro.getCampos().put("VALORTOTAL", "0");

            }
            else
            {
                registro.getCampos().put(FrmSolicitudCdpControladorEnum.VALORSOLICITADO.getValue(),
                                listaTotales.get(0).getCampos().get(FrmSolicitudCdpControladorEnum.VALORSOLICITADO.getValue()));
                registro.getCampos().put(FrmSolicitudCdpControladorEnum.VALORAPROBADO.getValue(),
                                listaTotales.get(0).getCampos().get(FrmSolicitudCdpControladorEnum.VALORAPROBADO.getValue()));
                registro.getCampos().put("VALORTOTAL",
                                listaTotales.get(0).getCampos().get(FrmSolicitudCdpControladorEnum.VALORAPROBADO.getValue()));

            }
        }

        else
        {

            if ("520206".equals(SessionUtil.getMenuActual()))
            {

                if ("CDP".equals(tipoTmodal))
                {

                    tituloFormulario = "CDP - CERTIFICADO DE VIABILIDAD";

                }
                else
                {
                    tituloFormulario = "MOD - MODIFICACI�N";
                }
            }
            else if (("52020303").equals(SessionUtil.getMenuActual()))
            {

                tituloFormulario = "SOLICITUD DE PERTINENCIA";
            }

            else
            {
                tituloFormulario = "SCD - SOLICITUD DE CERTIFICADO DE DISPONIBILIDAD PRESUPUESTAL";
            }

            ejecutarAccionI();
        }
        if ("52020301".equals(SessionUtil.getMenuActual()) || "52020303".equals(SessionUtil.getMenuActual())
                        || "520206".equals(SessionUtil.getMenuActual()))
        {

            try
            {

                Double valorSolicitado;

                Map<String, Object> paramDisminuido = new HashMap<>();
                paramDisminuido.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                paramDisminuido.put(GeneralParameterEnum.CLASE.getName(), registro.getCampos().get(claseTCons));
                paramDisminuido.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(tipoTCons));
                paramDisminuido.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(codigoCons));

                rsDisminuido = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmSolicitudCdpControladorUrlEnum.URL29289.getValue())
                                                                .getUrl(),
                                                paramDisminuido));

                if (rsDisminuido != null)
                {

                    valorDisminucion = Double.parseDouble(
                                    SysmanFunciones.nvl(rsDisminuido.getCampos().get("VALOR_DISMINUIDO"), "0").toString());
                }

                valorSolicitado = Double
                                .parseDouble(SysmanFunciones.nvl(registro.getCampos().get("VALORSOLICITADO"), "0").toString());
                Double total = (valorSolicitado - valorDisminucion);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL63413.getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put("VALOR_DISMINUIDO", valorDisminucion);
                fields.put("VALORTOTAL", total);
                fields.put(tipoTCons, registro.getCampos().get(tipoTCons));
                fields.put(claseTCons, registro.getCampos().get(claseTCons));
                fields.put(dependenciaCons, registro.getCampos().get(dependenciaCons));
                fields.put(codigoCons, registro.getCampos().get(codigoCons));
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);

                registro.getCampos().put("VALOR_DISMINUIDO", valorDisminucion);

                registro.getCampos().put("VALORTOTAL", total);

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        try
        {
            Fecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get(GeneralParameterEnum.DATE_CREATED.getName()));

            cargarListaTipotAfectar();

            modificaConsecutivo = SysmanFunciones.nvl(ejbSymanUtl.consultarParametro(compania,
                            "PERMITE MODIFICAR CONSECUTIVOS SOLICITUD CDP BP", modulo, new Date(), true), "NO").toString();

            modificaCons = modificaConsecutivo.equals("SI") ? true : false;

            if (!modificaCons)
            {

                bloqCodigo = !"520206".equals(SessionUtil.getMenuActual());
            }
            else
            {
                bloqCodigo = false;
            }

        }
        catch (ParseException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private List<Registro> listarTotales()
    {

        List<Registro> lista = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoTCons, SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "SCD"));
        param.put(claseTCons, SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B"));
        param.put(codigoCons, registro.getCampos().get(codigoCons));
        param.put(dependenciaCons, retornarString(registro, dependenciaCons));

        try
        {
            lista = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL87370
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return lista;
    }

    private void ejecutarAccionI()
    {
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), new Date());
        registro.getCampos().put(vigenciaCons, vigenciaPeriodo);
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "V");
    }

    /**
     * Determina si se deben bloquear algunos controles dependiendo del valor del atributo tipoT
     */
    private void bloquearControlesTipoP()
    {
        String permiteModificarCdp;
        voBo = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.VOBOBP.getValue());
        impreso = (Boolean) registro.getCampos().get(FrmSolicitudCdpControladorEnum.IMPRESO.getValue());

        try
        {
            permiteModificarCdp = ejbSysmanUtil.consultarParametro(compania,
                            "CONTROLAR MODIFICACION DE SOLICITUDES CDP BPPIM", SessionUtil.getModulo(), new Date(), true);

            if ("SCD".equals(SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "SCD"))
                            && (("SI".equals(permiteModificarCdp) && (voBo)) || (impreso)))
            {

                bloqueaNecesidadN = true;
                bloqueaFechaVoBo = true;
                bloqueaHoraVoBo = true;
                bloqueaProcesoFinanciero = true;
                bloqueaConComprobantePPTAL = true;
                bloqueaAfectado = true;
                bloqueaContDepBppim = true;
            }
            else
            {
                bloqueaNecesidadN = false;
                bloqueaFechaVoBo = false;
                bloqueaHoraVoBo = false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public boolean insertarAntes()
    {
        try
        {

            // <CODIGO_DESARROLLADO>
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

            registro.getCampos().put("FECHA_VALIDEZ", SysmanFunciones.sumarRestarDiasFecha(new Date(), 60));

            String parametro = ejbSysmanUtil.consultarParametro(compania, "CONSECUTIVO INICIAL DE SOLICITUD CDP",
                            SessionUtil.getModulo(), new Date(), true);

            registro.getCampos().put(tipoTCons,
                            "520206".equals(SessionUtil.getMenuActual()) ? tipoNovedad : retornarString(registro, tipoTCons));

            if (parametro == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2406"));
                return false;
            }

            agregarCamposRegistro(parametro);

        }
        catch (SystemException e)
        {
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

        registro.getCampos().remove("NOMBREDEPENDENCIA");
        registro.getCampos().remove(FrmSolicitudCdpControladorEnum.NOMBRERESPONSABLE.getValue());

        registro.getCampos().remove(nombreResponRevisCons);
        registro.getCampos().remove("NOMBRECARGORESPONSABLE");
        // programarDesdenovedades();

        return true;

        // </CODIGO_DESARROLLADO>
    }

    private void agregarCamposRegistro(String parametro)
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(dependenciaCons, registro.getCampos().get(dependenciaCons));
        param.put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registro.getCampos().get(GeneralParameterEnum.RESPONSABLE.getName()));
        param.put(GeneralParameterEnum.TIPO.getName(),
                        SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "SCD").toString());
        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL28616
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if ((rs != null) && (rs.getCampos().get(claseTCons) != null))
            {

                registro.getCampos().put(claseTCons, rs.getCampos().get(claseTCons));

            }
            else
            {
                registro.getCampos().put(claseTCons, "B");
            }

            rs = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmSolicitudCdpControladorUrlEnum.URL96498
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            rs.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

            if (modificaConsecutivo.equals("NO")
                            || "".equals(SysmanFunciones.nvl(registro.getCampos().get("CODIGO"), "")))
            {

                String condicion = SysmanFunciones.concatenar("COMPANIA = ''", compania, "'' AND TIPOT= ''",
                                "520206".equals(SessionUtil.getMenuActual()) ? tipoNovedad
                                                : retornarString(registro, tipoTCons),
                                "'' AND CLASET = ''", retornarString(registro, claseTCons), "''",
                                " AND VIGENCIA = ''",retornarString(registro, "VIGENCIA"),"''");

                long consecutivo = ejbSymanUtl.generarConsecutivoConValorInicial("BPNOVEDADPROYECTO", condicion,
                                "CODIGO", parametro);

                if ("".equals(SysmanFunciones.nvl(registro.getCampos().get("CODIGO"), "")))
                {
                    registro.getCampos().put(codigoCons, consecutivo);
                }
                else
                {
                    registro.getCampos().put(codigoCons, registro.getCampos().get("CODIGO"));
                }
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public boolean insertarDespues()
    {
        afectarDocumento();
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(nombreCargoResponsableCons);
        registro.getCampos().remove(nombreDependenciaCons);
        registro.getCampos().remove("NOMBREDEPENDECIASEC");
        registro.getCampos().remove("NOMBRE_RESPON_SEC");
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

        registro.getCampos().remove(nombreResponRevisCons);

        registro.getCampos().remove(FrmSolicitudCdpControladorEnum.NOMBRERESPONSABLE.getValue());

        // programarDesdenovedades();

        registro.getCampos().remove("SIN_SEGUNDA_FIRMA");
        // registro.getCampos().remove("MODIFICACION");

        return true;

    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        afectarDocumento();
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes()
    {
        try
        {
            ejbBanProyCinco.consultadDcumentoAfectar(compania, registro.getCampos().get("TIPOT").toString(),
                            registro.getCampos().get("CLASET").toString(),
                            Long.parseLong(registro.getCampos().get("CODIGO").toString()),
                            registro.getCampos().get("DEPENDENCIA").toString(), SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e)
        {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

            return false;

        }

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaSubpreguntascdp
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaSubpreguntascdp()
    {
        // try {
        // listaSubpreguntascdp = service.getListado(
        // ConectorPool.ESQUEMA_SYSMAN, "SELECT " +
        // " PREGUNTAS_PROYECTOS.ROWID, " +
        // " PREGUNTAS_PROYECTOS.COMPANIA, " +
        // " PREGUNTAS_PROYECTOS.PROYECTO, " +
        // " PREGUNTAS_PROYECTOS.PREGUNTA," +
        // " PREGUNTAS.NOMBRE, " +
        // " PREGUNTAS_PROYECTOS.RESPUESTA " +
        // " FROM " +
        // " PREGUNTAS_PROYECTOS INNER JOIN PREGUNTAS ON"
        // +
        // " PREGUNTAS_PROYECTOS.PREGUNTA=PREGUNTAS.CODIGO WHERE
        // COMPANIA='"
        // + compania + "' AND PROYECTO='" + registro
        // .getCampos().get("CODIGO")
        // + "'",
        // CacheUtil.getLlave(ConectorPool.ESQUEMA_SYSMAN,
        //// "PREGUNTAS_PROYECTOS"));
        // }
        // catch (NamingException | SQLException e) {
        // logger.error(e.getMessage(), e);
        // JsfUtil.agregarMensajeError(e.getMessage());
        // }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPON", registro.getCampos().get(tipoTCons));
        param.put("CLASEN", registro.getCampos().get(claseTCons));
        param.put("DEPENDENCIAN", registro.getCampos().get(dependenciaCons));
        param.put(FrmproyectosControladorEnum.CODIGONOVEDAD.getValue(), registro.getCampos().get(codigoCons));

        try
        {
            listaSubpreguntascdp = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.PREGUNTAS_NOVEDAD
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.PREGUNTAS_NOVEDAD.getTable()));
        }
        catch (SystemException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo de insercion del formulario Subpreguntascdp
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
    public void agregarRegistroSubSubpreguntascdp()
    {
        try
        {

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSub.getCampos().put("TIPONOVEDAD", registro.getCampos().get(tipoTCons));
            registroSub.getCampos().put("CLASENOVEDAD", registro.getCampos().get(claseTCons));
            registroSub.getCampos().put(GeneralParameterEnum.NOVEDAD.getName(), registro.getCampos().get(codigoCons));
            registroSub.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            registro.getCampos().get(dependenciaCons));

            registroSub.getCampos().put("PREGUNTA", registroSub.getCampos().get("PREGUNTA"));

            registroSub.getCampos().put("RESPUESTA", registroSub.getCampos().get("RESPUESTA"));
            registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.PREGUNTAS_NOVEDAD.getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
            cargarListaSubpreguntascdp();
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subpreguntascdp
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubpreguntascdp(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {

            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.PREGUNTAS_NOVEDAD.getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally
        {
            cargarListaSubpreguntascdp();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subpreguntascdp
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubpreguntascdp(Registro reg)
    {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.PREGUNTAS_NOVEDAD.getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubpreguntascdp();
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Subpreguntascdp
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cancelarEdicionSubpreguntascdp()
    {
        cargarListaSubpreguntascdp();
    }

    public Boolean getBloqueaNecesidadN()
    {
        return bloqueaNecesidadN;
    }

    public void setBloqueaNecesidadN(Boolean bloqueaNecesidadN)
    {
        this.bloqueaNecesidadN = bloqueaNecesidadN;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public Boolean getNovedadesTecnicasVisible()
    {
        return novedadesTecnicasVisible;
    }

    public void setNovedadesTecnicasVisible(Boolean novedadesTecnicasVisible)
    {
        this.novedadesTecnicasVisible = novedadesTecnicasVisible;
    }

    public String getNombreTM()
    {
        return nombreTM;
    }

    public void setNombreTM(String nombreTM)
    {
        this.nombreTM = nombreTM;
    }

    public String getNombreproyectoM()
    {
        return nombreproyectoM;
    }

    public void setNombreproyectoM(String nombreproyectoM)
    {
        this.nombreproyectoM = nombreproyectoM;
    }

    public String getFechaInicialM()
    {
        return fechaInicialM;
    }

    public void setFechaInicialM(String fechaInicialM)
    {
        this.fechaInicialM = fechaInicialM;
    }

    public String getFechaFinalM()
    {
        return fechaFinalM;
    }

    public void setFechaFinalM(String fechaFinalM)
    {
        this.fechaFinalM = fechaFinalM;
    }

    public String getCmbDependenciaM()
    {
        return cmbDependenciaM;
    }

    public void setCmbDependenciaM(String cmbDependenciaM)
    {
        this.cmbDependenciaM = cmbDependenciaM;
    }

    public String getEstadoFiltrarM()
    {
        return estadoFiltrarM;
    }

    public void setEstadoFiltrarM(String estadoFiltrarM)
    {
        this.estadoFiltrarM = estadoFiltrarM;
    }

    public String getTipoTFiltrarM()
    {
        return tipoTFiltrarM;
    }

    public void setTipoTFiltrarM(String tipoTFiltrarM)
    {
        this.tipoTFiltrarM = tipoTFiltrarM;
    }

    public String getProyectoM()
    {
        return proyectoM;
    }

    public void setProyectoM(String proyectoM)
    {
        this.proyectoM = proyectoM;
    }

    public String getVigenciaPeriodo()
    {
        return vigenciaPeriodo;
    }

    public void setVigenciaPeriodo(String vigenciaPeriodo)
    {
        this.vigenciaPeriodo = vigenciaPeriodo;
    }

    public Boolean getBloqueaHoraVoBo()
    {
        return bloqueaHoraVoBo;
    }

    public void setBloqueaHoraVoBo(Boolean bloqueaHoraVoBo)
    {
        this.bloqueaHoraVoBo = bloqueaHoraVoBo;
    }

    public Boolean getBloqueaFechaVoBo()
    {
        return bloqueaFechaVoBo;
    }

    public void setBloqueaFechaVoBo(Boolean bloqueaFechaVoBo)
    {
        this.bloqueaFechaVoBo = bloqueaFechaVoBo;
    }

    public Boolean getComando189Visible()
    {
        return comando189Visible;
    }

    public void setComando189Visible(Boolean comando189Visible)
    {
        this.comando189Visible = comando189Visible;
    }

    public Boolean getBloqueaAfectado()
    {
        return bloqueaAfectado;
    }

    public void setBloqueaAfectado(Boolean bloqueaAfectado)
    {
        this.bloqueaAfectado = bloqueaAfectado;
    }

    public Boolean getBloqueaConComprobantePPTAL()
    {
        return bloqueaConComprobantePPTAL;
    }

    public void setBloqueaConComprobantePPTAL(Boolean bloqueaConComprobantePPTAL)
    {
        this.bloqueaConComprobantePPTAL = bloqueaConComprobantePPTAL;
    }

    public Boolean getBloqueaVoBoBP()
    {
        return bloqueaVoBoBP;
    }

    public void setBloqueaVoBoBP(Boolean bloqueaVoBoBP)
    {
        this.bloqueaVoBoBP = bloqueaVoBoBP;
    }

    public Boolean getBloqueaProcesoFinanciero()
    {
        return bloqueaProcesoFinanciero;
    }

    public void setBloqueaProcesoFinanciero(Boolean bloqueaProcesoFinanciero)
    {
        this.bloqueaProcesoFinanciero = bloqueaProcesoFinanciero;
    }

    public Boolean getBloqueaVoBo()
    {
        return bloqueaVoBo;
    }

    public void setBloqueaVoBo(Boolean bloqueaVoBo)
    {
        this.bloqueaVoBo = bloqueaVoBo;
    }

    public Boolean getImpreso()
    {
        return impreso;
    }

    public void setImpreso(Boolean impreso)
    {
        this.impreso = impreso;
    }

    public Boolean getBloqueaImprimir()
    {
        return bloqueaImprimir;
    }

    public void setBloqueaImprimir(Boolean bloqueaImprimir)
    {
        this.bloqueaImprimir = bloqueaImprimir;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getArgNovedades()
    {
        return argNovedades;
    }

    public void setArgNovedades(String argNovedades)
    {
        this.argNovedades = argNovedades;
    }

    public List<Registro> getListaClasetAfectar()
    {
        return listaClasetAfectar;
    }

    public void setListaClasetAfectar(List<Registro> listaClasetAfectar)
    {
        this.listaClasetAfectar = listaClasetAfectar;
    }

    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public List<Registro> getListaComponenteNom()
    {
        return listaComponenteNom;
    }

    public void setListaComponenteNom(List<Registro> listaComponenteNom)
    {
        this.listaComponenteNom = listaComponenteNom;
    }

    public List<Registro> getListaActividadNom()
    {
        return listaActividadNom;
    }

    public void setListaActividadNom(List<Registro> listaActividadNom)
    {
        this.listaActividadNom = listaActividadNom;
    }

    public List<Registro> getListaIdMetaProductoNom()
    {
        return listaIdMetaProductoNom;
    }

    public void setListaIdMetaProductoNom(List<Registro> listaIdMetaProductoNom)
    {
        this.listaIdMetaProductoNom = listaIdMetaProductoNom;
    }

    public List<Registro> getListaNovedaestecnicas()
    {
        return listaNovedaestecnicas;
    }

    public void setListaNovedaestecnicas(List<Registro> listaNovedaestecnicas)
    {
        this.listaNovedaestecnicas = listaNovedaestecnicas;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaTipotAfectar()
    {
        return listaTipotAfectar;
    }

    public void setListaTipotAfectar(RegistroDataModelImpl listaTipotAfectar)
    {
        this.listaTipotAfectar = listaTipotAfectar;
    }

    public RegistroDataModelImpl getListaDocumentoAfectar()
    {
        return listaDocumentoAfectar;
    }

    public void setListaDocumentoAfectar(RegistroDataModelImpl listaDocumentoAfectar)
    {
        this.listaDocumentoAfectar = listaDocumentoAfectar;
    }

    public RegistroDataModelImpl getListaResponsable()
    {
        return listaResponsable;
    }

    public void setListaResponsable(RegistroDataModelImpl listaResponsable)
    {
        this.listaResponsable = listaResponsable;
    }

    public RegistroDataModelImpl getListaResponsableRevis()
    {
        return listaResponsableRevis;
    }

    public void setListaResponsableRevis(RegistroDataModelImpl listaResponsableRevis)
    {
        this.listaResponsableRevis = listaResponsableRevis;
    }

    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public Boolean getVoBo()
    {
        return voBo;
    }

    public void setVoBo(Boolean voBo)
    {
        this.voBo = voBo;
    }

    public String getVigencia()
    {
        return vigencia;
    }

    public void setVigencia(String vigencia)
    {
        this.vigencia = vigencia;
    }

    public String getTituloFormulario()
    {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario)
    {
        this.tituloFormulario = tituloFormulario;
    }

    public String getTipoNovedadAux()
    {
        return tipoNovedadAux;
    }

    public void setTipoNovedadAux(String tipoNovedadAux)
    {
        this.tipoNovedadAux = tipoNovedadAux;
    }

    public String geteCodigo()
    {
        return eCodigo;
    }

    public void seteCodigo(String eCodigo)
    {
        this.eCodigo = eCodigo;
    }

    public boolean isVarVolver()
    {
        return varVolver;
    }

    public void setVarVolver(boolean varVolver)
    {
        this.varVolver = varVolver;
    }

    public boolean isBloqueaContDepBppim()
    {
        return bloqueaContDepBppim;
    }

    public void setBloqueaContDepBppim(boolean bloqueaContDepBppim)
    {
        this.bloqueaContDepBppim = bloqueaContDepBppim;
    }

    public boolean isVisibleAfectar()
    {
        return visibleAfectar;
    }

    public void setVisibleAfectar(boolean visibleAfectar)
    {
        this.visibleAfectar = visibleAfectar;
    }

    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "" : reg.getCampos().get(campo).toString();

    }

    // ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA :(

    private void validadAbrirFormulario()
    {
        try
        {

            if (SessionUtil.getMenuActual().equals(FrmSolicitudCdpControladorEnum.C520206.getValue()))
            {

                String nomTipoNovedad = ejbBanProyCinco.getNombreTipoNovedad(compania,
                                SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "MOD").toString(),
                                SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B").toString());

                tituloFormulario = SysmanFunciones.concatenar(retornarString(registro, tipoTCons), " ", nomTipoNovedad);

                /*-Controlar Modificaciones si la novedad no es de banco de proyectos*/

                vigencia = retornarString(registro, vigenciaCons);

                /* Verifica que la variable no sea nula */

                asignarValor(retornarString(registro, tipoTCons));

                configurarNovedad();
            }

            else if (SessionUtil.getMenuActual().equals(FrmSolicitudCdpControladorEnum.C52020401.getValue()))
            {

                String nomTipoNovedad = ejbBanProyCinco.getNombreTipoNovedad(compania,
                                SysmanFunciones.nvl(registro.getCampos().get(tipoTCons), "SCD").toString(),
                                SysmanFunciones.nvl(registro.getCampos().get(claseTCons), "B").toString());

                tituloFormulario = SysmanFunciones.concatenar(retornarString(registro, tipoTCons), " ", nomTipoNovedad);

                /*-Controlar Modificaciones si la novedad no es de banco de proyectos*/

                vigencia = retornarString(registro, vigenciaCons);

                /* Vrifica que la variable no sea nula */

                asignarValor(retornarString(registro, tipoTCons));

                configurarNovedad();

            }

        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmSolicitudCdpControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Map<String, Object> validarNiveles(List<Registro> rs)
    {

        Map<String, Object> niveles = new HashMap<>();
        int contador = 0;
        if (Boolean.parseBoolean(rs.get(contador).getCampos().get("META_PRODUC").toString()))
        {

            niveles.put("META_PRODUC",
                            rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()));
        }
        contador = contador + 1;
        if (Boolean.parseBoolean(
                        rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.META_RESUL.getValue()).toString()))
        {
            niveles.put(FrmSolicitudCdpControladorEnum.META_RESUL.getValue(),
                            rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()));
        }
        contador = contador + 1;

        int digitos = Integer.parseInt(
                        rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()).toString());
        int metaResulAux = Integer.parseInt(
                        SysmanFunciones.nvl(niveles.get(FrmSolicitudCdpControladorEnum.META_RESUL.getValue()), "0").toString());
        if (digitos < metaResulAux)
        {
            niveles.put(FrmSolicitudCdpControladorEnum.PROGRAMA.getValue(),
                            rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()));
        }
        contador = contador + 1;

        digitos = Integer.parseInt(
                        rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()).toString());
        int programaAux = Integer.parseInt(
                        SysmanFunciones.nvl(niveles.get(FrmSolicitudCdpControladorEnum.PROGRAMA.getValue()), "0").toString());

        if (digitos < programaAux)
        {
            niveles.put(FrmSolicitudCdpControladorEnum.PROGRAMA.getValue(),
                            rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()));
        }
        contador = contador + 1;

        digitos = Integer.parseInt(
                        rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()).toString());
        int sectorAux = Integer.parseInt(
                        SysmanFunciones.nvl(niveles.get(FrmSolicitudCdpControladorEnum.PROGRAMA.getValue()), "0").toString());
        if (digitos < sectorAux)
        {
            niveles.put("EJE", rs.get(contador).getCampos().get(FrmSolicitudCdpControladorEnum.DIGITOS.getValue()));
        }

        return niveles;
    }

    private void afectarDocumento()
    {
        String claset = retornarString(registro, claseTCons);
        String tipo = retornarString(registro, tipoTCons);
        String codAux = retornarString(registro, codigoCons);
        String docAfectarAux = SysmanFunciones
                        .nvl(retornarString(registro, FrmSolicitudCdpControladorEnum.DOCUMENTO_AFECTAR.getValue()), "")
                        .toString();
        String dependeAux = retornarString(registro, dependenciaCons);

        try
        {
            if (!docAfectarAux.isEmpty() && "MOD".equals(tipoNovedad)
                            && "520206".equals(SessionUtil.getMenuActual()))
            {
                // ejbBanProyCinco.afectarNovedad(compania, claset, tipo, dependeAux,
                // Long.parseLong(SysmanFunciones.nvl(codAux, "0").toString()),
                // Long.parseLong(SysmanFunciones.nvl(registro.getCampos().get("DOCUMENTO_AFECTAR"), "").toString()),
                // Long.parseLong(docAfectarAux), SessionUtil.getUser().getCodigo());
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL0010.getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put(tipoTCons, tipo);
                fields.put(claseTCons, claset);
                fields.put(dependenciaCons, dependeAux);
                fields.put(codigoCons, docAfectarAux);
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            // JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void programarDesdenovedades()
    {
        String programar;
        boolean auxVobo = (boolean) registro.getCampos().get("VOBOBP");

        try
        {

            if (auxVobo)
            {

                programar = ejbSysmanUtil.consultarParametro(compania, "PROGRAMAR DESDE LAS NOVEDADES",
                                SessionUtil.getModulo(), new Date(), true);

                if ("SI".equals(programar))
                {
                    // Se debe arregalre la funcion act_programacion.

                    Map<String, Object> paramProyecto = new TreeMap<>();

                    paramProyecto.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                    paramProyecto.put("NOVEDAD", registro.getCampos().get("CODIGO").toString());

                    Registro rsp = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(FrmSolicitudCdpControladorUrlEnum.URL0008

                                                    .getValue())
                                    .getUrl(), paramProyecto));

                    String codigoProyecto = rsp.getCampos().get("PROYECTO").toString();

                    ejbBancoProDos.actualizarProgramacion(compania, registro.getCampos().get("TIPOT").toString(),
                                    registro.getCampos().get("CLASET").toString(),
                                    Long.parseLong(registro.getCampos().get("CODIGO").toString()),
                                    registro.getCampos().get("DEPENDENCIA").toString(),
                                    Integer.parseInt(registro.getCampos().get("VIGENCIA").toString()), codigoProyecto, 1, "",
                                    SessionUtil.getUser().getCodigo());

                    /**
                     * ejbBanProyDos.actualizarProgramacion(compania, SysmanFunciones.nvl( registro.getCampos() .get(tipoTCons), "SCD").toString(), registro.getCampos().get(claseTCons) .toString(),
                     * Long.parseLong(SysmanFunciones .nvl(registro.getCampos() .get(GeneralParameterEnum.CODIGO .getName()), "0") .toString()), retornarString(registro, dependenciaCons), Integer.parseInt(SysmanFunciones
                     * .nvl(vigencia, "0") .toString()), "", 0, "", SessionUtil.getUser().getCodigo());
                     */

                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validarNiveles()
    {
        try
        {

            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

            if ("ALCALDIA DE CHIA".equals(nombreCompania) || "ALCALDIA DE CHIA, CUNDINAMARCA".equals(nombreCompania))
            {
                consultaReportInscripBp = "800051inscripcionBpUno";

                Map<String, Object> paramNiveles = new TreeMap<>();
                paramNiveles.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                paramNiveles.put(vigenciaCons, registro.getCampos().get(vigenciaCons));

                List<Registro> rs = RegistroConverter
                                .toListRegistro(requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmSolicitudCdpControladorUrlEnum.URL34238.getValue())
                                                                .getUrl(),
                                                paramNiveles));

                if (!rs.isEmpty())
                {
                    Map<String, Object> niveles = validarNiveles(rs);

                    eje = SysmanFunciones.nvl(niveles.get("EJE"), "0").toString();
                    sector = SysmanFunciones.nvl(niveles.get("SECTOR"), "0").toString();

                    programa = SysmanFunciones.nvl(niveles.get(FrmSolicitudCdpControladorEnum.PROGRAMA.getValue()), "0")
                                    .toString();
                    metaResul = SysmanFunciones
                                    .nvl(niveles.get(FrmSolicitudCdpControladorEnum.META_RESUL.getValue()), "0").toString();
                }
            }
            else
            {
                consultaReportInscripBp = "800052inscripcionBpDos";

                eje = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS EJE", SessionUtil.getModulo(),
                                new Date(), true);
                sector = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS SECTOR", SessionUtil.getModulo(),
                                new Date(), true);
                programa = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS PROGRAMA",
                                SessionUtil.getModulo(), new Date(), true);
                meta = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS META-PRODUCTO",
                                SessionUtil.getModulo(), new Date(), true);
                subprograma = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS SUBPROGRAMA",
                                SessionUtil.getModulo(), new Date(), true);
                metaResul = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS META-RESULTADO",
                                SessionUtil.getModulo(), new Date(), true);
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getNombreDependenciaSec()
    {
        return nombreDependenciaSec;
    }

    public void setNombreDependenciaSec(String nombreDependenciaSec)
    {
        this.nombreDependenciaSec = nombreDependenciaSec;
    }

    public String getNombreResponsableSec()
    {
        return nombreResponsableSec;
    }

    public void setNombreResponsableSec(String nombreResponsableSec)
    {
        this.nombreResponsableSec = nombreResponsableSec;
    }

    public RegistroDataModelImpl getListaDependenciaSec()
    {
        return listaDependenciaSec;
    }

    public void setListaDependenciaSec(RegistroDataModelImpl listaDependenciaSec)
    {
        this.listaDependenciaSec = listaDependenciaSec;
    }

    public RegistroDataModelImpl getListaResponsableSec()
    {
        return listaResponsableSec;
    }

    public void setListaResponsableSec(RegistroDataModelImpl listaResponsableSec)
    {
        this.listaResponsableSec = listaResponsableSec;
    }

    public boolean isVisibleDependencia()
    {
        return visibleDependencia;
    }

    public void setVisibleDependencia(boolean visibleDependencia)
    {
        this.visibleDependencia = visibleDependencia;
    }

    public RegistroDataModelImpl getListaModalidadSeleccion()
    {
        return listaModalidadSeleccion;
    }

    public void setListaModalidadSeleccion(RegistroDataModelImpl listaModalidadSeleccion)
    {
        this.listaModalidadSeleccion = listaModalidadSeleccion;
    }

    public String getNombreModalidad()
    {
        return nombreModalidad;
    }

    public void setNombreModalidad(String nombreModalidad)
    {
        this.nombreModalidad = nombreModalidad;
    }

    public boolean isVisibleModalidad()
    {
        return visibleModalidad;
    }

    public void setVisibleModalidad(boolean visibleModalidad)
    {
        this.visibleModalidad = visibleModalidad;
    }

    /**
     * @return the activarVoBo
     */
    public Boolean getActivarVoBo()
    {
        return activarVoBo;
    }

    /**
     * @param activarVoBo
     * the activarVoBo to set
     */
    public void setActivarVoBo(Boolean activarVoBo)
    {
        this.activarVoBo = activarVoBo;
    }

    /**
     * @return the tipotconst
     */
    public String getTipotconst()
    {
        return tipotconst;
    }

    /**
     * @return the tipoTmodal
     */
    public String getTipoTmodal()
    {
        return tipoTmodal;
    }

    /**
     * @param tipoTmodal
     * the tipoTmodal to set
     */
    public void setTipoTmodal(String tipoTmodal)
    {
        this.tipoTmodal = tipoTmodal;
    }

    public String getReporte1()
    {
        return reporte1;
    }

    public void setReporte1(String reporte1)
    {
        this.reporte1 = reporte1;
    }

    public String getReporte2()
    {
        return reporte2;
    }

    public void setReporte2(String reporte2)
    {
        this.reporte2 = reporte2;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * @param createdBy
     * the createdBy to set
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * @return the bloqCodigo
     */
    public boolean isBloqCodigo()
    {
        return bloqCodigo;
    }

    /**
     * @param bloqCodigo
     * the bloqCodigo to set
     */
    public void setBloqCodigo(boolean bloqCodigo)
    {
        this.bloqCodigo = bloqCodigo;
    }

    public boolean isSinSegundaFir()
    {
        return sinSegundaFir;
    }

    public void setSinSegundaFir(boolean sinSegundaFir)
    {
        this.sinSegundaFir = sinSegundaFir;
    }

    public boolean isCamposSegResp()
    {
        return camposSegResp;
    }

    public void setCamposSegResp(boolean camposSegResp)
    {
        this.camposSegResp = camposSegResp;
    }

    public String getImagenLema()
    {
        return imagenLema;
    }

    public void setImagenLema(String imagenLema)
    {
        this.imagenLema = imagenLema;
    }

    public Boolean getModificacion()
    {
        return modificacion;
    }

    public void setModificacion(Boolean modificacion)
    {
        this.modificacion = modificacion;
    }

    /**
     * @return the titulo
     */
    public String getTitulo()
    {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public boolean isBloqueaFecha()
    {
        return bloqueaFecha;
    }

    public void setBloqueaFecha(boolean bloqueaFecha)
    {
        this.bloqueaFecha = bloqueaFecha;
    }

    /**
     * @return the cargarInfoAdicional
     */
    public boolean isCargarInfoAdicional()
    {
        return cargarInfoAdicional;
    }

    /**
     * @param cargarInfoAdicional
     * the cargarInfoAdicional to set
     */
    public void setCargarInfoAdicional(boolean cargarInfoAdicional)
    {
        this.cargarInfoAdicional = cargarInfoAdicional;
    }

    /**
     * @return the nit
     */
    public String getNit()
    {
        return nit;
    }

    /**
     * @param nit
     * the nit to set
     */
    public void setNit(String nit)
    {
        this.nit = nit;
    }

    /**
     * @return the cargarAdicional
     */
    public boolean isCargarAdicional()
    {
        return cargarAdicional;
    }

    /**
     * @param cargarAdicional
     * the cargarAdicional to set
     */
    public void setCargarAdicional(boolean cargarAdicional)
    {
        this.cargarAdicional = cargarAdicional;
    }

    /**
     * @return the listaSubpreguntascdp
     */
    public List<Registro> getListaSubpreguntascdp()
    {
        return listaSubpreguntascdp;
    }

    /**
     * @param listaSubpreguntascdp
     * the listaSubpreguntascdp to set
     */
    public void setListaSubpreguntascdp(List<Registro> listaSubpreguntascdp)
    {
        this.listaSubpreguntascdp = listaSubpreguntascdp;
    }

    /**
     * @return the listaPregunta
     */
    public List<Registro> getListaPregunta()
    {
        return listaPregunta;
    }

    /**
     * @param listaPregunta
     * the listaPregunta to set
     */
    public void setListaPregunta(List<Registro> listaPregunta)
    {
        this.listaPregunta = listaPregunta;
    }

    /**
     * @return the fecha
     */
    public String getFecha()
    {
        return Fecha;
    }

    /**
     * @param fecha
     * the fecha to set
     */
    public void setFecha(String fecha)
    {
        Fecha = fecha;
    }
    
    public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}

}